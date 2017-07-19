/**
 * Created by ping1zhong on 7/18/17.
 */
public interface MEServerEventListener {
    void postServerStarted();
    void onReceiveMsg(MEMsg msg);

}
