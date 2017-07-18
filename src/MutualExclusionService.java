import java.net.ServerSocket;
import java.util.LinkedList;
import java.io.*;

/**
 * Created by ping1zhong on 7/17/17.
 */
public class MutualExclusionService {

    private MENodeInfo local;
    private MENodeInfo holder;
    private MEServiceStatus status;
    private LinkedList<MERequest> queue;

    public MutualExclusionService(MENodeInfo local, MENodeInfo holder){
        this.local = local;
        this.holder = holder;
        status = MEServiceStatus.IDLE;
        queue = new LinkedList<>();
    }

    public void start(){
        startServer(local.getPort());
    }

    private void startServer(int port){
        try
        {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is running on " + local.getHost() + " with port number: " + local.getPort());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void processMsg(MERequest msg){}
    private void sendMsg(MERequest msg){}

    public void csEnter(){}
    public void csLeave(){}

}
