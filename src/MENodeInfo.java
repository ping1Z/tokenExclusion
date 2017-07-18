/**
 * Created by ping1zhong on 7/17/17.
 */
public class MENodeInfo {
    private int id;
    private String host;
    private int port;
    private int holderId;

    public MENodeInfo(int id, String host, int port, int holderId) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.holderId = holderId;
    }

    public int getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getHolderId() {
        return holderId;
    }
}
