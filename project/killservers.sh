#!/bin/bash


#awk '{print $2}' 第二列
#awk 'NR==1{print}' 第1行
#awk 'NR!=1{print}' 除了第一行的所有行
echo `lsof -i:4001,4002,4003,4004,4005,4006,4007,4008,4009,4010,4011,4012,4013,4014,4015,2016 | awk 'NR!=1{print $2}' | xargs kill -9`

echo down







