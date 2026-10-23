package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.facade.LootDataManager;
import top.dext.centurybridge.step.v1_20_5.rt.CbLootData;

/**
 * 1.20.4 ReloadableServerResources.getLootData() (the loot data manager these resources loaded), which
 * 1.20.5 replaced by the full registries these resources hold (the reloadable loot registries layered
 * over the rest): back as those registries' loot data viewed as a 1.20.4 manager (CbLootData.manager).
 * <p>
 * 1.20.4 updateRegistryTags(access): the loaded tags bound into the given registries, then the block
 * state cache rebuilt; 1.20.5's updateRegistryTags() binds them into these resources' own registries (and
 * also drops the furnace fuel map, which 1.20.5 caches from tags). The cover keeps 1.20.4's target and
 * 1.20.5's cache consistency.
 */
@Mixin(net.minecraft.class_5350.class)
public abstract class CbRestReloadableResourcesMixin {

    @Shadow
    @Final
    private net.minecraft.class_9383.class_9385 field_49921;

    @Shadow
    @Final
    private net.minecraft.class_3505 field_25338;

    @Shadow
    private static <T> void method_40422(net.minecraft.class_5455 registries, net.minecraft.class_3505.class_6863<T> result) {
        throw new AssertionError();
    }

    /** was getLootData() */
    public LootDataManager method_29469() {
        return CbLootData.manager(this.field_49921.method_58289());
    }

    /** was updateRegistryTags(RegistryAccess): 1.20.4's body */
    public void method_40421(net.minecraft.class_5455 registries) {
        for (net.minecraft.class_3505.class_6863<?> result : this.field_25338.method_40096()) {
            method_40422(registries, result);
        }
        net.minecraft.class_2609.method_56120();
        net.minecraft.class_2246.method_26979();
    }
}
