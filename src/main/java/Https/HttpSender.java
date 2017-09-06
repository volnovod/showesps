package Https;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

public class HttpSender implements Runnable {
    private final String WHO = "who";
    private final String NETWORK_SETUP = "networkSetup";
    private final String SET = "set";

    private String deviceAddress;
    private int DST_PORT;
    private String command;


    private HttpPost httpPost;
    private HttpClient client;
    private HttpResponse response;

    public HttpSender(String deviceAddress, String command) {
        this.deviceAddress = deviceAddress;
        this.command = command;
    }

    @Override
    public void run() {
        send();
    }

    public void send(){
        this.client = HttpClientBuilder.create().build();
        this.httpPost = new HttpPost(this.deviceAddress + ":" + String.valueOf(this.DST_PORT));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("command", this.command);
        if (this.command.equals(WHO)){
            jsonObject.put("port", "10060");
        }else if (this.command.equals(NETWORK_SETUP)){

        } else if (this.command.equals(SET)){

        }

    }

    public int getDST_PORT() {
        return DST_PORT;
    }

    public void setDST_PORT(int DST_PORT) {
        this.DST_PORT = DST_PORT;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
}
