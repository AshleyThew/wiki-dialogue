package com.dialouge_extractor;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("wikidialogue")
public interface DialogueExtractorConfig extends Config {
    @ConfigItem(
            keyName = "newline",
            name = "Include Newlines",
            description = "Include new lines in the copied text."
    )
    default boolean newline() {
        return true;
    }



    @ConfigItem(
            keyName = "usernameToken",
            name = "Username Token",
            description = "Replace players name in text with this."
    )
    default String usernameToken() {
        return "<player>";
    }

    @ConfigItem(
            keyName = "websocketConnection",
            name = "Websocket Connection",
            description = "Websocket to send extracts to."
    )
    default String websocketConnection() {
        return "ws://localhost:21902/";
    }
}
