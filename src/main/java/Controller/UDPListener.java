package Controller;

import Model.Device;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class UDPListener implements Runnable {

    private final int SRC_PORT = 10054;
    private String localIp;
    TableView<Device> tableView;
    Button scanButton;
    boolean work = true;

    public void setScanButton(Button scanButton) {
        this.scanButton = scanButton;
    }

    @Override
    public void run() {
        try {
            this.scanButton.setDisable(true);
            listenedUdpResponce();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenedUdpResponce() throws Exception {
        MulticastSocket socket = new MulticastSocket(SRC_PORT);
        try {

            byte[] receiveData = new byte[15];
            socket.joinGroup(new InetSocketAddress("239.1.2.17", SRC_PORT), getInterface());
            while (work){
                DatagramPacket recievePacket = new DatagramPacket(receiveData, receiveData.length);

                socket.receive(recievePacket);
                updateTable(new String(receiveData, 0, receiveData.length));
            }
            socket.close();

        } catch (SocketTimeoutException e) {

            this.scanButton.setDisable(false);
        } catch (SocketException e) {
            e.printStackTrace();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    public NetworkInterface getInterface() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

        for (NetworkInterface netIf : Collections.list(nets)) {
            for (InetAddress address : Collections.list(netIf.getInetAddresses())) {
                if (address.getHostAddress().equals(localIp)) {
                    return netIf;
                }
            }
        }
        return null;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public void updateTable(String address) {
        address = address.replaceAll("\u0000", "");
        ObservableList<Device> list = this.tableView.getItems();
        boolean isContains = false;
        if (list.size() > 0){
            for (int i = 0; i < list.size(); i++){
                Device current = list.get(i);
                if (current.getAddress().equals(address)){
                    isContains = true;
                    break;
                }
            }
        } else {
            isContains = false;
        }
        if (!isContains){
            int id = 0;
            if (list.size() == 0){
                id = 0;
            }else {
                id = this.tableView.getItems().size() + 1;
            }
            this.tableView.getItems().add(id, new Device(id+1, address));
            this.tableView.refresh();
            this.scanButton.setDisable(false);
        }
    }

    public void setTableView(TableView tableView) {
        this.tableView = tableView;
    }
}
