package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBlockModel;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelSpecial;
import top.dext.centurybridge.step.v1_21_4.rt.CbPrioritySplitClient;

/**
 * The injectors into 1.21.4's own BlockModel methods that go with {@link CbModelBlockModelMixin} (its restored 1.21.3
 * members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.3 injectors can anchor inside them): the
 * texture map, once asked for, is the model's texture slots; a mod's subclass, and a chain ending in one of 1.21.3's
 * builtin markers, bake as 1.21.3 baked them.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_793.class)
public abstract class CbModelBlockModelMixinHooks {

    /** the texture map, once asked for, is the model's texture slots (a write through it reaches baking, as in 1.21.3) */
    @Inject(method = "method_65539", at = @At("HEAD"), cancellable = true)
    private void cb$slotsFromMap(CallbackInfoReturnable<net.minecraft.class_10419.class_10420> cir) {
        java.util.Map<String, com.mojang.datafixers.util.Either<net.minecraft.class_4730, String>> map =
            ((CbPrioritySplitClient.BlockModelLegacy) (Object) this).cb$v1_21_4$textureMapIfSet();
        if (map != null) {
            cir.setReturnValue(CbModelBlockModel.toData(map));
        }
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
        CbPrioritySplitClient.BlockModelLegacy legacy = (CbPrioritySplitClient.BlockModelLegacy) (Object) this;
        boolean subclass = (Object) this.getClass() != net.minecraft.class_793.class;
        // a vanilla model keeps 1.21.4's bake unless its chain of BlockModels ends in one of 1.21.3's markers; a mod's
        // subclass is asked its root as 1.21.3's model bakery asked it
        net.minecraft.class_793 root = subclass ? legacy.cb$v1_21_4$rootModel() : CbModelBlockModel.lastBlockModel((net.minecraft.class_793) (Object) this);
        if (root == CbModelSpecial.generatedMarker()) {
            cir.setReturnValue(CbModelSpecial.generator().method_4753(slots, baker, state, ambientOcclusion, usesBlockLight, transforms));
        } else if (subclass || root == CbModelSpecial.blockEntityMarker()) {
            net.minecraft.class_10527 sprites = baker.method_65732();
            cir.setReturnValue(legacy.cb$v1_21_4$bake(baker, sprites::method_65741, state));
        }
    }
}
