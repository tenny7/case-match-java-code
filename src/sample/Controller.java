package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;


public class Controller implements Initializable {
    public TextField fullName;
    public Button addEmployee;
    public TextArea status;
    public ListView listEmployees;
    public Button login;
    public String api_token;
    public String loggedUser;
    public static HttpURLConnection connection;
    public Label loggedInId;
    public Button adminLoginButton;
    public Button generateList;
    public Label fullNameLabel;
    public static int loginCount;
    public static boolean isLoggedIn;

    Locale locale = new Locale("en", "US");
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
    DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
    String date = dateFormat.format(new Date());
    String time = timeFormat.format(new Date());

    HashMap<Integer, String> employee = new HashMap<>();
//    List<String> list = Arrays.asList("A", "B", "C", "D");
    ArrayList<String> list = new ArrayList<String>();
    String url = "https://case-match-api.azurewebsites.net/api/broadcast";
    String fetchUrl = "https://case-match-api.azurewebsites.net/api/fetch";

    String loginUrl ="https://case-match-api.azurewebsites.net/api/login";
    String adminLoginUrl ="https://case-match-api.azurewebsites.net/api/qmonitor";
    int count = 1;





    public void btn(ActionEvent actionEvent) {
        String full_name = fullName.getText().trim();
        list.add(full_name);
        status.setText(full_name + " added Successfully");
        fullName.setText(" ");
    }

    public void sendData(String nameList) throws ArrayIndexOutOfBoundsException{
        try {
            String urlParameters ="names="+nameList+"&performed_at="+date+" "+time+"";

            URL UrlObj = new URL(url);
            connection = (HttpURLConnection)UrlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty( "charset", "utf-8");
            connection.setDoOutput(true);

            OutputStream outputStream =  connection.getOutputStream();
            outputStream.write(urlParameters.getBytes());
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
                } in.close();
                status.setText("List generated and published!!!");
                status.setStyle("-fx-text-fill: green;");

                outputStream.flush();
            } else {
                status.setText("List generation or publishing failed to complete!!!");
            }

        }catch (NullPointerException | MalformedURLException e){
            status.setText("The value is null "+e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void handleFetchList(ActionEvent actionEvent) throws IOException {

        URL urlForGetRequest = new URL(fetchUrl);
        String readLine = null;
        connection = (HttpURLConnection) urlForGetRequest.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();

                while ((readLine = in.readLine()) != null) {
                    response.append(readLine + "\n");
                }
                status.setText("Pull request Successful!");
                in.close();

            JSONParser parser = new JSONParser();
            String s = response.toString();
            try {
                Object obj = parser.parse(s);
                JSONArray array = (JSONArray) obj;
                System.out.println(array);

                JSONObject obj2 = (JSONObject) array.get(0);
                String namesWithoutOpenSqaureBracket = obj2.get("names").toString().replace("[", " ");
                String namesWithClosedSqaureBracket = namesWithoutOpenSqaureBracket.replace("]", " ");
                List<String> list = new ArrayList<String>(Arrays.asList(namesWithClosedSqaureBracket.split(",")));

                ObservableList<String> items = FXCollections.observableArrayList (
                        list);
                listEmployees.setItems(items);
                SQLiteJDBC.insertNames(obj2.get("names").toString());
            } catch (ParseException pe) {

                System.out.println("position: " + pe.getPosition());

             } catch(NullPointerException e){
               status.setText("Empty db field detected!");
             } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            status.setText("Something went wrong!");
        }
    }

    public void handleListGeneration(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String names;
        if(list.isEmpty()){
            status.setText("Please Fetch List from remote source. Click \"Fetch List\" ");


            ResultSet res2 = SQLiteJDBC.selectRecords();
            StringBuffer sb = new StringBuffer();
            Map<String,String> listMap = new HashMap<>();
            while (res2.next()) {
                names = res2.getString("employee");
                String nameWithoutOpenSqaureBracket = names.replace("[", "");
                String nameWithoutCloseSqaureBracket = nameWithoutOpenSqaureBracket.replace("]", "");

                sb.append(nameWithoutCloseSqaureBracket);

                System.out.println("After removing square"+sb);

            }
            System.out.println("After removing square"+sb);
            list.add(sb.toString());
            System.out.println("Added to List "+ list);
            Collections.shuffle(list);
            String storedEmployeeName = employee.get(count - 1);
                if (count > 1 && storedEmployeeName.equals(list.get(0))) {
                    Collections.shuffle(list);
                    sendData(list.toString());
                } else {
                    employee.put(count, list.get(0));
                    sendData(list.toString());
                    count++;
                }
        }
        else{
            try {
                String storedEmployeeName = employee.get(count - 1);
                Collections.shuffle(list);

                if (count > 1 && storedEmployeeName.equals(list.get(0))) {
                    Collections.shuffle(list);
                    sendData(list.toString());
                } else {
                    employee.put(count, list.get(0));
                    sendData(list.toString());
                    count++;
                }
            }catch (IndexOutOfBoundsException e){
                status.setText("The List is empty:-> " + e.getMessage());
            }
        }


    }



    public void handleLogin(ActionEvent actionEvent) {
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setWidth(450);
        loginStage.setHeight(300);
        VBox vbox = new VBox();
        TextField username      = new TextField();
        PasswordField pass  = new PasswordField();
        Button loginButton = new Button();
        Label userLabel = new Label();
        Label passLabel = new Label();

        userLabel.setText("Username");
        passLabel.setText("Password");

//        username
        username.setMinWidth(100.0);
        username.setMinHeight(30.0);
        username.setMaxHeight(30.0);

//        password
        pass.setMinWidth(150.0);
        pass.setMinHeight(30.0);
        pass.setMaxHeight(30.0);

//        button
        loginButton.setText("Submit");
        loginButton.setMinWidth(80);
        loginButton.setMaxWidth(80);
        loginButton.setMinHeight(30);
        loginButton.setMaxHeight(30);


        loginButton.setOnAction(e->{
            String email = username.getText();
            String password = pass.getText();
            try {

                String urlParameters = "email="+email+"&password="+password+"";

                URL UrlObj = new URL(loginUrl);
                connection = (HttpURLConnection) UrlObj.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setDoOutput(true);

                OutputStream outputStream =  connection.getOutputStream();
                outputStream.write(urlParameters.getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder response = new StringBuilder();
                String inputLine;
                if (responseCode == HttpURLConnection.HTTP_OK) { //success
                    while ((inputLine = reader.readLine()) != null) {
                        response.append(inputLine);
                    } reader.close();

                    org.json.JSONObject jobject = new org.json.JSONObject(response.toString());
                    loggedUser  = jobject.get("user").toString();
                    loginCount++;
                    if(loginCount > 1){
                        SQLiteJDBC.selectUserSession(loggedUser);
                    }else{
                        SQLiteJDBC.insertUserSession(loggedUser,true,loginCount);
                    }

                    loginStage.close();
                    login.setVisible(false);
                    adminLoginButton.setVisible(false);
                    status.setText("Login Successful!");
                    loggedInId.setText("Logged in as: "+loggedUser);
                    loggedInId.setVisible(true);
                } else {

                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        });

        vbox.getChildren().addAll(userLabel,username,passLabel,pass,loginButton);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(60,20,10,10));
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setScene(new Scene(vbox));

        loginStage.show();
    }

    public void handleAdminLogin(ActionEvent actionEvent) {
        Stage adminLoginStage = new Stage();
        adminLoginStage.setTitle("Admin Login");
        adminLoginStage.setWidth(450);
        adminLoginStage.setHeight(300);
        VBox vbox = new VBox();
        TextField username      = new TextField();
        PasswordField pass  = new PasswordField();
        Button loginButton = new Button();
        Label userLabel = new Label();
        Label infoLabel = new Label();
        Label passLabel = new Label();

        userLabel.setText("email");
        passLabel.setText("Password");

//        username
        username.setMinWidth(100.0);
        username.setMinHeight(30.0);
        username.setMaxHeight(30.0);

//        password
        pass.setMinWidth(150.0);
        pass.setMinHeight(30.0);
        pass.setMaxHeight(30.0);

//        button
        loginButton.setText("Submit");
        loginButton.setMinWidth(80);
        loginButton.setMaxWidth(80);
        loginButton.setMinHeight(30);
        loginButton.setMaxHeight(30);

        loginButton.setOnAction(e->{

            String email = username.getText();
            String password = pass.getText();
            try {
                if(email != null && password != null) {
                int statusCode = PostRequestHandler.sendRequest(adminLoginUrl,email,password);
                StringBuilder response = PostRequestHandler.handleBuffering(statusCode);

                org.json.JSONObject jobject = new org.json.JSONObject(response.toString());
                loggedUser = jobject.get("user").toString();
                loginCount++;
                if(loginCount > 1){
                   SQLiteJDBC.selectUserSession(loggedUser);
                }else{
                    SQLiteJDBC.insertUserSession(loggedUser,true,loginCount);
                }
                adminLoginStage.close();

                login.setVisible(false);
                adminLoginButton.setVisible(false);
                generateList.setVisible(true);
                loggedInId.setVisible(true);
                addEmployee.setVisible(true);
                fullName.setVisible(true);
                fullNameLabel.setVisible(true);
                status.setText("Login Successful!");
                status.setStyle("-fx-text-fill:green");
                loggedInId.setText("Logged in as: " + loggedUser);
                }
            } catch (IOException ioException) {
                infoLabel.setText(ioException.getMessage());
                System.out.println(ioException.getMessage());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        });

        vbox.getChildren().addAll(userLabel,username,passLabel,pass,loginButton);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(60,20,10,10));
        adminLoginStage.initModality(Modality.APPLICATION_MODAL);
        adminLoginStage.setScene(new Scene(vbox));
        adminLoginStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generateList.setVisible(false);
        addEmployee.setVisible(false);
        fullName.setVisible(false);
        fullNameLabel.setVisible(false);
    }



//    HttpClient client = HttpClient.newHttpClient();
//    HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://case-match-api.azurewebsites.net/api/login")).build();
//            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//            .thenApply(HttpResponse::body)
//                    .thenAccept(System.out::println)
//                    .join();

//    public static String parse(String responseBody){
//        JSONArray data= new JSONArray(responseBody);
//        for(int i =0; i<data.length(); i++){
//            JSONObject dat = data.getJSONObject(i);
//            String names =dat.getString("names");
//            String email =dat.getString("email");
//            System.out.println(email + success);
////            System.out.println(names);
//        }
//        return null;
//    }


}
