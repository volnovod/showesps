package Https;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HttpSender  {
    final String WHO = "who";
    final String NETWORK_SETUP = "networkSetup";
    final String SET = "set";

    private String deviceAddress;
    private int DST_PORT = 80;
    private String command;
    private JSONObject jsonObject = new JSONObject();


    private HttpPost httpPost;
    private HttpClient client;

    public HttpSender(String deviceAddress, String command) {
        this.deviceAddress = deviceAddress;
        this.command = command;
    }


    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void send(){
        this.client = HttpClientBuilder.create().build();
        this.httpPost = new HttpPost("http://" + this.deviceAddress + ":" + String.valueOf(this.DST_PORT));
        jsonObject.put("command", this.command);
        if (this.command.equals(WHO)){
            jsonObject.put("port", "10060");
        }else if (this.command.equals(NETWORK_SETUP)){

        } else if (this.command.equals(SET)){

        }
        try {
            StringEntity msg = new StringEntity(jsonObject.toString());
            httpPost.setEntity(msg);
            HttpResponse response = client.execute(httpPost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String result = reader.readLine();
            reader.close();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
