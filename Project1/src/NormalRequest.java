import java.io.Serializable;


public class NormalRequest implements Serializable{
	String command;
	String hostname;
	int portNum;
	String title;
	Command cmd;
	
	public NormalRequest(String command, String hostname, int port, String title){
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
		return Command.WRONG;
	}
}	
