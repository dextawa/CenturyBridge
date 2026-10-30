package top.dext.centurybridge.step.v1_21_9.mixin;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.class_10081;
import net.minecraft.class_630;
import net.minecraft.class_7280;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_9.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8 WardenModel's emissive part lists, which 1.21.9 dropped when the warden's emissive layers became model
 * layers of their own (ModelLayers warden/tendrils, heart, bioluminescent, pulsating_spots). 1.21.8's bodies (javap
 * -c): each getter answers a list the constructor built from the model's parts, in 1.21.8's order -- tendrils
 * (field_38319, field_38318), heart (field_38316), bioluminescent (field_38317, field_38321, field_38322, field_38320,
 * field_38323), pulsating spots (field_38316 and the bioluminescent five); the same parts 1.21.9's model still holds.
 * The lists keep 1.21.8's names, one instance per model as on 1.21.8, built at the end of the constructor (where
 * 1.21.8's constructor body filled them) by CbWardenModelRestoredMixinHooks, at CbVanillaReads.ROUTE_FIRST so the
 * fill runs before any mod's constructor RETURN injector, which on 1.21.8 already saw the lists. This restorer stays at
 * CbVanillaReads.PRIORITY. 1.21.9 never calls the getters.
 */
@Mixin(value = class_7280.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWardenModelRestoredMixin implements CbPrioritySplitClient.WardenLists {

    @Shadow
    @Final
    protected class_630 field_38316;

    @Shadow
    @Final
    protected class_630 field_38317;

    @Shadow
    @Final
    protected class_630 field_38318;

    @Shadow
    @Final
    protected class_630 field_38319;

    @Shadow
    @Final
    protected class_630 field_38320;

    @Shadow
    @Final
    protected class_630 field_38321;

    @Shadow
    @Final
    protected class_630 field_38322;

    @Shadow
    @Final
    protected class_630 field_38323;

    private List<class_630> field_38451;
    private List<class_630> field_38452;
    private List<class_630> field_38453;
    private List<class_630> field_38454;

    @Override
    public void cb$v1_21_9$fillWardenLists() {
        this.field_38451 = ImmutableList.of(this.field_38319, this.field_38318);
        this.field_38452 = ImmutableList.of(this.field_38316);
        this.field_38453 = ImmutableList.of(this.field_38317, this.field_38321, this.field_38322, this.field_38320, this.field_38323);
        this.field_38454 = ImmutableList.of(this.field_38316, this.field_38317, this.field_38321, this.field_38322, this.field_38320,
            this.field_38323);
    }

    /** WardenModel.getTendrilsLayerModelParts(state) */
    public List<class_630> method_42738(class_10081 state) {
        return this.field_38451;
    }

    /** WardenModel.getHeartLayerModelParts(state) */
    public List<class_630> method_42739(class_10081 state) {
        return this.field_38452;
    }

    /** WardenModel.getBioluminescentLayerModelParts(state) */
    public List<class_630> method_42740(class_10081 state) {
        return this.field_38453;
    }

    /** WardenModel.getPulsatingSpotsLayerModelParts(state) */
    public List<class_630> method_42741(class_10081 state) {
        return this.field_38454;
    }
}
