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

/**
 * BlockModel's 1.21.3 members on 1.21.4's BlockModel (see {@link CbModelBlockModel}): the name, texture map and overrides
 * 1.21.4 dropped, and 1.21.3's chain-walking getters and bake. 1.21.4's own baking stays for its own models; a mod's
 * subclass, and a chain ending in one of 1.21.3's builtin markers, bake as 1.21.3 baked them.
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
    @Override
    public net.minecraft.class_1087 method_4753(net.minecraft.class_7775 baker, Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites,
            net.minecraft.class_3665 state) {
        return this.method_3446(sprites, state, true);
    }

    /** bake(spriteGetter, state, guiLight3d) */
    @Override
    public net.minecraft.class_1087 method_3446(Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites, net.minecraft.class_3665 state,
            boolean guiLight3d) {
        return CbModelBlockModel.bake(cb$self(), sprites, state, guiLight3d);
    }

    /** hasTexture(name) */
    @Override
    public boolean method_3432(String name) {
        return CbModelBlockModel.hasTexture(this, name);
    }

    /** getMaterial(name) */
    @Override
    public net.minecraft.class_4730 method_24077(String name) {
        return CbModelBlockModel.material(cb$self(), name, this.field_4252);
    }

    /** getRootModel() */
    @Override
    public net.minecraft.class_793 method_3431() {
        return CbModelBlockModel.rootModel(cb$self());
    }

    /** getTransforms() */
    @Override
    public net.minecraft.class_809 method_3443() {
        return CbModelBlockModel.transforms(cb$self());
    }

    /**
     * 1.21.4's bake: a chain ending in GENERATED_MARKER bakes through the item model generator (1.21.3's model bakery did);
     * a mod's subclass, and a chain ending in BLOCK_ENTITY_MARKER, bake as 1.21.3 baked (1.21.3's bake through the model's
     * own members, with the baker's sprites)
     */
    @Inject(method = "method_4753(Lnet/minecraft/class_10419;Lnet/minecraft/class_7775;Lnet/minecraft/class_3665;ZZLnet/minecraft/class_809;)Lnet/minecraft/class_1087;",
            at = @At("HEAD"), cancellable = true)
    private void cb$legacyBake(net.minecraft.class_10419 slots, net.minecraft.class_7775 baker, net.minecraft.class_3665 state, boolean ambientOcclusion,
            boolean usesBlockLight, net.minecraft.class_809 transforms, CallbackInfoReturnable<net.minecraft.class_1087> cir) {
        boolean subclass = (Object) this.getClass() != net.minecraft.class_793.class;
        // a vanilla model keeps 1.21.4's bake unless its chain of BlockModels ends in one of 1.21.3's markers; a mod's
        // subclass is asked its root as 1.21.3's model bakery asked it
        net.minecraft.class_793 root = subclass ? this.method_3431() : CbModelBlockModel.lastBlockModel(cb$self());
        if (root == CbModelSpecial.generatedMarker()) {
            cir.setReturnValue(CbModelSpecial.generator().method_4753(slots, baker, state, ambientOcclusion, usesBlockLight, transforms));
        } else if (subclass || root == CbModelSpecial.blockEntityMarker()) {
            net.minecraft.class_10527 sprites = baker.method_65732();
            cir.setReturnValue(this.method_4753(baker, sprites::method_65741, state));
        }
    }
}
