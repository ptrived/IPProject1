import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


class RFCData{
	String title;
	List<String> peers;
}
public class Server extends Thread{
	static final int portNum = 7734;
	ServerSocket myService = null ;
	Map<String, Integer> activePeers;
	Map<Integer, RFCData> peerMap;
	public Server(){
		activePeers = new HashMap<String, Integer>();
		peerMap = new HashMap<Integer, RFCData>();
		try {
			myService = new ServerSocket(portNum);
			//myService.setSoTimeout(10000);
		} catch (SocketException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

	public void run(){
		while(true){
			try {
				Socket serverSocket = myService.accept();
				DataInputStream in = new DataInputStream(serverSocket.getInputStream());
				OutputStream outToServer = serverSocket.getOutputStream();
				DataOutputStream out  = new DataOutputStream(outToServer);

				String command[] = in.readUTF().split(" ");
				Command cmd = Command.WRONG;
				if(command[0].equalsIgnoreCase("add"))
					cmd =  Command.ADD;
				if(command[0].equalsIgnoreCase("list"))
					cmd = Command.LIST;
				if(command[0].equalsIgnoreCase("lookup"))
					cmd = Command.LOOKUP;

				String hostname, title;
				int portNumber, RFCNum;
				RFCData data;
				switch(cmd){
				case WRONG:
					break;
				case ADD:
					hostname = in.readUTF();
					portNumber = in.readInt();
					activePeers.put(hostname, portNumber);
					RFCNum = Integer.parseInt(command[2]);
					title = in.readUTF();

					if(peerMap.containsKey(RFCNum)){
						data = peerMap.get(RFCNum);
						data.peers.add(hostname);
					}else{
						data = new RFCData();
						data.peers = new ArrayList<String>();
						data.peers.add(hostname);
						data.title = title;
					}
					
					//TODO :: output pending
					out.writeUTF("RFC " + RFCNum + " " + title + " " + hostname + " " + portNumber);
					
					break;
				case LOOKUP:
					hostname = in.readUTF();
					portNumber = Integer.parseInt(in.readUTF());
					RFCNum = Integer.parseInt(command[2]);
					title = in.readUTF();

					if(peerMap.containsKey(RFCNum)){
						data = peerMap.get(RFCNum);
						List<String> peerlist = data.peers;
						for(String peer : peerlist){
							portNumber = activePeers.get(peer);
							out.writeUTF("RFC " + RFCNum + " " + title + " " + peer + " " + portNumber);
						}
					}else{
						out.writeUTF("Sorry! Currently none of the active peers have RFC "+ RFCNum);
					}
					break;
				case LIST:
					Iterator<Map.Entry<Integer, RFCData>> it = peerMap.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry<Integer, RFCData> entry = it.next();
						RFCNum = entry.getKey();
						data = entry.getValue();
						List<String> peerlist = data.peers;
						for(String peer : peerlist){
							portNumber = activePeers.get(peer);
							out.writeUTF("RFC " + RFCNum + " " + data.title + " " + peer + " " + portNumber);
						}
					}
					break;
					//TODO :: case close:
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]){


		new Server().start();

	}
}
