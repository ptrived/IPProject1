import java.io.Serializable;
import java.util.Date;

public class P2PResponse implements Serializable{
	String version;
	Date date;
	String OS;
	Date lastModified;
	long contentLength;
	String contentType;
}
