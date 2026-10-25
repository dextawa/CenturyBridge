package top.dext.centurybridge.step.v1_21_4.mixin;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.facade.ItemOverride;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBlockModel;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelSpecial;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * BlockModel's 1.21.3 members on 1.21.4's BlockModel (see {@link CbModelBlockModel}): the name, texture map and overrides
 * 1.21.4 dropped, and 1.21.3's chain-walking getters and bake. Every BlockModel bakes through 1.21.3's bake, with 1.21.4's
 * result for 1.21.4's own models (cb$legacyBake), so 1.21.3 mods' mixins on bake and the members it asks reach vanilla.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_793.class)
public abstract class CbModelBlockModelMixin implements CbModelBlockModel.CbLegacyBlockModel {

    @Shadow
    @Final
    private List<net.minecraft.class_785> field_4245;

    @Shadow
    @Final
    private net.minecraft.class_1100.class_4751 field_21857;

    @Shadow
    @Final
    private Boolean field_4244;

    @Shadow
    @Final
    private net.minecraft.class_10419.class_10420 field_55272;

    @Shadow
    private net.minecraft.class_1100 field_4253;

    /** parentLocation: 1.21.3's was protected and writable (the layer's access widener gives that back) */
    @Shadow
    private net.minecraft.class_2960 field_4247;

    /** 1.21.3 name (set by the model manager to the model's id) */
    public String field_4252 = "";

    private Map<String, Either<net.minecraft.class_4730, String>> cb$textureMap;

    private List<ItemOverride> cb$overrides = List.of();

    private net.minecraft.class_793 cb$self() {
        return (net.minecraft.class_793) (Object) this;
    }

    // ---- state ----

    @Override
    public List<net.minecraft.class_785> cb$ownElements() {
        return this.field_4245;
    }

    @Override
    public net.minecraft.class_1100.class_4751 cb$ownGuiLight() {
        return this.field_21857;
    }

    @Override
    public net.minecraft.class_1100 cb$parent() {
        return this.field_4253;
    }

    @Override
    public void cb$setParent(net.minecraft.class_1100 parent) {
        this.field_4253 = parent;
    }

    @Override
    public net.minecraft.class_2960 cb$parentLocation() {
        return this.field_4247;
    }

    @Override
    public Map<String, Either<net.minecraft.class_4730, String>> cb$textureMap() {
        if (this.cb$textureMap == null) {
            this.cb$textureMap = CbModelBlockModel.toMap(this.field_55272);
        }
        return this.cb$textureMap;
    }

    @Override
    public void cb$setTextureMap(Map<String, Either<net.minecraft.class_4730, String>> map) {
        this.cb$textureMap = map;
    }

    @Override
    public void cb$setOverrides(List<ItemOverride> overrides) {
        this.cb$overrides = overrides;
    }

    @Override
    public String cb$name() {
        return this.field_4252;
    }

    @Override
    public void cb$setName(String name) {
        this.field_4252 = name;
    }

    /** the texture map, once asked for, is the model's texture slots (a write through it reaches baking, as in 1.21.3) */
    @Inject(method = "method_65539", at = @At("HEAD"), cancellable = true)
    private void cb$slotsFromMap(CallbackInfoReturnable<net.minecraft.class_10419.class_10420> cir) {
        if (this.cb$textureMap != null) {
            cir.setReturnValue(CbModelBlockModel.toData(this.cb$textureMap));
        }
    }

    // ---- 1.21.3 members ----

    /** was getElements() */
    @Override
    public List<net.minecraft.class_785> cb$getElements() {
        return CbModelBlockModel.elements(this);
    }

    /** hasAmbientOcclusion() */
    @CbVanillaReads("CbModelBlockModelMixin: every BlockModel bakes through 1.21.3's bake (cb$legacyBake), which asks it for the builder's ambient occlusion")
    @Override
    public boolean method_3444() {
        return CbModelBlockModel.hasAmbientOcclusion(this, this.field_4244);
    }

    /** isResolved() */
    @Override
    public boolean method_35789() {
        return CbModelBlockModel.isResolved(this);
    }

    /** getOverrides() */
    @Override
    public List<ItemOverride> method_3434() {
        return this.cb$overrides;
    }

    /** bake(baker, spriteGetter, state) */
    @CbVanillaReads("CbModelBlockModelMixin: 1.21.4's bake of every BlockModel enters it (cb$legacyBake), as 1.21.3's model bakery did")
    @Override
    public net.minecraft.class_1087 method_4753(net.minecraft.class_7775 baker, Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites,
            net.minecraft.class_3665 state) {
        return this.method_3446(sprites, state, true);
    }

    /**
     * bake(spriteGetter, state, guiLight3d): 1.21.3's body, inline (a 1.21.3 mixin anchored in bake finds its getMaterial,
     * getRootModel, getTransforms, sprite getter, element / face and builder calls here, in 1.21.3's order): the builtin/entity
     * marker's chain is a BuiltInModel, any other a SimpleBakedModel of the chain's elements, every member asked through the
     * model (a subclass's overrides count). The chain-walking members are 1.21.3's ({@link CbModelBlockModel}).
     */
    @CbVanillaReads("CbModelBlockModelMixin: 1.21.4's bake of every BlockModel enters it through bake(baker, sprites, state) (cb$legacyBake)")
    @Override
    public net.minecraft.class_1087 method_3446(Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites, net.minecraft.class_3665 state,
            boolean guiLight3d) {
        net.minecraft.class_1058 particle = sprites.apply(this.method_24077(CbModelBlockModel.PARTICLE));
        if (this.method_3431() == top.dext.centurybridge.step.v1_21_4.facade.SpecialModels.field_53680) {
            return new top.dext.centurybridge.step.v1_21_4.facade.BuiltInModel(this.method_3443(), particle,
                CbModelBlockModel.method_24298(cb$self()).method_24299());
        }
        net.minecraft.class_1093.class_1094 builder = new net.minecraft.class_1093.class_1094(this.method_3444(),
            CbModelBlockModel.method_24298(cb$self()).method_24299(), guiLight3d, this.method_3443()).method_4747(particle);
        for (net.minecraft.class_785 element : this.cb$getElements()) {
            for (net.minecraft.class_2350 direction : element.field_4230.keySet()) {
                net.minecraft.class_783 face = element.field_4230.get(direction);
                net.minecraft.class_1058 sprite = sprites.apply(this.method_24077(face.comp_2869()));
                if (face.comp_2867() == null) {
                    builder.method_4748(CbModelBlockModel.bakeFace(element, face, sprite, direction, state));
                } else {
                    builder.method_4745(net.minecraft.class_2350.method_23225(state.method_3509().method_22936(), face.comp_2867()),
                        CbModelBlockModel.bakeFace(element, face, sprite, direction, state));
                }
            }
        }
        return builder.method_4746();
    }

    /** hasTexture(name) */
    @Override
    public boolean method_3432(String name) {
        return CbModelBlockModel.hasTexture(this, name);
    }

    /** getMaterial(name) */
    @CbVanillaReads("CbModelBlockModelMixin: every BlockModel bakes through 1.21.3's bake (cb$legacyBake), which resolves the particle and every face's material through it")
    @Override
    public net.minecraft.class_4730 method_24077(String name) {
        return CbModelBlockModel.material(cb$self(), name, this.field_4252);
    }

    /** getRootModel() */
    @CbVanillaReads("CbModelBlockModelMixin: every BlockModel bakes through 1.21.3's bake (cb$legacyBake), which asks it for the builtin/entity marker")
    @Override
    public net.minecraft.class_793 method_3431() {
        return CbModelBlockModel.rootModel(cb$self());
    }

    /** getTransforms() */
    @CbVanillaReads("CbModelBlockModelMixin: every BlockModel bakes through 1.21.3's bake (cb$legacyBake), which gives the builder its transforms")
    @Override
    public net.minecraft.class_809 method_3443() {
        return CbModelBlockModel.transforms(cb$self());
    }

    /**
     * 1.21.4's bake. A chain ending in GENERATED_MARKER bakes through the item model generator (1.21.3's model bakery did).
     * Every other BlockModel -- 1.21.4's own models included -- bakes through 1.21.3's bake(baker, sprites, state) with the
     * baker's sprites, as 1.21.3's model bakery baked every top model, so a 1.21.3 mod's mixin on bake / getMaterial /
     * getTransforms / hasAmbientOcclusion / getRootModel changes vanilla's models as it did on 1.21.3. 1.21.3's bake over
     * 1.21.4's chain gives 1.21.4's model: the chain's elements (1.21.4 hands an elementless model's bake to its parent),
     * materials resolved through the chain's texture slots (missing references the missing sprite), the chain's ambient
     * occlusion, gui light and per-context transforms (what 1.21.4's caller passes, UnbakedModel.bakeWithTopModelValues),
     * gui-light-3d on, and each face baked by FaceBakery.bakeQuad exactly as SimpleBakedModel.bakeFace does (javap). Only
     * the top model's bake is entered: the route answers before 1.21.4's body hands an elementless model to its parent.
     */
    @Inject(method = "method_4753(Lnet/minecraft/class_10419;Lnet/minecraft/class_7775;Lnet/minecraft/class_3665;ZZLnet/minecraft/class_809;)Lnet/minecraft/class_1087;",
            at = @At("HEAD"), cancellable = true)
    private void cb$legacyBake(net.minecraft.class_10419 slots, net.minecraft.class_7775 baker, net.minecraft.class_3665 state, boolean ambientOcclusion,
            boolean usesBlockLight, net.minecraft.class_809 transforms, CallbackInfoReturnable<net.minecraft.class_1087> cir) {
        boolean subclass = (Object) this.getClass() != net.minecraft.class_793.class;
        net.minecraft.class_793 root = subclass ? this.method_3431() : CbModelBlockModel.lastBlockModel(cb$self());
        if (root == CbModelSpecial.generatedMarker()) {
            cir.setReturnValue(CbModelSpecial.generator().method_4753(slots, baker, state, ambientOcclusion, usesBlockLight, transforms));
            return;
        }
        net.minecraft.class_10527 sprites = baker.method_65732();
        cir.setReturnValue(this.method_4753(baker, sprites::method_65741, state));
    }
}
