[Read me] 

* Reference URL: 
  ==>http://puremonkey2010.blogspot.com/2014/11/java-junixsocket-unix-domain-socket-4j.html
  
* Junixsocket Library: 
  ==>/opt/newsclub/lib-native/libjunixsocket-linux-1.5-amd64.so

* IPC file path: 
  ==>/data/opt/ipc_tmp/ipc_tmp.txt

* My exported runnable file(jar): 
  ==>/StanfordDomainSocketServer/my_exported/DomainSocketServer/
     - StanfordDomainSocketServer_closeConn_Vx.0.jar 
     - chineseFactored.ser.gz
     - startup.sh

* Note: The command to run the jar in back ground:
  ==>startup.sh:
     # java -jar StanfordDomainSocketServer_closeConn_Vx.0.jar &
  ==>As the command above, need to add "&" at the end of the command line.
