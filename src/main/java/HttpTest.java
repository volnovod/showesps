import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class HttpTest {

//    static Logger log = Logger.getLogger(HttpTest.class.getName());
//
//    public static void main(String[] args) throws IOException {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("command", "networkSetup");
//        jsonObject.put("temperature", "22.3");
//        jsonObject.put("rele1", "1");
//        HttpClient client = HttpClientBuilder.create().build();
//        HttpPost httpPost = new HttpPost("http://192.168.1.112:61234/");
//        StringEntity stringEntity = new StringEntity(jsonObject.toString());
//        httpPost.addHeader("content-type", "application/json");
//        httpPost.addHeader("Connection", "close");
//        httpPost.setEntity(stringEntity);
//        HttpResponse response = client.execute(httpPost);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//        System.out.println(reader.readLine());
//
//
//
//    }


}
