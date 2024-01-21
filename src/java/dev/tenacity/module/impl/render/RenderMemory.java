package dev.tenacity.module.impl.render;

import dev.tenacity.NIXWARE;
import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.objects.Dragging;
import dev.tenacity.utils.render.Theme;
import dev.tenacity.utils.tuples.Pair;

import java.awt.*;

public class RenderMemory extends Module {
    private Pair<Color, Color> color;
    public RenderMemory() {
        super("RenderMemory",Category.RENDER,"show ur memory on screen");

    }
    private final Dragging drag = NIXWARE.INSTANCE.createDrag(this, "RenderMemory", 2, 2);
    private final Animation animation = new DecelerateAnimation(1000, 100);
    private String str;


    @Override
    public void onTickEvent(TickEvent event){
        long usedMemoty = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory();
        long perMem = usedMemoty*100L / Runtime.getRuntime().totalMemory();
        animation.setEndPoint((int)perMem);
        color = Theme.getCurrentTheme().getColors();
    //System.out.println(animation.getOutput().intValue()*100);
    }
    @Override
    public void onEnable(){
        animation.reset();
        super.onEnable();
    }

    @Override
    public void onRender2DEvent(Render2DEvent e) {
        long usedMemoty = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory();
        long perMem = usedMemoty*100L / Runtime.getRuntime().totalMemory();

        str = "Memory:"+ (int)perMem;
        drag.setHeight(20);
        mc.fontRendererObj.drawCenteredString(str,drag.getX()+599,drag.getY()+498, color.getFirst());
    }

}
