import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class P2PCommunication implements Runnable{
	ServerSocket clientService;
	Socket clientSocket;	
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

				//File Transfer
				String[] strArr = p2pReq.command.split(" ");
				String fileName = strArr[2];
				String filePath = "F:\\rfc\\"+fileName+".txt";
				byte[] b_arr = new byte[(int) filePath.length()];
				BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(filePath));
				inStream.read(b_arr, 0, b_arr.length);
				OutputStream outStream = clientSocket.getOutputStream();
				outStream.write(b_arr, 0, b_arr.length);
				outStream.flush();
				p2pResp.setVersion(Status.sysName);
				p2pResp.setDate(new Date());
				p2pResp.setOS(p2pReq.getOS());
				p2pResp.setContentLength(0);
				p2pResp.setStatusCode(200);
				out.writeObject(p2pResp);
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

}
