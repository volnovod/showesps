package Controller;

import Https.HTTPReceiver;
import Https.HttpSender;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConfigController {

    private Scene startScene;
    private Stage primaryStage;
    private Rectangle2D screen;
    private String defaultStyle;
    private String deviceAddress;
    @FXML
    private RadioButton mainDevRadioButton;

    @FXML
    private RadioButton simpleDevRadioButton;

    @FXML
    private VBox mainDevBox;

    @FXML
    private VBox simpleDevBox;

    @FXML
    private TextField mainId;

    @FXML
    private TextField groupId;

    @FXML
    private TextField ssid;

    @FXML
    private PasswordField password;

    @FXML
    private TextField existingSsid;

    @FXML
    private PasswordField existingPassword;

    @FXML
    private TextField simpleId;

    @FXML
    private TextField simpleExistingSsid;

    @FXML
    private PasswordField simpleExistingPassword;

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    @FXML
    public void initialize() {
        ToggleGroup radioGroup = new ToggleGroup();
        this.mainDevRadioButton.setToggleGroup(radioGroup);
        this.simpleDevRadioButton.setToggleGroup(radioGroup);
        this.mainDevRadioButton.setSelected(true);
        this.mainDevBox.setDisable(false);
        this.simpleDevBox.setDisable(true);
        radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue == this.mainDevRadioButton) {
                    this.simpleDevBox.setDisable(true);
                    this.simpleDevBoxDefault();
                    this.mainDevBox.setDisable(false);
                } else if (newValue == this.simpleDevRadioButton) {
                    this.simpleDevBox.setDisable(false);
                    this.mainDevBox.setDisable(true);
                    this.mainDevBoxDefault();
                }
            }
        });
        this.defaultStyle = mainId.getStyle();
    }

    @FXML
    public void save() {
        if (validate()) {

            Thread configThread = new Thread(() -> {
                HttpSender sender = new HttpSender(deviceAddress, "networkSetup");
                if (mainDevRadioButton.isSelected()) {
                    sender.getJsonObject().put("id", mainId.getText())
                            .put("groupId", groupId.getText())
                            .put("netId", ssid.getText())
                            .put("netPassword", password.getText())
                            .put("exNetId", existingSsid.getText())
                            .put("exNetPassword", existingPassword.getText());
                }else if (simpleDevRadioButton.isSelected()){
                    sender.getJsonObject().put("id", simpleId.getText())
                            .put("netId", simpleExistingSsid.getText())
                            .put("netPassword", simpleExistingPassword.getText());
                }
                sender.send();
            });
            configThread.start();
        }

    }

    private boolean validate() {
        boolean result = true;
        mainDevBoxDefault();
        simpleDevBoxDefault();
        if (mainDevRadioButton.isSelected()) {
            if (mainId.getText().length() >= 3) {
                mainId.setStyle(defaultStyle);
            } else {
                mainId.setStyle("-fx-border-color:red");
                result = false;
            }
            if (groupId.getText().length() > 3) {
                groupId.setStyle(defaultStyle);
            } else {
                groupId.setStyle("-fx-border-color: red");
                result = false;
            }
            if (ssid.getText().length() > 1) {
                ssid.setStyle(defaultStyle);
            } else {
                ssid.setStyle("-fx-border-color: red");
                result = false;
            }
            if (password.getText().length() > 7 && password.getText().length() < 33) {
                password.setStyle(defaultStyle);
            } else {
                password.setStyle("-fx-border-color: red");
                result = false;
            }
            if (existingSsid.getText().length() > 1) {
                existingSsid.setStyle(defaultStyle);
            } else {
                existingSsid.setStyle("-fx-border-color: red");
                result = false;
            }
            if (existingPassword.getText().length() > 7 && existingPassword.getText().length() < 33) {
                existingPassword.setStyle(defaultStyle);
            } else {
                existingPassword.setStyle("-fx-border-color: red");
                result = false;
            }
        } else if (simpleDevRadioButton.isSelected()) {
            if (simpleId.getText().length() > 1) {
                simpleId.setStyle(defaultStyle);
            } else {
                simpleId.setStyle("-fx-border-color: red");
                result = false;
            }
            if (simpleExistingSsid.getText().length() > 1) {
                simpleExistingSsid.setStyle(defaultStyle);
            } else {
                simpleExistingSsid.setStyle("-fx-border-color: red");
                result = false;
            }
            if (simpleExistingPassword.getText().length() > 7 && simpleExistingPassword.getText().length() < 33) {
                simpleExistingPassword.setStyle(defaultStyle);
            } else {
                simpleExistingPassword.setStyle("-fx-border-color: red");
                result = false;
            }
        }

        return result;
    }

    private void simpleDevBoxDefault() {
        simpleId.setStyle(defaultStyle);
        simpleExistingSsid.setStyle(defaultStyle);
        simpleExistingPassword.setStyle(defaultStyle);
    }

    private void mainDevBoxDefault() {
        mainId.setStyle(defaultStyle);
        groupId.setStyle(defaultStyle);
        ssid.setStyle(defaultStyle);
        password.setStyle(defaultStyle);
        existingSsid.setStyle(defaultStyle);
        existingPassword.setStyle(defaultStyle);
    }


    @FXML
    public void back() {
        clearData();
        primaryStage.setScene(getStartScene());
        primaryStage.show();
    }

    private void clearData() {
        mainId.setText("");
        groupId.setText("");
        ssid.setText("");
        password.setText("");
        existingSsid.setText("");
        existingPassword.setText("");
        simpleId.setText("");
        simpleExistingSsid.setText("");
        simpleExistingPassword.setText("");
    }


    public void setScreen(Rectangle2D screen) {
        this.screen = screen;
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
