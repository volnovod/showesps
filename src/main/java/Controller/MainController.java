package Controller;

import Https.HTTPReceiver;
import Model.Device;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class MainController {

    private Stage primaryStage;
    private Scene previousScene;
    private Rectangle2D screen;
    private HTTPReceiver server;
    private boolean isMouseOut = true;
    private FileReader fileReader;
    private JSONArray savedDevises;
    private boolean press = false;
    private double oldXPos;
    private double oldYPos;


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
    public void initialize(){
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
    }



    @FXML
    public void paneClick(){
        if ( this.isMouseOut){
            this.espPane.setVisible(false);
        }
    }

    @FXML
    public void addEspToPane(){
        Device current = espTable.getSelectionModel().getSelectedItem();
        if (current != null){
            JSONObject currentObject;
            for (int i=0; i < savedDevises.size(); i++){
                currentObject = (JSONObject)savedDevises.get(i);
                if ((currentObject).get("id").equals(current.getAddress())){
                    Circle circle = new Circle();
                    circle.setCenterX(Double.parseDouble(currentObject.get("xpos").toString()));
                    circle.setCenterY(Double.parseDouble(currentObject.get("ypos").toString()));
                    circle.setRadius(10);
                    circle.setFill(Color.GREEN);
                    mainPane.getChildren().add(circle);
                    EventHandler<MouseEvent> circleHandler = event -> {
                        if ( event.getEventType() == MouseEvent.MOUSE_PRESSED ){
                            this.press = true;
                            this.oldXPos = event.getX();
                            this.oldYPos = event.getY();
                        } else if ( event.getEventType() == MouseEvent.MOUSE_RELEASED ){
                            this.press = false;
                        }

                        if (press && event.getEventType() == MouseEvent.MOUSE_DRAGGED){
                            double currX = event.getX();
                            double currY = event.getY();
                            double dX = currX - oldXPos;
                            double dY = currY - oldYPos;
                            circle.setCenterX(circle.getCenterX() +dX);
                            circle.setCenterY(circle.getCenterY() + dY);
                            oldXPos = currX;
                            oldYPos = currY;

                        }
                    };
                    circle.addEventHandler(MouseEvent.MOUSE_DRAGGED, circleHandler);
                    circle.addEventHandler(MouseEvent.MOUSE_PRESSED, circleHandler);
                    circle.addEventHandler(MouseEvent.MOUSE_RELEASED, circleHandler);
                    break;
                }
            }
        }
    }

    @FXML
    public void showDevicesTable(){
        try {
            JSONParser parser = new JSONParser();
            fileReader = new FileReader("devices.json");
            JSONObject devices = (JSONObject) parser.parse(fileReader);
            fileReader.close();
            savedDevises = (JSONArray) devices.get("devices");
            List<Device> deviceList = new ArrayList<>();

            for (int j = 0; j < savedDevises.size(); j++){
                JSONObject current = (JSONObject) savedDevises.get(j);
                deviceList.add(new Device(j+1, current.get("id").toString()));
            }

            ObservableList<Device> list = FXCollections.<Device>observableList(deviceList);
           this.espTable.setItems(list);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        espPane.setVisible(true);
    }

    public Rectangle2D getScreen() {
        return screen;
    }

    public void setScreen(Rectangle2D screen) {
        this.screen = screen;
        image.setLayoutX((screen.getWidth() - image.getFitWidth())/2);
        image.setLayoutY(10);
        addButton.setLayoutX(screen.getWidth() - this.addButton.getPrefWidth() - 10);
        addButton.setLayoutY(screen.getHeight() - this.addButton.getPrefHeight() - 50);
    }

    public void setServer(HTTPReceiver server) {
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
