import java.io.Serializable;

@SuppressWarnings("serial")
public class P2SRequest implements Serializable{
	public static enum Command {
		  ADD, LIST, LOOKUP, GET, WRONG, BAD
	}
	String command;	
	int portNum;
	String title;
	Command cmd;

	public P2SRequest(String command, int port, String title){
		this.command = command;		
		this.portNum = port;
		this.title = title;
		this.cmd = parseCommand(this.command);
	}

	public static Command parseCommand(String command){
		String[] cmdArray = command.split(" ");	
		int size = cmdArray.length;
		if(size<3){
			return Command.BAD;
		}
		if(!cmdArray[size-1].trim().equals(Status.sysName)){
			return Command.WRONG;
		}
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
