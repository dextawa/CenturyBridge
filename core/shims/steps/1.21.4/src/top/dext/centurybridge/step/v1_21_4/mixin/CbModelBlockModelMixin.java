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
import top.dext.centurybridge.step.v1_21_4.facade.ItemOverride;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBlockModel;
import top.dext.centurybridge.step.v1_21_4.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * BlockModel's 1.21.3 members on 1.21.4's BlockModel (see {@link CbModelBlockModel}): the name, texture map and overrides
 * 1.21.4 dropped, and 1.21.3's chain-walking getters and bake. 1.21.4's own baking stays for its own models; a mod's
 * subclass, and a chain ending in one of 1.21.3's builtin markers, bake as 1.21.3 baked them (CbModelBlockModelMixinHooks,
 * default priority).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_793.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbModelBlockModelMixin implements CbModelBlockModel.CbLegacyBlockModel, CbPrioritySplitClient.BlockModelLegacy {

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

    /** the texture map as set (null until asked for or set), for 1.21.4's getTextureSlots (CbModelBlockModelMixinHooks) */
    @Override
    public Map<String, Either<net.minecraft.class_4730, String>> cb$v1_21_4$textureMapIfSet() {
        return this.cb$textureMap;
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

    /** 1.21.3's getRootModel called virtually, for 1.21.4's bake (CbModelBlockModelMixinHooks) */
    @Override
    public net.minecraft.class_793 cb$v1_21_4$rootModel() {
        return this.method_3431();
    }

    /** 1.21.3's bake(baker, spriteGetter, state) called virtually, for 1.21.4's bake (CbModelBlockModelMixinHooks) */
    @Override
    public net.minecraft.class_1087 cb$v1_21_4$bake(net.minecraft.class_7775 baker, Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites,
            net.minecraft.class_3665 state) {
        return this.method_4753(baker, sprites, state);
    }
}
