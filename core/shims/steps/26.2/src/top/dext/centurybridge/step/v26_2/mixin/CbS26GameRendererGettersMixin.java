package top.dext.centurybridge.step.v26_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlobalSettingsUniform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * 26.1.2's GameRenderer.getMinecraft() and getGlobalSettingsUniform(), which 26.2 dropped while keeping both fields
 * (javap of both: 26.1.2's getters returned the private final fields minecraft / globalSettingsUniform, which 26.2
 * still sets in its constructor and reads itself). 26.1.2's vanilla never called either getter, so nothing in 26.2 needs
 * routing through them.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = GameRenderer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26GameRendererGettersMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private GlobalSettingsUniform globalSettingsUniform;

    /** 26.1.2 GameRenderer.getMinecraft() */
    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    /** 26.1.2 GameRenderer.getGlobalSettingsUniform() */
    public GlobalSettingsUniform getGlobalSettingsUniform() {
        return this.globalSettingsUniform;
    }
}
