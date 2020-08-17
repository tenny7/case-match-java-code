package sample;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class PostRequestHandler {
    public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException, ProtocolException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        }
        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public static int sendRequest(String url, String parameter1, String parameter2) throws IOException {
        String urlParameters = "email="+parameter1+"&password="+parameter2+"&role=monitor";

        URL UrlObj = new URL(url);
        Controller.connection = (HttpURLConnection) UrlObj.openConnection();
        Controller.connection.getRequestMethod();
        Controller.connection.setRequestMethod("POST");
        Controller.connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        Controller.connection.setRequestProperty("charset", "utf-8");
        Controller.connection.setDoOutput(true);
        Controller.connection.getRequestMethod();

//        DataOutputStream outputStream = new DataOutputStream(Controller.connection.getOutputStream());
//        outputStream.writeBytes(PostRequestHandler.getParamsString(parameters));

        OutputStream outputStream =  Controller.connection.getOutputStream();
        outputStream.write(urlParameters.getBytes());
//        outputStream.write(parameters.getBytes());
        outputStream.flush();
        outputStream.close();

        int responseCode = Controller.connection.getResponseCode();
    return responseCode;
    }


    public static StringBuilder handleBuffering(int statusCode) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Controller.connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        try {
            if (statusCode == HttpURLConnection.HTTP_OK) { //success

                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                reader.close();
            } else {

            }
        }catch(Exception e){

        }
        return response;
    }
}
