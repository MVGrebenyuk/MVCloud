package Client;

import Client.AuthList_lite;
import Handler.FielMessage;
import io.netty.handler.codec.serialization.*;
import io.netty.util.collection.ByteObjectHashMap;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class Client_IO implements Initializable {

    public ListView<String> ClientList;
    public ListView<String> ServerList;
    public ListView<String> logpane;
    public TextField textArea;
    public Button send;
    public Button toClient;
    public Button toServer;
    public Button Refresh;

    private static Socket clientSocket;
    private static BufferedReader reader;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static AuthList_lite user;
    private static List<AuthList_lite> users = new LinkedList<AuthList_lite>();
    private static ObjectEncoderOutputStream os;
    private static ObjectDecoderInputStream is;


    public static void main(String[] args) throws Exception {
       /* try {
            try { */

       /* clientSocket = new Socket("localhost", 8189);
        users.add(new AuthList_lite("Max", "Pass"));
        reader = new BufferedReader(new InputStreamReader(System.in));
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());
        os = new ObjectEncoderOutputStream(clientSocket.getOutputStream());
        is = new ObjectDecoderInputStream(clientSocket.getInputStream());
        users.add(new AuthList_lite("Max", "Pass")); */

        /*MainController controller = new MainController();
        controller.start(new Stage());*/

             /*   clientSocket = new Socket("localhost", 8189);
                users.add(new AuthList_lite("Max", "Pass"));
                reader = new BufferedReader(new InputStreamReader(System.in));
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                os = new ObjectEncoderOutputStream(clientSocket.getOutputStream());
                is = new ObjectDecoderInputStream(clientSocket.getInputStream());
                auth();

               while (true) {
                    System.out.println("Пожалуйста, введите команду:");
                    String msg = reader.readLine();
                    if (msg.startsWith("/download")) {
                        String[] arr = msg.split(" ");
                        try {
                            if (arr[1] != null) {
                                os.writeObject(msg);
                            }
                            Path loadfile = Paths.get("src/main/resources/" + user.getName() + "/" + arr[1]);
                            try {
                                FielMessage file = null;
                                System.out.println("начинаем читать объект");
                                file = (FielMessage) is.readObject();
                                System.out.println("Прочитали объекта");
                                Files.createFile(loadfile);
                                System.out.println("Создали объект");
                                byte[] bytes = file.getFileByte();
                                Files.write(loadfile, bytes, StandardOpenOption.APPEND);
                                System.out.println("Готово");

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            msg = "/help";
                        }
                    }
                    if (msg.equalsIgnoreCase("/upload")) {
                        String[] arr = msg.split(" ");
                        if (arr[1] != null) {
                            os.writeObject(msg);
                        }
                        Path loadfile = Paths.get("src/main/resources/" + user.getName() + "/" + arr[1]);
                        FileInputStream input = new FileInputStream(loadfile.toFile());
                        byte[] bytes = new byte[input.available()];
                        input.read(bytes, 0, bytes.length);
                        FielMessage file = new FielMessage();
                        file.setFileName(loadfile.getFileName().toString());
                        file.setFileByte(bytes);
                        os.writeObject(file);
                        os.flush();
                    }
                    if (msg.startsWith("/delete")) {
                        os.writeObject(msg);
                        System.out.println("Удалено");
                    }
                    if (msg.equalsIgnoreCase("/help") || msg.equalsIgnoreCase("/h")) {
                        System.out.println("/download [filename] - загрузить файл с сервера|");
                        System.out.println("/upload [filename] - загрузить файл на сервер  |");
                        System.out.println("/clear all - удалить все файлы с сервера       |");

                    }

                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } finally {
            System.out.println("Клиент был закрыт...");
            clientSocket.close();
            in.close();
            out.close();
        }
    } */

    }

    public void logpaneAdd(String msg){
        Date date = new Date();
        logpane.getItems().add(msg);
        logpane.getItems().add(date.toString());
        logpane.refresh();
    }

    public void refreshAll() throws Exception{
        String msg = "/refresh";
        os.writeObject(msg);
        os.flush();

        ClientList.getItems().clear();
        File dir = new File("src/main/resources/max");
        for (String file : Objects.requireNonNull(dir.list())) {
            ClientList.getItems().add(file);

        }
        ClientList.refresh();

        ServerList.getItems().clear();
        LinkedList<String> list;
        list = (LinkedList<String>) is.readObject();
        for(String file: list){
            ServerList.getItems().add(file);
        }
    }

    public void refresh (javafx.event.ActionEvent actionEvent) throws Exception{
        String msg = "/refresh";
        os.writeObject(msg);
        os.flush();

        ClientList.getItems().clear();
        File dir = new File("src/main/resources/max");
        for (String file : Objects.requireNonNull(dir.list())) {
            ClientList.getItems().add(file);

        }
        ClientList.refresh();

        ServerList.getItems().clear();
        LinkedList<String> list;
        list = (LinkedList<String>) is.readObject();
        for(String file: list){
            ServerList.getItems().add(file);
        }
    }

    public void toServer(javafx.event.ActionEvent actionEvent){
        try {
        MultipleSelectionModel<String> ms = ClientList.getSelectionModel();
        String msg = "/upload " + ms.getSelectedItem();
        String[] arr = msg.split(" ");
            if (arr[1] != null) {
                os.writeObject(msg);
            }

        Path loadfile = Paths.get("src/main/resources/" + "max" + "/" + arr[1]);
        FileInputStream input = new FileInputStream(loadfile.toFile());
        byte[] bytes = new byte[input.available()];
        input.read(bytes, 0, bytes.length);
        FielMessage file = new FielMessage();
        file.setFileName(loadfile.getFileName().toString());
        file.setFileByte(bytes);
        os.writeObject(file);
        os.flush();
        refreshAll();
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка, не удалось загрузить файл на сервер");
        }
    }

    public void toClient (javafx.event.ActionEvent actionEvent) throws IOException {
        MultipleSelectionModel<String> ms = ServerList.getSelectionModel();
        String msg = "/download " + ms.getSelectedItem();
        if (msg.startsWith("/download")) {
            String[] arr = msg.split(" ");
            try {
                if (arr[1] != null) {
                    os.writeObject(msg);
                }
                Path loadfile = Paths.get("src/main/resources/" + "max"/*user.getname()*/ + "/" + arr[1]);
                        try {
                            FielMessage file = null;
                            System.out.println("начинаем читать объект");
                            file = (FielMessage) is.readObject();
                            System.out.println("Прочитали объекта");
                            Files.createFile(loadfile);
                            System.out.println("Создали объект");
                            byte[] bytes = file.getFileByte();
                            Files.write(loadfile, bytes, StandardOpenOption.APPEND);
                            System.out.println("Готово");
                            refreshAll();
                        }catch (Exception e){
                            e.printStackTrace();
                            System.out.println("Ошибка в потоке");
                        }



            } catch (Exception e) {
                e.printStackTrace();
                msg = "/help";
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            clientSocket = new Socket("localhost", 8189);
            users.add(new AuthList_lite("Max", "Pass"));
            reader = new BufferedReader(new InputStreamReader(System.in));
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            os = new ObjectEncoderOutputStream(clientSocket.getOutputStream());
            is = new ObjectDecoderInputStream(clientSocket.getInputStream());


        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            LinkedList<String> list;
            list = (LinkedList<String>) is.readObject();
            for(String file: list){
                ServerList.getItems().add(file);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshMethod();
        logpaneAdd("Приветствуем в MVCloud!");

    }

    public void refreshMethod(){
        File dir = new File("src/main/resources/max");
        for (String file : Objects.requireNonNull(dir.list())) {
            ClientList.getItems().add(file);

        }
    }
}




