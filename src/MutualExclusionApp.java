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

    private MutualExclusionService MES;
    private MENodeInfo local;
    private MENodeInfo holder;

    private Random randCS = new Random();
    private Random randWait = new Random();


    public static void main(String[] args) {
        MutualExclusionApp MEA = new MutualExclusionApp();
        MEA.readConfig(args[0], Integer.parseInt(args[1]));
        MEA.MES = new MutualExclusionService(MEA.local, MEA.holder);
        MEA.MES.start();
        MEA.start();
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
                            local = new MENodeInfo(Integer.parseInt(token[0]), token[1], Integer.parseInt(token[2]), Integer.parseInt(token[3]));
                            br.close();
                            isr.close();
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
                                        if (local.getHolderId() == Integer.parseInt(token[0])) {
                                            holder = new MENodeInfo(Integer.parseInt(token[0]), token[1], Integer.parseInt(token[2]), Integer.parseInt(token[3]));
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
        while (requestsNum > 0) {
            MES.csEnter();
            try {
                Thread.sleep(getNext(randCS, csExecutionTimeMean));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            MES.csLeave();
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
