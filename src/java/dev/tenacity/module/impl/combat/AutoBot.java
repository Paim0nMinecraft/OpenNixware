package dev.tenacity.module.impl.combat;

import dev.tenacity.commands.impl.FriendCommand;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.player.RotationUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class AutoBot extends Module {

    private EntityLivingBase target;
    private final List<EntityLivingBase> targets = new ArrayList<>();

    private final MultipleBoolSetting targetsSetting = new MultipleBoolSetting("Targets",
            new BooleanSetting("Players", true),
            new BooleanSetting("Animals", false),
            new BooleanSetting("Mobs", false),
            new BooleanSetting("Invisibles", false));

    private final NumberSetting minCPS = new NumberSetting("Min CPS", 10, 20, 1, 1);
    private final NumberSetting maxCPS = new NumberSetting("Max CPS", 10, 20, 1, 1);

    private final NumberSetting reach = new NumberSetting("Reach", 4, 6, 3, 0.1);

    private final TimerUtil attackTimer = new TimerUtil();

    public AutoBot() {
        super("AutoBot", Category.COMBAT, "Automatically finds the nearest player and attempts to kill them");
        this.addSettings(targetsSetting, minCPS, maxCPS, reach);
    }

    @Override
    public boolean onMotionEvent(MotionEvent event) {

        mc.gameSettings.keyBindForward.pressed = target != null && !(mc.thePlayer.getDistanceToEntity(target) <= 20);
        mc.gameSettings.keyBindJump.pressed = mc.thePlayer.isCollidedHorizontally || mc.thePlayer.isInWater();

        if(event.isPre()) {
            sortTargets();
            if(!targets.isEmpty()) {
                target = targets.get(0);
                final float[] rotations = RotationUtils.getRotations(target.posX, target.posY, target.posZ);
                mc.thePlayer.rotationYaw = rotations[0];
                mc.thePlayer.rotationPitch = rotations[1];
            } else {
                target = null;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        target = null;
        mc.gameSettings.keyBindForward.pressed = false;
        targets.clear();
        super.onDisable();
    }

    private void sortTargets() {
        targets.clear();
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (isValid(entity) && mc.thePlayer != entityLivingBase && !FriendCommand.isFriend(entityLivingBase.getName())) {
                    targets.add(entityLivingBase);
                }
            }
        }
        targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
    }

    private boolean isValid(Entity entity) {
        if (entity instanceof EntityPlayer && targetsSetting.getSetting("Players").isEnabled() && !entity.isInvisible())
            return true;

        if (entity instanceof EntityPlayer && targetsSetting.getSetting("Invisibles").isEnabled() && entity.isInvisible())
            return true;

        if (entity instanceof EntityAnimal && targetsSetting.getSetting("Animals").isEnabled())
            return true;

        if (entity instanceof EntityMob && targetsSetting.getSetting("Mobs").isEnabled())
            return true;

        if (entity.isInvisible() && targetsSetting.getSetting("Invisibles").isEnabled())
            return true;

        return false;
    }

}
