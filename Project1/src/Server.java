import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class RFCData{
	String title;
	List<String> peers;
}
public class Server{
	static final int portNum = 7734;
	String sysName = "P2P-CI/1.0";
	public static ServerSocket myService = null ;
	public static Map<String, Integer> activePeers;
	public static Map<Integer, RFCData> peerMap;
	
	static{
		activePeers = new HashMap<String, Integer>();
		peerMap = new HashMap<Integer, RFCData>();
		try {
			myService = new ServerSocket(portNum);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		System.out.println("P2P-CI/1.0 system is up");
		try {
			while(true){
				Socket serverSocket = myService.accept();
				ClientCommunication clientThread = new ClientCommunication(serverSocket);
				Thread t = new Thread(clientThread);
				t.start();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
