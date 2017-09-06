package Controller;

import Model.Device;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDPListener implements Runnable {


    private final int SRC_PORT = 10054;

    TableView<Device> tableView;
    Button scanButton;

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
        try   {

            byte[] receiveData = new byte[15];
            socket.joinGroup(InetAddress.getByName("239.1.2.17"));
            DatagramPacket recievePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.setSoTimeout(2000);
            socket.receive(recievePacket);
            socket.close();
            updateTable(new String(receiveData, 0, receiveData.length));


        } catch (SocketTimeoutException e){

            this.scanButton.setDisable(false);
        }        catch (SocketException e) {
            e.printStackTrace();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null){
                socket.close();
            }
        }
    }

    public void updateTable(String address){
        List<Device> list = new ArrayList<>();
        int id = this.tableView.getItems().size()+1;
        list.add(new Device(id, address));
        this.tableView.getItems().clear();
        this.tableView.getItems().addAll(list);
        this.tableView.refresh();
        this.scanButton.setDisable(false);
    }

    public void setTableView(TableView tableView) {
        this.tableView = tableView;
    }
}
