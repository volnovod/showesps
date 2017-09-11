package Controller;

import java.io.IOException;
import java.net.*;

public class UdpBroadcast implements Runnable {

    private final int DST_PORT = 10055;
    private final int SRC_PORT = 10056;
    private final String broadcastAddress = "239.1.2.17";
    private String localIp;

    public UdpBroadcast(String localIp) {
        this.localIp = localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    @Override
    public void run() {
        sendBroadcast();
    }

    public void sendBroadcast(){
        try {
            DatagramSocket socket = new DatagramSocket(SRC_PORT, InetAddress.getByName(localIp));
            byte[] msg = localIp.getBytes();
            DatagramPacket packet = new DatagramPacket(msg, msg.length, InetAddress.getByName(broadcastAddress), DST_PORT);
            socket.send(packet);
            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
