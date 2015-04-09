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
	String sysName = "P2P-CI/1.0";
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


				//while((request = (NormalRequest) in.readObject())!=null){

				request = (P2SRequest) in.readObject();

				String command[] = request.command.split(" ");
				switch(request.cmd){
				case WRONG:
					break;
				case ADD:
					Server.activePeers.put(request.hostname, request.portNum);
					RFCNum = Integer.parseInt(command[2]);

					if(Server.peerMap.containsKey(RFCNum)){
						data = Server.peerMap.get(RFCNum);
						data.peers.add(request.hostname);
					}else{
						data = new RFCData();
						data.peers = new ArrayList<String>();
						data.peers.add(request.hostname);
						data.title = request.title;
						Server.peerMap.put(RFCNum, data);
					}

					response.setStatusCode(200);
					response.setVersion(sysName);
					response.setPhrase(Status.statusMap.get(200));
					outputList.add("RFC " + RFCNum + " " + request.title + " " + request.hostname + " " + request.portNum);
					response.setResponseList(outputList);
					out.writeObject(response);
					break;
				case LOOKUP:
					RFCNum = Integer.parseInt(command[2]);
					if(Server.peerMap.containsKey(RFCNum)){
						data = Server.peerMap.get(RFCNum);
						List<String> peerlist = data.peers;				
						for(String peer : peerlist){
							portNumber = Server.activePeers.get(peer);
							outputList.add("RFC " + RFCNum + " " + request.title + " " + peer + " " + portNumber);
						}
						response.setStatusCode(200);
						response.setVersion(sysName);
						response.setPhrase(Status.statusMap.get(200));
						response.setResponseList(outputList);
					}else{
						response.setStatusCode(404);
						response.setVersion(sysName);
						response.setPhrase(Status.statusMap.get(404));
						response.setResponseList(outputList);
					}
					out.writeObject(response);
					break;
				case LIST:
					Iterator<Map.Entry<Integer, RFCData>> it = Server.peerMap.entrySet().iterator();
					if(Server.peerMap.size()==0){
						response.setStatusCode(404);
						response.setVersion(sysName);
						response.setPhrase(Status.statusMap.get(404));
						response.setResponseList(outputList);
					}else{
						while(it.hasNext()){
							Map.Entry<Integer, RFCData> entry = it.next();
							RFCNum = entry.getKey();
							data = entry.getValue();
							List<String> peerlist = data.peers;

							for(String peer : peerlist){
								portNumber = Server.activePeers.get(peer);
								outputList.add("RFC " + RFCNum + " " + request.title + " " + peer + " " + portNumber);
							}
						}
						response.setStatusCode(200);
						response.setVersion(sysName);
						response.setPhrase(Status.statusMap.get(200));
						response.setResponseList(outputList);
					}
					out.writeObject(response);
					break;
					//TODO :: case close:
				}
			}

		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		catch (IOException e) {
			//e.printStackTrace();
		}finally{
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}

}
