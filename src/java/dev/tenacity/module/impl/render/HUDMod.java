package dev.tenacity.module.impl.render;

import dev.tenacity.NIXWARE;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.event.impl.render.ShaderEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import net.minecraft.util.ResourceLocation;
import dev.tenacity.module.impl.combat.KillAura;
import dev.tenacity.module.impl.player.ChestStealer;
import dev.tenacity.module.impl.player.InvManager;
import dev.tenacity.module.settings.impl.*;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.misc.RomanNumeralUtils;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.render.*;
import dev.tenacity.utils.server.PingerUtils;
import dev.tenacity.utils.time.TimerUtil;
import dev.tenacity.utils.tuples.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class HUDMod extends Module {

    private final StringSetting clientName = new StringSetting("Client Name");
    private final ModeSetting watermarkMode = new ModeSetting("Watermark Mode", "Noraml", "Noraml", "Text", "Line", "Logo", "None");
    private final BooleanSetting Slide = new BooleanSetting("Slide",false);
    private final TimerUtil timer = new TimerUtil();
    private final NumberSetting SlideDelay = new NumberSetting("SlideDelay",400,1000,10,10);
    public static final ColorSetting color1 = new ColorSetting("Color 1", new Color(0xFFFFFF));
    public static final ColorSetting color2 = new ColorSetting("Color 2", new Color(0xFFFFFF));
    public static final ModeSetting theme = Theme.getModeSetting("Theme Selection", "Dev");
    public static final BooleanSetting customFont = new BooleanSetting("Custom Font", true);
    private static final MultipleBoolSetting infoCustomization = new MultipleBoolSetting("Info Options",
            new BooleanSetting("Show Ping", false),
            new BooleanSetting("Semi-Bold Info", true),
            new BooleanSetting("White Info", false),
            new BooleanSetting("XYZ", true),
            new BooleanSetting("Speed", false),
            new BooleanSetting("FPS", true),
            new BooleanSetting("Info Shadow", true));

    public static final MultipleBoolSetting hudCustomization = new MultipleBoolSetting("HUD Options",
            new BooleanSetting("Radial Gradients", true),
            new BooleanSetting("Potion HUD", true),
            new BooleanSetting("Armor HUD", true),
            new BooleanSetting("Render Cape", true),
            new BooleanSetting("Lowercase", false));

    private static final MultipleBoolSetting disableButtons = new MultipleBoolSetting("Disable Buttons",
            new BooleanSetting("Disable KillAura", true),
            new BooleanSetting("Disable InvManager", true),
            new BooleanSetting("Disable ChestStealer", true));

    public HUDMod() {
        super("HUD", Category.RENDER, "customizes the client's appearance");
        color1.addParent(theme, modeSetting -> modeSetting.is("Custom Theme"));
        color2.addParent(theme, modeSetting -> modeSetting.is("Custom Theme") && !color1.isRainbow());
        this.addSettings(clientName, watermarkMode, theme, color1, color2, customFont, infoCustomization, hudCustomization, disableButtons,Slide,SlideDelay);
        if (!enabled) this.toggleSilent();
    }

    public static int offsetValue = 0;
    private final Animation fadeInText = new DecelerateAnimation(500, 1);
    private final Animation alphaAni = new DecelerateAnimation(1000,1);
    private int ticks = 0;

    private boolean version = true;

    public static float xOffset = 0;


    @Override
    public void onShaderEvent(ShaderEvent e) {
        Pair<Color, Color> clientColors = getClientColors();
        String name = NIXWARE.NAME;


        if (e.isBloom()) {
            boolean glow = e.getBloomOptions().getSetting("Watermark").isEnabled();
            if (!glow) {
                clientColors = Pair.of(Color.BLACK);
            }

            if (!clientName.getString().equals("")) {
                name = clientName.getString().replace("%time%", getCurrentTimeStamp());
            }


            String finalName = get(name);
            String intentInfo = mc.getSession().getUsername();
            switch (watermarkMode.getMode()) {
                case "Logo":
                    float WH = 58 / 2f;
                    float textWidth = tenacityBoldFont32.getStringWidth(finalName);

                    GL11.glEnable(GL11.GL_SCISSOR_TEST);
                    RenderUtil.scissor(10, 7, 13 + WH + textWidth + 5, WH);

                    tenacityBoldFont32.drawString(finalName, (float) (((13 + WH) - textWidth) + (textWidth * fadeInText.getOutput().floatValue())), 8 + tenacityBoldFont32.getMiddleOfBox(WH), ColorUtil.applyOpacity(glow ? -1 : 0, (float) (fadeInText.getOutput().floatValue())));
                    GL11.glDisable(GL11.GL_SCISSOR_TEST);


                    GradientUtil.applyGradientCornerLR(27, 23, WH - 28, WH - 28, 1, clientColors.getSecond(), clientColors.getFirst(), () -> {
                        mc.getTextureManager().bindTexture(new ResourceLocation("nixware/watermarkBack.png"));
                        Gui.drawModalRectWithCustomSizedTexture(7, 7, 0, 0, WH, WH, WH, WH);
                    });
                    break;
                case "Line":
                    String text = NIXWARE.NAME + " - " + intentInfo + " - " + Minecraft.getDebugFPS() + "fps" + " - "
                            + PingerUtils.getPing() + "ms ";
                    float x = 4.5f, y = 4.5f;

                    Gui.drawRect2(x, y, bigFont18.getStringWidth(text) + 7, 18.5, glow ? new Color(10, 10, 10).getRGB() : Color.BLACK.getRGB());
                    break;
                case "Text":
                    AbstractFontRenderer fr = mc.fontRendererObj;
                    if (customFont.isEnabled()) {
                        fr = bigFont35;
                    }
                    AbstractFontRenderer finalFr = fr;
                    finalName = get(name);

                    fr.drawString(finalName, 6.5F,6.5F, Color.BLACK.getRGB());


                    String finalName2 = finalName;
                    GradientUtil.applyGradientHorizontal(6, 6, fr.getStringWidth(finalName), fr.getHeight(), 1, clientColors.getFirst(), clientColors.getSecond(), () -> {
                        RenderUtil.setAlphaLimit(0);
                        finalFr.drawString(finalName2, 6, 6, new Color(0, 0, 0, 0).getRGB());
                    });
                    break;
            }
        }
    }

//    主体--------------------------

    @Override
    public void onRender2DEvent(Render2DEvent e) {
        ScaledResolution sr = new ScaledResolution(mc);
        Pair<Color, Color> clientColors = getClientColors();
        String name = NIXWARE.NAME;
        Shader postProcessing = NIXWARE.INSTANCE.getModuleCollection().getModule(Shader.class);
        if (!postProcessing.isEnabled()) {
            version = false;
        }

        if (!clientName.getString().equals("")) {
            name = clientName.getString().replace("%time%", getCurrentTimeStamp());
        }

        version = name.equalsIgnoreCase(NIXWARE.NAME);

        String finalName = get(name);
        String intentInfo = mc.getSession().getUsername();

        switch (watermarkMode.getMode()) {
            case "Logo":

                float WH = 58 / 2f;

                if (MovementUtils.isMoving()) {
                    ticks = 0;
                } else {
                    ticks = Math.min(ticks + 1, 301);
                }

                fadeInText.setDirection(ticks < 300 ? Direction.BACKWARDS : Direction.FORWARDS);
                float textWidth = tenacityBoldFont32.getStringWidth(finalName);

                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.scissor(10, 7, 13 + WH + textWidth + 5, WH);

                tenacityBoldFont32.drawString(finalName, (float) (((13 + WH) - textWidth) + (textWidth * fadeInText.getOutput().floatValue())), 8 + tenacityBoldFont32.getMiddleOfBox(WH), ColorUtil.applyOpacity(-1, (float) (.7f * fadeInText.getOutput().floatValue())));
                GL11.glDisable(GL11.GL_SCISSOR_TEST);

                RenderUtil.color(Color.BLUE.getRGB());

                GradientUtil.applyGradientCornerLR(27, 23, WH - 28, WH - 28, 1, clientColors.getSecond(), clientColors.getFirst(), () -> {
                    mc.getTextureManager().bindTexture(new ResourceLocation("nixware/watermarkBack.png"));
                    Gui.drawModalRectWithCustomSizedTexture(7, 7, 0, 0, WH, WH, WH, WH);
                });

                RenderUtil.color(-1);
                GLUtil.startBlend();
                mc.getTextureManager().bindTexture(new ResourceLocation("nixware/watermarkT.png"));
                Gui.drawModalRectWithCustomSizedTexture(7, 7, 0, 0, WH, WH, WH, WH);

                break;
            case "Line":
                String text = NIXWARE.NAME + " - " + intentInfo + " - " + Minecraft.getDebugFPS() + "fps" + " - "
                        + PingerUtils.getPing() + "ms ";

                float x = 4.5f, y = 4.5f;

                int lineColor = new Color(10, 10, 10,1).darker().getRGB();
                Gui.drawRect2(x, y, bigFont18.getStringWidth(text) + 7, 18.5, new Color(80, 80, 80,0).getRGB());

                Gui.drawRect2(x + 2.5, y + 2.5, bigFont18.getStringWidth(text) + 2, 13, new Color(10, 10, 10,0).getRGB());

                // Top small bar
                Gui.drawRect2(x + 1, y + 1, bigFont18.getStringWidth(text) + 5, .5, lineColor);

                // Bottom small bar
                Gui.drawRect2(x + 1, y + 17, bigFont18.getStringWidth(text) + 5, .5, lineColor);

                // Left bar
                Gui.drawRect2(x + 1, y + 1.5, .5, 16, lineColor);

                // Right Bar
                Gui.drawRect2((x + 1.5) + bigFont18.getStringWidth(text) + 4, y + 1.5, .5, 16, lineColor);

                // Lowly saturated rainbow bar
                GradientUtil.drawGradientLR(x + 2.5f, y + 14.5f, bigFont18.getStringWidth(text) + 3, 2, 1, clientColors.getFirst(), clientColors.getSecond());

                // Bottom of the rainbow bar
                Gui.drawRect2(x + 2.5, y + 16, bigFont18.getStringWidth(text) + 2, .5, lineColor);
                bigFont18.drawString(text, x + 4.5f, y + 4f, Color.WHITE);
                break;
            case "Text":
                AbstractFontRenderer fr = mc.fontRendererObj;
                if (customFont.isEnabled()) {
                    fr = bigFont30;
                }
                AbstractFontRenderer finalFr = fr;
                finalName = get(name);

                fr.drawString(finalName, 5,5, Color.BLACK.getRGB());


                String finalName2 = finalName;
                GradientUtil.applyGradientHorizontal(6, 6, fr.getStringWidth(finalName), fr.getHeight(), 1, clientColors.getFirst(), clientColors.getSecond(), () -> {
                    RenderUtil.setAlphaLimit(0);
                    finalFr.drawString(finalName2, 6, 6, new Color(0, 0, 0, 0).getRGB());
                });
                break;
            case "OldOrigin":
                CustomFont m22 = neverloseFont.size(22), t18 = csgoFont18;
                String str = String.format("");
                float nw = m22.getStringWidth(name);
                String str1 = String.format("Version");
                String str2 = String.format(" " + NIXWARE.VERSION);
                RenderUtil.drawImage(new ResourceLocation("nixware/MainMenu/test.png"), 4.5F,4.5F, nw + t18.getStringWidth(str) + 6f, csgoFont18.getHeight() + 15);
                RoundedUtil.drawRound(4.5F, 4.5F, nw + t18.getStringWidth(str) + 6f, csgoFont18.getHeight() + 15, 0, new Color(0,0,0, 128));
                RoundedUtil.drawRound(4.5F, 4.5F, nw + t18.getStringWidth(str) + 6f, t18.getHeight() + 15, 0, new Color(255,255,255, 18));
                t18.drawString(str, 7.5F + nw, 7.5F, -1);
                csgoFont12.drawString(str1, 8F + 1, 20F, -1);
                csgoFont12.drawString(str2, 30F + 1, 20F, -1);
                m22.drawString(name, 7.5F, 8, new Color(140, 40, 255).getRGB());
                m22.drawString(name, 7, 7.5F, -1);
                //Stencil.dispose();
                //GaussianBlur.startBlur();
                //RoundedUtil.drawRound(4.5F, 4.5F, 39F, 21F, 0, new Color(-1));
                //GaussianBlur.endBlur(90F, 5);
                break;
            case "Noraml":
                StringBuilder stringBuilder = new StringBuilder(name.replace("Exhibition", "Exhibition")).insert(1, "§7");
                stringBuilder.append(" [§f").append((mc.isSingleplayer() ? "SinglePlayer" : mc.getCurrentServerData().serverIP)).append("§7]");
                stringBuilder.append(" [§f").append(Minecraft.getDebugFPS()).append(" FPS§7]");
                stringBuilder.append(" [§f").append(PingerUtils.getPing()).append("ms§7]");
//                stringBuilder.append(" [§f").append(String.valueOf(calculateBPS())).append("BPS§7]");
                RenderUtil.resetColor();
                bigFont17.drawStringWithShadow(stringBuilder.toString(), 4.5f, 3.5f, clientColors.getFirst());
                break;
        }


        RenderUtil.resetColor();
        drawBottomRight();

        RenderUtil.resetColor();
        drawInfo(clientColors);

        drawArmor(sr);
    }

    private void drawBottomRight() {
        AbstractFontRenderer fr = customFont.isEnabled() ? bigFont20 : bigFont16;
        ScaledResolution sr = new ScaledResolution(mc);
        float yOffset = (float) (14.5 * GuiChat.openingAnimation.getOutput().floatValue());

        boolean shadowInfo = infoCustomization.isEnabled("Info Shadow");

        if (hudCustomization.getSetting("Potion HUD").isEnabled()) {
            java.util.List<PotionEffect> potions = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
            potions.sort(Comparator.comparingDouble(e -> -fr.getStringWidth(I18n.format(e.getEffectName()))));

            int count = 0;
            for (PotionEffect effect : potions) {
                Potion potion = Potion.potionTypes[effect.getPotionID()];
                String name = I18n.format(potion.getName()) + (effect.getAmplifier() > 0 ? " " + RomanNumeralUtils.generate(effect.getAmplifier() + 1) : "");
                Color c = new Color(potion.getLiquidColor());
                String str = get(name + " §7[" + Potion.getDurationString(effect) + "]");
                fr.drawString(str, sr.getScaledWidth() - fr.getStringWidth(str) - 2,
                        -10 + sr.getScaledHeight() - fr.getHeight() + (7 - (10 * (count + 1))) - yOffset,
                        new Color(c.getRed(), c.getGreen(), c.getBlue(), 255).getRGB(), shadowInfo);
                count++;
            }

            offsetValue = count * fr.getHeight();
        }
    }

    private final Map<String, String> bottomLeftText = new LinkedHashMap<>();

    private void drawInfo(Pair<Color, Color> clientColors) {
        boolean shadowInfo = infoCustomization.isEnabled("Info Shadow");
        boolean semiBold = infoCustomization.isEnabled("Semi-Bold Info");
        boolean whiteInfo = infoCustomization.isEnabled("White Info");
        String titleBold = semiBold ? "§l" : "";
        ScaledResolution sr = new ScaledResolution(mc);

        if (infoCustomization.isEnabled("FPS")) {
            bottomLeftText.put("FPS", String.valueOf(Minecraft.getDebugFPS()));
            GuiNewChat.chatPos = 17 - 2;
        } else {
            GuiNewChat.chatPos = 17;
            bottomLeftText.remove("FPS");
        }

        if (infoCustomization.isEnabled("XYZ")) {
            bottomLeftText.put("XYZ", Math.round(mc.thePlayer.posX) + " " + Math.round(mc.thePlayer.posY) + " " + Math.round(mc.thePlayer.posZ));
            GuiNewChat.chatPos = 17 - 3;
        } else {
            GuiNewChat.chatPos = 17;
            bottomLeftText.remove("XYZ");
        }

        if (infoCustomization.isEnabled("Speed")) {
            bottomLeftText.put("Speed", String.valueOf(calculateBPS()));
            GuiNewChat.chatPos = 17 - 6;
        } else {
            GuiNewChat.chatPos = 17;
            bottomLeftText.remove("Speed");
        }

        if (infoCustomization.isEnabled("Show Ping")) {
            bottomLeftText.put("Ping", PingerUtils.getPing());
            GuiNewChat.chatPos = 17 - 4;
        } else {
            GuiNewChat.chatPos = 17;
            bottomLeftText.remove("Ping");
        }

        //InfoStuff
        AbstractFontRenderer nameInfoFr = bigFont20;
        if (!customFont.isEnabled()) {
            nameInfoFr = mc.fontRendererObj;
        }

        if (semiBold) {
            xOffset = nameInfoFr.getStringWidth("§lXYZ: " + bottomLeftText.get("XYZ"));
        } else {
            xOffset = nameInfoFr.getStringWidth("XYZ: " + bottomLeftText.get("XYZ"));
        }


        float yOffset = (float) (14.5 * GuiChat.openingAnimation.getOutput().floatValue());
        float f2 = customFont.isEnabled() ? 0.5F : 1.0F;
        float f3 = customFont.isEnabled() ? 1 : 0.5F;
        float yMovement = !customFont.isEnabled() ? -1 : 0;


        if (whiteInfo) {
            float boldFontMovement = nameInfoFr.getHeight() + 2 + yOffset + yMovement;
            for (Map.Entry<String, String> line : bottomLeftText.entrySet()) {
                nameInfoFr.drawString(get(titleBold + line.getKey() + "§r: " + line.getValue()), 2, sr.getScaledHeight() - boldFontMovement, -1, shadowInfo);
                boldFontMovement += nameInfoFr.getHeight() + f3;
            }
        } else {

            float f = nameInfoFr.getHeight() + 2 + yOffset + yMovement;
            for (Map.Entry<String, String> line : bottomLeftText.entrySet()) {
                // Simulate a shadow
                if (shadowInfo) {
                    nameInfoFr.drawString(get(line.getValue()), 2 + f2 + nameInfoFr.getStringWidth(titleBold + line.getKey() + ":§r "), sr.getScaledHeight() - f + f2, 0xFF000000);
                }

                nameInfoFr.drawString(get(line.getValue()), 2 + nameInfoFr.getStringWidth(titleBold + line.getKey() + ":§r "), sr.getScaledHeight() - f, -1);

                f += nameInfoFr.getHeight() + f3;
            }


            float height = (nameInfoFr.getHeight() + 2) * bottomLeftText.size();
            float width = nameInfoFr.getStringWidth(titleBold + "Speed:");
            AbstractFontRenderer finalFr = nameInfoFr;

            if (shadowInfo) {
                float boldFontMovement1 = finalFr.getHeight() + 2 + yOffset + yMovement;
                for (Map.Entry<String, String> line : bottomLeftText.entrySet()) {
                    finalFr.drawString(get(titleBold + line.getKey() + ": "), 2 + f2, sr.getScaledHeight() - boldFontMovement1 + f2, 0xFF000000);
                    boldFontMovement1 += finalFr.getHeight() + f3;
                }
            }

            GradientUtil.applyGradientVertical(2, sr.getScaledHeight() - (height + yOffset + yMovement), width, height, 1, clientColors.getFirst(), clientColors.getSecond(), () -> {
                float boldFontMovement = finalFr.getHeight() + 2 + yOffset + yMovement;
                for (Map.Entry<String, String> line : bottomLeftText.entrySet()) {
                    finalFr.drawString(get(titleBold + line.getKey() + ": "), 2, sr.getScaledHeight() - boldFontMovement, -1);
                    boldFontMovement += finalFr.getHeight() + f3;
                }
            });

        }
    }

    private double calculateBPS() {
        double bps = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
        return Math.round(bps * 100.0) / 100.0;
    }


    public static Pair<Color, Color> getClientColors() {
        return Theme.getThemeColors(theme.getMode());
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("h:mm a").format(new Date());
    }

    public static String get(String text) {
        return hudCustomization.getSetting("Lowercase").isEnabled() ? text.toLowerCase() : text;
    }

    private void drawArmor(ScaledResolution sr) {
        if (hudCustomization.getSetting("Armor HUD").isEnabled()) {
            List<ItemStack> equipment = new ArrayList<>();
            boolean inWater = mc.thePlayer.isEntityAlive() && mc.thePlayer.isInsideOfMaterial(Material.water);
            int x = -94;

            ItemStack armorPiece;
            for (int i = 3; i >= 0; i--) {
                if ((armorPiece = mc.thePlayer.inventory.armorInventory[i]) != null) {
                    equipment.add(armorPiece);
                }
            }
            Collections.reverse(equipment);

            for (ItemStack itemStack : equipment) {
                armorPiece = itemStack;
                RenderHelper.enableGUIStandardItemLighting();
                x += 15;
                GlStateManager.pushMatrix();
                GlStateManager.disableAlpha();
                GlStateManager.clear(256);
                mc.getRenderItem().zLevel = -150.0F;
                int s = mc.thePlayer.capabilities.isCreativeMode ? 15 : 0;
                mc.getRenderItem().renderItemAndEffectIntoGUI(armorPiece, -x + sr.getScaledWidth() / 2 - 4,
                        (int) (sr.getScaledHeight() - (inWater ? 65 : 55) + s - (16 * GuiChat.openingAnimation.getOutput().floatValue())));
                mc.getRenderItem().zLevel = 0.0F;
                GlStateManager.disableBlend();
                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                GlStateManager.enableDepth();
                GlStateManager.enableAlpha();
                GlStateManager.popMatrix();
                armorPiece.getEnchantmentTagList();
            }
        }
    }

    public static boolean isRainbowTheme() {
        return theme.is("Custom Theme") && color1.isRainbow();
    }

    public static boolean drawRadialGradients() {
        return hudCustomization.getSetting("Radial Gradients").isEnabled();
    }

    public static void addButtons(List<GuiButton> buttonList) {
        for (ModuleButton mb : ModuleButton.values()) {
            if (mb.getSetting().isEnabled()) {
                buttonList.add(mb.getButton());
            }
        }
    }

    public static void updateButtonStatus() {
        for (ModuleButton mb : ModuleButton.values()) {
            mb.getButton().enabled = NIXWARE.INSTANCE.getModuleCollection().getModule(mb.getModule()).isEnabled();
        }
    }

    public static void handleActionPerformed(GuiButton button) {
        for (ModuleButton mb : ModuleButton.values()) {
            if (mb.getButton() == button) {
                Module m = NIXWARE.INSTANCE.getModuleCollection().getModule(mb.getModule());
                if (m.isEnabled()) {
                    m.toggle();
                }
                break;
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum ModuleButton {
        AURA(KillAura.class, disableButtons.getSetting("Disable KillAura"), new GuiButton(2461, 3, 4, 120, 20, "Disable KillAura")),
        INVMANAGER(InvManager.class, disableButtons.getSetting("Disable InvManager"), new GuiButton(2462, 3, 26, 120, 20, "Disable InvManager")),
        CHESTSTEALER(ChestStealer.class, disableButtons.getSetting("Disable ChestStealer"), new GuiButton(2463, 3, 48, 120, 20, "Disable ChestStealer"));

        private final Class<? extends Module> module;
        private final BooleanSetting setting;
        private final GuiButton button;
    }

}

