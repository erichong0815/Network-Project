#Ka Hou Hong 22085304
import socket
class Requset:
    #CONSTRUCTOR
    def __init__(self,inp):
        self.inp =inp #INPUT
        self.uri =""
        self.request=""

    def parse(self):
        ii =-1 # READ INPUT VARIABLE
        try:
            self.request = str(self.inp.recv(1024)).strip() #HTTP REQUEST
            self.parseUri() #CALL THIS FUNCTION TO GET URIs eg:/?to=BusportF
        except Exception as e:
            #PRINT ERROR
            print(e)

    def parseUri(self):
        # FIND THE 1ST SPACE IN SELF.REQUEST AND FIND THE 2DN
        index1 = self.request.find(' ')
        index2 = -1
        if index1 != -1:
            index2 = self.request.find(' ',index1+1)
        if index2 > index1:
            self.uri =self.request[index1+1:index2]
            #print(str(self.uri)) #/?to=BusportF

    # THIS FUNCTION IS TO RETURN THE URI AND USED IN response.py
    def getUri(self):
        return self.uri


