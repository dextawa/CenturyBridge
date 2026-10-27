package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * FontManager.setRenames(renames): 1.20.4's fonts looked their font id up through this map first (getActualId:
 * renames.getOrDefault(id, id)) -- Minecraft set {default -> uniform} for the forced unicode font. 1.20.5
 * dropped the map: the forced unicode font is an option a font provider filters on. The renames come back,
 * applied where 1.20.5 looks a font set up by id (getFontSetRaw; CbClientFontManagerMixinHooks, default priority),
 * so ids a 1.20.4 mod renamed resolve to the set it named, as they did; with no renames set (the default) nothing
 * changes.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_378.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientFontManagerMixin implements CbPrioritySplitClient.FontRenames {

    /** 1.20.4's renames, empty until set */
    @Unique
    private volatile java.util.Map<net.minecraft.class_2960, net.minecraft.class_2960> cb$renames = java.util.Map.of();

    /** 1.20.4 FontManager.setRenames(renames) */
    public void method_27541(java.util.Map<net.minecraft.class_2960, net.minecraft.class_2960> renames) {
        this.cb$renames = renames;
    }

    @Override
    public java.util.Map<net.minecraft.class_2960, net.minecraft.class_2960> cb$v1_20_5$renames() {
        return this.cb$renames;
    }
}
