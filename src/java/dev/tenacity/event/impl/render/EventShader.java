package dev.tenacity.event.impl.render;

import dev.tenacity.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@Setter
@AllArgsConstructor
public class EventShader extends Event {
    private final boolean bloom;
    private final boolean blur;
    private final ScaledResolution resolution;
}
