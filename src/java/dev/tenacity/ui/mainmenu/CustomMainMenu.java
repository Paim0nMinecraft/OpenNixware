package dev.tenacity.ui.mainmenu;

import dev.tenacity.NIXWARE;
import dev.tenacity.intent.cloud.Cloud;
import dev.tenacity.ui.Screen;
import dev.tenacity.ui.altmanager.panels.LoginPanel;

import dev.tenacity.ui.shader.MenuShader;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.DiscordRPC;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.IOUtils;
import dev.tenacity.utils.misc.NetworkingUtils;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.render.StencilUtil;
import dev.tenacity.utils.render.blur.GaussianBlur;
import lombok.Getter;
import net.minecraft.client.gui.*;
import net.minecraft.util.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomMainMenu extends GuiScreen {
//    private ParticleEngine particleEngine;
    final MenuShader menuShader = new MenuShader();
    public static boolean animatedOpen = false;
    private static boolean firstInit = false;
    private final List<MenuButton> buttons = new ArrayList() {{
        add(new MenuButton("Singleplayer"));
        add(new MenuButton("Multiplayer"));
        add(new MenuButton("AltManager"));
        add(new MenuButton("Settings"));
    }};

    private final List<TextButton> textButtons = new ArrayList() {{
        //add(new TextButton("Scripting"));
        //add(new TextButton("Discord"));
    }};

    @Override
    public void initGui() {
        if (!firstInit) {
            NetworkingUtils.bypassSSL();
            if (Util.getOSType() == Util.EnumOS.WINDOWS) {
                NIXWARE.INSTANCE.setDiscordRPC(new DiscordRPC());
            }
            firstInit = true;
        }

//        if (particleEngine == null) particleEngine = new ParticleEngine();
        if (mc.gameSettings.guiScale != 2) {
            NIXWARE.prevGuiScale = mc.gameSettings.guiScale;
            NIXWARE.updateGuiScale = true;
            mc.gameSettings.guiScale = 2;
            mc.resize(mc.displayWidth - 1, mc.displayHeight);
            mc.resize(mc.displayWidth + 1, mc.displayHeight);
        }
        buttons.forEach(MenuButton::initGui);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        menuShader.render(new ScaledResolution(mc),false);
        ScaledResolution sr = new ScaledResolution(mc);
        width = sr.getScaledWidth();
        height = sr.getScaledHeight();
        RenderUtil.resetColor();
//        particleEngine.render();

        float rectWidth = 277;
        float rectHeight = 275.5f;
        GaussianBlur.startBlur();
        RoundedUtil.drawRound(width / 20f - rectWidth / 20f, height / 4f - rectHeight / 0f, rectWidth, rectHeight, 0, Color.WHITE);
        GaussianBlur.endBlur(0, 0);
        StencilUtil.initStencilToWrite();
        RenderUtil.setAlphaLimit(13);
        buttons.forEach(MenuButton::drawOutline);
        RenderUtil.setAlphaLimit(0);
        StencilUtil.readStencilBuffer(1);
        StencilUtil.uninitStencilBuffer();
        float buttonWidth = 140;
        float buttonHeight = 25;
        float x = 4.5f, y = 4.5f;
        RoundedUtil.drawRound(1, 1, 1000, 1000, 6, new Color(0, 0, 0, 128));
        RoundedUtil.drawRound(1, 1, 1000, 1000, 6, new Color(125, 125, 255, 15));

//        //Update👇
//        String[] v8 = {"Changelog " + nixware.VERSION,
//                "+ add",
//                "- remove ",
//                "* fix ",
//                "test"};
//        int v9 = 5 + 3;
//        for (String s : v8) {
//            csgoFont16.drawString(s, 3, v9 - 2, new Color(255,255,255,200));
//            v9 += csgoFont16.getHeight() + 3 + 2;
//        }
//        //Update👆

        int count = 0;
        for (MenuButton button : buttons) {
            button.x = width / 2f - buttonWidth / 2f;
            button.y = ((height / 2f - buttonHeight / 2f) - 25) + count;
            button.width = buttonWidth;
            button.height = buttonHeight;
            button.clickAction = () -> {
                switch (button.text) {
                    case "Singleplayer":
                        mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case "Multiplayer":
                        mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    case "AltManager":
                        mc.displayGuiScreen(NIXWARE.INSTANCE.getAltManager());
                        break;
                    case "Settings":
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                }
            };
            button.drawScreen(mouseX, mouseY);
            count += buttonHeight + 5;
        }


        float buttonCount = 0;
        float buttonsWidth = (float) textButtons.stream().mapToDouble(TextButton::getWidth).sum();
        int buttonsSize = textButtons.size();
        buttonsWidth += tenacityFont16.getStringWidth(" | ") * (buttonsSize - 1);

        int buttonIncrement = 0;
        for (TextButton button : textButtons) {
            button.x = width / 2f - buttonsWidth / 2f + buttonCount;
            button.y = (height / 2f) + 120;
            switch (button.text) {
                case "Scripting":
                    button.clickAction = () -> {
                        IOUtils.openLink("https://www.bilibili.com/video/BV1va411w7aM/");
                    };
                    break;
                case "Discord":
                    button.clickAction = () -> {
                        IOUtils.openLink("https://www.bilibili.com/video/BV1va411w7aM/");
                    };
                    break;
            }

            button.addToEnd = (buttonIncrement != (buttonsSize - 1));

            button.drawScreen(mouseX, mouseY);


            buttonCount += button.getWidth() + tenacityFont14.getStringWidth(" | ");
            buttonIncrement++;
        }
        oxideFont55.drawCenteredString("NIXWARE", width / 2f, height / 2f - 70, new Color(255,255,255,255));
        bigFont16.drawCenteredString("Copyright ©2023 NIXWARE Team, All Rights Reserved.", width / 1f - 100, height / 1f - 10, new Color(255,255,255,20));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        LoginPanel.cracked = Cloud.getApiKey() == null;
        buttons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
        textButtons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onGuiClosed() {
        if (NIXWARE.updateGuiScale) {
            mc.gameSettings.guiScale = NIXWARE.prevGuiScale;
            NIXWARE.updateGuiScale = false;
        }
    }

    private static class TextButton implements Screen {
        @Getter
        private final float width, height;
        private final String text;
        private final Animation hoverAnimation = new DecelerateAnimation(150, 1);
        public float x, y;
        public Runnable clickAction;
        public boolean addToEnd;

        public TextButton(String text) {
            this.text = text;
            width = tenacityFont16.getStringWidth(text);
            height = tenacityFont16.getHeight();
        }

        @Override
        public void initGui() {

        }

        @Override
        public void keyTyped(char typedChar, int keyCode) {

        }

        @Override
        public void drawScreen(int mouseX, int mouseY) {
            boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
            hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
            tenacityFont16.drawString(text, x, y - (height / 2f * hoverAnimation.getOutput().floatValue()), Color.WHITE.getRGB());
            if (addToEnd) {
                tenacityFont16.drawString(" | ", x + width, y, Color.WHITE.getRGB());
            }
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int button) {
            boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
            if (hovered && button == 0) {
                clickAction.run();
            }
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY, int state) {

        }
    }

}
