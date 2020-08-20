import Client.AuthList_lite;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class Authentification implements Initializable {

    public TextField name;
    public PasswordField pass;
    public Button auth;

    private static Socket clientSocket;
    private static BufferedReader reader;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static AuthList_lite user;
    private static List<AuthList_lite> users = new LinkedList<AuthList_lite>();
    private static ObjectEncoderOutputStream os;
    private static ObjectDecoderInputStream is;

    private MainController controller = new MainController();

    @Override
    public void initialize(URL location, ResourceBundle resources){
        try {
            clientSocket = new Socket("localhost", 8189);
            users.add(new AuthList_lite("Max", "Pass"));
            reader = new BufferedReader(new InputStreamReader(System.in));
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            os = new ObjectEncoderOutputStream(clientSocket.getOutputStream());
            is = new ObjectDecoderInputStream(clientSocket.getInputStream());
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendCommand(javafx.event.ActionEvent actionEvent) throws IOException {
        String s1 = name.getText();
        String p = pass.getText();
        for (AuthList_lite list : users) {
            String s = list.getName();
            if (s.equalsIgnoreCase(s1)) {
                System.out.println("Введите пароль:");
                String p1 = list.getPass();
                if (p.equalsIgnoreCase(p1)) {
                    System.out.println("Поздравляем, вы успешно залогинились");
                    user = list;
                    os.writeObject(list);
                    try {
                        Stage stage = new Stage();
                        controller.start(stage, clientSocket, user);
                    } catch (Exception e){
                        System.out.println("ОШИБКА ПРИ СОЗДАНИИ КОНТРОЛЛЕРА");
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Неверно введён пароль");
                }

            } else {
                System.out.println("Такого логина не существует");
            }
        }
    }
}
