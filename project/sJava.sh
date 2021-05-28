# Ka Hou Hong 22085304

javac Server.java
javac ServerThread.java
javac UdpThread.java
javac Response.java
javac Request.java
java Server JunctionA 4001 4002 4012 &
java Server BusportB 4003 4004 4006 4008 4010 4012 &
java Server StationC 4005 4006 4004 4012 &
java Server TerminalD 4007 4008 4004 & 
java Server JunctionE 4009 4010 4004 & 
java Server BusportF 4011 4012 4002 4004 4006 &
