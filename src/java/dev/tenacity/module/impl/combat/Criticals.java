package dev.tenacity.module.impl.combat;

import dev.tenacity.NIXWARE;
import dev.tenacity.event.impl.network.PacketEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.api.TargetManager;
import dev.tenacity.module.impl.movement.Flight;
import dev.tenacity.module.impl.movement.Step;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;


public final class Criticals extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Watchdog", "Packet", "Custom", "Jump", "DCJ", "LowHop","HmXix");
    private final ModeSetting watchdogMode = new ModeSetting("Watchdog Mode", "Packet", "Packet", "Edit");
    private final ModeSetting dcjMode = new ModeSetting("dcj Mode", "combo", "combo");
    private final NumberSetting delay = new NumberSetting("Delay", 1, 20, 0, 1);
    private final NumberSetting motionY = new NumberSetting("MotionY", 0.18, 0.42, 0.01, 0.01);
    private final BooleanSetting PacketLimit = new BooleanSetting("PacketLimit",true);
    private final TimerUtil timer = new TimerUtil();
    private boolean stage;
    int i = 0;
    private double offset;
    private int groundTicks;

    public Criticals() {
        super("Criticals", Category.COMBAT, "Crit attacks");
        delay.addParent(mode, m -> !(m.is("Verus") || (m.is("Watchdog") && watchdogMode.is("Edit"))));
        watchdogMode.addParent(mode, m -> m.is("Watchdog"));
        this.addSettings(mode, watchdogMode, delay, motionY);
    }

    public void onPacketEvent(PacketEvent e) {
        this.setSuffix(mode.getMode());
            C03PacketPlayer player = (C03PacketPlayer)  e.getPacket();
            if (mode.getMode().equals("HmXix")){
                mc.thePlayer.onGround = false;
            }
        }


    @Override
    public boolean onMotionEvent(MotionEvent e) {
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Watchdog":
                if (watchdogMode.is("Packet")) {
                    if (KillAura.attacking && e.isOnGround() && !Step.isStepping) {
                        if (TargetManager.target != null && TargetManager.target.hurtTime >= delay.getValue().intValue()) {
                            for (double offset : new double[]{0.06f, 0.01f}) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset + (Math.random() * 0.001), mc.thePlayer.posZ, false));
                            }
                        }
                    }
                }
                if (e.isPre() && watchdogMode.is("Edit") && !NIXWARE.INSTANCE.isEnabled(Flight.class) && !Step.isStepping && KillAura.attacking) {
                    if (e.isOnGround()) {
                        groundTicks++;
                        if (groundTicks > 2) {
                            stage = !stage;
                            e.setY(e.getY() + (stage ? 0.015 : 0.01) - Math.random() * 0.0001);
                            e.setOnGround(false);
                        }
                    } else {
                        groundTicks = 0;
                    }
                }
                break;
            case "dcj":
                if (dcjMode.is("combo")) {
                    if (KillAura.attacking && e.isOnGround() && !Step.isStepping) {
                        if (TargetManager.target != null && TargetManager.target.hurtTime >= delay.getValue().intValue()) {
                            for (double offset : new double[]{0.06f, 0.01f}) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset + (Math.random() * 0.001), mc.thePlayer.posZ, false));
                            }
                        }
                    }
                }
                break;
            case "Packet":
                if (mc.objectMouseOver.entityHit != null && mc.thePlayer.onGround) {
                    if (mc.objectMouseOver.entityHit.hurtResistantTime > delay.getValue().intValue()) {
                        for (double offset : new double[]{0.006253453, 0.002253453, 0.001253453}) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                        }
                    }
                }
                break;
            case "LowHop":
                if (KillAura.attacking && TargetManager.target != null && e.isOnGround()) {
                    mc.thePlayer.motionY = 0.10000000149011612;
                    mc.thePlayer.fallDistance = 0.1f;
                    mc.thePlayer.onGround = false;
                }
                break;
            case "Custom":
                if (KillAura.attacking && TargetManager.target != null && e.isOnGround()) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = motionY.getValue();
                        mc.thePlayer.onGround = false;
                        mc.thePlayer.fallDistance = motionY.getValue().floatValue();
                    }
                }
                break;
            case "Jump":
                if (KillAura.attacking && TargetManager.target != null && e.onGround) {
                    mc.thePlayer.jump();
                }
                break;
        }
        return false;
    }
}