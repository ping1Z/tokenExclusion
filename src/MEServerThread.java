import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ping1zhong on 7/17/17.
 */
public class MEServerThread extends Thread{;
    private int port;
    private List<MEServerEventListener> listeners;
    public MEServerThread(int port) {
        super("GCServerThread");
        this.port = port;


    }

    public void registerEventListenser(MEServerEventListener listener){
        if(listeners == null){
            listeners = new ArrayList<MEServerEventListener>();
        }
        listeners.add(listener);
    }

    public void run(){

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            postServerStarted();

            Socket socket = serverSocket.accept();

                try (
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()));
                ) {
                    String inputLine;
                    MEMsg msg = new MEMsg();
                    MELogger.Debug("\n=== Received Msg Start ===");
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.equals(""))
                            break;
                        MELogger.Debug(inputLine);
                        String[] kv = inputLine.trim().split(":",2);
                        if(kv.length<2){
                            continue;
                        }

                        if(kv[0].equals("src-id")){
                            msg.setId(Integer.parseInt(kv[1].trim()));
                        }else if(kv[0].equals("src-host")){
                            msg.setHost(kv[1].trim());
                        }else if(kv[0].equals("src-port")) {
                            msg.setPort(Integer.parseInt(kv[1].trim()));
                        }else if(kv[0].equals("content")){
                            msg.setContent(kv[1].trim());
                        }
                    }
                    out.println("200");
                    socket.close();
                    MELogger.Debug("Socket closed.");
                    onReceiveMsg(msg);
                } catch (IOException e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    MELogger.Error(sw.toString());
                }
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            MELogger.Error(sw.toString());
        }
    }

    private void postServerStarted(){
        if(listeners.isEmpty())return;

        for(MEServerEventListener listener: listeners){
            listener.postServerStarted();
        }
    }

    private void onReceiveMsg(MEMsg msg){
        if(listeners.isEmpty())return;

        for(MEServerEventListener listener: listeners){
            listener.onReceiveMsg(msg);
        }
    }


}
