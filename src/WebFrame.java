import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * gui
 * 
 * @author Nghi Nguyen
 *
 */
public class WebFrame {

	private String guiState;// gui state: either ready or running
	// table holds url and their statuses
	private TableView<WebModel> table;
	private TableColumn<WebModel, String> urlCol;
	private TableColumn<WebModel, String> statusCol;

	private Button fetch;
	private Label runningLabel;
	private Label completedLabel;
	private Label elapsedLabel;
	private Button stop;
	private Button concurrentFetchButton;
	private ProgressBar progressBar = new ProgressBar(0);
	private TextField numOfThread;
	private long start;// holds start time

	List<Integer> statecount;// hold numbers of running and completed workers

	ObservableList<WebModel> data = FXCollections.observableArrayList();

	WebWorker[] workers = new WebWorker[data.size()];

	/**
	 * constructor
	 * 
	 * @param filename
	 */
	public WebFrame(String filename) {

		loadFile(filename);
		this.guiState = "ready";
		workers = new WebWorker[data.size()];
		statecount = new ArrayList<>();
		statecount.add(0);// complete count
		statecount.add(0);// running count
	}

	/**
	 * getter
	 * 
	 * @return guiState
	 */
	public String getGuiState() {
		return this.guiState;
	}

	/**
	 * getter
	 * 
	 * @param guiState
	 */
	public void setGuiState(String guiState) {
		this.guiState = guiState.trim();
	}

	/**
	 * getter
	 * 
	 * @return data
	 */
	public ObservableList<WebModel> getData() {
		return this.data;
	}

	/**
	 * getter
	 * 
	 * @return start - start time
	 */
	public long getStart() {
		return this.start;
	}

	/**
	 * getter
	 * 
	 * @return statecount list
	 */
	public List<Integer> getStateCountList() {
		return this.statecount;
	}

	/**
	 * read the file and add each line to data as WebModel
	 * 
	 * @param filename
	 */
	private void loadFile(String filename) {

		String line = "";
		BufferedReader bufferedReader = null;
		InputStream in = getClass().getResourceAsStream(filename);
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(in));

			try {
				while ((line = bufferedReader.readLine()) != null) {

					data.add(new WebModel(line.trim(), "n/a"));

				}
			} catch (IOException e) {

			}
		} catch (Exception e) {
			System.out.println("File not found.");
		}

	}

	/**
	 * getter
	 * 
	 * @return fetch button
	 */
	public Button getFetchButton() {
		return this.fetch;
	}

	/**
	 * getter
	 * 
	 * @return stop button
	 */
	public Button getStopButton() {
		return this.stop;
	}

	/**
	 * getter
	 * 
	 * @return table
	 */
	@SuppressWarnings("rawtypes")
	public TableView getTableView() {
		return this.table;
	}

	/**
	 * getter
	 * 
	 * @return concurrentFetchButton
	 */
	public Button getConcurrentFetchButton() {
		return this.concurrentFetchButton;
	}

	/**
	 * getter
	 * 
	 * @return progressBar
	 */
	public ProgressBar getProgressBar() {
		return this.progressBar;
	}

	/**
	 * getter
	 * 
	 * @return runningLabel
	 */
	public Label getRunningLabel() {
		return this.runningLabel;
	}

	/**
	 * getter
	 * 
	 * @return completedLabel
	 */
	public Label getCompletedLabel() {
		return this.completedLabel;
	}

	/**
	 * getter
	 * 
	 * @return elapsedLabel
	 */
	public Label getElapsedLabel() {
		return this.elapsedLabel;
	}

	/**
	 * getter
	 * 
	 * @return numOfThread textfield
	 */
	public TextField getNumOfThreadTextField() {
		return this.numOfThread;
	}

	/**
	 * create table gui holds links and their statuses
	 * 
	 * @return table
	 */
	@SuppressWarnings("unchecked")
	private TableView<WebModel> tableGUI() {

		table = new TableView<WebModel>();

		// url column
		urlCol = new TableColumn<>("url");
		urlCol.setMinWidth(400);
		urlCol.setCellValueFactory(cellData -> cellData.getValue().getUrlProperty());

		// status column
		statusCol = new TableColumn<>("status");
		statusCol.setMinWidth(300);
		statusCol.setCellValueFactory(cellData -> cellData.getValue().getSatusProperty());

		table.getColumns().addAll(urlCol, statusCol);
		table.setItems(data);

		return table;
	}

	/**
	 * create the gui control app
	 * 
	 * @return Vbox
	 */
	private VBox controlGUI() {
		VBox control = new VBox(10);
		fetch = new Button("fetch");
		// when fetch button is click fetchHandle() is called
		fetch.setOnAction(e -> fetchHandle());

		HBox concurrentFetchBox = new HBox(10);

		Label label = new Label("Number of permit: ");
		numOfThread = new TextField();
		numOfThread.setText("1");
		numOfThread.setMaxWidth(30);

		concurrentFetchButton = new Button("concurrent Fetch");
		// when fetch button is click concurrentFetchHandle() is called
		concurrentFetchButton.setOnAction(e -> concurrentFetchHandle());

		concurrentFetchBox.getChildren().addAll(label, numOfThread, concurrentFetchButton);

		HBox runningBox = new HBox(5);
		Label label1 = new Label("Running: ");
		runningLabel = new Label("");
		runningBox.getChildren().addAll(label1, runningLabel);

		HBox completeBox = new HBox(5);
		Label label2 = new Label("Completed: ");
		completedLabel = new Label("");

		completeBox.getChildren().addAll(label2, completedLabel);

		HBox elapsedBox = new HBox(5);
		Label label3 = new Label("Elapsed: ");
		elapsedLabel = new Label("");
		elapsedBox.getChildren().addAll(label3, elapsedLabel);

		progressBar.setMinWidth(680);

		stop = new Button("stop");
		stop.setDisable(true);
		// when fetch button is click stopHandle() is called
		stop.setOnAction(e -> stopHandle());
		control.getChildren().addAll(fetch, concurrentFetchBox, runningBox, completeBox, elapsedBox, progressBar, stop);

		return control;
	}

	/**
	 * called when fetch button is clicked
	 */
	private void fetchHandle() {
		// initialize start time
		start = System.currentTimeMillis();

		// set gui to "running" state
		setGuiState("running");

		// update gui as running state
		updateGui(getGuiState());

		// create worker with number of permit is 1
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new WebWorker(this, data.get(i).getUrl(), i, 1);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			workers[i].start();

		}

	}

	/**
	 * called when stop button is clicked
	 */
	private void stopHandle() {

		stop.setDisable(true);
		fetch.setDisable(false);
		concurrentFetchButton.setDisable(false);
		// interrupt all threads
		for (WebWorker w : workers) {
			w.interrupt();
		}

	}

	/**
	 * called when concurrent fetch button is clicked
	 */
	private void concurrentFetchHandle() {
		// initialize start time
		start = System.currentTimeMillis();

		// set gui to "running" state
		setGuiState("running");

		// update gui as running state
		updateGui(getGuiState());

		// create workers with specified number of permits
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new WebWorker(this, data.get(i).getUrl(), i, Integer.parseInt(numOfThread.getText()));
			workers[i].start();

		}

	}

	/**
	 * update gui with specified state either ready or running
	 * 
	 * @param guiState
	 */
	protected void updateGui(String guiState) {
		if (guiState.trim().equals("ready")) {
			// reset StateCountList
			getStateCountList().set(0, 0);
			getStateCountList().set(1, 0);
			getStopButton().setDisable(true);
			getFetchButton().setDisable(false);
			getConcurrentFetchButton().setDisable(false);
			getNumOfThreadTextField().setDisable(false);

		} else if (guiState.trim().equals("running")) {
			getStopButton().setDisable(false);
			getFetchButton().setDisable(true);
			getConcurrentFetchButton().setDisable(true);
			getProgressBar().setProgress(0);
			getRunningLabel().setText("...");
			getCompletedLabel().setText("...");
			getElapsedLabel().setText("...");
			getNumOfThreadTextField().setDisable(true);
		}
	}

	public void start(Stage primaryStage) {
		System.out.println("start");
		VBox root = new VBox(10);

		root.getChildren().addAll(tableGUI(), controlGUI());
		Scene scene = new Scene(root, 700, 500);
		primaryStage.setScene(scene);
		primaryStage.setTitle("WebFrame");
		primaryStage.show();

	}

}
