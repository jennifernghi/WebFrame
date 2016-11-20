import javafx.application.Application;
import javafx.stage.Stage;
/**
 * main launcher for the app
 * @author Nghi Nguyen
 *
 */
public class WebFrameDemo extends Application {

	private static String filename = "";

	public static void main(String[] args) {
		try{
			filename = args[0].trim();
			launch(filename);
		}catch(Exception e){
			System.out.println("wrong commandline");
			System.out.println("java -jar WebLoader.jar links");
			System.out.println("java WebFrameDemo links");
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		WebFrame gui = new WebFrame(filename);
		gui.start(primaryStage);
	}

}
