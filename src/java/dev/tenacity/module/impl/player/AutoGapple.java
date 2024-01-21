package dev.tenacity.module.impl.player;

import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class AutoGapple extends Module {

    private final NumberSetting delay = new NumberSetting("Delay", 750, 3000, 0, 50);
    private final NumberSetting health = new NumberSetting("Health", 10, 20, 1, 1);
    private final BooleanSetting onground = new BooleanSetting("OnGround", false);
    private final BooleanSetting packetlimit = new BooleanSetting("PacketLimit", true);
    private long lastEatTime = 0L;

    public AutoGapple() {
        super("AutoGapple", Category.PLAYER, "auto consume gapple");
        this.addSettings(delay, health, onground);

    }

    @Override
    public boolean onMotionEvent(MotionEvent e) {
        if (mc.thePlayer.getHealth() <= health.getValue()) {
            int foodSlot = findFoodInHotbar();
            if (foodSlot != -1 && System.currentTimeMillis() - lastEatTime > delay.getValue()) {
                int prevSlot = mc.thePlayer.inventory.currentItem;
                mc.thePlayer.inventory.currentItem = foodSlot;
                mc.playerController.updateController();
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                if (mc.thePlayer.onGround && onground.isEnabled()) {
                    for (int i = 0; i < 35; i++) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                    }
                } else {
                    for (int i = 0; i < 35; i++) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                    }
                }
                lastEatTime = System.currentTimeMillis();
                mc.thePlayer.inventory.currentItem = prevSlot;
                mc.playerController.updateController();
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        }
        return false;
    }

    ;

    private int findFoodInHotbar() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemFood) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e) {
        if (packetlimit.isEnabled()) {
            int lol = 0;
            if (e.getPacket() instanceof C03PacketPlayer) {
                ++lol;
                if (lol >= 5) ;
                {
                    e.cancel();
                }


            }
        }

    }
}

