package com.wikidialogue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WikiDialogueDialogueServer extends WebSocketServer {

    private static WikiDialogueDialogueServer instance = new WikiDialogueDialogueServer();

    public static WikiDialogueDialogueServer getInstance() {
        return instance;
    }
    private static Gson gson;

    public WikiDialogueDialogueServer() {
        super(new InetSocketAddress("localhost", 21902));
        gson = new GsonBuilder().create();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        log.info(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected to websocket.");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "join");
        jsonObject.addProperty("message", "Welcome.");
        conn.send(gson.toJson(jsonObject));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        log.info(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " closed websocket.");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {

    }

    public void send(Object o){
        String text = gson.toJson(o);
        broadcast(text);
    }

}
