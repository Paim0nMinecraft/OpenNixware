package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.*;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.player.RotationUtils;
import dev.tenacity.utils.player.ScaffoldUtils;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.server.PacketUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static dev.tenacity.utils.player.MovementUtils.setMoveSpeed;

public class Scaffold extends Module {

    private final ModeSetting countMode = new ModeSetting("Block Counter", "Tenacity", "None", "Tenacity", "Basic", "Polar");
    private final BooleanSetting rotations = new BooleanSetting("Rotations", true);
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "Watchdog", "Watchdog", "NCP", "Back", "45", "Enum", "Down", "0");
    private final ModeSetting placeType = new ModeSetting("Place Type", "Post", "Pre", "Post", "Legit", "Dynamic");
    public static ModeSetting keepYMode = new ModeSetting("Keep Y Mode", "Always", "Always", "Speed toggled");
    public static ModeSetting sprintMode = new ModeSetting("Sprint Mode", "Vanilla", "Vanilla", "Watchdog", "Cancel");
    public static ModeSetting towerMode = new ModeSetting("Tower Mode", "Watchdog", "Vanilla", "NCP", "Watchdog", "Verus");
    public static ModeSetting swingMode = new ModeSetting("Swing Mode", "Client", "Client", "Silent");

    public static NumberSetting delay = new NumberSetting("Delay", 0, 2, 0, 0.05);
    //public static NumberSetting extend = new NumberSetting("Extend", 0, 6, 0, 0.05);
    private final NumberSetting timer = new NumberSetting("Timer", 1, 5, 0.1, 0.1);
    public static final BooleanSetting SpeedSet = new BooleanSetting("Speed Set", false);
    public static final ModeSetting SpeedSetMode = new ModeSetting("Speed mode", "Hypixel", "Hypixel", "Custom");
    public static final NumberSetting Speed = new NumberSetting("Speed", 1, 5, 0.01, 0.01);
    public static BooleanSetting onlySpeedPotion = new BooleanSetting("only speed effect", false);

    public static final BooleanSetting itemSpoof = new BooleanSetting("Item Spoof", false);
    public static final BooleanSetting downwards = new BooleanSetting("Downwards", false);
    public static final BooleanSetting safewalk = new BooleanSetting("Safewalk", false);
    public static final BooleanSetting sprint = new BooleanSetting("Sprint", false);
    private final BooleanSetting sneak = new BooleanSetting("Sneak", false);
    public static final BooleanSetting tower = new BooleanSetting("Tower", false);
    private final NumberSetting towerTimer = new NumberSetting("Tower Timer Boost", 1.2, 5, 0.1, 0.1);
    private final NumberSetting towerSpeed = new NumberSetting("tower Speed", 1.5, 5, 0.1, 0.1);
    private final BooleanSetting swing = new BooleanSetting("Swing", true);
    private final BooleanSetting autoJump = new BooleanSetting("Auto Jump", false);
    private final BooleanSetting hideJump = new BooleanSetting("Hide Jump", false);
    private final BooleanSetting baseSpeed = new BooleanSetting("Base Speed", false);
    public static BooleanSetting keepY = new BooleanSetting("Keep Y", false);

    private ScaffoldUtils.BlockCache blockCache, lastBlockCache;
    private float y;
    private float speed;
    private final MouseFilter pitchMouseFilter = new MouseFilter();
    private final TimerUtil delayTimer = new TimerUtil();
    private final TimerUtil timerUtil = new TimerUtil();
    public static double keepYCoord;
    private Vec3 lastHitVec;

    private Vec3 lastPosition;
    private boolean shouldSendPacket;
    private boolean shouldTower;
    private boolean firstJump;

    public static boolean perspectiveToggled = false;
    public static float cameraYaw = 0F;
    public static float cameraPitch = 0F;
    private static int previousPerspective = 0;
    private boolean pre;
    private int jumpTimer;
    private int slot;
    private int prevSlot;
    private int airticks = 0;
    private boolean spoof = false;
    private float[] cachedRots = new float[2];


    private final Animation anim = new DecelerateAnimation(250, 1);

    public Scaffold() {
        super("Scaffold", Category.MOVEMENT, "Automatically places blocks under you");
        this.addSettings(countMode, rotations, rotationMode, placeType, keepYMode, sprintMode, towerMode, swingMode, delay, timer,
                SpeedSet, SpeedSetMode, Speed, onlySpeedPotion, itemSpoof, downwards, safewalk, sprint, sneak, tower, towerSpeed, towerTimer,
                swing, autoJump, hideJump, baseSpeed, keepY);
        rotationMode.addParent(rotations, ParentAttribute.BOOLEAN_CONDITION);
        sprintMode.addParent(sprint, ParentAttribute.BOOLEAN_CONDITION);
        towerMode.addParent(tower, ParentAttribute.BOOLEAN_CONDITION);
        swingMode.addParent(swing, ParentAttribute.BOOLEAN_CONDITION);
        towerTimer.addParent(tower, ParentAttribute.BOOLEAN_CONDITION);
        keepYMode.addParent(keepY, ParentAttribute.BOOLEAN_CONDITION);
        hideJump.addParent(autoJump, ParentAttribute.BOOLEAN_CONDITION);
        SpeedSetMode.addParent(SpeedSet, ParentAttribute.BOOLEAN_CONDITION);
        onlySpeedPotion.addParent(SpeedSetMode, modeSetting -> SpeedSetMode.is("Custom"));
        Speed.addParent(SpeedSetMode, modeSetting -> SpeedSetMode.is("Custom"));
        towerSpeed.addParent(towerMode, modeSetting -> towerMode.is("Watchdog"));

    }


    public void idk1(double d) {
        float f = MathHelper.wrapAngleTo180_float((float) ((float) Math.toDegrees(Math.atan2(mc.thePlayer.motionZ, mc.thePlayer.motionX)) - 90.0f));
        mc.thePlayer.motionX = d;
        mc.thePlayer.motionY = f;
    }
    @Override
    public boolean onMotionEvent(MotionEvent e) {
        // Timer Stuff
        if (!mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.timer.timerSpeed = timer.getValue().floatValue();
        } else {
            mc.timer.timerSpeed = tower.isEnabled() ? towerTimer.getValue().floatValue() : 1;
        }


        if (SpeedSet.isEnabled()) {
            if (SpeedSetMode.is("Hypixel")) {if (mc.thePlayer.onGround && (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) && !mc.gameSettings.keyBindJump.isKeyDown()) {
                    setMoveSpeed(0.116 + MovementUtils.getSpeedPotion() * 0.05);
                }
            } else {
                if (mc.thePlayer.onGround && (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0)) {
                    if (onlySpeedPotion.isEnabled()) {
                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                            setMoveSpeed(Speed.getValue() / 10);
                        }
                    } else {
                        setMoveSpeed(Speed.getValue() / 10);
                    }
                }
            }
        }


        if (e.isPre()) {
            // Auto Jump
            if (baseSpeed.isEnabled()) {
                MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed() * 0.7);
            }
            if (autoJump.isEnabled() && mc.thePlayer.onGround && MovementUtils.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.thePlayer.jump();
            }


            // Rotations
            if (rotations.isEnabled()) {
                float[] rotations = new float[]{0, 0};
                switch (rotationMode.getMode()) {
                    case "Watchdog":
                        rotations = new float[]{MovementUtils.getMoveYaw(e.getYaw()) - 180, y};
                        e.setRotations(rotations[0], rotations[1]);
                        break;
                    case "NCP":
                        float prevYaw = cachedRots[0];
                        if ((blockCache = ScaffoldUtils.getBlockInfo()) == null) {
                            blockCache = lastBlockCache;
                        }
                        if (blockCache != null && (mc.thePlayer.ticksExisted % 3 == 0
                                || mc.theWorld.getBlockState(new BlockPos(e.getX(), ScaffoldUtils.getYLevel(), e.getZ())).getBlock() == Blocks.air)) {
                            cachedRots = RotationUtils.getRotations(blockCache.getPosition(), blockCache.getFacing());
                        }
                        if ((mc.thePlayer.onGround || (MovementUtils.isMoving() && tower.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown())) && Math.abs(cachedRots[0] - prevYaw) >= 90) {
                            cachedRots[0] = MovementUtils.getMoveYaw(e.getYaw()) - 180;
                        }
                        rotations = cachedRots;
                        e.setRotations(rotations[0], rotations[1]);
                        break;
                    case "Back":
                        rotations = new float[]{MovementUtils.getMoveYaw(e.getYaw()) - 180, 77};
                        e.setRotations(rotations[0], rotations[1]);
                        break;
                    case "Down":
                        e.setPitch(90);
                        break;
                    case "45":
                        float val;
                        if (MovementUtils.isMoving()) {
                            float f = MovementUtils.getMoveYaw(e.getYaw()) - 180;
                            float[] numbers = new float[]{-135, -90, -45, 0, 45, 90, 135, 180};
                            float lastDiff = 999;
                            val = f;
                            for (float v : numbers) {
                                float diff = Math.abs(v - f);
                                if (diff < lastDiff) {
                                    lastDiff = diff;
                                    val = v;
                                }
                            }
                        } else {
                            val = rotations[0];
                        }
                        rotations = new float[]{
                                (val + MathHelper.wrapAngleTo180_float(mc.thePlayer.prevRotationYawHead)) / 2.0F,
                                (77 + MathHelper.wrapAngleTo180_float(mc.thePlayer.prevRotationPitchHead)) / 2.0F};
                        e.setRotations(rotations[0], rotations[1]);
                        break;
                    case "Enum":
                        if (lastBlockCache != null) {
                            float yaw = RotationUtils.getEnumRotations(lastBlockCache.getFacing());
                            e.setRotations(yaw, 77);
                        } else {
                            e.setRotations(mc.thePlayer.rotationYaw + 180, 77);
                        }
                        break;
                    case "0":
                        e.setRotations(0, 0);
                        break;
                }
                RotationUtils.setVisualRotations(e);
            }

            // Speed 2 Slowdown


            if (sneak.isEnabled()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);

            // Save ground Y level for keep Y
            if (mc.thePlayer.onGround) {
                keepYCoord = Math.floor(mc.thePlayer.posY - 1.0);
            }

            if (tower.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown()) {
                double centerX = Math.floor(e.getX()) + 0.5, centerZ = Math.floor(e.getZ()) + 0.5;
                switch (towerMode.getMode()) {
                    case "Vanilla":
                        mc.thePlayer.motionY = 0.42f;
                        break;
                    case "Verus":
                        if (mc.thePlayer.ticksExisted % 2 == 0)
                            mc.thePlayer.motionY = 0.42f;
                        break;
                    case "Watchdog":
                            if (mc.thePlayer.onGround) {
                                setMoveSpeed(towerSpeed.getValue() / 10);
                                float var10 = mc.thePlayer.rotationYaw * 0.017453292F;
                                mc.thePlayer.motionX -= (double) (MathHelper.sin(var10) * 0.2F) * 25D / 100.0D;
                                mc.thePlayer.motionY = 0.41999998688697815D;
                                mc.thePlayer.motionZ += (double) (MathHelper.cos(var10) * 0.2F) * 25D / 100.0D;
                                return false;
                            }
                            if (mc.thePlayer.motionY > -0.0784000015258789D) {
                                int var9 = (int) Math.round(mc.thePlayer.posY % 1.0D * 100.0D);
                                if (var9 == 42) mc.thePlayer.motionY = 0.33D;
                                if (var9 == 75) mc.thePlayer.motionY = 1.0D - mc.thePlayer.posY % 1.0D;
                                if (var9 == 0) mc.thePlayer.motionY = -0.0784000015258789D;
                            }
                        break;
                    case "NCP":
                        if (!MovementUtils.isMoving() || MovementUtils.getSpeed() < 0.16) {
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.motionY = 0.42;
                            } else if (mc.thePlayer.motionY < 0.23) {
                                mc.thePlayer.setPosition(mc.thePlayer.posX, (int) mc.thePlayer.posY, mc.thePlayer.posZ);
                                mc.thePlayer.motionY = 0.42;
                            }
                        }
                        break;
                }
            }

            // Setting Block Cache
            blockCache = ScaffoldUtils.getBlockInfo();
            if (blockCache != null) {
                lastBlockCache = ScaffoldUtils.getBlockInfo();
            } else {
                return false;
            }

            if (mc.thePlayer.ticksExisted % 4 == 0) {
                pre = true;
            }

            // Placing Blocks (Pre)
            if (placeType.is("Pre") || (placeType.is("Dynamic") && pre)) {
                if (place()) {
                    pre = false;
                }
            }

            if (itemSpoof.isEnabled() && !spoof && mc.thePlayer.inventory.currentItem != slot) {
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
                spoof = true;
            }

            if (!itemSpoof.isEnabled()) {
                mc.thePlayer.inventory.currentItem = slot;
            }
        } else {
            // Setting Item Slot

            // Placing Blocks (Post)
            if (placeType.is("Post") || (placeType.is("Dynamic") && !pre)) {
                place();
            }

            pre = false;
        }


        return false;
    }

    private boolean place() {
        int slot = ScaffoldUtils.getBlockSlot();
        if (blockCache == null || lastBlockCache == null || slot == -1) return false;

        if (this.slot != slot) {
            this.slot = slot;
            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(this.slot));
        }


        boolean placed = false;
        if (delayTimer.hasTimeElapsed(delay.getValue() * 1000)) {
            firstJump = false;
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(this.slot),
                    lastBlockCache.getPosition(), lastBlockCache.getFacing(),
                    ScaffoldUtils.getHypixelVec3(lastBlockCache))) {
                placed = true;
                y = MathUtils.getRandomInRange(79.5f, 83.5f);
                if (swing.isEnabled()) {
                    if (swingMode.is("Client")) {
                        mc.thePlayer.swingItem();
                    } else {
                        PacketUtils.sendPacket(new C0APacketAnimation());
                    }
                }
            }
            delayTimer.reset();
            blockCache = null;
        }
        return placed;
    }

    @Override
    public void onBlockPlaceable(BlockPlaceableEvent event) {
        if (placeType.is("Legit")) {
            place();
        }
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if (mc.thePlayer == null) return;
        if (hideJump.isEnabled() && !mc.gameSettings.keyBindJump.isKeyDown() && MovementUtils.isMoving() && !mc.thePlayer.onGround && autoJump.isEnabled()) {
            mc.thePlayer.posY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            mc.thePlayer.lastTickPosY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            mc.thePlayer.cameraYaw = mc.thePlayer.cameraPitch = 0.1F;
        }
        if (downwards.isEnabled()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            mc.thePlayer.movementInput.sneak = false;
        }
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            if (itemSpoof.isEnabled() && spoof) {
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            } else if (!itemSpoof.isEnabled()) {
                mc.thePlayer.inventory.currentItem = prevSlot;
            }
            if (mc.thePlayer.isSneaking() && sneak.isEnabled())
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindSneak));
        }
        mc.timer.timerSpeed = 1;
        airticks = 0;
        spoof = false;

        super.onDisable();
    }

    @Override
    public void onEnable() {
        lastBlockCache = null;

        if (mc.thePlayer != null) {
            prevSlot = mc.thePlayer.inventory.currentItem;
            slot = mc.thePlayer.inventory.currentItem;
            if (mc.thePlayer.isSprinting() && sprint.isEnabled() && (sprintMode.is("Cancel") || sprintMode.is("Watchdog"))) {
                PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }


        }


        firstJump = true;
        speed = 1.1f;
        timerUtil.reset();
        jumpTimer = 0;
        y = 80;
        super.onEnable();
    }


    public void renderCounterBlur() {
        if (!enabled && anim.isDone()) return;
        int slot = ScaffoldUtils.getBlockSlot();
        ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
        int count = slot == -1 ? 0 : ScaffoldUtils.getBlockCount();
        String countStr = String.valueOf(count);
        IFontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);
        int color;
        float x, y;
        String str = countStr + " block" + (count != 1 ? "s" : "");
        float output = anim.getOutput().floatValue();
        switch (countMode.getMode()) {
            case "Tenacity":
                float blockWH = heldItem != null ? 15 : -2;
                int spacing = 3;
                String text = "§l" + countStr + "§r block" + (count != 1 ? "s" : "");
                float textWidth = tenacityFont18.getStringWidth(text);

                float totalWidth = ((textWidth + blockWH + spacing) + 6) * output;
                x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
                y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f - 20);
                float height = 20;
                RenderUtil.scissorStart(x - 1.5, y - 1.5, totalWidth + 3, height + 3);

                RoundedUtil.drawRound(x, y, totalWidth, height, 5, Color.BLACK);
                RenderUtil.scissorEnd();
                break;
            case "Basic":
                x = sr.getScaledWidth() / 2F - fr.getStringWidth(str) / 2F + 1;
                y = sr.getScaledHeight() / 2F + 10;
                RenderUtil.scaleStart(sr.getScaledWidth() / 2.0F, y + fr.FONT_HEIGHT / 2.0F, output);
                fr.drawStringWithShadow(str, x, y, 0x000000);
                RenderUtil.scaleEnd();
                break;
            case "Polar":
                x = sr.getScaledWidth() / 2F - fr.getStringWidth(countStr) / 2F + (heldItem != null ? 6 : 1);
                y = sr.getScaledHeight() / 2F + 10;

                GlStateManager.pushMatrix();
                RenderUtil.fixBlendIssues();
                GL11.glTranslatef(x + (heldItem == null ? 1 : 0), y, 1);
                GL11.glScaled(anim.getOutput().floatValue(), anim.getOutput().floatValue(), 1);
                GL11.glTranslatef(-x - (heldItem == null ? 1 : 0), -y, 1);

                fr.drawOutlinedString(countStr, x, y, ColorUtil.applyOpacity(0x000000, output), true);

                if (heldItem != null) {
                    double scale = 0.7;
                    GlStateManager.color(1, 1, 1, 1);
                    GlStateManager.scale(scale, scale, scale);
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(
                            heldItem,
                            (int) ((sr.getScaledWidth() / 2F - fr.getStringWidth(countStr) / 2F - 7) / scale),
                            (int) ((sr.getScaledHeight() / 2F + 8.5F) / scale)
                    );
                    RenderHelper.disableStandardItemLighting();
                }
                GlStateManager.popMatrix();
                break;
        }
    }

    public void renderCounter() {
        anim.setDirection(enabled ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!enabled && anim.isDone()) return;
        int slot = ScaffoldUtils.getBlockSlot();
        ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
        int count = slot == -1 ? 0 : ScaffoldUtils.getBlockCount();
        String countStr = String.valueOf(count);
        IFontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);
        int color;
        float x, y;
        String str = countStr + " block" + (count != 1 ? "s" : "");
        float output = anim.getOutput().floatValue();
        switch (countMode.getMode()) {
            case "Tenacity":
                float blockWH = heldItem != null ? 15 : -2;
                int spacing = 3;
                String text = "§l" + countStr + "§r block" + (count != 1 ? "s" : "");
                float textWidth = tenacityFont18.getStringWidth(text);

                float totalWidth = ((textWidth + blockWH + spacing) + 6) * output;
                x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
                y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f - 20);
                float height = 20;
                RenderUtil.scissorStart(x - 1.5, y - 1.5, totalWidth + 3, height + 3);

                RoundedUtil.drawRound(x, y, totalWidth, height, 5, ColorUtil.tripleColor(20, .45f));

                tenacityFont18.drawString(text, x + 3 + blockWH + spacing, y + tenacityFont18.getMiddleOfBox(height) + .5f, -1);

                if (heldItem != null) {
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(heldItem, (int) x + 3, (int) (y + 10 - (blockWH / 2)));
                    RenderHelper.disableStandardItemLighting();
                }
                RenderUtil.scissorEnd();
                break;
            case "Basic":
                x = sr.getScaledWidth() / 2F - fr.getStringWidth(str) / 2F + 1;
                y = sr.getScaledHeight() / 2F + 10;
                RenderUtil.scaleStart(sr.getScaledWidth() / 2.0F, y + fr.FONT_HEIGHT / 2.0F, output);
                fr.drawStringWithShadow(str, x, y, -1);
                RenderUtil.scaleEnd();
                break;
            case "Polar":
                color = count < 24 ? 0xFFFF5555 : count < 128 ? 0xFFFFFF55 : 0xFF55FF55;
                x = sr.getScaledWidth() / 2F - fr.getStringWidth(countStr) / 2F + (heldItem != null ? 6 : 1);
                y = sr.getScaledHeight() / 2F + 10;

                GlStateManager.pushMatrix();
                RenderUtil.fixBlendIssues();
                GL11.glTranslatef(x + (heldItem == null ? 1 : 0), y, 1);
                GL11.glScaled(anim.getOutput().floatValue(), anim.getOutput().floatValue(), 1);
                GL11.glTranslatef(-x - (heldItem == null ? 1 : 0), -y, 1);

                fr.drawOutlinedString(countStr, x, y, ColorUtil.applyOpacity(color, output), true);

                if (heldItem != null) {
                    double scale = 0.7;
                    GlStateManager.color(1, 1, 1, 1);
                    GlStateManager.scale(scale, scale, scale);
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(
                            heldItem,
                            (int) ((sr.getScaledWidth() / 2F - fr.getStringWidth(countStr) / 2F - 7) / scale),
                            (int) ((sr.getScaledHeight() / 2F + 8.5F) / scale)
                    );
                    RenderHelper.disableStandardItemLighting();
                }
                GlStateManager.popMatrix();
                break;
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e) {
        if (e.getPacket() instanceof C0BPacketEntityAction
                && ((C0BPacketEntityAction) e.getPacket()).getAction() == C0BPacketEntityAction.Action.START_SPRINTING
                && sprint.isEnabled() && (sprintMode.is("Cancel") || sprintMode.is("Watchdog"))) {
            e.cancel();
        }

        if (e.getPacket() instanceof C09PacketHeldItemChange && itemSpoof.isEnabled()) {
            e.cancel();
        }

    }

    @Override
    public void onSafeWalkEvent(SafeWalkEvent event) {
        if ((safewalk.isEnabled() && !isDownwards()) || ScaffoldUtils.getBlockCount() == 0) {
            event.setSafe(true);
        }
    }

    public static boolean isDownwards() {
        return downwards.isEnabled() && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak);
    }

}
