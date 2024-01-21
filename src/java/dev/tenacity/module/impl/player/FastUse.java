package dev.tenacity.module.impl.player;

import dev.tenacity.module.settings.impl.*;
import dev.tenacity.module.*;
import dev.tenacity.event.impl.player.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.*;

import static dev.tenacity.utils.server.PacketUtils.sendPacketNoEvent;

public class FastUse extends Module {

    private final ModeSetting mode;

    public FastUse() {

        super("FastUse", Category.PLAYER, "fast to use");
        this.mode = new ModeSetting("Mode", "Vanilla", new String[]{"Vanilla", "NCP", "Matrix"});


        this.addSettings(this.mode);
    }

    public void send(final int needWhile) {
        for (int i = 0; i < needWhile; i++) {
            sendPacketNoEvent(new C03PacketPlayer(mc.thePlayer.onGround));
        }
    }

    private boolean isUsingFood() {
        if (mc.thePlayer.getItemInUse() == null) {
            return false;
        }
        Item usingItem = mc.thePlayer.getItemInUse().getItem();
        return mc.thePlayer.isUsingItem() && (usingItem instanceof ItemFood || usingItem instanceof ItemBucketMilk || usingItem instanceof ItemPotion);
    }

    @Override
    public boolean onMotionEvent(final MotionEvent e) {
        final String mode = this.mode.getMode();
        switch (mode) {
            case "Vanilla": {
                if (isUsingFood()) {
                    send(35);
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                }
            }
            break;
            case "NCP": {
                if (isUsingFood() && mc.thePlayer.getItemInUseDuration() == 15) {
                    send(20);
                }
            }
            break;
            case "Matrix": {
                mc.timer.timerSpeed = isUsingFood() ? 0.5f : 1f;
                if (isUsingFood()) {
                    send(0);
                }
            }
            break;
        }
        return false;
    }
}