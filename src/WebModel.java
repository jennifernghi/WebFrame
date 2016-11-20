import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 * 
 * @author Nghi Nguyen
 *
 */
public class WebModel {
	private StringProperty url; //url
	private StringProperty status; // status: could be n/a, successed, running, error or interrupted
	
	/**
	 * constructor
	 * @param url
	 * @param status
	 */
	public WebModel(String url, String status) {
		this.url = new SimpleStringProperty(url);
		this.status = new SimpleStringProperty(status);
		
	}
	/**
	 * setter
	 * @param url
	 */
	public void setUrl(String url) {
		this.url.set(url);
	}
	/**
	 * getter
	 * @return url StringProperty
	 */
	public StringProperty getUrlProperty() {
		return this.url;
	}
	/**
	 * getter
	 * @return url String
	 */
	public String getUrl() {
		return this.url.get();
	}
	/**
	 * setter
	 * @param status
	 */
	public void setStatus(String status) {
		this.status.set(status);
	}
	/**
	 * getter
	 * @return status StringProperty
	 */
	public StringProperty getSatusProperty() {
		return this.status;
	}
	/**
	 * getter
	 * @return status String
	 */
	public String getSatus() {
		return this.status.get();
	}
}
