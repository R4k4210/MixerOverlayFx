package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sample.fxml"));
        Parent root = (Parent) loader.load();
        primaryStage.setTitle("Mixer Overlay");
        Controller controller = (Controller) loader.getController();
        controller.setPrimaryStage(primaryStage);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
	    primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
