package dev.tenacity.module.impl.player;

import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.SlowDownEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlowdown extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog",
            "Vanilla", "NCP", "Watchdog", "Hypixel", "OldWatchdog");
    private final BooleanSetting HypConsumeNoslow = new BooleanSetting("HypConsumeNoslow", true);
    private boolean synced;

    public NoSlowdown() {
        super("NoSlowdown", Category.PLAYER, "prevent item slowdown");
        this.addSettings(mode, HypConsumeNoslow);
    }

    @Override
    public void onSlowDownEvent(SlowDownEvent event) {
        event.cancel();
    }

    @Override
    public boolean onMotionEvent(MotionEvent e) {
        switch (mode.getMode()) {
            case "OldWatchdog": {
                if (mc.thePlayer.onGround && mc.thePlayer.isUsingItem() && MovementUtils.isMoving()) {
                    if (e.isPre()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        synced = true;
                    } else {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem < 8 ? mc.thePlayer.inventory.currentItem + 1 : mc.thePlayer.inventory.currentItem - 1));
                        synced = false;
                    }
                }
                if (!synced) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    synced = true;
                }
                break;
            }
            case "NCP": {
                if (MovementUtils.isMoving() && mc.thePlayer.isUsingItem()) {
                    if (e.isPre()) {
                        PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    } else {
                        PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
                    }
                }
                break;
            }
            case "UpdatedNCP": {
                if (mc.thePlayer.isUsingItem() && e.isPre()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
                break;
            }
            case "Hypixeltest": {
                if (mc.thePlayer.onGround && mc.thePlayer.isUsingItem() && MovementUtils.isMoving()) {
                    if (e.isPre()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                    } else {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }
                }
                break;
            }
            case "Hypixel":
        }
        return false;
    }



        @Override
        public void onPacketSendEvent (PacketSendEvent e){
        if (mc.thePlayer == null) {
            return;
        }

        if (this.HypConsumeNoslow.isEnabled() && mc.thePlayer.isEating()
                && MovementUtils.isMoving() && e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            e.cancel();
        }
    }
}