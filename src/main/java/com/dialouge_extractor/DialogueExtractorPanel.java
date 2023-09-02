package com.dialouge_extractor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class DialogueExtractorPanel extends PluginPanel {

    private final List<CustomWidgetInfo> messageInfos;
    private final List<CustomWidgetInfo> optionsInfos;
    private final List<CustomWidgetInfo> allInfos;
    private final JLabel uiLabel = new JLabel("Open a dialogue box.");
    private final JLabel serverLabel = new JLabel("Open the dialogue editor.");
    private final Client client;
    private final DialogueExtractorPlugin plugin;
    private JButton[] buttons = new JButton[0];
    private Widget lastWidget;
    private String lastText = "";
    private boolean shiftPressed;
    private boolean ctrlPressed;


    @Inject
    public DialogueExtractorPanel(Client client, DialogueExtractorPlugin plugin) {
        super();
        this.messageInfos = Arrays.asList(CustomWidgetInfo.DIALOG_NPC_TEXT, CustomWidgetInfo.DIALOG_PLAYER_TEXT, CustomWidgetInfo.DIALOG_SPRITE_TEXT, CustomWidgetInfo.DIALOG_SPRITE_DOUBLE_TEXT, CustomWidgetInfo.DIALOG_CHATBOX_MESSAGE);
        this.optionsInfos = Arrays.asList(CustomWidgetInfo.DIALOG_OPTION_OPTIONS);
        List<CustomWidgetInfo> allList = new ArrayList<>();
        allList.addAll(messageInfos);
        allList.addAll(optionsInfos);
        this.allInfos = allList;
        this.client = client;
        this.plugin = plugin;

        setBorder(new EmptyBorder(6, 6, 6, 6));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        setLayout(new GridLayout(10, 1));

        serverLabel.setForeground(Color.WHITE);
        uiLabel.setForeground(Color.WHITE);
        setVisible(true);


        add(serverLabel);
        add(uiLabel);
    }

    private String replace(String dialogue) {
        String username = client.getLocalPlayer().getName();
        dialogue = dialogue.replaceAll(username, plugin.getConfig().usernameToken());
        if (plugin.getConfig().newline()) {
            dialogue = dialogue.replace("<br>", "\n");
        }
        return dialogue;
    }

    private void removeButtons() {
        if (buttons.length > 0) {
            for (JButton button : buttons) {
                remove(button);
            }
            buttons = new JButton[0];
        }
    }

    private void setupCopyNormal(Widget widget, CustomWidgetInfo widgetInfo) {
        removeButtons();
        remove(uiLabel);
        try {
            SwingUtilities.invokeAndWait(() -> {
                String title = "";
                switch (widgetInfo){
                    case DIALOG_PLAYER_TEXT:
                        title = "<player>";
                        break;
                    case DIALOG_NPC_TEXT:
                        title = client.getWidget(WidgetInfo.DIALOG_NPC_NAME).getText();
                        break;
                }

                JButton copy = new JButton("Copy Dialogue");
                String dialogue = replace(widget.getText());
                copy.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent mouseEvent) {
                        StringSelection stringSelection = new StringSelection(dialogue);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);
                    }
                });
                add(copy);
                buttons = new JButton[]{copy};
                refresh();
                if(!shiftPressed) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type", "dialogue");
                    jsonObject.addProperty("title", title);
                    jsonObject.addProperty("dialogue", dialogue);
                    jsonObject.addProperty("link", ctrlPressed);
                    plugin.getWebsocket().send(jsonObject);
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void refresh() {
        revalidate();
        repaint();
    }

    private void setupCopyOption(Widget option) {
        removeButtons();
        remove(uiLabel);
        try {
            SwingUtilities.invokeAndWait(() -> {
                Widget[] dialogueOptions = option.getChildren();
                String[] optionsText = new String[dialogueOptions.length];
                buttons = new JButton[dialogueOptions.length];
                JsonArray jsonArray = new JsonArray();
                for (int i = 0; i < dialogueOptions.length; i++) {
                    String option1 = replace(dialogueOptions[i].getText());
                    String info = i == 0 ? "Title" : "Option " + i;
                    JButton copy = new JButton("Copy " + info);
                    copy.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent mouseEvent) {
                            StringSelection stringSelection = new StringSelection(option1);
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            clipboard.setContents(stringSelection, null);
                        }
                    });
                    if (option1.length() > 0) {
                        add(copy);
                        if(i != 0){
                            jsonArray.add(option1);
                        }
                    }
                    buttons[i] = copy;
                }
                refresh();
                JsonObject jsonObject = new JsonObject();
                if(!shiftPressed) {
                    jsonObject.addProperty("type", "option");
                    jsonObject.add("options", jsonArray);
                    jsonObject.addProperty("link", ctrlPressed);
                    plugin.getWebsocket().send(jsonObject);
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if(plugin.getWebsocket().getConnections().size() > 0){
            serverLabel.setText("Dialogue editors connected: " + plugin.getWebsocket().getConnections().size());
        }else{
            serverLabel.setText("Open the dialogue editor.");
        }

        Widget currentWidget = null;
        CustomWidgetInfo currentWidgetInfo = null;
        String currentText = null;

        for (CustomWidgetInfo widgetInfo : allInfos) {
            currentWidget = client.getWidget(widgetInfo.groupId, widgetInfo.childId);
            if (currentWidget != null) {
                currentWidgetInfo = widgetInfo;
                if(messageInfos.contains(currentWidgetInfo)){
                    currentText = currentWidget.getText();
                }else if(optionsInfos.contains(currentWidgetInfo)){
                    currentText = Arrays.stream(currentWidget.getChildren()).map(Widget::getText).collect(Collectors.joining("\n"));
                }
                break;
            }
        }
        if (currentWidget != null) {
            if (currentWidget != lastWidget || !lastText.equals(currentText)) {
                lastWidget = currentWidget;
                lastText = currentText;
                if(messageInfos.contains(currentWidgetInfo)){
                    setupCopyNormal(currentWidget, currentWidgetInfo);
                }else if(optionsInfos.contains(currentWidgetInfo)){
                    setupCopyOption(currentWidget);
                }
            }
        } else if (lastWidget != null) {
            removeButtons();
            add(uiLabel);
            refresh();
        }
    }

    public void setCtrlPressed(boolean ctrlPressed) {
        this.ctrlPressed = ctrlPressed;
    }

    public void setShiftPressed(boolean shiftPressed) {
        this.shiftPressed = shiftPressed;
    }
}
