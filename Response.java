// Ka Hong Hong (22085304)
import java.net.DatagramPacket;
import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/*
  HTTP Response =
    response line
    response headers
    response body(html)
*/

public class Response {

    Request request;
    OutputStream output;
    // RAW RESQUEST, THE RESPNOSE MESSAGE WILL BE APPENDED LATER
    private static final String RAW_RES = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html\r\n"+"\r\n"+
            "<html><p>";
    // INVALID REQUEST
    private static final String wrongtime ="HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html\r\n"+
            "Content-Length: 100\r\n" + "\r\n" +
            "<html><h1>Invalid time, please take the transport tomorrow</h1><html>";
    // FOR APPENDING STRING LATER
    private StringBuffer response = new StringBuffer(RAW_RES);
    private String finalDes  = null; // FINAL DESTINATION VARIABLE
    private String latest = null; // THE LATEST TIME ON TIME TABLE
    private ArrayList<String> temptimelist = new ArrayList<String>(); // FOR STORING THE TRANSPORT TIME
    public Response(OutputStream output) {
        this.output = output;
    }

    public void packageRequest(Request request) {
        this.request = request;
        this.finalDes = request.getUri().split("=")[1].trim();// GET THE NAME OF DESTINATION
    }
    
    // THIS FUNCTION IS TO GET THE LAST LINE OF TIMETABLE
    private String lastLine(String file){
        FileReader reader = null;
        String read_line = null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // ERROR
        }
        Scanner sc = new Scanner(reader); // USING SCANNER TO DO ITERATION
        while((sc.hasNextLine()&&(read_line=sc.nextLine())!=null)){
            if(!sc.hasNextLine()){
                return read_line; //20:10,busA_F,stopA,20:27,BusportF
            }
        }
        sc.close(); // CLOSE 
        return null;
    }

    public void sendResponse() throws IOException {
        // USE lastLine() TO GET THE LAST LINE AND SPLIT TO GET THE TIME
        latest = lastLine("tt-"+Server.getServerName()).split(",")[0];
        doResponse(Server.getServerName(),null);
    }


    public void doResponse(String destination,String recentlyTimeLine)throws IOException{
        // TIME FORMAT
        DateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//LIKE 2016-08-10 20:40
        // GET THE QUERY TIME OF USER
        File html_date = new File("myform.html");
        String html_date_string = simpleFormat.format(html_date.lastModified());
        html_date_string = html_date_string.split(" ")[1];
        // TIME LINE FORMING
        if(recentlyTimeLine==null){
            recentlyTimeLine = html_date_string+",busA_F,stopA,23:59,"+finalDes; 
        }

        // LEAVE TIME, IT WILL BE CHANGED IN EVERY RECURSION
        String leaveTime = recentlyTimeLine.split(",")[0].trim();

        try{
            // CHECK IF THE LEAVE TIME IS LATER THAN THE LATEST TRANSPORT TIME 
            Date leaveD = simpleFormat.parse("2020-01-01 "+leaveTime);
            Date latestD = simpleFormat.parse("2020-01-01 "+latest);
            if(leaveD.compareTo(latestD) >0){
                output.write(wrongtime.getBytes()); // SEND TIME INVALIDATION MESSAGE
                return;
            }
        }catch (Exception e){
            e.printStackTrace(); // ERROR
        }

        try {
            // GET FILE CREATION TIME AND MODIFIED TIME, LEARNED FROM: https://stackoverflow.com/questions/2723838/determine-file-creation-date-in-java
            File f = new File("tt-"+destination);
            BasicFileAttributes attributes = null;
            try
            {
                attributes = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
            }catch (IOException exception)
            {
                System.out.println("Error: " + exception.getMessage());
            }
            String ctime = attributes.creationTime().toString();
            String mtime = attributes.lastAccessTime().toString();
            String timeLine = null;
            String temDes = null;
            // PARSE ARBITRARY DATE WITH LEAVETIME Wed Jan 01 10:36:00 AWST 2020
            Date fromDate = simpleFormat.parse("2020-01-01 "+leaveTime);
            // TIME TWO TIMES ARE SAME AND ARRAY LIST IS NOT EMPTY,
            // THE ARRAYLIST WILL PROVIDE THE TIMETABLE, ELSE OPEN FILE INSTEAD  
            if (ctime.compareTo(mtime)==0 && !temptimelist.isEmpty()){
                for(int i=0; i<temptimelist.size(); i++){ 
                    String recentlyArriveT = "23:59"; // SET THE LARGEST TIME OF A DAY
                    timeLine = temptimelist.get(i);
                    // IGNORE THE FIRST LINE
                    if(timeLine.split(",").length<4)continue;
                    //LEAVE TIME AND ARRIVE TIME
                    String leaveT =timeLine.split(",")[0].trim();
                    String arriveT =timeLine.split(",")[3].trim();
                    // TEMP LEAVE DATE 
                    Date temLeaveDate = simpleFormat.parse("2020-01-01 "+leaveT);
                    // COMPARE TIME
                    if(temLeaveDate.compareTo(fromDate)>=0){
                        if(arriveT.compareTo(recentlyArriveT)<0){//cur < recently
                            recentlyArriveT = arriveT;
                            recentlyTimeLine = timeLine;
                        }
                    }
                }
                // CHECK IF TEMDES IS NOT THE FINAL DESTINATION.
                // OTHER TXT FILE WILL BE READ
                temDes =recentlyTimeLine.split(",")[4].trim();
                if(!temDes.equals(finalDes)){
                    temptimelist.clear();
                }
            }else{
                timeLine = null;
                String recentlyArriveT = "23:59"; // SET THE LARGEST TIME OF A DAY
                FileReader tt = new FileReader("tt-"+destination);
                BufferedReader bufferedtt = new BufferedReader(tt);                                  
                // READ LINES
                while ((timeLine=bufferedtt.readLine())!=null){
                    // CHECK LEAVE TIME
                    //THE FIRST LINE IS IGNORED
                    if(timeLine.split(",").length<4)continue;
                    temptimelist.add(timeLine); // STORING TIME LINE TO ARRAYLIST
                    //LEAVE TIME AND ARRIVE TIME
                    String leaveT =timeLine.split(",")[0].trim();
                    String arriveT =timeLine.split(",")[3].trim();//len>=4
                    // TEMP LEAVE DATE
                    Date temLeaveDate = simpleFormat.parse("2020-01-01 "+leaveT);
                    // COMPARE TIME
                    if(temLeaveDate.compareTo(fromDate)>=0){
                        if(arriveT.compareTo(recentlyArriveT)<0){//cur < recently
                            recentlyArriveT = arriveT;
                            recentlyTimeLine = timeLine;
                        }
                    }
                }   
            }
            // THE RESPONSE MESSAGE
            String returnrecentlyTimeLine = "At "+recentlyTimeLine.split(",")[0]+", catch "+recentlyTimeLine.split(",")[1]
                                +", from "+recentlyTimeLine.split(",")[2]+". You will arrive at "+recentlyTimeLine.split(",")[4]
                                +" at "+ recentlyTimeLine.split(",")[3]+".";
            // THE TEMP DESTINATION FOR DOING RECURSION IF WE NEED TO TRANSFORM
            temDes =recentlyTimeLine.split(",")[4].trim();
            response.append(returnrecentlyTimeLine+"</br>");
            // IF THEY ARE SAME, THE RESPONSE MESSAGE WILL BE SENT
            if(temDes.equals(finalDes)){
                response.append("</p></html>");
                output.write(response.toString().getBytes());
                return;
            }
            // INDIRECT ARRIVE - WE NEED TO TRANSFORM
            latest = lastLine("tt-"+temDes).split(",")[0];
            doResponse(temDes,recentlyTimeLine); //RECURSION
            
        } catch (Exception e) { // ERROR MESSAGE 
            String errorMessage = "HTTP/1.1 500 INTERNAL SERVER ERR\r\n" +
                    "Content-Type: text/html\r\n"+
                    "Content-Length: 100\r\n" + "\r\n" +
                    "<h1>Internal Server Error"+e+"</h1>";
            output.write(errorMessage.getBytes()); // SEND ERROR MESSAGE
            // thrown if cannot instantiate a File object
            e.printStackTrace(); 
        } finally {
            output.close(); // CLOSE OUTPUT
        }
    }
}