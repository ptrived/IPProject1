import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Map;
import java.util.Set;
import java.util.List;
class RFCData{
	String title;
	List<String> peers;
}
public class Server extends Thread{
	static final int portNum = 7734;
	ServerSocket myService = null ;
	Set<String> activePeers;
	Map<Integer, RFCData> peerMap;
	public Server(){
		
		try {
			myService = new ServerSocket(portNum);
			myService.setSoTimeout(10000);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		while(true){
			try {
				Socket serverSocket = myService.accept();
				//serverSocket.getRemoteSocketAddress()
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]){
		

		new Server().start();
		
	}
}
