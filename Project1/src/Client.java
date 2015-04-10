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
				switch(cmd){
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
					System.out.println("Host : ");
					hostname = br.readLine();
					System.out.print("OS : ");
					String OS  = br.readLine();
					System.out.print("Port : ");
					portNum  = Integer.parseInt(br.readLine());
					P2PRequest p2pReq = new P2PRequest();
					p2pReq.setCommand(command);
					p2pReq.setHost(hostname);
					p2pReq.setOS(OS);
					try{
						Socket peerSocket = new Socket(hostname, portNum);
						System.out.println("Peer Connected"); 
						ObjectInputStream inPeer = new ObjectInputStream(peerSocket.getInputStream());
						ObjectOutputStream outPeer = new ObjectOutputStream(peerSocket.getOutputStream());
						outPeer.writeObject(p2pReq);
						//File Transfer
						String[] strArr = p2pReq.command.split(" ");
						String fileName = strArr[2];
						String filePath = "F:\\rfc\\"+fileName+".txt";
						byte[] b_arr = new byte[1024];
						InputStream inStream = peerSocket.getInputStream();
						FileOutputStream f = new FileOutputStream(filePath);
						BufferedOutputStream outStream = new BufferedOutputStream(f);
						int bytesRead = inStream.read(b_arr, 0, b_arr.length);
						outStream.write(b_arr, 0, bytesRead);
						outStream.close();
						P2PResponse p2pResp = (P2PResponse) inPeer.readObject();
						printP2PResponse(p2pResp);
						peerSocket.close();
					}
					catch(Exception e){
						e.printStackTrace();
					}
					break;
				}	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			//			try {
			//				client.close();
			//			} catch (IOException e) {
			//				e.printStackTrace();
			//			}
		}
	}
	private static void printP2PResponse(P2PResponse p2pResp) {
		System.out.println(p2pResp.version+" "+p2pResp.statusCode+" "+Status.statusMap.get(p2pResp.statusCode));
		System.out.println("Date : "+p2pResp.getDate());
		System.out.println("OS : "+p2pResp.getOS());
		System.out.println("Last-Modified : "+p2pResp.getLastModified());
		System.out.println("Content-Length : "+p2pResp.getContentLength());
		System.out.println("Content-Type : "+p2pResp.getContentType());
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
