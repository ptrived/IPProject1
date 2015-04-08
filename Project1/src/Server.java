import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
	String sysName = "P2P-CI/1.0";
	ServerSocket myService = null ;
	Map<String, Integer> activePeers;
	Map<Integer, RFCData> peerMap;
	public Server(){
		activePeers = new HashMap<String, Integer>();
		peerMap = new HashMap<Integer, RFCData>();
		try {
			myService = new ServerSocket(portNum);
		} catch (SocketException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

	public void run(){
		try {
			System.out.println("P2P-CI/1.0 system is up");
			Socket serverSocket = myService.accept();
			while(true){
				BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
				//DataInputStream in = new DataInputStream(serverSocket.getInputStream());
				OutputStream outToServer = serverSocket.getOutputStream();
				DataOutputStream out  = new DataOutputStream(outToServer);


				String command[] = in.readLine().split(" ");
				Command cmd = Command.WRONG;
				if(command[0].equalsIgnoreCase("add"))
					cmd =  Command.ADD;
				if(command[0].equalsIgnoreCase("list all"))
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
					hostname = in.readLine();
					portNumber = Integer.parseInt(in.readLine());
					activePeers.put(hostname, portNumber);
					RFCNum = Integer.parseInt(command[2]);
					title = in.readLine();

					if(peerMap.containsKey(RFCNum)){
						data = peerMap.get(RFCNum);
						data.peers.add(hostname);
					}else{
						data = new RFCData();
						data.peers = new ArrayList<String>();
						data.peers.add(hostname);
						data.title = title;
						peerMap.put(RFCNum, data);
					}
					// System.out.println("SERVER :: After add : count = " + peerMap.size());
					// sending output to client
					int statusNum=200;				
					String output = sysName+" "+statusNum+" "+Status.statusMap.get(statusNum)+"\n";
					//output.concat(" "+statusNum+" "+Status.statusMap.get(statusNum)+"\n");
					System.out.println(output);
					out.writeBytes(output);
					out.writeBytes("RFC " + RFCNum + " " + title + " " + hostname + " " + portNumber+"\n");

					break;
				case LOOKUP:
					hostname = in.readLine();
					portNumber = Integer.parseInt(in.readLine());
					RFCNum = Integer.parseInt(command[2]);
					title = in.readLine();

					if(peerMap.containsKey(RFCNum)){
						data = peerMap.get(RFCNum);
						List<String> peerlist = data.peers;
						System.out.println("SERVER :: sending count = " + peerlist.size());
						out.writeBytes(peerlist.size()+ "\n");
						for(String peer : peerlist){
							portNumber = activePeers.get(peer);
							out.writeBytes("RFC " + RFCNum + " " + title + " " + peer + " " + portNumber + "\n");
						}
					}else{
						out.writeBytes("0"+ "\n");
					}
					break;
				case LIST:
					Iterator<Map.Entry<Integer, RFCData>> it = peerMap.entrySet().iterator();
					out.writeBytes(peerMap.size()+ "\n");
					while(it.hasNext()){
						Map.Entry<Integer, RFCData> entry = it.next();
						RFCNum = entry.getKey();
						data = entry.getValue();
						List<String> peerlist = data.peers;
						out.writeBytes(peerlist.size()+ "\n");
						for(String peer : peerlist){
							portNumber = activePeers.get(peer);
							out.writeBytes("RFC " + RFCNum + " " + data.title + " " + peer + " " + portNumber + "\n");
						}
					}
					break;
					//TODO :: case close:
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{

		}
	}

	public static void main(String args[]){


		new Server().start();

	}
}
