package dev.tenacity.module.impl.movement;

import dev.tenacity.NIXWARE;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.player.NoSlowdown;
import dev.tenacity.module.settings.impl.BooleanSetting;

public class Sprint extends Module {

    private final BooleanSetting omniSprint = new BooleanSetting("Omni Sprint", false);

    public Sprint() {
        super("Sprint", Category.MOVEMENT, "Sprints automatically");
        this.addSettings(omniSprint);
    }

    @Override
    public boolean onMotionEvent(MotionEvent event) {
        if (NIXWARE.INSTANCE.getModuleCollection().get(Scaffold.class).isEnabled() && (!Scaffold.sprint.isEnabled() || Scaffold.isDownwards())) {
            mc.gameSettings.keyBindSprint.pressed = false;
            mc.thePlayer.setSprinting(false);
            return false;
        }
        if (omniSprint.isEnabled()) {
            mc.thePlayer.setSprinting(true);
        } else {
            if (mc.thePlayer.isUsingItem()) {
                if (mc.thePlayer.moveForward > 0 && (NIXWARE.INSTANCE.isEnabled(NoSlowdown.class) || !mc.thePlayer.isUsingItem()) && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
                    mc.thePlayer.setSprinting(true);
                }
            } else {
                mc.gameSettings.keyBindSprint.pressed = true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setSprinting(false);
        mc.gameSettings.keyBindSprint.pressed = false;
        super.onDisable();
    }

}
