package dev.tenacity.utils.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class ChatUtils {
    protected static Minecraft mc;

    static {
        mc = Minecraft.getMinecraft();
    }
    public static void Warning(final String msg) {
        final EntityPlayerSP thePlayer = mc.thePlayer;
        final String format = "%s%s";
        final Object[] args = new Object[2];
        final int n = 0;
        String append = EnumChatFormatting.RED +
                "Warning" + EnumChatFormatting.GRAY + ": ";
        args[0] = append;
        args[1] = msg;
        thePlayer.addChatMessage(new ChatComponentText(String.format("%s%s", args)));
    }
    public static void sendMessage(final String msg) {
        final EntityPlayerSP thePlayer = mc.thePlayer;
        final String format = "%s%s";
        final Object[] args = new Object[2];
        final int n = 0;
        String append = EnumChatFormatting.WHITE +
                "nixware  " + EnumChatFormatting.GRAY + "： ";
        args[0] = append;
        args[1] = msg;
        thePlayer.addChatMessage(new ChatComponentText(String.format("%s%s", args)));
    }

    public static void sendMessage(float timerSpeed) {
        final EntityPlayerSP thePlayer = mc.thePlayer;
        final String format = "%s%s";
        final Object[] args = new Object[2];
        final int n = 0;
        String append = EnumChatFormatting.WHITE +
                "nixware  " + EnumChatFormatting.GRAY + "： ";
        args[0] = append;
        args[1] = timerSpeed;
        thePlayer.addChatMessage(new ChatComponentText(String.format("%s%s", args)));
    }

    public static void sendMessage(double motionY) {
        final EntityPlayerSP thePlayer = mc.thePlayer;
        final String format = "%s%s";
        final Object[] args = new Object[2];
        final int n = 0;
        String append = EnumChatFormatting.WHITE +
                "nixware  " + EnumChatFormatting.GRAY + "： ";
        args[0] = append;
        args[1] = motionY;
        thePlayer.addChatMessage(new ChatComponentText(String.format("%s%s", args)));
    }

    public static void sendMessage(List<EntityPlayer> bots) {
        final EntityPlayerSP thePlayer = mc.thePlayer;
        final String format = "%s%s";
        final Object[] args = new Object[2];
        final int n = 0;
        String append = EnumChatFormatting.WHITE +
                "nixware  " + EnumChatFormatting.GRAY + "： ";
        args[0] = append;
        args[1] = bots;
        thePlayer.addChatMessage(new ChatComponentText(String.format("%s%s", args)));
    }
}
