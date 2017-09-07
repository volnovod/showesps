package Controller;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConfigController {

    private Scene startScene;
    private Stage primaryStage;
    private Rectangle2D screen;

    @FXML
    private RadioButton mainDevRadioButton;

    @FXML
    private RadioButton simpleDevRadioButton;

    @FXML
    private VBox mainDevBox;

    @FXML
    private VBox simpleDevBox;

    @FXML
    public void initialize(){
        ToggleGroup radioGroup = new ToggleGroup();
        this.mainDevRadioButton.setToggleGroup(radioGroup);
        this.simpleDevRadioButton.setToggleGroup(radioGroup);
        this.mainDevRadioButton.setSelected(true);
        this.mainDevBox.setDisable(false);
        this.simpleDevBox.setDisable(true);
        radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null ){
                if (newValue == this.mainDevRadioButton){
                    this.simpleDevBox.setDisable(true);
                    this.mainDevBox.setDisable(false);
                } else if (newValue == this.simpleDevRadioButton) {
                    this.simpleDevBox.setDisable(false);
                    this.mainDevBox.setDisable(true);
                }
            }
        });
    }


    public void setScreen(Rectangle2D screen) {
        this.screen = screen;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getStartScene() {
        return startScene;
    }

    public void setStartScene(Scene startScene) {
        this.startScene = startScene;
    }
}
