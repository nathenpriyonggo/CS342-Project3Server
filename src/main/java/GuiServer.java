
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/*
Gui Server class
 */
public class GuiServer extends Application{

	Server serverConnection;
	ListView<Text> listItems;
	Label title;


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		// Discord-style fonts
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Regular.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Medium.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Semibold.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Bold.ttf"), 14);

		// Callback execution
		serverConnection = new Server(data -> {
			Platform.runLater(()->{

				Message msg = (Message) data;

				// Callback message is a text
				Text newText = new Text(msg.getData());
				if (msg.isPrivateText() || msg.isPublicText()) {

					newText.setFont(Font.font("gg sans Regular", 14));
				}
				// Callback message is a notification
				else {

					newText.setFont(Font.font("gg sans Semibold", 14));
					newText.setFill(Color.web("#800020"));
				}
				listItems.getItems().add(0, newText);
			});
		});


		// Server Gui scene: list items list view
		listItems = new ListView<Text>();
		listItems.setStyle("-fx-font-family: 'gg sans Medium';"
				+ "-fx-font-size: 15");
		// Server Gui scene: title label
		title = new Label("text-it Log");
		title.setStyle("-fx-font-family: 'gg sans Bold';"
				+ "-fx-font-size: 50;");
		title.setAlignment(Pos.CENTER);


		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

		// Set initial scene
		primaryStage.setScene(createServerGui());
		primaryStage.setTitle("Server");
		primaryStage.show();
	}


	/*
	Server Gui code
	 */
	public Scene createServerGui() {

		VBox paneVertical = new VBox(10, title, listItems);
		paneVertical.setAlignment(Pos.CENTER);

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(50));
		pane.setCenter(paneVertical);
		pane.setStyle("-fx-background-color: #FAD5A5;");

		return new Scene(pane, 600, 600);
	}
}
