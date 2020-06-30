#Ka Hou Hong 22085304
import socket,signal
import sys,time
import os,_thread
from request import  Requset
from response import Response
from udpThread import udpThread

UFT8 = 'utf-8'
IP = '127.0.0.1'


# GET /index.html HTTP/1.1
# Host: localhost:xxxx
# Connection: keep-alive
# Cache-Control: max-age=0

desNameMap = {}
serverName = ""

def startService(argv):
    # exit
    #_thread.start_new_thread(do_exit(),None)
    global serverName # SET A GLOBAL VARIABLE
    # IF NOT ENOUGH OF ARGS
    #print(argv)
    if (len(argv)<4):
        print("args not enough");
    #NAME, TCP PORT, UDP PORT AND ADJACENCY
    serverName = argv[1]
    tcpPort = int(argv[2])
    udpPort = int(argv[3])
    des = []
    # CREAT UDPSOCKET AND BIND THE PORT
    udpSocket = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
    udpSocket.bind((IP,udpPort))

    for port in argv[4:]:
        des.append(int(port))
        # TO START A NEW THREAD, LEARNED FROM: _thread — Low-level threading API
        # https://docs.python.org/3/library/_thread.html
        _thread.start_new_thread(udpThread,(desNameMap,serverName,udpSocket,int(port)))

    # CREAT A TCP SOCKET AND BIND THE PORT
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.bind((IP, tcpPort))
    sock.listen(5) # TCP SOCKET LISTENING MODE

    # NEVER STOP
    while True:
         # MAXIMUM NUMBER OF REQUESTS WAITING
         print(serverName+" wait a conn in....")
         conn, addr = sock.accept() # INITIATES A CONNECTION WITH THE CLIENT
         doTcpThread(conn) # USE TCP THREAD



# REQUEST() IS TO GET THE DESTINATION NAME OF ONE JOURNEY.
# RESPONSE() IS TO CALCULATE THE TRANSPORT TIMETABLE AND SEND MESSAGE TO THE BROWSER.
def doTcpThread(so):
    try:
        # REQUEST THE SOCKET
        req = Requset(so)
        # CALL PARSE() TO GET THE URI: /?to=BusportF
        req.parse()
        # CREATE RES AND SEND RESPONSE
        res = Response(req,desNameMap,serverName)
        res.sendRes(so)
    except Exception as e:
        # PRINT EXCEPTION ERROR
        print(e)
    finally:
        # CLOSE SOCKET
        so.close()

if __name__ == '__main__':
    # START THE SERVICEs
    startService(sys.argv)





