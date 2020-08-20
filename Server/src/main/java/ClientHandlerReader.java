import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.scene.control.TextField;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class ClientHandlerReader extends SimpleChannelInboundHandler<String> {

    private AuthList_lite user;
    private String userName;
    private LinkedList<String> serverList = new LinkedList<>();
    private LinkedList<String> list = new LinkedList<>();
    private Path path;
    private UserAuth userAuth;
    public String centralPath = ServerData.centralPath;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            File file = new File(centralPath + "/UData.txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент отключился");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof AuthList_lite) {
            System.out.println("Объект обнаружен");
            user = (AuthList_lite) msg;
            userName = user.getName();
            Path userDir = Paths.get(centralPath);
            if (Files.notExists(userDir)) {
                Files.createDirectory(userDir);

                System.out.println("Библиотека пользователя создана");
                System.out.println("____________________________________");
            } else {
                System.out.println("Библиотека пользователя найдена");
                System.out.println("____________________________________");
            }
            System.out.println("____________________________________");
            System.out.println("Клиент " + userName + " подключился");
            File dir = new File(centralPath);
            for (String file : Objects.requireNonNull(dir.list())) {
                serverList.add(file);

            }
            ctx.writeAndFlush(serverList);
        } else if (msg instanceof String) {
            String message = msg.toString();
            if (message.startsWith("/download")) {
                //_____________________DOWNLOAD_______________________________
                System.out.println("Команда от пользователя " + userName + ": " + message);
                String[] arrFile = message.split(" ");
                Path path = Paths.get(centralPath + "/" + arrFile[1]);
                FielMessage fileMsg = new FielMessage();
                FileInputStream input = new FileInputStream(path.toFile());
                fileMsg.setFileName(path.getFileName().toString());
                byte[] bytes = new byte[input.available()];
                input.read(bytes, 0, bytes.length);
                input.close();
                fileMsg.setFileByte(bytes);
                System.out.println(Arrays.toString(fileMsg.getFileByte()));
                ctx.writeAndFlush(fileMsg);
                System.out.println("Записано в буффер и отправлено на сервер");
                //_______________________DOWNLOAD_____________________________
            } else if (message.startsWith("/delete")) {
                //-----------------------DELETE-------------------------------
                String[] arrFile = message.split(" ");
                Path create = Paths.get(centralPath + "/" + arrFile[1]);
                Files.delete(create);
                System.out.println("Файл " + create.getName(5) + " удалён с сервера");
                //-----------------------DELETE------------------------------
            } else if (message.startsWith("/upload")) {
                //-----------------------UPLOAD P1------------------------------
                System.out.println("Команда от пользователя " + userName + ": " + message);
                String[] arrFile = message.split(" ");
                path = Paths.get(centralPath + "/" + arrFile[1]);
            } else if(message.startsWith("/refresh")) {
                serverList.clear();
                File dir = new File(centralPath);
                for (String file : Objects.requireNonNull(dir.list())) {
                    serverList.add(file);

                }
                ctx.writeAndFlush(serverList);
            }
                //----------------------UPLOAD P1---------------------------------
            }
                //---------------------UPLOAD P2----------------------------------
        else if (msg instanceof FielMessage) {
            FielMessage file = (FielMessage) msg;
            Files.createFile(path);
            Files.write(path, file.getFileByte(), StandardOpenOption.APPEND);
            System.out.println("Готово! Файл загружен на сервер");
            //------------------------UPLOAD P2-----------------------------------
            //-----------------------AUTHENTIFICATION-----------------------------
        } else if (msg instanceof UserAuth){
            userAuth = (UserAuth) msg;
            for(String all: list){
                String[] namepass = all.split(" ");
                if(namepass[0].equals(userAuth.getName())){
                    if(namepass[1].equals(userAuth.getPass())){
                        System.out.println("Успешная аутентификация");
                        ctx.writeAndFlush(userAuth);
                        break;
                    }
                } else {
                    System.out.println("Пользователь не найден");
                }

            }
            //-----------------------AUTHENTIFICATION-----------------------------

        } else {
            String message = msg.toString();
            System.out.println("Сообщение от пользователя " + userName + ": " + message);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println("Прочитано: " + s);
    }
}
