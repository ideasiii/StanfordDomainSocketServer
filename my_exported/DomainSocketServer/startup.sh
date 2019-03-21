#!/bin/sh
kill -9 $(ps aux | grep StanfordDomainSocketServer | awk '{print $2}')
java -jar /data/opt/DomainSocketServer/StanfordDomainSocketServer_closeConn_V3.0.jar /data/opt/DomainSocketServer/chineseFactored.ser.gz &