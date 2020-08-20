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
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class Authentification implements Initializable {

    public TextField name;
    public PasswordField pass;
    public Button auth;
    public Stage stage = new Stage();
    public TextField Path;

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

    public void sendCommand(javafx.event.ActionEvent actionEvent) throws Exception {
        String s1 = name.getText();
        String p = pass.getText();
        UserAuth auth = new UserAuth();
        auth.setName(s1); auth.setPass(p);
        os.writeObject(auth);
        os.flush();
        auth = null;
        while(auth == null){
            auth = (UserAuth) is.readObject();
        }
        if(auth != null){
            user = new AuthList_lite(auth.getName(), auth.getPass());
            Common.user = user;
            Common.socket = clientSocket;
            Common.path = Paths.get(Path.getText());
            os.writeObject(user);
            controller.start(stage);

        }
    }
}
