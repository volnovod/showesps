package Controller;

import Https.HttpSender;
import Model.Device;
import View.DevBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainController {

    private Stage primaryStage;
    private HttpSender server;
    private Scene previousScene;
    private Rectangle2D screen;
    private boolean isMouseOut = true;
    private FileReader fileReader;
    private JSONArray savedDevises;
    private JSONArray espesJsonArray;
    private String address;
    private Timer timer;
    private List<DevBox> espList;


    @FXML
    private Button addButton;

    @FXML
    private ImageView image;

    @FXML
    private Pane espPane;

    @FXML
    private TableView<Device> espTable;

    @FXML
    private Pane mainPane;

    @FXML
    private VBox tableBox;

    @FXML
    public void initialize() {
        espPane.setVisible(false);
        espPane.setPrefHeight(150);
        espPane.setPrefWidth(150);
        espTable.setPrefHeight(espPane.getPrefHeight());
        espTable.setPrefWidth(espPane.getPrefWidth());
        espTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                    } else {
                    }
                }
        );
        TableColumn idCol = new TableColumn("№");
        idCol.setPrefWidth(espTable.getPrefWidth() * 0.3);
        idCol.setCellValueFactory(new PropertyValueFactory<Device, Integer>("id"));

        TableColumn addressCol = new TableColumn("Адреса");
        addressCol.setPrefWidth(espTable.getPrefWidth() * 0.8);
        addressCol.setCellValueFactory(new PropertyValueFactory<Device, String>("address"));
        espTable.getColumns().addAll(idCol, addressCol);
        espPane.setOnMouseEntered(event -> {
            this.isMouseOut = false;
        });
        espPane.setOnMouseExited(event -> {
            this.isMouseOut = true;
        });

        tableBox.setPrefHeight(this.espPane.getPrefHeight());
        this.tableBox.setPrefWidth(this.espPane.getPrefWidth());
        this.espList = new ArrayList<>();
        getSavedDevices();
        for (int i = 0; i < savedDevises.size(); i++){
            JSONObject currentEsp = (JSONObject)savedDevises.get(i);
            if (currentEsp.get("visible").toString().equals("yes")){
                DevBox savedDevice = new DevBox(currentEsp);
                espList.add(savedDevice);
                this.mainPane.getChildren().add(savedDevice);

            }
        }
    }

    public boolean saveDevices() {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("devices.json");
            fileWriter.write(savedDevises.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean getSavedDevices() {
        JSONParser parser = new JSONParser();
        try {
            fileReader = new FileReader("devices.json");
            savedDevises = (JSONArray) parser.parse(fileReader);
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                server = new HttpSender(address, "who");
                server.send();
                Platform.runLater(() -> {
                    espesJsonArray = server.getJsonArray();
                    updateVisibleDevices();
                });
            }
        }, 0, 10000);
    }

    public void updateVisibleDevices() {
        if (espesJsonArray != null) {
            for (int i = 0; i < espList.size(); i++) {
                DevBox currentBox = espList.get(i);
                for (int j = 0; j < espesJsonArray.size(); j++) {
                    JSONObject currentDev = (JSONObject) espesJsonArray.get(j);
                    if (currentBox.getEsp().get("id").toString().equals(currentDev.get("id").toString())) {
                        currentBox.setEsp(currentDev);
                        currentBox.update();
                    }
                }
            }

        }

    }


    public void showEsp(DevBox esp) {

    }


    @FXML
    public void paneClick() {
        if (this.isMouseOut) {
            this.espPane.setVisible(false);
        }
    }

    @FXML
    public void addEspToPane() {
        Device current = espTable.getSelectionModel().getSelectedItem();
        if (current != null) {
            JSONObject currentObject;
            for (int i = 0; i < savedDevises.size(); i++) {
                currentObject = (JSONObject) savedDevises.get(i);
                if ((currentObject).get("id").equals(current.getAddress()) && currentObject.get("visible").equals("no")) {
                    DevBox devVBox = new DevBox(currentObject);
                    currentObject.put("visible", "yes");
                    if (saveDevices()) {
                        getSavedDevices();
                    }
                    for (int j = 0; j < espTable.getItems().size(); j++) {
                        if (espTable.getItems().get(j).getAddress().equals(currentObject.get("id").toString())) {
                            espTable.getItems().remove(j);
                            break;
                        }
                    }
                    mainPane.getChildren().add(devVBox);
                    this.espList.add(devVBox);
                    break;
                }
            }
        }
    }

    @FXML
    public void showDevicesTable() {
        getSavedDevices();
        List<Device> deviceList = new ArrayList<>();

        for (int j = 0; j < savedDevises.size(); j++) {
            JSONObject current = (JSONObject) savedDevises.get(j);
            if (current.get("visible").toString().equals("no")) {
                deviceList.add(new Device(j + 1, current.get("id").toString()));
            }
        }

        ObservableList<Device> list = FXCollections.<Device>observableList(deviceList);
        this.espTable.setItems(list);
        espPane.setVisible(true);
    }

    public Rectangle2D getScreen() {
        return screen;
    }

    public void setScreen(Rectangle2D screen) {
        this.screen = screen;
        image.setLayoutX((screen.getWidth() - image.getFitWidth()) / 2);
        image.setLayoutY(10);
        addButton.setLayoutX(screen.getWidth() - this.addButton.getPrefWidth() - 10);
        addButton.setLayoutY(screen.getHeight() - this.addButton.getPrefHeight() - 50);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setServer(HttpSender server) {
        this.server = server;
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
