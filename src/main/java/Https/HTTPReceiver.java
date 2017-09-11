package Https;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPReceiver {

    private final int port = 10060;
    private String result = "";

    public String getResult() {
        return result;
    }

    public void receive(){

//        HTTPServer
        try {
            ServerSocket serverSocket= new ServerSocket();
            serverSocket.bind( new InetSocketAddress(InetAddress.getByName("192.168.4.100"), port));
            Socket socket = serverSocket.accept();
            socket.setSoTimeout(4000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine();
            while (line != null){
                result += line;
                line = reader.readLine();
            }
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
