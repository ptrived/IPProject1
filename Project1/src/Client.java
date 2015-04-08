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
			DataInputStream in = new DataInputStream(client.getInputStream());
			
			while(true){
				System.out.println("Enter the command:");
				String command = br.readLine();
				Command cmd = Command.WRONG;
				String[] cmdArray = command.split(" ");

				if(cmdArray[0].equalsIgnoreCase("add"))
					cmd =  Command.ADD;
				if(cmdArray[0].equalsIgnoreCase("list"))
					cmd = Command.LIST;
				if(cmdArray[0].equalsIgnoreCase("lookup"))
					cmd = Command.LOOKUP;
				String hostname, title;
				int portNum;
				
				switch(cmd){
				case WRONG:
					continue;
				case ADD:
					out.writeUTF(command);
					System.out.print("Host : ");
					hostname = br.readLine();
					System.out.println();
					out.writeUTF(hostname);
					System.out.print("Port : ");
					portNum = Integer.parseInt(br.readLine());
					System.out.println();
					out.writeInt(portNum);
					System.out.print("Title : ");
					title = br.readLine();
					System.out.println();
					out.writeUTF(title);
					System.out.println(in.readUTF());
					break;
				case LIST:
					out.writeUTF(command);
					System.out.print("Host : ");
					hostname = br.readLine();
					System.out.println();
					out.writeUTF(hostname);
					System.out.print("Port : ");
					portNum = Integer.parseInt(br.readLine());
					System.out.println();
					out.writeInt(portNum);
					break;
				case LOOKUP:
					out.writeUTF(command);
					System.out.print("Host : ");
					hostname = br.readLine();
					System.out.println();
					out.writeUTF(hostname);
					System.out.print("Port : ");
					portNum = Integer.parseInt(br.readLine());
					System.out.println();
					out.writeInt(portNum);
					System.out.print("Title : ");
					title = br.readLine();
					System.out.println();
					out.writeUTF(title);
					break;
				}
				
				//TODO :: server response part 
				//client.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
