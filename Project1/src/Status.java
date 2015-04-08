import java.util.HashMap;


public class Status {
	public static HashMap<Integer,String> statusMap = new HashMap<Integer,String>();
	static{
		statusMap.put(200,"OK");
		statusMap.put(400,"Bad Request");
		statusMap.put(404,"Not Found");
		statusMap.put(505,"P2P-CI Version Not Supported");
	}
}