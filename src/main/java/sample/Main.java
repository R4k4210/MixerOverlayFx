package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.ResizeHelper;


public class Main extends Application {

    private static Stage primaryStage; // **Declare static Stage**

    private void setPrimaryStage(Stage stage) {
        Main.primaryStage = stage;
    }
    private Controller controller;

    static public Stage getPrimaryStage() {
        return Main.primaryStage;
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        setPrimaryStage(primaryStage);
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sample.fxml"));
        Parent root = (Parent) loader.load();

        primaryStage.setTitle("Mixer Overlay");
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(root, 314, 500);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.setScene(scene);

        primaryStage.setAlwaysOnTop(true);
	    primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.show();

        ResizeHelper.addResizeListener(primaryStage);

        controller = loader.getController();
        ResizeHelper.controller = controller;
        
        ConfigurationController.sampleController = controller;
        

        //bind nodes to stage size

        controller.getMainContainer().prefWidthProperty().bind(primaryStage.widthProperty());
        //controller.getMainContainer().prefHeightProperty().bind(primaryStage.heightProperty());
        controller.getSplitPane().prefWidthProperty().bind(primaryStage.widthProperty());
        //controller.getSplitPane().prefHeightProperty().bind(primaryStage.heightProperty());
        //bind scrollpane to bottom anchorPane
        controller.getScrPanel().prefHeightProperty().bind(controller.getBottomAnchorOnSplitPane().heightProperty());
        controller.getScrPanel().prefWidthProperty().bind(controller.getBottomAnchorOnSplitPane().widthProperty());
        controller.getOpacityWindows().prefWidthProperty().bind(controller.getScrPanel().widthProperty());
        controller.getOpacityWindows().prefHeightProperty().bind(controller.getScrPanel().heightProperty());

    }

    public static void main(String[] args) {
        launch(args);
    }


}
