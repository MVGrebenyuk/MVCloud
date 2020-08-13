package Client;

import Handler.FielMessage;
import Server.ClientHandlerReader;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.serialization.*;
import io.netty.util.collection.ByteObjectHashMap;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.util.*;

public class Client_IO {

    //private static Node central;
    public ListView<String> ClientList;
    public ListView<String> ServerList;
    public TextField name;
    public PasswordField pass;
    public Button auth;
    public TextField textArea;
    public Button send;
    public Button toClient;
    public Button toServer;
    public Pane pane;
    public static AnchorPane central;
    private static Socket clientSocket;
    private static BufferedReader reader;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static AuthList_lite user;
    private static List<AuthList_lite> users = new LinkedList<AuthList_lite>();
    private static ObjectEncoderOutputStream os;
    private static ObjectDecoderInputStream is;


    public static void main(String[] args) throws IOException {
        try {
            try {

                clientSocket = new Socket("localhost", 8189);
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
    }


    private static void auth() throws IOException {
        BufferedReader readerAuth = new BufferedReader(new InputStreamReader(System.in));
        while (user == null) {
            System.out.println("Введите Ваш логин:");
            String s1 = readerAuth.readLine();
            for (AuthList_lite list : users) {
                String s = list.getName();
                if (s.equalsIgnoreCase(s1)) {
                    System.out.println("Введите пароль:");
                    String p = readerAuth.readLine();
                    String p1 = list.getPass();
                    if (p.equalsIgnoreCase(p1)) {
                        System.out.println("Поздравляем, вы успешно залогинились");
                        user = list;
                        os.writeObject(list);

                    } else {
                        System.out.println("Пароль введён неверно");
                    }

                } else {
                    System.out.println("Такого логина не существует");
                }
            }
        }
    }

   /* public void sendCommand(javafx.event.ActionEvent actionEvent) throws IOException {
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

                } else {
                    System.out.println("Пароль введён неверно");
                }

            } else {
                System.out.println("Такого логина не существует");
            }
        }
    } */

  /*  @Override
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
        } */

       /* File dir = new File("src/main/resources/max");
        for (String file : Objects.requireNonNull(dir.list())) {
            ClientList.getItems().add(file);

        }*/
    }

