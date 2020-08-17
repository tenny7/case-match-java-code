package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class Login {
    private String loginUrl ="https://case-match-api.azurewebsites.net/api/login";
    Controller controller = new Controller();
    public void authenticate(String email, String password) throws IOException {
        String urlParameters = "email=" + email + "";

        URL UrlObj = new URL(loginUrl);
        HttpURLConnection connection = (HttpURLConnection) UrlObj.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(urlParameters.getBytes());
//            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
//            outputStream.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        outputStream.flush();
        outputStream.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
//                    status.setText(inputLine);
            }
            in.close();
            System.out.println(response);
            controller.status.setText("List generated and published!!!");
            controller.status.setStyle("-fx-text-fill: green;");
            outputStream.flush();
        } else {

            controller.status.setText("List generation or publishing failed to complete!!!");
        }
    }


}
