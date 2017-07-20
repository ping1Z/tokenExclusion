public class MEMsg {
    private int id;
    private String content;

    MEMsg(String msg) {
        String[] token = msg.split("\\|");
        this.content = token[0];
        this.id = Integer.parseInt(token[1]);
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return content + "|" + id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
