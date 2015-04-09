import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class P2SResponse implements Serializable{
	String version;
	int statusCode;
	String phrase;
	List<String> responseList = new ArrayList<String>();
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getPhrase() {
		return phrase;
	}
	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}
	public List<String> getResponseList() {
		return responseList;
	}
	public void setResponseList(List<String> responseList) {
		this.responseList = responseList;
	}
	
}
