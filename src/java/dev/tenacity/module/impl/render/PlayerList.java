package dev.tenacity.module.impl.render;

import dev.tenacity.NIXWARE;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.objects.Dragging;
import dev.tenacity.utils.render.GradientUtil;
import dev.tenacity.utils.render.RenderUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerList extends Module {

    private final Dragging pos = NIXWARE.INSTANCE.createDrag(this, "playerList", 4, 30);

    public PlayerList() {
        super("PlayerList", Category.RENDER, "Displays a list of players in your world");
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        List<EntityPlayer> players = mc.theWorld.playerEntities.stream().filter(p -> p != null && !p.isDead).collect(Collectors.toList());

        float height = 35 + (players.size() - 1) * (csgoFont16.getHeight() + 8);
        float width = 175;
        float x = pos.getX(), y = pos.getY();
        pos.setWidth(width - 6);
        pos.setHeight(height);

        GlStateManager.color(1, 1, 1, 1);

        Gui.drawRect2(x, y, width - 6, height, new Color(59, 57, 57, 255).getRGB());
        GradientUtil.drawGradientLR(x + 0.5f, y + 1.0f, 168, 1.5f, 1, Color.CYAN, Color.magenta);
        Gui.drawRect2(x + 4, y + 17, width - 14, 0.5, Color.GRAY.getRGB());

        csgoFont20.drawString("Player List", x + 4, y + 5, -1, true);

        csgoFont16.drawString(String.valueOf(players.size() + 1),
                x + width - csgoFont16.getStringWidth(String.valueOf(players.size() + 1)) - 10,
                y + 6, -1, true
        );

        y += 18;

        for (int i = 0; i < players.size(); i++) {
            EntityPlayer player = players.get(i);
            renderPlayer(player, i, x, y);
        }
    }

    private void renderPlayer(EntityPlayer player, int i, float x, float y) {
        float height = csgoFont16.getHeight() + 8;
        float offset = i * (height);
        float healthPercent = MathHelper.clamp_float((player.getHealth() + player.getAbsorptionAmount()) / (player.getMaxHealth() + player.getAbsorptionAmount()), 0, 1);
        Color healthColor = healthPercent > .75 ? new Color(66, 246, 123) : healthPercent > .5 ? new Color(228, 255, 105) : healthPercent > .35 ? new Color(236, 100, 64) : new Color(255, 65, 68);
        String healthText = (int) MathUtils.round(healthPercent * 100, .01) + "%";
        csgoFont16.drawStringWithShadow("§f§l" + player.getName() + "§r " + healthText, x + 18, y + offset + csgoFont16.getMiddleOfBox(height), healthColor);

        float headX = x + 4;
        float headWH = 32;
        float headY = y + offset + height / 2f - 6;
        float f = 0.35F;
        RenderUtil.resetColor();
        RenderUtil.scaleStart(headX, headY, f);
        mc.getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
        Gui.drawTexturedModalRect(headX, headY, (int) headWH, (int) headWH, (int) headWH, (int) headWH);
        RenderUtil.scaleEnd();

        if (player == mc.thePlayer) {
            tenacityFont18.drawStringWithShadow("*", x + 159 - tenacityFont18.getStringWidth("*"), y + offset + 6.75F, HUDMod.getClientColors().getFirst().getRGB());
        }


    }

}
