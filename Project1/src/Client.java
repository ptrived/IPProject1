import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
	final static int RFC_SIZE = 100000;
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
			while(true){
				P2SRequest request;
				P2SResponse response;
				System.out.println("Menu:");
				System.out.println("(i)\t ADD RFC <RFC Num> P2P-CI/1.0");
				System.out.println("(ii)\t LOOKUP RFC <RFC Num> P2P-CI/1.0");
				System.out.println("(iii)\t LIST ALL P2P-CI/1.0");
				System.out.println("(iv)\t GET RFC <RFC Num> P2P-CI/1.0");
				System.out.println("(v)\t END");
				System.out.println("Enter the command in the above mentioned format:");
				String command = br.readLine();
				if(command.trim().equalsIgnoreCase("end")){
					System.out.println("Closing Client");
					client.close();
					System.exit(1);
				}
				P2SRequest.Command cmd = P2SRequest.parseCommand(command);		
				switch(cmd){
				case END:
					client.close();
					System.exit(1);
				case BAD:
					response = new P2SResponse();
					response.setVersion(Status.sysName);
					response.setStatusCode(400);
					response.setPhrase(Status.statusMap.get(400));
					printP2SResponse(response);
					break;
				case WRONG:
					response = new P2SResponse();
					response.setVersion(Status.sysName);
					response.setStatusCode(505);
					response.setPhrase(Status.statusMap.get(505));
					printP2SResponse(response);
					continue;
				case ADD:
					System.out.print("Title : ");
					title = br.readLine();
					request = new P2SRequest(command, clientPortNum, title);
					out.writeObject(request);
					response = (P2SResponse) in.readObject();
					printP2SResponse(response);
					break;
				case LOOKUP:
					System.out.print("Title : ");
					title = br.readLine();
					request = new P2SRequest(command, clientPortNum, title);
					out.writeObject(request);
					response = (P2SResponse) in.readObject();
					printP2SResponse(response);
					break;
				case LIST:
					request = new P2SRequest(command, clientPortNum, "");
					out.writeObject(request);
					response = (P2SResponse) in.readObject();
					printP2SResponse(response);
					break;
				case GET:
					try{
					System.out.println("Host : ");
					hostname = br.readLine();
					//System.out.print("OS : ");
					//String OS  = br.readLine();
					String OS = System.getProperty("os.name");
					System.out.print("Port : ");
					portNum  = Integer.parseInt(br.readLine());
					P2PRequest p2pReq = new P2PRequest();
					p2pReq.setCommand(command);
					p2pReq.setHost(hostname);
					p2pReq.setOS(OS);
					
						Socket peerSocket = new Socket(hostname, portNum);
						System.out.println("Peer Connected"); 
						InputStream inStream = peerSocket.getInputStream();
						ObjectInputStream inPeer = new ObjectInputStream(inStream);
						ObjectOutputStream outPeer = new ObjectOutputStream(peerSocket.getOutputStream());
						outPeer.writeObject(p2pReq);
						//File Transfer
						String[] strArr = p2pReq.command.split(" ");
						String fileName = strArr[2];
	
						String filePath = fileName + ".txt";
						FileOutputStream f = new FileOutputStream(filePath);
						BufferedOutputStream outStream = new BufferedOutputStream(f);
						
						P2PResponse p2pResp = (P2PResponse) inPeer.readObject();
						printP2PResponse(p2pResp);
						outStream.write(p2pResp.getData().getBytes());
						outStream.close();
						peerSocket.close();
					}
					catch(Exception e){
						System.out.println("Exception occurred during File transfer:" + e.getMessage());
					}
					
					break;
				}	
			}
		}catch(IOException e){
			System.out.println("Closing the socket due to " + e.getMessage());
			try {
				client.close();
				System.exit(0);
			} catch (Exception e1) {
				System.exit(0);
			}
		}catch(Exception e){
			System.out.println("Exception occurred while executing the commands:" + e.getMessage());
		}
		
	}
	private static void printP2PResponse(P2PResponse p2pResp) {
		System.out.println(p2pResp.version+" "+p2pResp.statusCode+" "+Status.statusMap.get(p2pResp.statusCode));
		System.out.println("Date : "+p2pResp.getDate());
		System.out.println("OS : "+p2pResp.getOS());
		System.out.println("Last-Modified : "+p2pResp.getLastModified());
		System.out.println("Content-Length : "+p2pResp.getContentLength());
		System.out.println("Content-Type : "+p2pResp.getContentType());
		System.out.println(p2pResp.getData());
	}
	private static void printP2SResponse(P2SResponse response){
		int listSize = response.responseList.size();
		if(listSize <0 && response.statusCode!=200){
			response.setStatusCode(404);
			response.setPhrase(Status.statusMap.get(404));
		}
		System.out.println(response.version+" "+response.statusCode+" "+response.phrase);		
		if(listSize > 0 && response.statusCode==200){
			for (String str : response.responseList) {
				System.out.println(str);
			}					
		}
	}
}
