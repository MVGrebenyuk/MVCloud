import Client.AuthList_lite;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.Socket;

public class MainController extends Application {

    private Socket clientSocket;
    private AuthList_lite user;

    public MainController(){

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Controller.fxml"));
        primaryStage.setTitle("MVCloud");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void start(Stage primaryStage, Socket socket, AuthList_lite user) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Controller.fxml"));
        primaryStage.setTitle("MVCloud");
        primaryStage.setScene(new Scene(root));
        this.clientSocket = socket;
        this.user = user;
        primaryStage.show();
    }


    public static void main(String[] args) {

        launch(args);

    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public AuthList_lite getUser() {
        return user;
    }
}
