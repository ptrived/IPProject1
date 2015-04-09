import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class P2PCommunication implements Runnable{
	ServerSocket clientService;
	Socket clientSocket;
	String sysName = "P2P-CI/1.0";
	
	public P2PCommunication(ServerSocket socket){
		this.clientService = socket;
		
	}
	
	@Override
	public void run() {
		while(true){
			try {
				clientSocket = clientService.accept();				
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
				P2PRequest p2pReq = (P2PRequest) in.readObject();
				P2PResponse p2pResp = new P2PResponse();
				p2pResp.setVersion(sysName);
				p2pResp.setDate(new Date());
				p2pResp.setOS(p2pReq.getOS());
				p2pResp.setContentLength(0);
				out.writeObject(p2pResp);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}

}
