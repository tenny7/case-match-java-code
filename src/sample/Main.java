package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Case Order  v1.0.0.0.1  Developed by Tennyson Onovwiona");
        primaryStage.setScene(new Scene(root, 648, 513));
        primaryStage.setResizable(false);

        primaryStage.show();
//        Controller controller = new Controller();
//        controller.userIcon.setVisible(false);
//        controller.loggedInId.setVisible(false);

//        primaryStage.setOnCloseRequest(event -> {
//            try {
//                SQLiteJDBC.connection.commit();
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//            Platform.exit();
//            System.exit(0);
//        });
    }



    public static void main(String[] args) {
        launch(args);
    }
}
