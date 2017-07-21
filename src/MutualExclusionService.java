import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

public class MutualExclusionService{

    private MENodeInfo local;
    private MENodeInfo holder;
    private boolean ASKED = false;
    public boolean inCS = false;
    private HashMap<Integer, MENodeInfo> neighbours;
    private LinkedList<Integer> queue = new LinkedList<>();
    private int timeStamp;
    public int getTimeStamp(){
        return timeStamp;
    }
    public void setTimeStamp(int timeStamp){
        this.timeStamp = timeStamp;
    }
    private MEServerThread server;

    public MutualExclusionService(MENodeInfo local, MENodeInfo holder, HashMap<Integer, MENodeInfo> neighbours) {
        this.local = local;
        this.holder = holder;
        this.neighbours = neighbours;
    }

    public void start() {
        startServer();
    }

    public  void startServer() {
        if(this.server != null) {
            MELogger.Info("MEServer is running already.");
            return;
        }
        this.server = new MEServerThread(this.local.getPort());

        this.server.registerEventListenser(new MEServerEventListener() {

            @Override
            public void postServerStarted() {
                MELogger.Info("postServerStarted");
            }

            @Override
            public void onReceiveMsg(MEMsg msg) {
                MELogger.Info("onReceiveMsg: " + msg.getContent() + " " + msg.getId());
                processMsg(msg);
            }
        });

        this.server.start();
    }
    private void makeRequest() {
        if (holder.getId() != local.getId() && !queue.isEmpty() && !ASKED) {
            sendRequest();
            ASKED = true;
        }
    }

    private void assignToken() {
        if (holder.getId() == local.getId() && !inCS && !queue.isEmpty()) {
            int requestId = queue.poll();
            if (requestId != local.getId()) {
                sendToken(requestId);
                holder = neighbours.get(requestId);
                ASKED = false;
            } else {
                inCS = true;
            }
        }
    }

    public void processMsg(MEMsg msg) {
        if (msg.getContent().equals("REQUEST")) {
            queue.add(msg.getId());
        } else {
            setTimeStamp(msg.getTimeStamp());
            holder = local;
        }
        assignToken();
        makeRequest();
    }

    public void csEnter() {
        MELogger.Info("Request %d", local.getId());
        queue.add(local.getId());
        assignToken();
        makeRequest();
    }

    public void csLeave() {
        inCS = false;
        setTimeStamp(getTimeStamp() + 1);
        assignToken();
        makeRequest();
    }

    private void sendRequest() {
        try
        {
            String msg = "REQUEST|" + local.getId() + "|" + getTimeStamp();
            Socket clientSocket = new Socket(holder.getHost(), holder.getPort());
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            out.write(msg);
            out.close();
            MELogger.Info("Send Request to %d", holder.getId());
            clientSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToken(int requestId) {
        try
        {
            String msg = "TOKEN|" + local.getId() + "|" + getTimeStamp();
            MENodeInfo sendTo = neighbours.get(requestId);
            Socket clientSocket = new Socket(sendTo.getHost(), sendTo.getPort());
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            out.write(msg);
            MELogger.Info("Send Token to %d", sendTo.getId());
            out.close();
            clientSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
