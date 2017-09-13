package Controller;

import Model.Device;
import com.sun.javafx.collections.MappingChange;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class UDPListener implements Runnable {

//    static Logger log = Logger.getLogger(UDPListener.class.getName());
    private final int SRC_PORT = 10054;
    private String localIp;
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
            socket.joinGroup( new InetSocketAddress("239.1.2.17",SRC_PORT), getInterface());
            DatagramPacket recievePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.setSoTimeout(1000);
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

    public NetworkInterface getInterface() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

        for (NetworkInterface netIf : Collections.list(nets)) {
            for (InetAddress address: Collections.list(netIf.getInetAddresses())){
                if (address.getHostAddress().equals(localIp)){
                    System.out.println(netIf.getName());
                    return netIf;
                }
            }
        }
        return null;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public void updateTable(String address){
        address = address.replaceAll("\u0000", "");
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
