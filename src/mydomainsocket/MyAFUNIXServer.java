package mydomainsocket;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chineseparser.ChineseParser;

/**
 * Reference: 
 * ==>http://puremonkey2010.blogspot.com/2014/11/java-junixsocket-unix-domain-socket-4j.html
 * 
 */
public class MyAFUNIXServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyAFUNIXServer.class);
//	private static final String IPC_FILE_DIR = "/home/johann/ipc_tmp/";
	private static final String IPC_FILE_DIR = "/data/opt/ipc_tmp/";
	private static final String IPC_FILE_NAME = "ipc_tmp.txt";
	private static final int MAX_LENGTH = 200;
	private static ChineseParser stanfordChineseParser;

	public static void main(String[] args) {
		LOGGER.info("MyAFUNIXServer Start...");
		try {
			File parent = new File(IPC_FILE_DIR);
			if (!parent.exists()) {
				boolean mkdirs = parent.mkdirs();
				LOGGER.info("mkdirs: " + mkdirs);
			}
			File socketFile = new File(parent, IPC_FILE_NAME);
			if (socketFile.exists()) {
				socketFile.delete();
			}
			
			AFUNIXServerSocket server = AFUNIXServerSocket.newInstance();
			server.bind(new AFUNIXSocketAddress(socketFile));
			LOGGER.info("server: " + server);
			
			// Initialise the ChineseParser of Stanford
			String parserFile = null;
			if (args != null && args.length > 0) {
				parserFile = args[0].trim();
			}
			stanfordChineseParser = new ChineseParser(parserFile);
			
			while (true) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error(e.getMessage());
				}
				
				LOGGER.info("Waiting for connection...");
	            final Socket sock = server.accept();
	            LOGGER.info("Connected: " + sock);
	            
	            while (true) {
					try {
						Thread.sleep(1);
						
						// 1.Receive message from client
						if (sock.isClosed()) break;
						InputStream is = sock.getInputStream();
						byte[] buf = new byte[128];
    		            int read = is.read(buf);
    		            if (read == -1) {
    		            	LOGGER.info("read: " + read);
    		            	break;
    		            }
    		            String msgFromClient = new String(buf, 0, read);
    		            msgFromClient = msgFromClient.replaceAll("(\\r\\n|\\n)", "");
    		            msgFromClient = msgFromClient.trim();
    		            LOGGER.info("msgFromClient: >>>" + msgFromClient + "<<<");
    		            
    		            // 2.Parse the message and send to client
    		            OutputStream os = sock.getOutputStream();
    		            String msgToClient = "EMPTY_STR";
    		            int msgFromClientLength = msgFromClient.length();
    		            if (msgFromClientLength > 0 && msgFromClientLength <= MAX_LENGTH) {
//        		            msgToClient = "一起/AD 去/VV 旅行/NN";
        		            msgToClient = stanfordChineseParser.beginParse(msgFromClient);
        		        } else {
        		        	msgToClient = "Error: Invalid Length! msgFromClientLength = " + msgFromClientLength;
    		            }
    		            os.write(msgToClient.getBytes());
    		            os.flush();
    		            LOGGER.info("msgToClient: >>>" + msgToClient + "<<<");
    		            
    		            // After processing the message from Client, close OutputStream, InputStream, Socket.
    		            os.close();
    		            is.close();
    		            sock.close();
    		            LOGGER.info("The socket client is closed. sock:" + sock);
					} catch (Exception e) {
						e.printStackTrace();
						LOGGER.error(e.getMessage());
					}	    					
				}
				// sock.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("MyAFUNIXServer End!");
	}

}
