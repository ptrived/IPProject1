import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;



public class Client extends Thread{
	Socket clientSocket = null;
	static final int portNum = 7734;

	public static Command parseCommand(String command){
		String[] cmdArray = command.split(" ");
		int size = cmdArray.length;
		if(size<3){
			return Command.WRONG;
		}
		//		if(cmdArray[size-1]!="P2P-CI/1.0"){
		//			return Command.WRONG;
		//		}
		if(cmdArray[0].equalsIgnoreCase("add"))
			return Command.ADD;
		if(cmdArray[0].equalsIgnoreCase("list"))
			return Command.LIST;
		if(cmdArray[0].equalsIgnoreCase("lookup"))
			return Command.LOOKUP;
		return Command.WRONG;
	}

	public static void main(String[] args) {
		Socket client;
		try{
			client = new Socket("localhost", portNum);

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out  = new DataOutputStream(outToServer);
			//DataInputStream in = new DataInputStream(client.getInputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

			while(true){
				System.out.println("Enter the command:");
				String command = br.readLine();
				Command cmd = parseCommand(command);
				String hostname, title, portNum;
				switch(cmd){
				case WRONG:
					continue;
				case ADD:
					out.writeBytes(command+"\n");
					System.out.print("Host : ");
					hostname = br.readLine();
					out.writeBytes(hostname+"\n");
					System.out.print("Port : ");
					portNum = br.readLine();
					out.writeBytes(portNum+"\n");
					System.out.print("Title : ");
					title = br.readLine();
					out.writeBytes(title+"\n");
					System.out.println(in.readLine());
					System.out.println(in.readLine());
					break;
				case LOOKUP:
					out.writeBytes(command + "\n");
					System.out.print("Host : ");
					hostname = br.readLine();
					out.writeBytes(hostname + "\n");
					System.out.print("Port : ");
					portNum = br.readLine();
					out.writeBytes(portNum + "\n");
					System.out.print("Title : ");
					title = br.readLine();
					out.writeBytes(title + "\n");
					//read and print output from server
					int count = Integer.parseInt(in.readLine());
					System.out.println(in.readLine());
					//System.out.println("Client: count received = " + count);
					if(count == 0){
						System.out.println("Sorry currently none of the peers contain the requested RFC");
					}else{
						for(int i = 0; i < count; i++){
							System.out.println(in.readLine());
						}
					}
					break;
				case LIST:
					out.writeBytes(command + "\n");
					System.out.print("Host : ");
					hostname = br.readLine();
					out.writeBytes(hostname + "\n");
					System.out.print("Port : ");
					portNum = br.readLine();
					out.writeBytes(portNum + "\n");
					//read and print output from server
					int listSize , mapSize = Integer.parseInt(in.readLine());
					for(int i = 0; i < mapSize; i++){
						listSize = Integer.parseInt(in.readLine());
						System.out.println(in.readLine());
						for(int j = 0; j < listSize; j++){
							System.out.println(in.readLine());
						}
					}
					break;

				}				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
