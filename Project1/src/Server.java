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
	private static ServerSocket myService = null ;
	private  Map<String, Integer> activePeers;	
	private  Map<Integer, RFCData> peerMap;
	private static Server server;

	static{
//		server.activePeers = new HashMap<String, Integer>();
//		server.peerMap = new HashMap<Integer, RFCData>();		
	}
	
	public  void setActivePeers(Map<String, Integer> activePeers) {
		synchronized (this.activePeers) {
			this.activePeers = activePeers;
		}		
	}
	
	public  void setPeerMap(Map<Integer, RFCData> peerMap) {
		this.peerMap = peerMap;
	}
	
	public  Map<Integer, RFCData> getPeerMap() {
		return this.peerMap;
	}
	
	public  Map<String, Integer> getActivePeers() {
		synchronized (this.activePeers) {
			return this.activePeers;
		}
	}
	
	private Server(){
	}
	
	public static Server getInstance(){
		if(server == null){
			server = new Server();
			server.activePeers = new HashMap<String, Integer>();
			server.peerMap = new HashMap<Integer, RFCData>();	
		}
			return server;
		
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
			e.printStackTrace();
		}
	}
}
