package com.dialouge_extractor;

import net.runelite.api.widgets.WidgetID;

public enum CustomWidgetInfo {
    DIALOG_NPC_TEXT(WidgetID.DIALOG_NPC_GROUP_ID, 6),
    DIALOG_PLAYER_TEXT(WidgetID.DIALOG_PLAYER_GROUP_ID, 6),
    DIALOG_OPTION_OPTIONS(WidgetID.DIALOG_OPTION_GROUP_ID, 1),
    DIALOG_SPRITE_TEXT(WidgetID.DIALOG_SPRITE_GROUP_ID, 2),
    DIALOG_SPRITE_DOUBLE_TEXT(11, 2),
    DIALOG_CHATBOX_MESSAGE(229, 1),
    ;

    final int groupId, childId;


    CustomWidgetInfo(int groupId, int childId) {
        this.groupId = groupId;
        this.childId = childId;
    }
}
