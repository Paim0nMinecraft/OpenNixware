package dev.tenacity.module.impl.render.targethud;

import dev.tenacity.module.impl.combat.KillAura;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.impl.render.targethud.utils.AnimationUtils;
import dev.tenacity.module.impl.render.targethud.utils.StencilUtils;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MoonTargetHUD extends TargetHUD {

    public MoonTargetHUD() {
        super("Moon");
    }


    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {

        /*
        if (getTarget() != null) {
            scale = AnimationUtils.animation((float) scale, (float) 1, (float) (6 * Inferline.deltaTime()));
        } else {
            scale = AnimationUtils.animation((float) scale, (float) 0, (float) (6 * Inferline.deltaTime()));
        }

         */

        if (getTarget() != null) {
            float getMaxHel;
            if (getTarget().getMaxHealth() < 20) {
                getMaxHel = getTarget().getMaxHealth();
            } else {
                getMaxHel = 20;
            }
            GL11.glPushMatrix();
            GL11.glTranslated(x + 50, y + 31, 0);
            GL11.glScaled(1, 1, 0);
            GL11.glTranslated(-(x + 50), -(y + 31), 0);

            // Background
            RoundedUtil.drawRound(x, y, Math.max(39 + (getMaxHel * 3), (39 + csgoFont18.getStringWidth(getTarget().getName()))), 36, 8,
                    new Color(0, 0, 0, 150));

            // Head
            GL11.glPushMatrix();
            GL11.glScalef(1, 1, 1);
            GL11.glTranslatef(((45 * 0.5f * (0))), ((45 * 0.5f * (0))), 0f);
            GL11.glColor4f(1f, 1, 1, 1f);
            StencilUtils.write(false);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glPushMatrix();
            RenderUtil.fastRoundedRect(x + 3, y + 3, x + 33, y + 33, 7F);
            GL11.glPopMatrix();
            GlStateManager.resetColor();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            StencilUtils.erase(true);
            GL11.glPushMatrix();
            RenderUtil.drawHead(getTarget().getLocationSkin(), (int) x + 3, (int) y + 3, 30, 30, 1F);
            GL11.glPopMatrix();
            GlStateManager.resetColor();
            StencilUtils.dispose();
            GL11.glPopMatrix();

            // Name
            csgoFont18.drawString(getTarget().getName(), x + 36, y + 8, new Color(255, 255, 255).getRGB());

            // Health
            double nmsl;
            if ((getTarget().getHealth() - Math.floor(getTarget().getHealth())) >= 0.5) {
                nmsl = 0.5;
            } else {
                nmsl = 0;
            }
            csgoFont12.drawString(Math.floor(getTarget().getHealth()) + nmsl + " HP", x + 36, y + 11 + csgoFont18.getHeight(), new Color(255, 255, 255).getRGB());
            int FPS = Minecraft.getDebugFPS() == 0 ? 1 : Minecraft.getDebugFPS();
            anim = AnimationUtils.moveUD(anim, getTarget().getHealth() * 3, processFPS(FPS, 1000, 0.01F), processFPS(FPS, 1000, 0.008F));
            RoundedUtil.drawGradientRoundLR(x + 36, y + 21 + csgoFont16.getHeight(), getTarget().getHealth() < 20 ? anim : 20 * 3, 5,
                    2.5f, new Color(255,255,255,255),new Color(255,255,255,255));
            GL11.glPopMatrix();
            setWidth(Math.max((26 + (getMaxHel * 3) + csgoFont18.getStringWidth(getTarget().getName()) - 34), 28 + (getMaxHel * 3)));
            setHeight(36);
        }
    }

    public float processFPS(float fps, float defF, float defV) {
        return defV / (fps / defF);
    }
    float anim;
    // Call by shader events

    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        /*
        if (getTarget() != null) {
            scale = AnimationUtils.moveUD((float) scale, (float) 1, 0.00001f, 0.0000001f);
        } else {
            scale = AnimationUtils.moveUD((float) scale, (float) 0, 0.00001f, 0.0000001f);
        }

         */

        if (getTarget() != null) {
          //  float x = getWidth();
          //  float y = getHeight();
            float getMaxHel;
            if (getTarget().getMaxHealth() < 20) {
                getMaxHel = getTarget().getMaxHealth();
            } else {
                getMaxHel = 20;
            }
            GL11.glPushMatrix();
            GL11.glTranslated(x + 50, y + 31, 0);
            GL11.glScaled(1, 1, 1);
            GL11.glTranslated(-(x + 50), -(y + 31), 0);
            // Background
            RoundedUtil.drawRound(x, y, Math.max(39 + (getMaxHel * 3), (39 + csgoFont18.getStringWidth(getTarget().getName()))), 36, 8,
                    new Color(0, 0, 0, 255));
            GL11.glPopMatrix();

            setWidth(120);
            setHeight(120);
        }
    }

    private EntityLivingBase getTarget() {
        if(KillAura.targets.isEmpty()){
           return mc.thePlayer;
        }
        return KillAura.targets.get(0);
    }
}
