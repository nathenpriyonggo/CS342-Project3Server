
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GuiServer extends Application{

	HashMap<String, Scene> sceneMap;
	Server serverConnection;
	
	ListView<String> listItems, listItems2;

	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Regular.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Medium.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Semibold.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Bold.ttf"), 14);

		serverConnection = new Server(data -> {
			Platform.runLater(()->{
				listItems.getItems().add(0, data.toString());
			});
		});

		
		listItems = new ListView<String>();

		sceneMap = new HashMap<String, Scene>();
		
		sceneMap.put("server",  createServerGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

		primaryStage.setScene(sceneMap.get("server"));
		primaryStage.setTitle("This is the Server");
		primaryStage.show();
		
	}
	
	public Scene createServerGui() {


		VBox paneVertical = new VBox(10, listItems);
		paneVertical.setStyle("-fx-font-family: 'gg sans Semibold'");


		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: white;" + "-fx-font-family: 'gg sans Bold'");
        pane.setCenter(paneVertical);

		return new Scene(pane, 600, 400);
	}
}
