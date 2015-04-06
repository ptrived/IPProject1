import java.net.Socket;


public class Client extends Thread{
	Socket clientSocket = null;
	static final int portNum = 7734;
	
	public static void main(String[] args) {
		try{
			Socket client = new Socket("localhost", portNum);
			//send data
			client.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
