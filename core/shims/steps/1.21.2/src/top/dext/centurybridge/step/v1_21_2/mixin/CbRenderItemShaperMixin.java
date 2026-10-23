package top.dext.centurybridge.step.v1_21_2.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderItemModels;

/**
 * 1.21.1 ItemModelShaper over 1.21.2's (class_763).
 *
 * 1.21.1: an item's model was the ModelResourceLocation registered for it (register(item, location), kept in the public
 * Int2ObjectMap shapes by item id), baked into a cache by rebuildCache() (at every model reload); getItemModel(item)
 * answered the cache, getItemModel(stack) the item's, or the missing model. 1.21.2: a stack's model is the one its
 * item_model component names. Kept here as 1.21.1 kept them: shapes and the cache; a registered item is drawn with its
 * registered model (getItemModel(stack) answers it, missing model when not yet cached, as 1.21.1); any other item with
 * 1.21.2's own model -- the model 1.21.1 registered for every vanilla item is the one its default item_model names.
 * The cache is rebuilt whenever 1.21.2 rebuilds its own (invalidateCache, at model reload).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_763.class)
public abstract class CbRenderItemShaperMixin implements CbRenderItemModels.Shaper {

    @Unique
    private net.minecraft.class_1092 cb$modelManager;

    @Unique
    private final Int2ObjectMap<net.minecraft.class_1091> cb$shapes = new Int2ObjectOpenHashMap<>(256);

    @Unique
    private final Int2ObjectMap<net.minecraft.class_1087> cb$cache = new Int2ObjectOpenHashMap<>(256);

    @Inject(method = "<init>(Lnet/minecraft/class_1092;)V", at = @At("TAIL"))
    private void cb$keepManager(net.minecraft.class_1092 manager, CallbackInfo ci) {
        this.cb$modelManager = manager;
    }

    @Override
    public Int2ObjectMap<net.minecraft.class_1091> cb$shapes() {
        return this.cb$shapes;
    }

    /** 1.21.1 ItemModelShaper.getItemModel(item) */
    public net.minecraft.class_1087 method_3304(net.minecraft.class_1792 item) {
        int id = net.minecraft.class_1792.method_7880(item);
        if (this.cb$shapes.containsKey(id)) {
            return this.cb$cache.get(id);
        }
        return ((net.minecraft.class_763) (Object) this).method_3308(new net.minecraft.class_1799(item));
    }

    /** 1.21.1 ItemModelShaper.register(item, location) */
    public void method_3309(net.minecraft.class_1792 item, net.minecraft.class_1091 location) {
        this.cb$shapes.put(net.minecraft.class_1792.method_7880(item), location);
    }

    /** 1.21.1 ItemModelShaper.getModelManager() */
    public net.minecraft.class_1092 method_3303() {
        return this.cb$modelManager;
    }

    /** 1.21.1 ItemModelShaper.rebuildCache() */
    public void method_3310() {
        this.cb$cache.clear();
        for (Int2ObjectMap.Entry<net.minecraft.class_1091> e : this.cb$shapes.int2ObjectEntrySet()) {
            this.cb$cache.put(e.getIntKey(), this.cb$modelManager.method_4742(e.getValue()));
        }
    }

    @Inject(method = "method_64056()V", at = @At("TAIL"))
    private void cb$rebuildWithVanilla(CallbackInfo ci) {
        if (this.cb$modelManager != null) {
            this.method_3310();
        }
    }

    @Inject(method = "method_3308(Lnet/minecraft/class_1799;)Lnet/minecraft/class_1087;", at = @At("HEAD"), cancellable = true)
    private void cb$registeredModel(net.minecraft.class_1799 stack, CallbackInfoReturnable<net.minecraft.class_1087> cir) {
        int id = net.minecraft.class_1792.method_7880(stack.method_7909());
        if (this.cb$shapes.containsKey(id)) {
            net.minecraft.class_1087 m = this.cb$cache.get(id);
            cir.setReturnValue(m == null ? this.cb$modelManager.method_4744() : m);
        }
    }
}
