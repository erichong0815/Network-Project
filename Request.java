// Ka Hou Hong 22085304
import java.io.InputStream;
import java.io.IOException;

public class Request {

    private InputStream input;
    // /xxx-station.html
    private String uri;
    private StringBuffer request;

    //CONSTRUCTOR
    public Request(InputStream input) {
        this.input = input;
    }

    // LEARNED INPUTSTREAM METHOD FROM: Java.io.InputStream Class in Java
    // https://www.geeksforgeeks.org/java-io-inputstream-class-in-java/
    // READ REQUEST INFORMATION FROM INPUTSTREAM AND GET URI VALUE FROM REQUEST
    public void parse() {
        // CHARACTERS AND SUBSTRINGS CAN BE APPENDED TO THE END USING STRING BUFFER
        request = new StringBuffer(2048);
        int ii=-1; //READ INPUT VARIABLE
        byte[] buffer = new byte[2048];
        try {
            //READ DATA FROM INPUT STREAM
            ii = input.read(buffer);
        } catch (IOException e) {
            //PRINT ERROR
            e.printStackTrace();
            System.exit(0);
        }
        //APPEND DATA TO REQUEST
        for (int j = 0; j < ii; j++) {
            request.append((char) buffer[j]);
        }
        uri = parseUri(request.toString()); // GET THE URI
    }

    /**
     *
     * requestString format is like：
     * GET /index.html HTTP/1.1
     * Host: localhost:xxxx
     * Connection: keep-alive
     * ...
     * The purpose of this function is to get the /index.html string
     */
    private String parseUri(String requestString) {
        //System.out.println("requestString: "+requestString);
        int index1, index2;
        //FIND THE 1ST SPACE IN REQUESRSTRING AND FIND THE 2DN
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1)
                ///RETURN URI eg./?to=BusportF 
                return requestString.substring(index1 + 1, index2);
        }
        return null;
    }
    //RETURN URI
    public String getUri() {
        return uri;
    }
}