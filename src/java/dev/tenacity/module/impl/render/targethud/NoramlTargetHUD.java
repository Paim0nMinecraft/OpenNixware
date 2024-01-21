package dev.tenacity.module.impl.render.targethud;

import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.render.StencilUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class NoramlTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();

    public NoramlTargetHUD() {
        super("Noraml");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        setWidth(Math.max(145, FontUtil.bigFont26.getStringWidth(target.getName()) + 40));
        setHeight(37);

        Color c1 = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
        Color c2 = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), alpha);
        Color color = new Color(20, 18, 18, (int) (90 * alpha));
        int textColor = ColorUtil.applyOpacity(-1, alpha);

        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 1, color);
//        RenderUtil.drawImage(new ResourceLocation("nixware/MainMenu/test.png"), x , y, 145, 37);
//line
//        Color color2 = ColorUtil.interpolateColorsBackAndForth(15, 1, c1, c2, false);
//        RoundedUtil.drawRoundOutline(x, y, getWidth(), getHeight(), 1,0, new Color(0, 0, 0,1), color2);
//line
        RoundedUtil.drawRound(x, y, 145, 37, 1, new Color(0,0,0, 128));
        RoundedUtil.drawRound(x, y, 145, 37, 1, new Color(255,255,255, 18));

        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + 3, y + 3, 31, 31, 1, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            renderPlayer2D(x + 3, y + 3, 31, 31, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        } else {
            FontUtil.bigFont32.drawCenteredStringWithShadow("?", x + 20, y + 17 - FontUtil.bigFont32.getHeight() / 2f, textColor);
        }


        FontUtil.bigFont26.drawStringWithShadow(target.getName(), x + 39, y + 5, textColor);

        float healthPercent = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);

        float realHealthWidth = getWidth() - 44;
        float realHealthHeight = 3;
        animation.animate(realHealthWidth * healthPercent, 18);
        Color backgroundHealthColor = new Color(0, 0, 0, ((int) alpha * 110));

        float healthWidth = animation.getOutput();

        RoundedUtil.drawRound(x + 39, (y + getHeight() - 12), 98, realHealthHeight, 1, backgroundHealthColor);
        RoundedUtil.drawGradientHorizontal(x + 39, (y + getHeight() - 12), healthWidth, realHealthHeight, 1, c1, c2);
//        AbstractFontRenderer font = dev.tenacity.utils.font.hanabi.FontUtil.getFromCustomFont(FontUtil.bigFont16);
//        String healthText = (int) MathUtils.round(healthPercent * 100, .01) + "§C❤";
//        font.drawStringWithShadow(healthText, x + 28 + Math.min(Math.max(1, healthWidth), realHealthWidth - 11), y + getHeight() - (14 + FontUtil.tenacityFont16.getHeight()), textColor);
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 1, ColorUtil.applyOpacity(Color.BLACK, alpha));
    }

}
