package com.metype.hidenseek.Utilities;

import com.metype.hidenseek.Handlers.ActionBarInfo;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class StringUtils {

    private static final ArrayList<ActionBarInfo> actionBarInfoList = new ArrayList<>();

    public static String PrettyifySeconds(int secondsNum) {
        String val = "";
        if(secondsNum >= 60) {
            int minuteVal = ((int)Math.floor(secondsNum / 60.0));
            val += minuteVal + " minute" + ((minuteVal > 1)?"s":"");
            if(secondsNum % 60 > 0) {
                val += " and " + (secondsNum%60) + " seconds";
            }
        } else {
            val += (secondsNum%60) + " seconds";
        }
        return val;
    }

    public static void ShowActionBarText(Player player, String text, int priority, int secondsMustBeActive) {
        ActionBarInfo currentInfo = null;
        for(var info : actionBarInfoList) {
            if(info.getPlayer() == player) {
                currentInfo = info;
            }
        }
        boolean shouldShowNewText;
        if(currentInfo != null) {
            shouldShowNewText = false;
            if(currentInfo.isSuperceded(priority) || currentInfo.isExpired()) {
                shouldShowNewText = true;
                actionBarInfoList.remove(currentInfo);
            }
        } else {
            shouldShowNewText = true;
        }
        if(shouldShowNewText) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
            actionBarInfoList.add(new ActionBarInfo(player, secondsMustBeActive, priority));
        }
    }
}
