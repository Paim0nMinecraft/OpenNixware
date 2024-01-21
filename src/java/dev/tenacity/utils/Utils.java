package dev.tenacity.utils;

import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

public interface Utils {
    Minecraft mc = Minecraft.getMinecraft();
    IFontRenderer fr = mc.fontRendererObj;

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    FontUtil.FontType tenacityFont = FontUtil.FontType.TENACITY,
            iconFont = FontUtil.FontType.ICON,
            icon2Font = FontUtil.FontType.ICON2,
            csgoFont = FontUtil.FontType.CSGO,
            exhiFont = FontUtil.FontType.EXHI,
            arialFont = FontUtil.FontType.ARIAL,
            bigFont = FontUtil.FontType.BIG,
//            nixFont = FontUtil.FontType.NIX,
            miconFont = FontUtil.FontType.MICON,
            waterFont = FontUtil.FontType.WATER,
            oxideFont = FontUtil.FontType.OXIDE,


            neverloseFont = FontUtil.FontType.NEVERLOSE,
            tahomaFont = FontUtil.FontType.TAHOMA,
            rubikFont = FontUtil.FontType.RUBIK;


    //Regular Fonts
    CustomFont tenacityFont12 = tenacityFont.size(12),
            tenacityFont14 = tenacityFont.size(14),
            tenacityFont16 = tenacityFont.size(16),
            tenacityFont18 = tenacityFont.size(18),
            tenacityFont20 = tenacityFont.size(20),
            tenacityFont22 = tenacityFont.size(22),
            tenacityFont24 = tenacityFont.size(24),
            tenacityFont26 = tenacityFont.size(26),
            tenacityFont28 = tenacityFont.size(28),
            tenacityFont32 = tenacityFont.size(32),
            tenacityFont36 = tenacityFont.size(36),
            tenacityFont40 = tenacityFont.size(40),
            tenacityFont80 = tenacityFont.size(80);
    //oxide Fonts
    CustomFont oxideFont12 = oxideFont.size(12),
            oxideFont14 = oxideFont.size(14),
            oxideFont16 = oxideFont.size(16),
            oxideFont17 = oxideFont.size(17),
            oxideFont18 = oxideFont.size(18),
            oxideFont20 = oxideFont.size(20),
            oxideFont22 = oxideFont.size(22),
            oxideFont24 = oxideFont.size(24),
            oxideFont26 = oxideFont.size(26),
            oxideFont30 = oxideFont.size(30),
            oxideFont32 = oxideFont.size(32),
            oxideFont35 = oxideFont.size(35),
            oxideFont40 = oxideFont.size(40),
            oxideFont45 = oxideFont.size(45),
            oxideFont50 = oxideFont.size(50),
            oxideFont55 = oxideFont.size(55),
            oxideFont60 = oxideFont.size(60);

    //Bold Fonts
    CustomFont tenacityBoldFont12 = tenacityFont12.getBoldFont(),
            tenacityBoldFont14 = tenacityFont14.getBoldFont(),
            tenacityBoldFont16 = tenacityFont16.getBoldFont(),
            tenacityBoldFont18 = tenacityFont18.getBoldFont(),
            tenacityBoldFont20 = tenacityFont20.getBoldFont(),
            tenacityBoldFont22 = tenacityFont22.getBoldFont(),
            tenacityBoldFont24 = tenacityFont24.getBoldFont(),
            tenacityBoldFont26 = tenacityFont26.getBoldFont(),
            tenacityBoldFont28 = tenacityFont28.getBoldFont(),
            tenacityBoldFont32 = tenacityFont32.getBoldFont(),
            tenacityBoldFont40 = tenacityFont40.getBoldFont(),
            tenacityBoldFont80 = tenacityFont80.getBoldFont();

    //Icon Fontsor i
    CustomFont iconFont16 = iconFont.size(16),
            iconFont20 = iconFont.size(20),
            iconFont26 = iconFont.size(26),
            iconFont35 = iconFont.size(35),
            iconFont40 = iconFont.size(40);

    //Icon Fontsor 2
    CustomFont icon2Font16 = icon2Font.size(16),
            icon2Font20 = icon2Font.size(20),
            icon2Font26 = icon2Font.size(26),
            icon2Font35 = icon2Font.size(35),
            icon2Font40 = icon2Font.size(40);

    //micon Fontsor m
    CustomFont miconFont16 = miconFont.size(16),
            miconFont20 = miconFont.size(20),
            miconFont26 = miconFont.size(26),
            miconFont35 = miconFont.size(35),
            miconFont40 = miconFont.size(40);

    //water - water
    CustomFont waterFont14 = waterFont.size(14),
            waterFont16 = waterFont.size(16),
            waterFont18 = waterFont.size(18),
            waterFont20 = waterFont.size(20),
            waterFont22 = waterFont.size(22),
            waterFont26 = waterFont.size(26),
            waterFont30 = waterFont.size(30),
            waterFont32 = waterFont.size(32),
            waterFont35 = waterFont.size(35),
            waterFont40 = waterFont.size(40),
            waterFont45 = waterFont.size(45),
            waterFont50 = waterFont.size(50),
            waterFont55 = waterFont.size(55),
            waterFont60 = waterFont.size(60),
            waterFont100 = waterFont.size(100);

    //big - Big
    CustomFont bigFont14 = bigFont.size(14),
            bigFont16 = bigFont.size(16),
            bigFont17 = bigFont.size(17),
            bigFont18 = bigFont.size(18),
            bigFont19 = bigFont.size(19),
            bigFont20 = bigFont.size(20),
            bigFont22 = bigFont.size(22),
            bigFont24 = bigFont.size(24),
            bigFont26 = bigFont.size(26),
            bigFont30 = bigFont.size(30),
            bigFont32 = bigFont.size(32),
            bigFont35 = bigFont.size(35),
            bigFont40 = bigFont.size(40),
            bigFont45 = bigFont.size(45),
            bigFont50 = bigFont.size(50),
            bigFont55 = bigFont.size(55),
            bigFont60 = bigFont.size(60);

    //nix font
//    CustomFont nixFont14 = nixFont.size(14),
//            nixFont16 = nixFont.size(16),
//            nixFont17 = nixFont.size(17),
//            nixFont18 = nixFont.size(18),
//            nixFont19 = nixFont.size(19),
//            nixFont20 = nixFont.size(20),
//            nixFont22 = nixFont.size(22),
//            nixFont26 = nixFont.size(26),
//            nixFont30 = nixFont.size(30),
//            nixFont32 = nixFont.size(32),
//            nixFont35 = nixFont.size(35),
//            nixFont40 = nixFont.size(40),
//            nixFont45 = nixFont.size(45),
//            nixFont50 = nixFont.size(50),
//            nixFont55 = nixFont.size(55),
//            nixFont60 = nixFont.size(60);

    //csgo - gamesense
    CustomFont exhiFont12 = exhiFont.size(12),
            exhiFont14 = exhiFont.size(14),
            exhiFont16 = exhiFont.size(16),
            exhiFont17 = exhiFont.size(17),
            exhiFont18 = exhiFont.size(18),
            exhiFont20 = exhiFont.size(20),
            exhiFont22 = exhiFont.size(22),
            exhiFont24 = exhiFont.size(24),
            exhiFont26 = exhiFont.size(26),
            exhiFont30 = exhiFont.size(30),
            exhiFont32 = exhiFont.size(32),
            exhiFont35 = exhiFont.size(35),
            exhiFont40 = exhiFont.size(40),
            exhiFont45 = exhiFont.size(45),
            exhiFont50 = exhiFont.size(50),
            exhiFont55 = exhiFont.size(55),
            exhiFont60 = exhiFont.size(60);

    //csgo - gamesense
    CustomFont csgoFont12 = csgoFont.size(12),
            csgoFont14 = csgoFont.size(14),
            csgoFont16 = csgoFont.size(16),
            csgoFont17 = csgoFont.size(17),
            csgoFont18 = csgoFont.size(18),
            csgoFont20 = csgoFont.size(20),
            csgoFont22 = csgoFont.size(22),
            csgoFont24 = csgoFont.size(24),
            csgoFont26 = csgoFont.size(26),
            csgoFont30 = csgoFont.size(30),
            csgoFont32 = csgoFont.size(32),
            csgoFont35 = csgoFont.size(35),
            csgoFont40 = csgoFont.size(40),
            csgoFont45 = csgoFont.size(45),
            csgoFont50 = csgoFont.size(50),
            csgoFont55 = csgoFont.size(55),
            csgoFont60 = csgoFont.size(60);

    //aira - arialFont
    CustomFont arialFont12 = arialFont.size(12),
            arialFont14 = arialFont.size(14),
            arialFont16 = arialFont.size(16),
            arialFont18 = arialFont.size(18),
            arialFont20 = arialFont.size(20),
            arialFont24 = arialFont.size(24),
            arialFont26 = arialFont.size(26),
            arialFont30 = arialFont.size(30),
            arialFont32 = arialFont.size(32),
            arialFont35 = arialFont.size(35),
            arialFont40 = arialFont.size(40),
            arialFont45 = arialFont.size(45),
            arialFont50 = arialFont.size(50),
            arialFont55 = arialFont.size(55),
            arialFont60 = arialFont.size(60);


}
