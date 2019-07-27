#!/bin/bash
cd /home/centos/webapp
sudo chown -R centos:centos /home/centos/webapp
sudo chmod +x webApp-0.0.1-SNAPSHOT.jar
source /etc/profile.d/envvariable.sh
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/centos/webapp/cloudwatchconfig.json -s
kill -9 $(ps -ef|grep webApp | grep -v grep | awk '{print$2}')
nohup java -jar webApp-0.0.1-SNAPSHOT.jar > /home/centos/output.txt 2> /home/centos/output.txt < /home/centos/output.txt &
