import java.util.LinkedList;

/**
 * Created by ping1zhong on 7/17/17.
 */
public class MutualExclusionService {

    private MENodeInfo local;
    private MEServiceStatus status;
    private MENodeInfo holder;
    private LinkedList<MERequest> queue;

    public MutualExclusionService(){
    }

    public void init(){}
    public void start(){}

    private void startServer(){}
    private void processMsg(MERequest msg){}
    private void sendMsg(MERequest msg){}

    public void csEnter(){}
    public void csLeave(){}

}
