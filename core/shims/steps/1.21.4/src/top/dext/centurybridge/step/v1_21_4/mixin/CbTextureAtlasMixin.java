package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/** TextureAtlas's 1.21.3 updateFilter(preparations): mipmapped when the atlas has mip levels, never blurred */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1059.class)
public abstract class CbTextureAtlasMixin extends net.minecraft.class_1044 {

    /** was updateFilter(preparations) */
    public void method_24198(net.minecraft.class_7766.class_7767 preparations) {
        this.method_4527(false, preparations.comp_1042() > 0);
    }
}
