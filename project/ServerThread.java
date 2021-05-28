// Ka Hou Hong 22085304
import java.io.*;
import java.net.*;
public class ServerThread implements Runnable{
    private Socket socket;

    //WE NEED TO PACKAGE THE req/res
    public ServerThread(Socket socket){
        this.socket =socket;
    }

    // REQUEST() IS TO GET THE DESTINATION NAME OF ONE JOURNEY.
    // RESPONSE() IS TO CALCULATE THE TRANSPORT TIMETABLE AND SEND MESSAGE TO THE BROWSER.
    public void run(){
        InputStream input = null;
        OutputStream output = null;
        try {
            //WAIT FOR CONNECTION, AFTER SUCCESSFUL CONNECTION, RETURN A SOCKET OBJECT
            //TAKE THE INPUT FROM THE CLINE SOCKET
            input = socket.getInputStream();
            //SEND OUTPUT TO THE SOCKET
            output = socket.getOutputStream();
            //CREATE REQUEST OBJECT AND PARSE
            Request request = new Request(input);
            //CALL PARSE() TO GET THE URI: /?to=BusportF
            request.parse();

            //CREATE RESPONSE OBJECT
            Response response = new Response(output);
            //CALL PACKAGEREQUEST() AND SENDRESPONSE()
            response.packageRequest(request);
            response.sendResponse();

            // CLOSE SOCKET
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
