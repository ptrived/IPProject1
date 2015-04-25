import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class P2SCommunication implements Runnable{
	Socket socket = null;
	public P2SCommunication(Socket socket){
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			System.out.println("Client Connected");
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			while(true){
				int portNumber, RFCNum;
				RFCData data;
				P2SRequest request;
				P2SResponse response = new P2SResponse();
				List<String> outputList = new ArrayList<String>();
				request = (P2SRequest) in.readObject();
				String command[] = request.command.split(" ");
				switch(request.cmd){
				case WRONG:
					break;
				case ADD:
					Server.getInstance().getActivePeers().put(getHostIp(this.socket.getRemoteSocketAddress().toString()), request.portNum);
					RFCNum = Integer.parseInt(command[2]);
					if(Server.getInstance().getPeerMap().containsKey(RFCNum)){
						data = Server.getInstance().getPeerMap().get(RFCNum);
						data.peers.add(getHostIp(this.socket.getRemoteSocketAddress().toString()));
					}else{
						data = new RFCData();
						data.peers = new ArrayList<String>();
						data.peers.add(getHostIp(this.socket.getRemoteSocketAddress().toString()));
						data.title = request.title;
						Server.getInstance().getPeerMap().put(RFCNum, data);
					}
					response.setStatusCode(200);
					response.setVersion(Status.sysName);
					response.setPhrase(Status.statusMap.get(200));
					outputList.add("RFC " + RFCNum + " " + request.title + " " + getHostIp(this.socket.getRemoteSocketAddress().toString()) + " " + request.portNum);
					response.setResponseList(outputList);
					out.writeObject(response);
					break;
				case LOOKUP:
					RFCNum = Integer.parseInt(command[2]);
					if(Server.getInstance().getPeerMap().containsKey(RFCNum)){
						data = Server.getInstance().getPeerMap().get(RFCNum);
						List<String> peerlist = data.peers;				
						for(String peer : peerlist){
							portNumber = Server.getInstance().getActivePeers().get(peer);
							outputList.add("RFC " + RFCNum + " " + request.title + " " + peer + " " + portNumber);
						}
						response.setStatusCode(200);
						response.setVersion(Status.sysName);
						response.setPhrase(Status.statusMap.get(200));
						response.setResponseList(outputList);
					}else{
						response.setStatusCode(404);
						response.setVersion(Status.sysName);
						response.setPhrase(Status.statusMap.get(404));
						response.setResponseList(outputList);
					}
					out.writeObject(response);
					break;
				case LIST:
					Iterator<Map.Entry<Integer, RFCData>> it = Server.getInstance().getPeerMap().entrySet().iterator();
					if(Server.getInstance().getPeerMap().size()==0){
						response.setStatusCode(404);
						response.setVersion(Status.sysName);
						response.setPhrase(Status.statusMap.get(404));
						response.setResponseList(outputList);
					}else{
						while(it.hasNext()){
							Map.Entry<Integer, RFCData> entry = it.next();
							RFCNum = entry.getKey();
							data = entry.getValue();
							List<String> peerlist = data.peers;

							for(String peer : peerlist){
								portNumber = Server.getInstance().getActivePeers().get(peer);
								outputList.add("RFC " + RFCNum + " " + request.title + " " + peer + " " + portNumber);
							}
						}
						response.setStatusCode(200);
						response.setVersion(Status.sysName);
						response.setPhrase(Status.statusMap.get(200));
						response.setResponseList(outputList);
					}
					out.writeObject(response);
					break;
				default:
					break;
				}
			}

		} catch (ClassNotFoundException e) {
			System.out.println("Encountered Exception " + e.getMessage());
		}
		catch (IOException e) {
			System.out.println("Encountered IO Exception " + e.getMessage());
		}finally{
			try {
				String hostName = getHostIp(this.socket.getRemoteSocketAddress().toString());
				Server.getInstance().getActivePeers().remove(hostName);
				Map<Integer, RFCData> map = Server.getInstance().getPeerMap();
				Iterator<Integer> it = map.keySet().iterator();
				while(it.hasNext()){
					RFCData rfc = map.get(it.next());
					List<String> list = rfc.peers;
					if(list.contains(hostName)){
						list.remove(hostName);
					}
				}
				socket.close();
			} catch (IOException e) {
				System.out.println("Encountered IO Exception " + e.getMessage());
			}
		}
	}

	private String getHostIp(String string) {
		String [] arr = string.split(":");
		String rs = arr[0];
		rs = rs.substring(1);
		return rs;
	}

}
