import java.io.Serializable;


@SuppressWarnings("serial")
public class P2PRequest implements Serializable{
	String command;
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getOS() {
		return OS;
	}
	public void setOS(String oS) {
		OS = oS;
	}
	String host;
	String OS;
}
