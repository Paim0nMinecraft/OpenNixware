package dev.tenacity.ui.mainmenu.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import static org.lwjgl.opengl.GL11.*;

public class MenuShader {
    public final ShaderProgram menuShader = new ShaderProgram("fragment/" + "lb.frag");
    protected static final Minecraft mc = Minecraft.getMinecraft();
    private float time;

    private static double delta;

    public static float processFPS(final float defV) {
        final float defF = 1000;
        int limitFPS = Math.abs(mc.getDebugFPS());
        return defV / (limitFPS <= 0 ? 1 : limitFPS / defF);
    }

    public final void render(final ScaledResolution scaledResolution) {
        menuShader.init();
        setupUniforms();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        menuShader.renderCanvas(scaledResolution);
        glDisable(GL_BLEND);
        menuShader.uninit();
        time += mc.getDebugFPS() < 60 ? 0.002 * 10 : 0.002 * 10;
    }

    public void setupUniforms() {
        menuShader.setUniformf("time", time);
        menuShader.setUniformf("resolution", mc.displayWidth, mc.displayHeight);
    }

}
