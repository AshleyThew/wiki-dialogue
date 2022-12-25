package com.dialouge_extractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.annotation.Nullable;

@Slf4j
public class DialogueExtractorClient extends WebSocketListener {

    private static DialogueExtractorClient instance = new DialogueExtractorClient();

    public static DialogueExtractorClient getInstance() {
        return instance;
    }
    private Gson gson;
    private WebSocket socket;
    private boolean connected = false;

    public DialogueExtractorClient() {
        gson = new GsonBuilder().create();
    }

    public void start(){
        Request request = new Request.Builder().url("ws://protective-fishy-consonant.glitch.me:5656/").build();
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.retryOnConnectionFailure();
        socket = httpClient.newWebSocket(request, this);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        log.info("Connected to websocket.");
        connected = true;
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        log.info("Closed websocket.");
        connected = false;
    }

    public void send(Object o){
        String text = gson.toJson(o);
        socket.send(text);
    }

    public boolean isConnected(){
        return connected;
    }

}
