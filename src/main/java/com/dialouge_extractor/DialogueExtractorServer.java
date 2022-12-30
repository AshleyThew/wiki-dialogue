package com.dialouge_extractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;

@Slf4j
@Singleton
public class DialogueExtractorServer extends WebSocketServer {

    private Gson gson;

    @Inject
    public DialogueExtractorServer(Gson gson, DialogueExtractorConfig config) {
        super(new InetSocketAddress("localhost", config.websocketPort()));
        this.gson = gson;
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
