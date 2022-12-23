package com.wikidialogue;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
        name = "Wiki Dialogue"
)
public class WikiDialoguePlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private EventBus eventBus;

    @Inject
    private WikiDialogueConfig config;

    @Inject
    private WikiDialoguePanel wikiDialoguePanel;

    private NavigationButton navButton;

    @Override
    protected void startUp() throws Exception {


        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "normal.png");

        navButton = NavigationButton.builder()
                .tooltip("Wiki Dialogue")
                .icon(icon)
                .panel(wikiDialoguePanel)
                .build();

        clientToolbar.addNavigation(navButton);
        eventBus.register(wikiDialoguePanel);
        WikiDialogueDialogueServer.getInstance().start();
        log.info("Wiki Dialogue started!");
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
        eventBus.unregister(wikiDialoguePanel);
        //overlayManager.remove(wikiDialogueOverlay);
        log.info("Wiki Dialogue stopped!");
    }

    public Logger getLogger() {
        return log;
    }

    public WikiDialogueConfig getConfig() {
        return config;
    }

    @Provides
    WikiDialogueConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(WikiDialogueConfig.class);
    }

}
