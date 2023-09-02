package com.dialouge_extractor;

import com.google.gson.Gson;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
        name = "Dialogue Extractor"
)
public class DialogueExtractorPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private EventBus eventBus;

    @Inject
    private DialogueExtractorConfig config;

    @Inject
    private DialogueExtractorPanel dialogueExtractorPanel;

    @Inject
    private KeyManager keyManager;

    @Inject
    private Gson gson;


    @Getter
    private DialogueExtractorServer websocket;

    private NavigationButton navButton;

    @Override
    protected void startUp() throws Exception {
        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "normal.png");

        navButton = NavigationButton.builder()
                .tooltip("Dialogue Extractor")
                .icon(icon)
                .panel(dialogueExtractorPanel)
                .build();

        clientToolbar.addNavigation(navButton);
        eventBus.register(dialogueExtractorPanel);
        websocket = new DialogueExtractorServer(gson, config);
        websocket.start();
        keyManager.registerKeyListener(shiftListener);
        log.debug("Dialogue Extractor started!");
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
        eventBus.unregister(dialogueExtractorPanel);
        keyManager.unregisterKeyListener(shiftListener);
        websocket.stop();
        log.debug("Dialogue Extractor stopped!");
    }

    public Logger getLogger() {
        return log;
    }

    public DialogueExtractorConfig getConfig() {
        return config;
    }

    @Provides
    DialogueExtractorConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DialogueExtractorConfig.class);
    }

    private final KeyListener shiftListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getExtendedKeyCode() == KeyEvent.VK_SHIFT) {
                dialogueExtractorPanel.setShiftPressed(true);
            }
            if (e.getExtendedKeyCode() == KeyEvent.VK_CONTROL) {
                dialogueExtractorPanel.setCtrlPressed(true);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getExtendedKeyCode() == KeyEvent.VK_SHIFT) {
                dialogueExtractorPanel.setShiftPressed(false);
            }
            if (e.getExtendedKeyCode() == KeyEvent.VK_CONTROL) {
                dialogueExtractorPanel.setCtrlPressed(false);
            }
        }
    };

}
