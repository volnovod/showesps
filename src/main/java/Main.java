import Controller.ConfigController;
import Controller.MainController;
import Controller.StartController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {


    public void start(Stage primaryStage) throws Exception {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });

        FXMLLoader startLoader = new FXMLLoader(getClass().getResource("/fxml/startPage.fxml"));
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/mainPage.fxml"));
        FXMLLoader configLoader = new FXMLLoader(getClass().getResource("/fxml/configPage.fxml"));

        Scene startScene = new Scene(startLoader.load());
        Scene mainScene = new Scene(mainLoader.load());
        Scene configScene = new Scene(configLoader.load());

        StartController startController = startLoader.getController();
        MainController mainController = mainLoader.getController();
        ConfigController configController = configLoader.getController();

        startController.setMainScene(mainScene);
        startController.setConfigScene(configScene);
        startController.setPrimaryStage(primaryStage);

        mainController.setPreviousScene(startScene);

        mainController.setPrimaryStage(primaryStage);

        configController.setPrimaryStage(primaryStage);
        configController.setStartScene(startScene);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        mainController.setScreen(primaryScreenBounds);
        startController.setScreen(primaryScreenBounds);
        configController.setScreen(primaryScreenBounds);

        //set Stage boundaries to visible bounds of the main screen
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.setResizable(false);
        primaryStage.setScene(startScene);
//        primaryStage.setScene(configScene);
        primaryStage.show();



    }
}
