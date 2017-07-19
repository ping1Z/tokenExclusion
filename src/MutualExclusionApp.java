import java.io.*;
import java.util.Random;

/**
 * Created by ping1zhong on 7/17/17.
 */
public class MutualExclusionApp {
    private int nodesNum = 0;
    private int interRequestDelayMean;
    private int csExecutionTimeMean;
    private int requestsNum;

    private MutualExclusionService meService;
    private MENodeInfo local;
    private MENodeInfo holder;

    public static void main(String[] args) {
        String configPath = args.length>0? args[0]: "../configs";
        int localId = args.length>1? Integer.parseInt((args[1])):1;
        String logPath = args.length>2?(args[2]):"./logs";

        MELogger.Init(logPath +"/node_"+ localId+".log");
        MELogger.Info("Init node......");


        MutualExclusionApp meApp = new MutualExclusionApp();
        meApp.readConfig(configPath, localId);
        meApp.meService = new MutualExclusionService(meApp.local, meApp.holder);
        meApp.meService.start();

        MELogger.Info("MutualExclusionService is started.");

        meApp.start();

        MELogger.Info("MutualExclusionApp is Started.");
    }

    private void readConfig(String fileName, int localId) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("No File Found " + fileName);
        }
        InputStreamReader isr;
        try
        {
            isr = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (isValidLine(line)) {
                    String realLine = line.split("#")[0];
                    if (nodesNum == 0) {
                        String[] token = realLine.split("\\s+");
                        nodesNum = Integer.parseInt(token[0]);
                        interRequestDelayMean = Integer.parseInt(token[1]);
                        csExecutionTimeMean = Integer.parseInt(token[2]);
                        requestsNum = Integer.parseInt(token[3]);
                    } else {
                        String[] token = realLine.split("\\s+");
                        if (localId == Integer.parseInt(token[0])) {
                            int nodeId = Integer.parseInt(token[0]);
                            String nodeHost = token[1];
                            int nodePort = Integer.parseInt(token[2]);
                            int holderId = Integer.parseInt(token[3]);
                            local = new MENodeInfo(nodeId, nodeHost, nodePort);

                            br.close();
                            isr.close();

                            // read config of holder
                            isr = new InputStreamReader(new FileInputStream(file));
                            br = new BufferedReader(isr);
                            int validLine = 0;
                            while ((line = br.readLine()) != null) {
                                line = line.trim();
                                if (isValidLine(line)) {
                                    realLine = line.split("#")[0];
                                    if (validLine == 0) validLine++;
                                    else {
                                        token = realLine.split("\\s+");
                                        if (holderId == Integer.parseInt(token[0])) {
                                            holder = new MENodeInfo(Integer.parseInt(token[0]), token[1], Integer.parseInt(token[2]));
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            br.close();
            isr.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isValidLine(String line) {
        return line.length() > 0 && Character.isDigit(line.charAt(0));
    }

    private void start() {
        Random randCS = new Random();
        Random randWait = new Random();
        while (requestsNum > 0) {
            MELogger.Info("Try to enter CRITICAL SECTION.");
            meService.csEnter();
            try {
                Thread.sleep(getNext(randCS, csExecutionTimeMean));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            meService.csLeave();
            MELogger.Info("Leave CRITICAL SECTION.");
            try {
                Thread.sleep(getNext(randWait, interRequestDelayMean));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            requestsNum--;
        }
    }

    private long getNext(Random rand, int randomMean) {
        return  (long)(Math.log(1- rand.nextDouble())/(-(1.0 / randomMean)));
    }
}
