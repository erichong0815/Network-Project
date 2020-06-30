// Ka Hou Hong 22085304
import java.io.*;
import java.net.*;

public class UdpThread implements Runnable{
    private DatagramSocket socket;
    private int[]adjacency;
    private int number;
    //WE NEED TO PACKAGE THE req/res
    public UdpThread(DatagramSocket socket,int[]adjacency,int i){
        this.socket =socket;
        this.adjacency =adjacency;
        this.number=i;
    }


    // THIS FUNCTION IS TO SAVE THE PORT AND ITS NAME INTO THE HASHMAP 
    public void run(){
        try {
            System.out.printf(Server.getServerName()+" send req to a specific port:%d\n",adjacency[number]);
            byte[] sendBytes = Server.getServerName().getBytes();
            //CREAT THE DATAGRAMPACKET FOR SENDING THE DATA WITH (BUF, THE LENGTH OF BUF, IP AND ADJACENCY)
            socket.send(new DatagramPacket(sendBytes,sendBytes.length,InetAddress.getByName("127.0.0.1"),adjacency[number]));
            //RECEIVE CLIENT NAME
            byte[] recvBytes = new byte[1024];
            //CREAT THE DATAGRAMPACKET FOR RECEIVING DATA WITH (recvBytes, the length of recvBytes)
            DatagramPacket recvPacket = new DatagramPacket(recvBytes,1024);
            socket.receive(recvPacket);
            String name = new String(recvBytes);
            //System.out.println(Server.getServerName()+" neighbour's name is :"+name);
            //INSERT THE PORT AND NAME INTO THE MAP
            Server.getMap().put(adjacency[number], name);
            

        } catch (Exception e) {
            e.printStackTrace(); // ERROR
        }
    }
}
