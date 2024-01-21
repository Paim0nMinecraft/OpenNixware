package dev.tenacity.utils.player;

import dev.tenacity.event.impl.player.MoveEvent;
import dev.tenacity.event.impl.player.PlayerMoveUpdateEvent;
import dev.tenacity.utils.Utils;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector2f;

public class MovementUtils implements Utils {

    public static boolean isMoving() {
        if (mc.thePlayer == null) {
            return false;
        }
        return (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
    }

    public double getPredictedMotionY(final double motionY) {
        return (motionY - 0.08) * 0.98F;
    }

    public void strafe(final double speed) {
        if (!isMoving()) {
            return;
        }

        final double yaw = direction();
        mc.thePlayer.motionX = -Math.sin((float) yaw) * speed;
        mc.thePlayer.motionZ = Math.cos((float) yaw) * speed;
    }
    public static void setMoveSpeed(double speed) {
        double yaw = get_MoveYaw();
        if (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
            mc.thePlayer.motionX = -Math.sin(Math.toRadians(yaw)) * speed;
            mc.thePlayer.motionZ = Math.cos(Math.toRadians(yaw)) * speed;
        }
    }

    private static double get_MoveYaw() {
        double moveYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward != 0 && mc.thePlayer.moveStrafing == 0) {
            moveYaw += mc.thePlayer.moveForward > 0 ? 0 : 180;
        } else if (mc.thePlayer.moveForward != 0 && mc.thePlayer.moveStrafing != 0) {
            if (mc.thePlayer.moveForward > 0)
                moveYaw += mc.thePlayer.moveStrafing > 0 ? -45 : 45;
            else
                moveYaw -= mc.thePlayer.moveStrafing > 0 ? -45 : 45;

            moveYaw += mc.thePlayer.moveForward > 0 ? 0 : 180;
        } else if (mc.thePlayer.moveStrafing != 0 && mc.thePlayer.moveForward == 0) {
            moveYaw += mc.thePlayer.moveStrafing > 0 ? -90 : 90;
        }
        return moveYaw;
    }

    public static double getSpeedPotion() {
        return (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0);
    }

    public final void strafe(final float speed) {
        mc.thePlayer.motionX = -Math.sin(this.getDirection()) * speed;
        mc.thePlayer.motionZ = Math.cos(this.getDirection()) * speed;
    }
    public double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (mc.thePlayer.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (mc.thePlayer.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    public void strafe() {
        strafe(getSpeed());
    }

    public double direction() {
        float rotationYaw = mc.thePlayer.movementYaw;

        if (mc.thePlayer.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.thePlayer.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0) {
            rotationYaw -= 90 * forward;
        }

        if (mc.thePlayer.moveStrafing < 0) {
            rotationYaw += 90 * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    public static float getMoveYaw(float yaw) {
        Vector2f from = new Vector2f((float) mc.thePlayer.lastTickPosX, (float) mc.thePlayer.lastTickPosZ),
                to = new Vector2f((float) mc.thePlayer.posX, (float) mc.thePlayer.posZ),
                diff = new Vector2f(to.x - from.x, to.y - from.y);

        double x = diff.x, z = diff.y;
        if (x != 0 && z != 0) {
            yaw = (float) Math.toDegrees((Math.atan2(-x, z) + MathHelper.PI2) % MathHelper.PI2);
        }
        return yaw;
    }

    public static void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }
        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        mc.thePlayer.motionX = forward * moveSpeed * mx + strafe * moveSpeed * mz;
        mc.thePlayer.motionZ = forward * moveSpeed * mz - strafe * moveSpeed * mx;
    }



    public static void setSpeed(double moveSpeed) {
        setSpeed(moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe, mc.thePlayer.movementInput.moveForward);
    }

    public static void setSpeed(MoveEvent moveEvent, double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }
        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        moveEvent.setX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
        moveEvent.setZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
    }

    public static void setSpeed(MoveEvent moveEvent, double moveSpeed) {
        setSpeed(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe, mc.thePlayer.movementInput.moveForward);
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = mc.thePlayer.capabilities.getWalkSpeed() * 2.873;
        if (mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
            baseSpeed /= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1);
        }
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }

    public static void sendFlyingCapabilities(final boolean isFlying, final boolean allowFlying) {
        final PlayerCapabilities playerCapabilities = new PlayerCapabilities();
        playerCapabilities.isFlying = isFlying;
        playerCapabilities.allowFlying = allowFlying;
        PacketUtils.sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
    }

    public static double getBaseMoveSpeed2() {
        double baseSpeed = mc.thePlayer.capabilities.getWalkSpeed() * (mc.thePlayer.isSprinting() ? 2.873 : 2.215);
        if (mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
            baseSpeed /= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1);
        }
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }

    public static double getBaseMoveSpeedStupid() {
        double sped = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            sped *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return sped;
    }

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, -height, 0)).isEmpty();
    }

    public static float getSpeed() {
        if (mc.thePlayer == null || mc.theWorld == null) return 0;
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static float getMaxFallDist() {
        return mc.thePlayer.getMaxFallHeight() + (mc.thePlayer.isPotionActive(Potion.jump) ? mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1 : 0);
    }

}
