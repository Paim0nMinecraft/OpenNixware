package dev.tenacity.module.impl.render;

import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.ui.notifications.Notification;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class NotificationsMod extends Module {
    private final NumberSetting time = new NumberSetting("Time on Screen", 2, 10, 1, .5);
    public static final ModeSetting mode = new ModeSetting("Mode", "nixware", "Default", "SuicideX", "nixware", "Exhibition", "Gamesense");
    public static final BooleanSetting onlyTitle = new BooleanSetting("Only Title", false);
    public static final BooleanSetting toggleNotifications = new BooleanSetting("Show Toggle", true);

    public NotificationsMod() {
        super("Notifications", Category.RENDER, "Allows you to customize the client notifications");
        onlyTitle.addParent(mode, modeSetting -> modeSetting.is("Default"));
        this.addSettings(time, mode, onlyTitle, toggleNotifications);
        if (!enabled) this.toggleSilent();
    }

    public void render() {
        float yOffset = 0;
        int notificationHeight = 0;
        int notificationWidth;
        int actualOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);

        NotificationManager.setToggleTime(time.getValue().floatValue());

        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;

            switch (mode.getMode()) {
                case "Default":
                    animation.setDuration(250);
                    actualOffset = 8;
                    if (onlyTitle.isEnabled()) {
                        notificationHeight = 19;
                        notificationWidth = (int) tenacityBoldFont22.getStringWidth(notification.getTitle()) + 35;
                    } else {
                        notificationHeight = 28;
                        notificationWidth = (int) Math.max(tenacityBoldFont22.getStringWidth(notification.getTitle()), tenacityFont18.getStringWidth(notification.getDescription())) + 35;
                    }

                    x = sr.getScaledWidth() - (notificationWidth + 5) * (float) animation.getOutput().floatValue();
                    y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight + (15 * (float) GuiChat.openingAnimation.getOutput().floatValue()));

                    notification.drawDefault(x, y, notificationWidth, notificationHeight, (float) animation.getOutput().floatValue(), onlyTitle.isEnabled());
                    break;
                case "Gamesense":
                    animation.setDuration(1);
                    animation.setDuration(220); //蓝色推出去
                    actualOffset = 3 + 9;
                    notificationHeight = 10;
                    notificationWidth = 189;

                    x = sr.getScaledWidth() - ((sr.getScaledWidth() / 9f + notificationWidth / 2f) * (float) animation.getOutput().floatValue());
                    y = sr.getScaledHeight() / 500 + yOffset + 25;
                    notification.drawsense(-x + 758 , y, notificationWidth, notificationHeight, (float) animation.getOutput().floatValue());

                    animation.setDuration(1);
                    animation.setDuration(1500); //蓝色缩回来
                    actualOffset = 3 + 9;
                    notificationHeight = 10;
                    notificationWidth = 189;

                    x = sr.getScaledWidth() - ((sr.getScaledWidth() / 9f + notificationWidth / 2f) * (float) animation.getOutput().floatValue());
                    y = sr.getScaledHeight() / 500 + yOffset + 25;
                    notification.drawsense(-x + 758 , y, notificationWidth, notificationHeight, (float) animation.getOutput().floatValue());

                    animation.setDuration(1);
                    animation.setDuration(800); //黑色推出去+缩回来
                    actualOffset = 3 + 9;
                    notificationHeight = 10;
                    notificationWidth = 189;

                    x = sr.getScaledWidth() - ((sr.getScaledWidth() / 9f + notificationWidth / 2f) * (float) animation.getOutput().floatValue());
                    y = sr.getScaledHeight() / 500 + yOffset + 25;
                    notification.drawgamesense(-x + 758 , y, notificationWidth, notificationHeight, (float) animation.getOutput().floatValue());
                    break;
                case "nixware":
                    animation.setDuration(200);
                    actualOffset = 8;
                    if (onlyTitle.isEnabled()) {
                        notificationHeight = 19;
                        notificationWidth = (int) bigFont20.getStringWidth(notification.getTitle()) + 30;
                    } else {
                        notificationHeight = 28;
                        notificationWidth = (int) Math.max(bigFont20.getStringWidth(notification.getTitle()), bigFont20.getStringWidth(notification.getDescription())) + 30;
                    }
//                    RoundedUtil.drawRound(x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue(), y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight + (15 * GuiChat.openingAnimation.getOutput().floatValue())), notificationWidth, 25, 0, new Color(0,0,0, 128));
//                    RenderUtil.drawImage(new ResourceLocation("nixware/MainMenu/test.png"), x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue(), y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight + (15 * GuiChat.openingAnimation.getOutput().floatValue())), notificationWidth, 25);
                    x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue();
                    y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight + (15 * GuiChat.openingAnimation.getOutput().floatValue()));
                    RoundedUtil.drawRound(x, y, notificationWidth, notificationHeight, 2, new Color(0, 0, 0, 50));

                    notification.drawnixware(x, y, notificationWidth, notificationHeight, (float) animation.getOutput().floatValue());
                    break;
                case "Exhibition":
                    animation.setDuration(350);
                    actualOffset = 3;
                    notificationHeight = 25;
                    notificationWidth = (int) Math.max(tahomaFont.size(18).getStringWidth(notification.getTitle()), tahomaFont.size(14).getStringWidth(notification.getDescription())) + 30;

                    x = sr.getScaledWidth() - ((sr.getScaledWidth() / 2f + notificationWidth / 2f) * (float) animation.getOutput().floatValue());
                    y = sr.getScaledHeight() / 500 - notificationHeight / 500 + yOffset;

                    notification.drawExhi(x, y, notificationWidth, notificationHeight);
                    break;
            }


            yOffset += (notificationHeight + actualOffset) * animation.getOutput().floatValue();

        }
    }

//blur 👇

    public void renderEffects(boolean glow) {
        float yOffset = 0;
        int notificationHeight = 0;
        int notificationWidth;
        int actualOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);


        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;

            switch (mode.getMode()) {
                case "Default":
                    actualOffset = 8;
                    if (onlyTitle.isEnabled()) {
                        notificationHeight = 19;
                        notificationWidth = (int) tenacityBoldFont22.getStringWidth(notification.getTitle()) + 35;
                    } else {
                        notificationHeight = 28;
                        notificationWidth = (int) Math.max(tenacityBoldFont22.getStringWidth(notification.getTitle()), tenacityFont18.getStringWidth(notification.getDescription())) + 35;
                    }
                    RoundedUtil.drawRound(x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue(), y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight + (15 * GuiChat.openingAnimation.getOutput().floatValue())), notificationWidth, 25, 0, new Color(0,0,0, 128));
                    RenderUtil.drawImage(new ResourceLocation("nixware/MainMenu/test.png"), x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue(), y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight + (15 * GuiChat.openingAnimation.getOutput().floatValue())), notificationWidth, 25);
                    x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue();
                    y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight + (15 * GuiChat.openingAnimation.getOutput().floatValue()));

                    notification.blurDefault(x, y, notificationWidth, notificationHeight, animation.getOutput().floatValue(), glow);
                    break;
                case "nixware":
                    actualOffset = 8;
                    if (onlyTitle.isEnabled()) {
                        notificationHeight = 19;
                        notificationWidth = (int) bigFont20.getStringWidth(notification.getTitle()) + 30;
                    } else {
                        notificationHeight = 28;
                        notificationWidth = (int) Math.max(bigFont20.getStringWidth(notification.getTitle()), bigFont20.getStringWidth(notification.getDescription())) + 30;
                    }

                    x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue();
                    y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight + (15 * GuiChat.openingAnimation.getOutput().floatValue()));
                    RoundedUtil.drawRound(x, y, notificationWidth, notificationHeight, 2, new Color(0, 0, 0, 50));
                    notification.blurnixware(x, y, notificationWidth, notificationHeight, animation.getOutput().floatValue());
                    break;
            }


            yOffset += (notificationHeight + actualOffset) * animation.getOutput().floatValue();

        }
    }


}
