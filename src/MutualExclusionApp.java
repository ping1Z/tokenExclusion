import java.io.*;
import java.util.HashMap;
import java.util.Random;

public class MutualExclusionApp {
    private int nodesNum = 0;
    private int interRequestDelayMean;
    private int csExecutionTimeMean;
    private int requestsNum;

    private MutualExclusionService meService;
    private MENodeInfo local;
    private MENodeInfo holder;
    private HashMap<Integer, MENodeInfo> neighbours = new HashMap<>();

    public static void main(String[] args) {

        try {
            String configPath = args.length > 0 ? args[0] : "../config.txt";
            int localId = args.length > 1 ? Integer.parseInt((args[1])) : 1;
            String logPath = args.length > 2 ? (args[2]) : "./logs";

            MELogger.Init(logPath + "/node_" + localId + ".log");
            MELogger.Info("Init node......");

            MutualExclusionApp meApp = new MutualExclusionApp();
            meApp.readConfig(configPath, localId);
            meApp.meService = new MutualExclusionService(meApp.local, meApp.holder, meApp.neighbours);
            meApp.meService.start();

            MELogger.Info("MutualExclusionService is started.");

            int count = 5;
            while(count>0){
                MELogger.Info("%d seconds to start.",count);
                Thread.sleep(1000);
                count--;
            }

            while(!meApp.meService.isRunning()){
                Thread.sleep(1000);
            }

            MELogger.Info("MutualExclusionApp is Started.");

            meApp.start();



        }catch (Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            MELogger.Error(sw.toString());
        }
    }

    private void readConfig(String fileName, int localId) throws Exception {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("No File Found " + fileName);
        }
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            int myHolderId = 0;
            isr = new InputStreamReader(new FileInputStream(file));
            br = new BufferedReader(isr);
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
                            myHolderId = holderId;

                            br.close();
                            isr.close();

                            // read config of neighbours
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
                                        nodeId = Integer.parseInt(token[0]);
                                        nodeHost = token[1];
                                        nodePort = Integer.parseInt(token[2]);
                                        holderId = Integer.parseInt(token[3]);
                                        if (nodeId == myHolderId || localId == holderId) {
                                            neighbours.put(nodeId, new MENodeInfo(nodeId, nodeHost, nodePort));
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            holder = myHolderId == localId ? local : neighbours.get(myHolderId);

        }catch (Exception e){
            throw e;

        }finally {
            if(br !=null){
                br.close();
            }
            if(isr !=null) {
                isr.close();
            }
        }

    }

    private boolean isValidLine(String line) {
        return line.length() > 0 && Character.isDigit(line.charAt(0));
    }

    private void start() {

        try {
            Random randCS = new Random();
            Random randWait = new Random();
            // to calculate system throughput
            long appStart = System.currentTimeMillis();
            while (requestsNum > 0) {
                MELogger.Info("Try to enter CRITICAL SECTION.");
                // to calculate response time
                long repsonseStart = System.currentTimeMillis();

                meService.csEnter();

                // to calculate execution time
                long executionStart = System.currentTimeMillis();

                MELogger.Info("LocalId: %d, TimeStamp: %d, enter the CRITICAL SECTION.", local.getId(), meService.getTimeStamp());

                // to evaluate implementation correctness
                MELogger.Debug("[ME_REPORT_TIMESTAMP] %d %d %s %d", meService.getTimeStamp(), local.getId(),local.getHost(),local.getPort());

                Thread.sleep(getNext(randCS, csExecutionTimeMean));
                MELogger.Debug("[ME_REPORT_EXECUTION] %d %d %d", local.getId(), requestsNum, System.currentTimeMillis() - executionStart);

                meService.csLeave();
                MELogger.Debug("[ME_REPORT_RESPONSE] %d %d %d", local.getId(), requestsNum, System.currentTimeMillis() - repsonseStart);
                MELogger.Info("Leave CRITICAL SECTION.");

                Thread.sleep(getNext(randWait, interRequestDelayMean));

                requestsNum--;
            }
            // to calculate system throughput
            MELogger.Debug("[ME_REPORT_APP] %d %d", local.getId(), System.currentTimeMillis() - appStart);
        }catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            MELogger.Error(sw.toString());
        }
    }

    private long getNext(Random rand, int randomMean) {
        return  (long)(Math.log(1- rand.nextDouble())/(-(1.0 / randomMean)));
    }
}
