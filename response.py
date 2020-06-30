# Ka Hou Hong 22085304
import socket
import os
import time
from datetime import  datetime


class Response:
    def __init__(self,req,desNameMap,serverName):
        self.ser = serverName
        self.map =desNameMap
        self.req = req
        # HTTP RESPONSE, WILL BE APPEND LATER
        self.response = '''HTTP/1.1 200 OK
            Content-Type: text/html\r\n
            <html><p>
            '''
        # INVALID REQUEST
        self.wrongtime = '''HTTP/1.1 500 INTERNAL ERROR
                Content-Type: text/html
                Content-Length: 100\r\n
                <h1>Invalid time, please take the transport tomorrow</h1>
                '''
        # GET THE NAME OF DESTINATION
        self.finalDes = req.getUri().split('=')[1].strip()
        self.latest = ""  # THE LATEST TIME ON TIME TABLE
        self.temptimelist = [] # FOR STORING THE TRANSPORT TIME

    def sendRes(self,so):
        # GET THE LAST LINE's LEAVE TIME OF TIMETABLE
        with open("tt-"+self.ser) as file:
            for last_line in file:
                pass
        self.latest = last_line.split(",")[0]
        file.close() # CLOSE FILE
        self.doSendRes(self.ser,None,so)

    def doSendRes(self,destination,recentlyTimeLine,so):
        # learn from geesforgeeks: https://www.geeksforgeeks.org/python-os-path-getmtime-method/
        # GET THE QUERY TIME OF USER
        html_date = os.path.getmtime("myform.html")
        local_time = time.ctime(html_date) 
        #like 10:36
        html_time= local_time.split(" ")[3].split(":")[0] +":" + local_time.split(" ")[3].split(":")[1]
        # TIME LINE FORMING
        if(recentlyTimeLine==None):
            recentlyTimeLine= html_time+",busA_F,stopA,23:59,"+self.finalDes
        # LEAVE TIME, IT WILL BE CHANGED IN EVERY RECURSION
        leaveTime = recentlyTimeLine.split(",")[0].strip()
        # TIME FORMAT
        Hmformat = '%H:%M'
        # CHECK IF THE LEAVE TIME IS LATER THAN THE LATEST TRANSPORT TIME 
        if(datetime.strptime(leaveTime,Hmformat) > datetime.strptime(self.latest,Hmformat)):
            so.sendall(bytes(self.wrongtime,encoding="utf-8")) # SEND TIME INVALIDATION MESSAGE
            return
        try:
            # DATE FORMAT
            format = '%Y-%m-%d %H:%M'
            temDes = None
            # CHANGE DATE FORMAT
            fromDate = datetime.strptime("2020-01-01 " + leaveTime, format)
            #file
            f  = "tt-"+destination
            mtime = time.ctime(os.path.getmtime(f)) #  FILE MODIFIED TIME
            ctime = time.ctime(os.path.getctime(f)) #  FILE CREATION TIME
            timeLine = None
            # TIME TWO TIMES ARE SAME AND THE LIST IS NOT EMPTY,
            # THE LIST WILL PROVIDE THE TIMETABLE, ELSE OPEN FILE INSTEAD
            if (mtime == ctime and self.temptimelist != [] and temDes==self.ser):
                recentlyArriveT = "23:59" # SET THE LARGEST TIME OF A DAY
                for timeLine in self.temptimelist:
                    # IGNORE THE FIRST LINE
                    if len(timeLine.split(","))<4 :
                        continue
                    # LEAVE TIME AND ARRIVE TIME
                    leaveT = timeLine[0].split(",")[0]
                    arriveT = timeLine[0].split(",")[3]
                    # TEMP LEAVE DATE 
                    temLeaveDate = datetime.strptime("2020-01-01 "+leaveT, format)
                    # COMPARE TIME
                    if temLeaveDate>fromDate:
                        if arriveT < recentlyArriveT: # cur < recently
                            recentlyArriveT = arriveT 
                            recentlyTimeLine = timeLine
                # CHECK IF TEMDES IS NOT THE FINAL DESTINATION.
                # OTHER TXT FILE WILL BE READ
                temDes = recentlyTimeLine.split(",")[4].strip()
                if(temDes != self.finalDes) : 
                    self.temptimelist.clear()    
            else:           
                with open("tt-"+destination) as tt: 
                    tt_lines = tt.readlines() # READ LINE
                    timeLine = None
                    recentlyArriveT = "23:59" # SET THE LARGEST TIME OF A DAY
                    for timeLine in tt_lines:
                        # IGNORE THE FIRST LINE
                        if len(timeLine.split(","))<4 :
                            continue
                        # STORING TIME LINE TO THE LIST
                        self.temptimelist.append(timeLine)
                        # LEAVE TIME AND ARRIVE TIME
                        leaveT = timeLine.split(",")[0].strip()
                        arriveT = timeLine.split(",")[3].strip()
                        # TEMP LEAVE DATE
                        temLeaveDate = datetime.strptime("2020-01-01 "+leaveT, format)
                        # COMPARE TIME
                        if temLeaveDate>fromDate:
                            if arriveT < recentlyArriveT :
                                recentlyArriveT = arriveT 
                                recentlyTimeLine = timeLine      
            # THE RESPONSE MESSAGE
            returnrecentlyTimeLine = "At "+recentlyTimeLine.split(",")[0]+", catch "+recentlyTimeLine.split(",")[1]+", from "+recentlyTimeLine.split(",")[2]+". You will arrive at "+recentlyTimeLine.split(",")[4]+" at "+ recentlyTimeLine.split(",")[3]+"."
            # THE TEMP DESTINATION FOR DOING RECURSION IF WE NEED TO TRANSFORM
            temDes = recentlyTimeLine.split(",")[4].strip()
            self.response = self.response+returnrecentlyTimeLine + "</br>"
            # IF THEY ARE SAME, THE RESPONSE MESSAGE WILL BE SENT
            if temDes==self.finalDes:
                self.response+="</p></html>"
                so.sendall(bytes(self.response, encoding="utf-8"))
                return

            # INDIRECT ARRIVE - WE NEED TO TRANSFORM
            with open("tt-"+temDes) as file:
                for last_line in file:
                    pass
            self.latest = last_line.split(",")[0]
            self.doSendRes(temDes, recentlyTimeLine,so)#//RECURSION

        # PRINT ERROR AND SEND ERROR MESSAGE
        except Exception as  e:
            print(e) 
            err = '''HTTP/1.1 500 INTERNAL ERROR
                Content-Type: text/html
                Content-Length: 100\r\n
                <h1>Server internal err</h1>'''
            so.sendall(bytes(err,encoding="utf-8"))
        finally:
            so.close() # CLOSE SOCKET




