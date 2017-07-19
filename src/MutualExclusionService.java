import java.net.ServerSocket;
import java.util.LinkedList;
import java.io.*;

/**
 * Created by ping1zhong on 7/17/17.
 */
public class MutualExclusionService{

    private MENodeInfo local;
    private MENodeInfo holder;
    private MEServiceStatus status;
    private LinkedList<MEMsg> queue;

    private MEServerThread server;

    public MutualExclusionService(MENodeInfo local, MENodeInfo holder){
        this.local = local;
        this.holder = holder;
        status = MEServiceStatus.CLOSE;
        queue = new LinkedList<>();
    }

    public void start(){
        startServer();
    }

    public  void startServer(){
        if(this.server!=null){
            MELogger.Info("MEServer is running already.");
            return;
        }
        this.server = new MEServerThread(this.local.getPort());

        this.server.registerEventListenser(new MEServerEventListener(){

            @Override
            public void postServerStarted(){
                MELogger.Info("postServerStarted");
                setStatus(MEServiceStatus.IDLE);
            }

            @Override
            public void onReceiveMsg(MEMsg msg) {
                MELogger.Info("onReceiveMsg");
                processMsg(msg);
            }
        });

        this.server.start();
    }
    private void setStatus(MEServiceStatus s){
        this.status = s;
    }

    private void processMsg(MEMsg msg){}
    private void sendMsg(MEMsg msg){}
    public void csEnter(){}
    public void csLeave(){}

}
