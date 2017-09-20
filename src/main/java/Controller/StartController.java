package Controller;

import Https.HttpSender;
import Model.Device;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


public class StartController {


    private UdpBroadcast broadcast;
    private UDPListener listener;

    private Rectangle2D screen;

    private String deviceAddress;
    private String localAddress;
    private String json;
    private List<InetAddress> ipList = new ArrayList<>();
    List<InetAddress> broadcastList = new ArrayList<>();


    private Stage primaryStage;
    private Scene mainScene;
    private Scene configScene;
    private ConfigController configController;
    private FileWriter fileWriter;
    private FileReader fileReader;

    @FXML
    private Pane pane;

    @FXML
    private Button scanButton;

    @FXML
    private Button exitButton;


    @FXML
    private TableView<Device> devTable;

    @FXML
    private Button connectButton;

    @FXML
    private ChoiceBox networkList;

    @FXML
    private Button updateNetListButton;

    public void setConfigController(ConfigController configController) {
        this.configController = configController;
    }

    public Scene getConfigScene() {
        return configScene;
    }

    public void setConfigScene(Scene configScene) {
        this.configScene = configScene;
    }

    public void setScreen(Rectangle2D screen) {
        this.screen = screen;
        this.pane.setPrefWidth(screen.getWidth());
        this.pane.setPrefHeight(screen.getHeight());


        this.networkList.setPrefWidth(screen.getWidth() * 0.19);
        this.networkList.setLayoutX(screen.getWidth() * 0.01);
        this.networkList.setLayoutY(screen.getHeight() * 0.05);

        this.devTable.setPrefWidth(screen.getWidth() * 0.5);
        this.devTable.setPrefHeight(screen.getHeight() * 0.35);
        this.devTable.setLayoutX(screen.getWidth() * 0.35);

        this.connectButton.setLayoutY(screen.getHeight() * 0.9);
        double xStep = (screen.getWidth() - this.connectButton.getWidth() * 3) / 4;
        this.connectButton.setLayoutX(xStep);
        this.scanButton.setLayoutX(this.connectButton.getLayoutX() + this.connectButton.getWidth() + xStep);
        this.scanButton.setLayoutY(this.connectButton.getLayoutY());
        this.exitButton.setLayoutX(this.scanButton.getLayoutX() + this.scanButton.getWidth() + xStep);
        this.exitButton.setLayoutY(this.connectButton.getLayoutY());

        this.updateNetListButton.setLayoutY(this.networkList.getLayoutY());
        this.updateNetListButton.setLayoutX(this.networkList.getLayoutX() + this.networkList.getPrefWidth() + 10);


    }


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getMainScene() {
        return mainScene;
    }

    public void setMainScene(Scene nextScene) {
        this.mainScene = nextScene;
    }

    @FXML
    public void updateList() {
        setInterfacesList();
    }

    @FXML
    public void initialize() {
        this.listener = new UDPListener();
        this.connectButton.setDisable(true);
        this.devTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        this.connectButton.setDisable(false);
                        this.deviceAddress = newValue.getAddress();
                    } else {
                        this.connectButton.setDisable(true);
                        this.deviceAddress = "";
                    }
                }
        );
        TableColumn idCol = new TableColumn("№");
        idCol.setPrefWidth(this.devTable.getPrefWidth() * 0.2);
        idCol.setCellValueFactory(new PropertyValueFactory<Device, Integer>("id"));

        TableColumn addressCol = new TableColumn("Адреса");
        addressCol.setPrefWidth(this.devTable.getPrefWidth() * 0.8);
        addressCol.setCellValueFactory(new PropertyValueFactory<Device, String>("address"));
        devTable.getColumns().addAll(idCol, addressCol);
        setInterfacesList();
        this.listener = new UDPListener();
        this.listener.setTableView(this.devTable);
        this.listener.setScanButton(this.scanButton);
        if (ipList.size() > 0) {
            this.networkList.getItems().addAll(FXCollections.observableList(ipList));
            this.networkList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    this.scanButton.setDisable(false);
                    this.localAddress = this.ipList.get(this.networkList.getSelectionModel().getSelectedIndex()).getHostAddress();
                    this.listener.setLocalIp(this.localAddress);
                    Thread udpListner = new Thread(this.listener);
                    udpListner.start();

                } else {
                    this.scanButton.setDisable(true);
                }
            });
        }





    }

    @FXML
    public void updateNetList() {
        setInterfacesList();
        this.networkList.setItems(FXCollections.observableList(ipList));
    }

    @FXML
    public void connect() {
        deviceAddress = deviceAddress.replaceAll("\\u0000", "");
        Thread httpSender = new Thread(() -> {
            HttpSender sender =new HttpSender( deviceAddress, "who");
            sender.send();
            Platform.runLater(() -> resultProcess(sender.getJsonArray()));
        });
        httpSender.start();
    }

    public void resultProcess(JSONArray jsonArray){
        if (jsonArray.size() > 0){
            try {
                for ( int i = 0; i < jsonArray.size(); i++){

                    JSONObject inputObject = (JSONObject) jsonArray.get(i);
                    JSONParser parser = new JSONParser();
                    JSONObject devices ;
                    fileReader = new FileReader("devices.json");
                    devices = (JSONObject) parser.parse(fileReader);
                    fileReader.close();
                    JSONArray savedDevises = (JSONArray) devices.get("devices");
                    boolean ifSave = true;
                    for (int j = 0; j < savedDevises.size(); j++){
                        JSONObject savedDevice = (JSONObject) savedDevises.get(j);
                        if (savedDevice.get("id").equals(inputObject.get("id"))){
                            ifSave = false;
                            break;
                        }
                    }
                    if ( ifSave){
                        System.out.println("Saving device");
                        JSONObject objToSave = inputObject;
                        objToSave.put("xpos", "100");
                        objToSave.put("ypos", "100");
                        objToSave.remove("command");
                        objToSave.remove("setup");
                        fileWriter = new FileWriter("devices.json");
                        savedDevises.add(objToSave);
                        devices.put("devices", savedDevises);
                        fileWriter.write(devices.toJSONString());
                        fileWriter.close();
                    }
                    if (inputObject.get("setup").equals("required")){
//                        this.listener.work = false;
                        this.configController.setDeviceAddress(deviceAddress);
                        this.configController.setObjectForConfig(inputObject);
                        primaryStage.setScene(getConfigScene());
                        primaryStage.show();
                    } else if (!inputObject.get("setup").equals("required")){
//                        this.listener.work = false;
                        primaryStage.setScene(getMainScene());
                        primaryStage.show();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    public void exit() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void scanDevices() {
        this.listener = new UDPListener();
        this.listener.setTableView(this.devTable);
        this.listener.setScanButton(this.scanButton);
        this.broadcast = new UdpBroadcast(this.localAddress);
        this.listener.setLocalIp(this.localAddress);
        Thread udpBroadcast = new Thread(this.broadcast);
        udpBroadcast.start();

    }

    public void setInterfacesList() {
        List<InetAddress> ipList = new ArrayList<>();
        List<InetAddress> broadcastList = new ArrayList<>();
        try {
            InetAddress candidateAddress = null;
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                List<InterfaceAddress> list2 = iface.getInterfaceAddresses();
                Iterator<InterfaceAddress> it = list2.iterator();

                while (it.hasNext()) {
                    InterfaceAddress ia = it.next();
                    InetAddress inetAddress = ia.getAddress();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress.isSiteLocalAddress()) {
                            if (!ipList.contains(inetAddress)){
                                ipList.add(inetAddress);
                            }

                        }
                    }
                }
            }

        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
        }
        this.ipList = ipList;
        this.broadcastList = broadcastList;

    }


}
