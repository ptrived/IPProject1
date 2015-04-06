import java.io.BufferedReader;
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
		try{
			Socket client = new Socket("localhost", portNum);
			System.out.println("Enter the command:");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out  = new DataOutputStream(outToServer);
			while(true){
				String command = br.readLine();
				Command cmd = Command.WRONG;
				String[] cmdArray = command.split(" ");

				if(cmdArray[0].equalsIgnoreCase("add"))
					cmd =  Command.ADD;
				if(cmdArray[0].equalsIgnoreCase("list"))
					cmd = Command.LIST;
				if(cmdArray[0].equalsIgnoreCase("lookup"))
					cmd = Command.LOOKUP;
				if(cmd == Command.WRONG){
					continue;
				}else{
					out.writeUTF(command);
					System.out.print("Host : ");
					String hostname = br.readLine();
					System.out.println();
					out.writeUTF("Host:"+ hostname);
					System.out.print("Port : ");
					int portNum = Integer.parseInt(br.readLine());
					System.out.println();
					out.writeUTF("Port:"+ portNum);
					if(cmd == Command.ADD || cmd == Command.LOOKUP){
						System.out.print("Title : ");
						String title = br.readLine();
						System.out.println();
						out.writeUTF("Title:"+ title);
					}
				}
				//TODO :: server response part 
				client.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
