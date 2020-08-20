import io.netty.handler.codec.serialization.*;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class Client_IO implements Initializable {

    public ListView<String> ClientList;
    public ListView<String> ServerList;
    public ListView<String> logpane;
    public ListView<String> Time;
    public TextField textArea;
    public Button send;
    public Button toClient;
    public Button toServer;
    public Button Refresh;
    public Path path = Paths.get(String.valueOf(Common.path));

    private static Socket clientSocket;
    private static AuthList_lite user;
    private static List<AuthList_lite> users = new LinkedList<AuthList_lite>();
    private static ObjectEncoderOutputStream os;
    private static ObjectDecoderInputStream is;


    public static void main(String[] args) throws Exception {
      /*
                    if (msg.startsWith("/delete")) {
                        os.writeObject(msg);
                        System.out.println("Удалено");
                    }
                    if (msg.equalsIgnoreCase("/help") || msg.equalsIgnoreCase("/h")) {
                        System.out.println("/download [filename] - загрузить файл с сервера|");
                        System.out.println("/upload [filename] - загрузить файл на сервер  |");
                        System.out.println("/clear all - удалить все файлы с сервера       |");

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
        Time.getItems().add(date.toString());
        logpane.refresh();
        Time.refresh();
    }

    public void refreshAll() throws Exception{
        String msg = "/refresh";
        os.writeObject(msg);
        os.flush();

        ClientList.getItems().clear();
        File dir = new File(path.toFile().getAbsolutePath());
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
        File dir = new File(path.toFile().getAbsolutePath());
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

        Path loadfile = Paths.get(path.toFile().getAbsolutePath() + "/" + arr[1]);
        FileInputStream input = new FileInputStream(loadfile.toFile());
        byte[] bytes = new byte[input.available()];
        input.read(bytes, 0, bytes.length);
        FielMessage file = new FielMessage();
        file.setFileName(loadfile.getFileName().toString());
        file.setFileByte(bytes);
        os.writeObject(file);
        os.flush();
        logpaneAdd("Файл загружен на сервер");
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
                Path loadfile = Paths.get(path.toFile().getAbsolutePath() + "/" + arr[1]);
                        try {
                            FielMessage file = null;
                            System.out.println("начинаем читать объект");
                            file = (FielMessage) is.readObject();
                            System.out.println("Прочитали объекта");
                            Files.createFile(loadfile);
                            System.out.println("Создали объект");
                            byte[] bytes = file.getFileByte();
                            Files.write(loadfile, bytes, StandardOpenOption.APPEND);
                            logpaneAdd("Готово");
                            refreshAll();
                        }catch (Exception e){
                            e.printStackTrace();
                            logpaneAdd("Ошибка в потоке");
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

            clientSocket = Common.socket;
            os = new ObjectEncoderOutputStream(clientSocket.getOutputStream());
            is = new ObjectDecoderInputStream(clientSocket.getInputStream());
            user = Common.user;
            logpaneAdd("Имя присвоено. Имя: " + user.getName());
            File dir = new File(path.toFile().getAbsolutePath());
            if(!dir.exists()){
                dir.mkdir();
                logpaneAdd("Библиотека пользователя: " + path.toFile().getAbsolutePath() + " \nсоздана");
            }


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
        File dir = new File(path.toFile().getAbsolutePath());
        for (String file : Objects.requireNonNull(dir.list())) {
            ClientList.getItems().add(file);

        }
    }
}




