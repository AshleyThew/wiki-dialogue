package com.dialouge_extractor;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DialogueExtractorPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(DialogueExtractorPlugin.class);
		RuneLite.main(args);
	}
}