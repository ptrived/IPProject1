import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
			try {
				clientSocket = clientService.accept();			 
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
				P2PRequest p2pReq = (P2PRequest) in.readObject();			

				//File Transfer
				String[] strArr = p2pReq.command.split(" ");
				String fileName = strArr[2];
				String filePath = System.getProperty("user.dir") + System.getProperty("file.separator")+"Upload"+System.getProperty("file.separator") + fileName + ".txt";
				//String filePath = "\\Upload\\"+fileName+".txt";
				File file = new File(filePath);
				//BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(filePath));
				/*byte[] b_arr = new byte[(int) file.length()];
				BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(filePath));
				inStream.read(b_arr, 0, b_arr.length);
				OutputStream outStream = clientSocket.getOutputStream();
				outStream.write(b_arr, 0, b_arr.length);
				outStream.flush();*/
				p2pResp.setVersion(Status.sysName);				
				p2pResp.setOS(System.getProperty("os.name"));
				p2pResp.setContentLength(file.length());
				SimpleDateFormat date_format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");				
				p2pResp.setDate(date_format.format(new Date()));
				p2pResp.setLastModified(date_format.format(file.lastModified()));
				p2pResp.setContentType("text/plain");
				p2pResp.setData(getContent(file));
				p2pResp.setStatusCode(200);
				out.writeObject(p2pResp);
				//inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
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

