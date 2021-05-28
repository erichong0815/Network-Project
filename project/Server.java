// Ka Hou Hong 22085304

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    //FOR STORING THE PORT AND NAME
    private static final HashMap<Integer,String> myMap = new HashMap<>();
    private static String name;
    private static int udpPort;
    private static DatagramSocket udpSocket;

    public static  void startService( String[] args) throws Exception{
        // IF NOT ENOUGH OF ARGS
        if(args.length<4)throw new IllegalArgumentException("args not enough");
        //NAME, TCP PORT, UDP PORT AND ADJACENCY
        name =args[0];
        int tcpPort = Integer.parseInt(args[1]);
        udpPort = Integer.parseInt(args[2]);
        int[] adjacency = new int[args.length-3];
        //CREATE THE DATAGRAMSOCKET AND BIND THE UDPPORT
        udpSocket = new DatagramSocket(udpPort);
        for (int i = 0; i <adjacency.length ; i++) {
            adjacency[i]=Integer.parseInt(args[3+i]);
            //USE UDPTHREAD()
            UdpThread udpThread = new UdpThread(udpSocket,adjacency,i);
            udpThread.run();
        }

        //TCP SOCKET
        //MAKE A SERVERSOCKET FOR THE SPECIFIC TCPPORT
        ServerSocket serverSocket = new ServerSocket(tcpPort);
        //SERVER NEVER STOP
        while (true){
            //WATING A CONNECTION WITH A CLIENT
            System.out.println(Server.getServerName()+" Waiting a socket in ");
            //BLOCK HERE UNTIL A CLIENT CONNECTS THE SERVER
            final Socket socket =  serverSocket.accept();
            //USE SERVERTHREAD
            ServerThread thread  = new ServerThread(socket);
            thread.run();
        }

    }

    // RETURN HASH MAP (PORT AND ITS NAME)
    public static HashMap<Integer, String> getMap() {
        return myMap;
    }

    //RETURN SERVERNAME
    public static String getServerName() {
        return name;
    }


    public static void main(String[] args) {
        try {
            Server.startService(args); // START
        }catch (Exception e){
            e.printStackTrace(); // ERROR
        }
    }

}
