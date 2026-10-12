package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemTags;

/**
 * Keeps an item stack's 1.20.4 tag view (CbItemTags) in step with its components: every ItemStack
 * method that reads the component map first takes the view's pending changes (flush), every method
 * that writes it then updates the view (refresh). These are all the places that touch the map
 * (field "components" is read only inside ItemStack and its stream codec, CbItemStackStreamMixin):
 * getComponents (and through it get / has / getOrDefault), getComponentsPatch, copy,
 * transmuteCopyIgnoreEmpty, isSameItemSameComponents, getTooltipLines, the two codec getters; set,
 * remove, applyComponents, applyComponentsAndValidate (update goes through set).
 */
@Mixin(net.minecraft.class_1799.class)
public abstract class CbItemStackMixin implements CbItemTags.State {

    @Shadow
    @Final
    net.minecraft.class_9335 field_49270;

    @Unique
    private net.minecraft.class_2487 cb$s4tag;

    @Unique
    private net.minecraft.class_2487 cb$s4snap;

    @Unique
    private boolean cb$s4busy;

    @Override
    public net.minecraft.class_2487 cb$s4Tag() {
        return cb$s4tag;
    }

    @Override
    public void cb$s4Tag(net.minecraft.class_2487 tag) {
        cb$s4tag = tag;
    }

    @Override
    public net.minecraft.class_2487 cb$s4Snap() {
        return cb$s4snap;
    }

    @Override
    public void cb$s4Snap(net.minecraft.class_2487 snap) {
        cb$s4snap = snap;
    }

    @Override
    public boolean cb$s4Busy() {
        return cb$s4busy;
    }

    @Override
    public void cb$s4Busy(boolean busy) {
        cb$s4busy = busy;
    }

    @Override
    public net.minecraft.class_9335 cb$s4Components() {
        return field_49270;
    }

    @Unique
    private net.minecraft.class_1799 cb$self() {
        return (net.minecraft.class_1799) (Object) this;
    }

    // ---------------------------------------------------------------- reads: flush first

    /** getComponents() */
    @Inject(method = "method_57353", at = @At("HEAD"))
    private void cb$getComponents(CallbackInfoReturnable<?> cir) {
        CbItemTags.flush(cb$self());
    }

    /** getComponentsPatch() */
    @Inject(method = "method_57380", at = @At("HEAD"))
    private void cb$getComponentsPatch(CallbackInfoReturnable<?> cir) {
        CbItemTags.flush(cb$self());
    }

    /** copy() */
    @Inject(method = "method_7972", at = @At("HEAD"))
    private void cb$copy(CallbackInfoReturnable<?> cir) {
        CbItemTags.flush(cb$self());
    }

    /** transmuteCopyIgnoreEmpty(ItemLike, int) */
    @Inject(method = "method_56704", at = @At("HEAD"))
    private void cb$transmute(net.minecraft.class_1935 item, int count, CallbackInfoReturnable<?> cir) {
        CbItemTags.flush(cb$self());
    }

    /** getTooltipLines(TooltipContext, Player, TooltipFlag) */
    @Inject(method = "method_7950", at = @At("HEAD"))
    private void cb$tooltip(net.minecraft.class_1792.class_9635 context, net.minecraft.class_1657 player,
                            net.minecraft.class_1836 flag, CallbackInfoReturnable<?> cir) {
        CbItemTags.flush(cb$self());
    }

    /** isSameItemSameComponents(ItemStack, ItemStack) */
    @Inject(method = "method_31577", at = @At("HEAD"))
    private static void cb$same(net.minecraft.class_1799 a, net.minecraft.class_1799 b, CallbackInfoReturnable<Boolean> cir) {
        CbItemTags.flush(a);
        CbItemTags.flush(b);
    }

    /** CODEC's components getter (stack -> stack.components.asPatch()) */
    @Inject(method = "method_58094", at = @At("HEAD"))
    private static void cb$codec(net.minecraft.class_1799 stack, CallbackInfoReturnable<?> cir) {
        CbItemTags.flush(stack);
    }

    /** SINGLE_ITEM_CODEC's components getter */
    @Inject(method = "method_57385", at = @At("HEAD"))
    private static void cb$singleCodec(net.minecraft.class_1799 stack, CallbackInfoReturnable<?> cir) {
        CbItemTags.flush(stack);
    }

    // ---------------------------------------------------------------- writes: flush first, refresh after

    /** set(DataComponentType, Object) */
    @Inject(method = "method_57379", at = @At("HEAD"))
    private void cb$setHead(net.minecraft.class_9331<?> type, Object value, CallbackInfoReturnable<?> cir) {
        CbItemTags.flush(cb$self());
    }

    @Inject(method = "method_57379", at = @At("RETURN"))
    private void cb$setTail(net.minecraft.class_9331<?> type, Object value, CallbackInfoReturnable<?> cir) {
        CbItemTags.refresh(cb$self());
    }

    /** remove(DataComponentType) */
    @Inject(method = "method_57381", at = @At("HEAD"))
    private void cb$removeHead(net.minecraft.class_9331<?> type, CallbackInfoReturnable<?> cir) {
        CbItemTags.flush(cb$self());
    }

    @Inject(method = "method_57381", at = @At("RETURN"))
    private void cb$removeTail(net.minecraft.class_9331<?> type, CallbackInfoReturnable<?> cir) {
        CbItemTags.refresh(cb$self());
    }

    /** applyComponents(DataComponentMap) */
    @Inject(method = "method_57365", at = @At("HEAD"))
    private void cb$applyMapHead(net.minecraft.class_9323 map, CallbackInfo ci) {
        CbItemTags.flush(cb$self());
    }

    @Inject(method = "method_57365", at = @At("RETURN"))
    private void cb$applyMapTail(net.minecraft.class_9323 map, CallbackInfo ci) {
        CbItemTags.refresh(cb$self());
    }

    /** applyComponents(DataComponentPatch) */
    @Inject(method = "method_57366", at = @At("HEAD"))
    private void cb$applyPatchHead(net.minecraft.class_9326 patch, CallbackInfo ci) {
        CbItemTags.flush(cb$self());
    }

    @Inject(method = "method_57366", at = @At("RETURN"))
    private void cb$applyPatchTail(net.minecraft.class_9326 patch, CallbackInfo ci) {
        CbItemTags.refresh(cb$self());
    }

    /** applyComponentsAndValidate(DataComponentPatch) */
    @Inject(method = "method_59692", at = @At("HEAD"))
    private void cb$validateHead(net.minecraft.class_9326 patch, CallbackInfo ci) {
        CbItemTags.flush(cb$self());
    }

    @Inject(method = "method_59692", at = @At("RETURN"))
    private void cb$validateTail(net.minecraft.class_9326 patch, CallbackInfo ci) {
        CbItemTags.refresh(cb$self());
    }
}
