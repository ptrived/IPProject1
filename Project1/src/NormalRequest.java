import java.io.Serializable;


public class NormalRequest implements Serializable{
	String command;
	String hostname;
	int portNum;
	String title;
	
	public NormalRequest(String command, String hostname, int port, String title){
		this.command = command;
		this.hostname = hostname;
		this.portNum = port;
		this.title = title;
	}
}	
