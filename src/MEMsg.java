public class MEMsg {
    private int id;
    private String content;
    private int timeStamp;

    MEMsg(String msg) {
        String[] token = msg.split("\\|");
        this.content = token[0];
        this.id = Integer.parseInt(token[1]);
        this.timeStamp = Integer.parseInt(token[2]);
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String toString() {
        return content + "|" + id + "|" + timeStamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
