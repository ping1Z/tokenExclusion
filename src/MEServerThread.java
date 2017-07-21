import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MEServerThread extends Thread{;
    private int port;
    private List<MEServerEventListener> listeners;

    public MEServerThread(int port) {
        super("GCServerThread");
        this.port = port;
    }

    public void registerEventListenser(MEServerEventListener listener) {
        if(listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    public void run(){

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            postServerStarted();

            while (true) {
                Socket socket = serverSocket.accept();

                try (
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ) {
                    String inputLine;
                    MELogger.Debug("\n=== Received Msg Start ===");
                    while ((inputLine = in.readLine()) != null) {
                        MEMsg msg = new MEMsg(inputLine);
                        MELogger.Debug(inputLine);
                        onReceiveMsg(msg);
                    }
                    out.println("200");
                    socket.close();
                    MELogger.Debug("Socket closed.");
                } catch (IOException e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    MELogger.Error(sw.toString());
                }
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
