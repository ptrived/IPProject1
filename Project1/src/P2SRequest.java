import java.io.Serializable;


public class P2SRequest implements Serializable{
	String command;
	String hostname;
	int portNum;
	String title;
	Command cmd;
	
	public P2SRequest(String command, String hostname, int port, String title){
		this.command = command;
		this.hostname = hostname;
		this.portNum = port;
		this.title = title;
		this.cmd = parseCommand(this.command);
	}
	
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
		if(cmdArray[0].equalsIgnoreCase("get"))
			return Command.GET;
		return Command.WRONG;
	}
}	
