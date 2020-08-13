package Server;

import Client.AuthList_lite;
import Handler.FielMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class ClientHandlerReader extends SimpleChannelInboundHandler<String> {

    private AuthList_lite user;
    private String userName;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("____________________________________");
        System.out.println("Клиент " + ctx.name() + " подключился");
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
            Path userDir = Paths.get("src/main/resources/ServerFiles/" + userName);
            if (Files.notExists(userDir)) {
                Files.createDirectory(userDir);
                System.out.println("Библиотека пользователя создана");
                System.out.println("____________________________________");
            } else {
                System.out.println("Библиотека пользователя найдена");
                System.out.println("____________________________________");
            }
        } else if (msg instanceof String) {
            String message = msg.toString();
            if (message.startsWith("/download")) {
                //_____________________DOWNLOAD_______________________________
                System.out.println("Команда от пользователя " + userName + ": " + message);
                String[] arrFile = message.split(" ");
                Path path = Paths.get("src/main/resources/ServerFiles/" + userName + "/" + arrFile[1]);
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
                Path create = Paths.get("src/main/resources/ServerFiles/" + userName + "/" + arrFile[1]);
                Files.delete(create);
                System.out.println("Файл " + create.getName(5) + " удалён с сервера");
                //-----------------------DELETE------------------------------
            } else if (message.startsWith("/upload")) {
                //-----------------------UPLOAD------------------------------
                System.out.println("Команда от пользователя " + userName + ": " + message);
                String[] arrFile = message.split(" ");
                Path path = Paths.get("src/main/resources/ServerFiles/" + userName + "/" + arrFile[1]);
                FielMessage file = (FielMessage) msg;
                if (file instanceof FielMessage) {
                    Files.createFile(path);
                    Files.write(path, file.getFileByte(), StandardOpenOption.APPEND);
                    System.out.println("Готово!");
                }
                //----------------------UPLOAD---------------------------------
            }
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
