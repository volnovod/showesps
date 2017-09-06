package Controller;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


public class MainController {

    private Stage primaryStage;
    private Scene previousScene;
    private Rectangle2D screen;


    @FXML
    private Button addButton;

    @FXML
    private ImageView image;

    @FXML
    public void initialize(){
    }

    @FXML
    public void addEspToPlan(){

    }

    public Rectangle2D getScreen() {
        return screen;
    }

    public void setScreen(Rectangle2D screen) {
        this.screen = screen;
        image.setLayoutX((screen.getWidth() - image.getFitWidth())/2);
        image.setLayoutY(((screen.getHeight() - image.getFitHeight())/2));
        addButton.setLayoutX(screen.getWidth() - this.addButton.getPrefWidth() - 10);
        addButton.setLayoutY(screen.getHeight() - this.addButton.getPrefHeight() - 50);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getPreviousScene() {
        return previousScene;
    }

    public void setPreviousScene(Scene previousScene) {
        this.previousScene = previousScene;
    }
}
