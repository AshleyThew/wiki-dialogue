package com.wikidialogue;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class WikiDialoguePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(WikiDialoguePlugin.class);
		RuneLite.main(args);
	}
}