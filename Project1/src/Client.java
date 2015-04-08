import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



public class Client extends Thread{
	static Socket client = null;
	static final int portNum = 7734;
	
	public static void main(String[] args) {
		//Socket client;
		System.out.println("Client Started");
		try{
			client = new Socket("localhost", portNum);
			System.out.println("Client Connected");
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String hostname = null, title;
			int portNum = 0;
			System.out.println("Streams created");
			while(true){
				NormalRequest request;
				NormalResponse response;
				System.out.println("Enter the command:");
				String command = br.readLine();
				Command cmd = NormalRequest.parseCommand(command);		
				
				if(cmd != Command.WRONG){
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
					request = new NormalRequest(command, hostname, portNum, title);
					out.writeObject(request);
					//TODO :: response
					break;
				case LOOKUP:
					System.out.print("Title : ");
					title = br.readLine();
					request = new NormalRequest(command, hostname, portNum, title);
					out.writeObject(request);
					//TODO :: response
					break;
				case LIST:
					request = new NormalRequest(command, hostname, portNum, "");
					out.writeObject(request);
					//TODO :: response
					break;

				}	
				response = (NormalResponse) in.readObject();
				System.out.println(response.version+" "+response.statusCode+" "+response.phrase);
				int listSize = response.responseList.size();
				if(listSize > 0 && response.statusCode==200){
					for (String str : response.responseList) {
						System.out.println(str);
					}					
				}
			
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
