import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class P2PCommunication implements Runnable{
	ServerSocket clientService;
	Socket clientSocket;	
	public P2PCommunication(ServerSocket socket){
		this.clientService = socket;
	}

	@Override
	public void run() {
		while(true){
			P2PResponse p2pResp = new P2PResponse();
			ObjectOutputStream out = null;
			try {

				clientSocket = clientService.accept();			 
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
				P2PRequest p2pReq = (P2PRequest) in.readObject();			
				try{
					//File Transfer
					String[] strArr = p2pReq.command.split(" ");
					String fileName = strArr[2];
					//String filePath = System.getProperty("user.dir") + System.getProperty("file.separator")+"Upload"+System.getProperty("file.separator") + fileName + ".txt";
					String filePath = fileName+".txt";

					File file = new File(filePath);
					p2pResp.setVersion(Status.sysName);				
					p2pResp.setOS(System.getProperty("os.name"));
					p2pResp.setContentLength(file.length());
					SimpleDateFormat date_format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");				
					p2pResp.setDate(date_format.format(new Date()));
					p2pResp.setLastModified(date_format.format(file.lastModified()));
					p2pResp.setContentType("text/plain");
					p2pResp.setData(getContent(file));
					p2pResp.setStatusCode(200);
					p2pResp.setPhrase(Status.statusMap.get(200));
					out.writeObject(p2pResp);
				} catch (FileNotFoundException e) {
					System.out.println("File not found, Exception: " + e.getMessage());
					p2pResp.setStatusCode(400);
					p2pResp.setVersion(Status.sysName);
					p2pResp.setPhrase(Status.statusMap.get(400));
					out.writeObject(p2pResp);
				}
			} catch (IOException e) {
				System.out.println("Encountered IO Exception " + e.getMessage());
				System.out.println("File not found, Exception: " + e.getMessage());
				p2pResp.setStatusCode(400);
				p2pResp.setVersion(Status.sysName);
				p2pResp.setPhrase(Status.statusMap.get(400));
				try {
					out.writeObject(p2pResp);
				} catch (IOException e1) {
				}
			} catch (ClassNotFoundException e1) {
				System.out.println("Encountered ClassNotFound Exception " + e1.getMessage());
				p2pResp.setStatusCode(400);
				p2pResp.setVersion(Status.sysName);
				p2pResp.setPhrase(Status.statusMap.get(400));
				try {
					out.writeObject(p2pResp);
				} catch (IOException e) {					
				}
			} 		
		}

	}

	private String getContent(File file) {
		StringBuilder result = new StringBuilder((int)file.length());
		Scanner cin = null;
		try {
			cin = new Scanner(file);
			String lineSeparator = System.getProperty("line.separator");
			while(cin.hasNextLine()) {        
				result.append(cin.nextLine() + lineSeparator);
			}
			return result.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			cin.close();
		}
		return null;
	}
}

