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
			String command = br.readLine();
			Command cmd = parseCommand(command);
			switch(cmd){
			case ADD:
				break;
				
			}
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF(command);
			
			client.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
