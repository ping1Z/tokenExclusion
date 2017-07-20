public interface MEServerEventListener {
    void postServerStarted();
    void onReceiveMsg(MEMsg msg);
}
