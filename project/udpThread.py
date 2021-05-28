#Ka Hou Hong 22085304
import socket


IP = "127.0.0.1"
def udpThread(desNameMap, serverName,udpSocket,port):
    try:
        # TRANSMIT THE UDP MESSAGE 
        udpSocket.sendto(bytes(serverName,encoding="utf-8"),(IP,port))
        # RECEIVE THE MESSAGE FROM UDP SOCKET
        recvStr = str(udpSocket.recv(1024)).strip() ## b'JunctionA'
        # STORE THE NAME CORRESPONDING TO THE PORT NUMBER IN DICTIONARY
        desNameMap[port] = recvStr

    except Exception as e:
        # PRINT ERROR
        print(e)







