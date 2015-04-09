import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;



public class Client {
	static Socket client = null;
	static final int portNum = 7734;
	static int clientPortNum;
	static ServerSocket clientService = null ;
	static{
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter the upload port number : ");
			clientPortNum = Integer.parseInt(br.readLine());
			clientService = new ServerSocket(clientPortNum);
			P2PCommunication comm = new P2PCommunication(clientService);
			Thread t = new Thread(comm);
			t.start();
		} catch (IOException e) {
			System.out.println("exception while creating clientSocket " + e);
			System.exit(0);
		}
	}

	public static void main(String[] args) {		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter Server address");

		try{
			String serverAddr = br.readLine();	
			client = new Socket(serverAddr, portNum);
			System.out.println("Connected to server");
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			String hostname = null, title;
			int portNum = 0;
			System.out.println("Streams created");
			while(true){
				P2SRequest request;
				P2SResponse response;
				System.out.println("Enter the command:");
				String command = br.readLine();
				Command cmd = P2SRequest.parseCommand(command);		

				if(cmd != Command.WRONG && cmd != Command.GET){
					System.out.print("Host : ");
					hostname = br.readLine();
					System.out.print("Port : ");
					portNum = Integer.parseInt(br.readLine());
				}

				switch(cmd){
				case WRONG:
					continue;

				case ADD:
					System.out.print("Title : ");
					title = br.readLine();
					request = new P2SRequest(command, hostname, portNum, title);
					out.writeObject(request);
					response = (P2SResponse) in.readObject();
					printP2SResponse(response);
					break;

				case LOOKUP:
					System.out.print("Title : ");
					title = br.readLine();
					request = new P2SRequest(command, hostname, portNum, title);
					out.writeObject(request);
					response = (P2SResponse) in.readObject();
					printP2SResponse(response);
					break;

				case LIST:
					request = new P2SRequest(command, hostname, portNum, "");
					out.writeObject(request);
					response = (P2SResponse) in.readObject();
					printP2SResponse(response);
					break;

				case GET:
					System.out.println("Host : ");
					hostname = br.readLine();
					System.out.print("OS : ");
					String OS  = br.readLine();
					P2PRequest p2pReq = new P2PRequest();
					p2pReq.setCommand(command);
					p2pReq.setCommand(hostname);
					p2pReq.setOS(OS);
					if(Server.getActivePeers().containsKey(hostname)){
						portNum = Server.activePeers.get(hostname);
						Socket peerSocket = new Socket(hostname, portNum);
						System.out.println("Peer Connected");
						ObjectInputStream inPeer = new ObjectInputStream(peerSocket.getInputStream());
						ObjectOutputStream outPeer = new ObjectOutputStream(peerSocket.getOutputStream());
						outPeer.writeObject(p2pReq);
						P2PResponse p2pResp = (P2PResponse) inPeer.readObject();
						printP2PResponse(p2pResp);
						peerSocket.close();
					}
					else{
						System.out.println("Bad Request");
					}
					break;
				}	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private static void printP2PResponse(P2PResponse p2pResp) {
		System.out.println(p2pResp.version+" "+p2pResp.statusCode+" "+Status.statusMap.get(p2pResp.statusCode));
		System.out.println(p2pResp.getDate());
		System.out.println(p2pResp.getOS());
		System.out.println(p2pResp.getLastModified());
		System.out.println(p2pResp.getContentLength());
		System.out.println(p2pResp.getContentType());


	}
	public static void printP2SResponse(P2SResponse response){
		System.out.println(response.version+" "+response.statusCode+" "+response.phrase);
		int listSize = response.responseList.size();
		if(listSize > 0 && response.statusCode==200){
			for (String str : response.responseList) {
				System.out.println(str);
			}					
		}
	}
}
