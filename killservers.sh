# Ka Hou Hong 22085304
#learned from : stack overflow: Shell script to capture Process ID and kill it if exist [duplicate]
#https://stackoverflow.com/questions/13910087/shell-script-to-capture-process-id-and-kill-it-if-exist

echo `lsof -i:4001,4002,4003,4004,4005,4006,4007,4008,4009,4010,4011,4012,4013,4014,4015 | awk 'NR!=1{print $2}' | xargs kill -9`

echo down







