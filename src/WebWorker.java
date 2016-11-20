import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import javafx.collections.ObservableList;
/**
 * background worker
 * using semaphore to set the limits
 * @author Nghi Nguyen
 *
 */
public class WebWorker extends Thread {

	private WebFrame gui;
	private String urlString;
	private String status;
	private int row;
	Semaphore semaphore;
	/**
	 * constructor
	 * @param gui
	 * @param url
	 * @param row - the row of the url being processed 
	 * @param permits - number of permit
	 */
	public WebWorker(WebFrame gui, String url, int row, int permits) {

		this.gui = gui;
		this.urlString = url;
		this.row = row;
		this.semaphore = new Semaphore(permits);

	}
	/**
	 * download method
	 * @param urlString
	 */
	private void download(String urlString) {

		System.out.println("Fetching...." + urlString);

		InputStream input = null;
		StringBuilder contents = null;
		try {
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();

			connection.setConnectTimeout(5000);

			connection.connect();
			input = connection.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			char[] array = new char[1000];
			int len;
			contents = new StringBuilder(1000);
			while ((len = reader.read(array, 0, array.length)) > 0) {
				System.out.println("Fetching...." + urlString + len);
				contents.append(array, 0, len);
				Thread.sleep(400);
			}
			
			System.out.println(contents.toString());

		}
		// Otherwise control jumps to a catch...
		catch (MalformedURLException ignored) {
			this.interrupt();
			status = "error";

			gui.data.get(row).setStatus(status);
		} catch (InterruptedException exception) {
			this.interrupt();
			status = "interrupted";

			gui.data.get(row).setStatus(status);
		} catch (IOException ignored) {
			this.interrupt();
			status = "error";
			gui.data.get(row).setStatus(status);
		}
		// "finally" clause, to close the input stream
		// in any case
		finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException ignored) {
				this.interrupt();
				status = "error";
				gui.data.get(row).setStatus(status);
			}
		}
	}
	/**
	 * calculate elapsed time
	 * @return elapsed time
	 */
	private long calculateElasedTime() {

		return (System.currentTimeMillis() - gui.getStart());

	}
	/**
	 * calculate percentage of completed download
	 * used for progressbar
	 * @param i 
	 * @param max
	 * @return percentage
	 */
	private double calculatePercentage(int i, int max) {

		return (double) (i) / (double) max;
	}
	/**
	 * update State count
	 * @param data
	 * @return statecount
	 */
	private List<Integer> updateStateCount(ObservableList<WebModel> data) {

		int completedCount = 1;
		int runningCount = data.size() - 1;
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getSatus().equals("successed")) {
				gui.getStateCountList().set(0, completedCount++);
				gui.getStateCountList().set(1, runningCount--);
			}

		}
		return gui.getStateCountList();
	}

	@Override
	public void run() {

		try {

			while (!isInterrupted() && status != "interrupted" && status != "successed" & status != "error") {

				semaphore.acquire();

				status = "running";
				gui.data.get(row).setStatus(status);

				Thread.sleep(200);
				//download the url
				download(urlString);

				if (!isInterrupted()) {
					status = "successed";
					gui.data.get(row).setStatus("successed");

				}
				//update gui
				updateGuiOnJavaFXThread();

			}
			semaphore.release();
		} catch (InterruptedException e) {
			status = "interrupted";
			gui.data.get(row).setStatus(status);
			semaphore.release();

		}

	}
	
	public void updateGuiOnJavaFXThread() {
		//update gui
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				gui.getRunningLabel().setText(String.valueOf(updateStateCount(gui.data).get(1)));
				gui.getCompletedLabel().setText(String.valueOf(updateStateCount(gui.data).get(0)));
				gui.getElapsedLabel().setText(String.valueOf(calculateElasedTime()) + " ms");
				gui.getProgressBar()
						.setProgress(calculatePercentage(updateStateCount(gui.data).get(0), gui.data.size()));
				if (updateStateCount(gui.data).get(0) == gui.getData().size()) {
					//when everything done, switch gui back to ready state
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					gui.setGuiState("ready");
					gui.updateGui(gui.getGuiState());
				}
				
			}

		});
	}

}