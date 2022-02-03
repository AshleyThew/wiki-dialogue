package com.wikidialogue;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class WikiDialoguePanel extends PluginPanel {

    private final WidgetInfo[] widgetInfos;
    private final JPanel overallPanel = new JPanel();
    private final JLabel uiLabel = new JLabel("Open a dialogue box.");
    private final Client client;
    private final WikiDialoguePlugin plugin;
    private JButton[] buttons = new JButton[0];
    private Widget lastWidget;

    @Inject
    public WikiDialoguePanel(Client client, WikiDialoguePlugin plugin) {
        super();
        this.client = client;
        this.plugin = plugin;
        this.widgetInfos = new WidgetInfo[]{WidgetInfo.DIALOG_NPC_TEXT, WidgetInfo.DIALOG_PLAYER_TEXT, WidgetInfo.DIALOG_OPTION_OPTIONS, WidgetInfo.DIALOG_SPRITE_TEXT};

        setBorder(new EmptyBorder(6, 6, 6, 6));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        setLayout(new GridLayout(10, 1));

        uiLabel.setForeground(Color.WHITE);
        setVisible(true);


        add(uiLabel);
    }

    private String replace(String dialogue) {
        String username = client.getLocalPlayer().getName();
        dialogue = dialogue.replaceAll(username, plugin.getConfig().usernameToken());
        if (plugin.getConfig().newline()) {
            dialogue.replaceAll("<br>", "\n");
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

    private void setupCopyNormal(Widget widget) {
        removeButtons();
        remove(uiLabel);
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    JButton copy = new JButton("Copy Dialogue");
                    copy.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent mouseEvent) {
                            String myString = replace(widget.getText());
                            StringSelection stringSelection = new StringSelection(myString);
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            clipboard.setContents(stringSelection, null);
                        }
                    });
                    add(copy);
                    buttons = new JButton[]{copy};
                    refresh();
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
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
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    Widget[] dialogueOptions = option.getChildren();
                    String[] optionsText = new String[dialogueOptions.length];
                    buttons = new JButton[dialogueOptions.length];
                    for (int i = 0; i < dialogueOptions.length; i++) {
                        String option = replace(dialogueOptions[i].getText());
                        String info = i == 0 ? "Title" : "Option " + i;
                        JButton copy = new JButton("Copy " + info);
                        copy.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mousePressed(MouseEvent mouseEvent) {
                                StringSelection stringSelection = new StringSelection(option);
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(stringSelection, null);
                            }
                        });
                        if (option.length() > 0) {
                            add(copy);
                        }
                        buttons[i] = copy;
                    }
                    refresh();
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyText() {

    }

    private void copyOption(int option) {

    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        //chatField.setText("" + (i++));
        Widget currentWidget = null;
        WidgetInfo currentWidgetInfo = null;

        for (WidgetInfo widgetInfo : widgetInfos) {
            currentWidget = client.getWidget(widgetInfo);
            if (currentWidget != null) {
                currentWidgetInfo = widgetInfo;
                break;
            }
        }
        if (currentWidget != null) {
            if (currentWidget != lastWidget) {
                lastWidget = currentWidget;
                Widget finalCurrentWidget = currentWidget;
                switch (currentWidgetInfo) {
                    case DIALOG_NPC_TEXT:
                    case DIALOG_PLAYER_TEXT:
                    case DIALOG_SPRITE_TEXT:

                        setupCopyNormal(finalCurrentWidget);
                        break;
                    case DIALOG_OPTION_OPTIONS:
                        setupCopyOption(finalCurrentWidget);
                        break;
                }
            }
        } else if (lastWidget != null) {
            removeButtons();
            add(uiLabel);
            refresh();
        }
    }
}
