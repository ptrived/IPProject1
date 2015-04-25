import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class Server{
	static final int portNum = 7734;
	private static ServerSocket myService = null ;
	private  Map<String, Integer> activePeers;	
	private  Map<Integer, RFCData> peerMap;
	private static Server server;

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
			System.out.println(Status.sysName+ " system is up");
			while(true){
				Socket serverSocket = myService.accept();
				P2SCommunication clientThread = new P2SCommunication(serverSocket);
				Thread t = new Thread(clientThread);
				t.start();
			}
		} catch (SocketException e) {
			System.out.println("Encountered Socket Exception " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Encountered IO Exception " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Encountered Exception " + e.getMessage());
		}
	}
}
