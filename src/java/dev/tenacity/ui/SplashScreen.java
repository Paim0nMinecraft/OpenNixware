package dev.tenacity.ui;

import dev.tenacity.utils.Utils;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@SuppressWarnings("DuplicatedCode")
public class SplashScreen implements Utils {

    public static void continueCount() {
        continueCount(true);
    }

    public static void continueCount(boolean continueCount) {
        drawSplash();
        if (continueCount) {
            count++;
        }
    }

    private static Framebuffer framebuffer;
    private static int count;

    /**
     * Render the splash screen background
     */
    public static void drawSplash() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        // Create the scale factor
        int scaleFactor = sr.getScaleFactor();
        // Bind the width and height to the framebuffer
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);

        // Create the projected image to be rendered
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.color(0, 0, 0, 0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawSplashBackground(sr.getScaledWidth(), sr.getScaledHeight(), 1);

        RenderUtil.resetColor();
        GL11.glEnable(GL11.GL_BLEND);
        RenderUtil.setAlphaLimit(0);

        if (count > 3) {
            count = 0;
        }

        mojang.drawCenteredString("NIXWARE", sr.getScaledWidth() / 2f, (sr.getScaledHeight() - mojang.getHeight()) / 2.5f, -1);
        studio.drawCenteredString("S T U D I O", sr.getScaledWidth() / 2f, (sr.getScaledHeight() - mojang.getHeight()) / 1.75f, -1);

        // Unbind the width and height as it's no longer needed
        framebuffer.unbindFramebuffer();

        // Render the previously used frame buffer
        framebuffer.framebufferRender(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor);

        // Update the texture to enable alpha drawing
        RenderUtil.setAlphaLimit(1);

        // Update the users screen
        Minecraft.getMinecraft().updateDisplay();
    }

    private static Animation progressAnim;
    private static Animation fadeAnim;

    private static final AbstractFontRenderer mojang = new CustomFont(FontUtil.getFontData(new ResourceLocation("nixware/Fonts/jellomedium.ttf"), 80));
    private static final AbstractFontRenderer studio = new CustomFont(FontUtil.getFontData(new ResourceLocation("nixware/Fonts/jelloregular.ttf"), 30));

    private static void drawScreen(float width, float height) {
        Gui.drawRect2(0, 0, width, height, Color.BLACK.getRGB());
        drawSplashBackground(width, height, 1);
        mojang.drawCenteredString("NIXWARE", width / 2f, (height - mojang.getHeight()) / 2.5f, -1);
        studio.drawCenteredString("S T U D I O", width / 2f, (height - mojang.getHeight()) / 1.75f, -1);
        float rectWidth = mojang.getStringWidth("NIXWARE") + 110;
        float rectHeight = 5;
        float roundX = (width / 2f - rectWidth / 2f);
        float roundY = height / 2f - rectHeight / 2f + 40;
        if (progressAnim.timerUtil.getTime() >= 1800 && fadeAnim == null) fadeAnim = new DecelerateAnimation(600, 1);
        float reduceAlpha = fadeAnim == null ? 0f : fadeAnim.getOutput().floatValue();
        float progress = progressAnim.getOutput().floatValue();
        RoundedUtil.drawRoundOutline(roundX - 2, roundY - 2, rectWidth + 4, rectHeight + 4, (rectHeight / 2f) - .25f, 0.5f, new Color(0, 0, 0, 0), new Color(255, 255, 255, (int) ((1f - reduceAlpha) * 255f)));
        RoundedUtil.drawRound(roundX, roundY, rectWidth * progress, rectHeight, (rectHeight / 2f) - .25f, new Color(255, 255, 255, (int) ((1f - reduceAlpha) * 255f)));
    }

    public static void drawScreen() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        // Create the scale factor
        int scaleFactor = sr.getScaleFactor();
        // Bind the width and height to the framebuffer
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        progressAnim = new DecelerateAnimation(2400, 1);
        while (!progressAnim.isDone()) {
            framebuffer.framebufferClear();
            framebuffer.bindFramebuffer(true);
            // Create the projected image to be rendered
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();


            GlStateManager.color(0, 0, 0, 0);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            drawScreen(sr.getScaledWidth(), sr.getScaledHeight());

            // Unbind the width and height as it's no longer needed
            framebuffer.unbindFramebuffer();

            // Render the previously used frame buffer
            framebuffer.framebufferRender(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor);

            // Update the texture to enable alpha drawing
            RenderUtil.setAlphaLimit(1);

            // Update the users screen
            Minecraft.getMinecraft().updateDisplay();
        }
    }


    public static void drawSplashBackground(float width, float height, float alpha) {
        RenderUtil.resetColor();
        GlStateManager.color(1, 1, 1, alpha);
        Gui.drawRect(0, 0, width, height, new Color(20, 20, 20).getRGB());
    }
//    public static void drawSplashBackground(float width, float height, float alpha) {
//        RenderUtil.resetColor();
//        GlStateManager.color(1, 1, 1, alpha);
//        mc.getTextureManager().bindTexture(new ResourceLocation("nixware/splashscreen.png"));
//        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);
//    }
}
