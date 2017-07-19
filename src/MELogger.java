import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MELogger {

    public static PrintStream outputStream = null;
    public static void Init(String path){
        try {
            File logFile = new File(path);
            logFile.createNewFile(); // if file already exists will do nothing
            outputStream = new PrintStream(logFile);
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw.toString());
        }

    }

    public static void Info(String log,  Object... args){
        try {
            if (args != null) {
                log = String.format(log, args);
            }
            log = String.format("[Info] %s: %s", getCurrentTimeStamp(), log);
            System.out.println(log);

            if (outputStream != null) {
                outputStream.println(log);
            }
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw.toString());
        }
    }

    public static void Error(String log,  Object... args) {
        try {
            if (args != null) {
                log = String.format(log, args);
            }
            log = String.format("[Error] %s: %s", getCurrentTimeStamp(), log);
            System.out.println(log);
            if (outputStream != null) {
                outputStream.println(log);
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw.toString());
        }
    }

    public static void Debug(String log,  Object... args) {
        try {
            if (args != null) {
                log = String.format(log, args);
            }
            log = String.format("[Debug] %s: %s", getCurrentTimeStamp(), log);
            // System.out.println(log);
            if (outputStream != null) {
                outputStream.println(log);
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw.toString());
        }
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
