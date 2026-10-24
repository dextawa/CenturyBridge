package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBakery;

/**
 * 1.21.1 ModelBakery.getModelGroups(): the blockstate groups of a bakery 1.21.1's constructor built (CbRenderBakery);
 * a bakery 1.21.2 built has none (its ModelManager keeps the groups itself), an empty map.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1088.class)
public abstract class CbRenderBakeryMixin implements CbRenderBakery.Legacy {

    @Unique
    private it.unimi.dsi.fastutil.objects.Object2IntMap<net.minecraft.class_2680> cb$groups;

    @Override
    public void cb$groups(it.unimi.dsi.fastutil.objects.Object2IntMap<net.minecraft.class_2680> groups) {
        this.cb$groups = groups;
    }

    /** 1.21.1 ModelBakery.getModelGroups() */
    public it.unimi.dsi.fastutil.objects.Object2IntMap<net.minecraft.class_2680> method_21605() {
        return this.cb$groups != null ? this.cb$groups : it.unimi.dsi.fastutil.objects.Object2IntMaps.emptyMap();
    }
}
