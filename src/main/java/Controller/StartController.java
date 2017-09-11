package Controller;

import Https.HTTPReceiver;
import Https.HttpSender;
import Model.Device;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import org.json.JSONObject;

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

    private HttpURLConnection connection;

    private Stage primaryStage;
    private Scene mainScene;
    private Scene configScene;
    private ConfigController configController;

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

    public void setConfigController(ConfigController configController) {
        this.configController = configController;
    }

    public Scene getConfigScene() {
        return configScene;
    }

    public void setConfigScene(Scene configScene) {
        this.configScene = configScene;
    }

    public Rectangle2D getScreen() {
        return screen;
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


    }

    public Stage getPrimaryStage() {
        return primaryStage;
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
        if (ipList.size() > 0) {
            this.networkList.getItems().addAll(FXCollections.observableList(ipList));
            this.networkList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    this.scanButton.setDisable(false);
                    this.localAddress = this.ipList.get(this.networkList.getSelectionModel().getSelectedIndex()).getHostAddress();
                } else {
                    this.scanButton.setDisable(true);
                }
            });
        }

    }

    @FXML
    public void connect() {
        deviceAddress = deviceAddress.replaceAll("\\u0000", "");
        Thread receiver = new Thread(() -> {
            HTTPReceiver httpReceiver = new HTTPReceiver();
            httpReceiver.receive();
            Platform.runLater(() -> resultProcess(httpReceiver.getResult()));
        });
        Thread httpSender = new Thread(() -> {
            HttpSender sender =new HttpSender( deviceAddress, "who");
            sender.send();
        });
        receiver.start();
        httpSender.start();
    }

    public void resultProcess(String result){
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.get("id").equals("0")){
            this.configController.setDeviceAddress(deviceAddress);
            primaryStage.setScene(getConfigScene());
            primaryStage.show();
        } else if (!jsonResult.get("id").equals("0")){
            primaryStage.setScene(getMainScene());
            primaryStage.show();
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
        Thread udpListner = new Thread(this.listener);
        udpListner.start();
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
                            broadcastList.add(ia.getBroadcast());

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
