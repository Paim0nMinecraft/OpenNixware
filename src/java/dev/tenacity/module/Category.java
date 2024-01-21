package dev.tenacity.module;

import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.objects.Drag;
import dev.tenacity.utils.objects.Scroll;
import lombok.Getter;

public enum Category {

    COMBAT("Combat", FontUtil.c),
    MOVEMENT("Movement", FontUtil.m),
    RENDER("Render", FontUtil.r),
    PLAYER("Player", FontUtil.p),
    EXPLOIT("Exploit", FontUtil.e),
    MISC("Misc", FontUtil.ms);
    public final String name;
    public final String icon;
    public final int posX;
    public final boolean expanded;

    @Getter
    private final Scroll scroll = new Scroll();

    @Getter
    private final Drag drag;
    public int posY = 20;

    Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
        posX = 20 + (Module.categoryCount * 120);
        drag = new Drag(posX, posY);
        expanded = true;
        Module.categoryCount++;
    }

}
