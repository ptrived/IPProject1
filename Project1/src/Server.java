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
	public static Map<String, Integer> getActivePeers() {
		return activePeers;
	}
	public static Map<Integer, RFCData> peerMap;
	
	public static Map<Integer, RFCData> getPeerMap() {
		return peerMap;
	}
	static{
		activePeers = new HashMap<String, Integer>();
		peerMap = new HashMap<Integer, RFCData>();
		
	}
	public static void main(String args[]){
		try {
			myService = new ServerSocket(portNum);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("P2P-CI/1.0 system is up");
		try {
			while(true){
				Socket serverSocket = myService.accept();
				P2SCommunication clientThread = new P2SCommunication(serverSocket);
				Thread t = new Thread(clientThread);
				t.start();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
