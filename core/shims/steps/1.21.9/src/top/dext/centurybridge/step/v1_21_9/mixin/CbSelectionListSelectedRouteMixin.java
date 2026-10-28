package top.dext.centurybridge.step.v1_21_9.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.class_332;
import net.minecraft.class_350;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_9.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21_9.rt.CbSelectionLists;

/**
 * 1.21.8's renderItem drew the selection box when isSelectedItem(index) answered true (javap: 1.21.8
 * AbstractSelectionList.renderItem @37); 1.21.9's draws it when entriesCanBeSelected() and the entry is getSelected()
 * (1.21.9 renderItem @0..13). For a list whose class a converted mod wrote with a 1.21.8 isSelectedItem override
 * ({@link CbSelectionLists.LegacyIsSelectedItem}, the step's adapter hook), or when a converted mod mixes the restored
 * isSelectedItem (CbMixedMembers), the test asks the restored isSelectedItem(index of the entry) instead: the
 * entriesCanBeSelected() value becomes its answer and getSelected() the entry itself, so the box is drawn exactly when
 * isSelectedItem says so. Every other list keeps 1.21.9's values (both are expression modifiers: native 1.21.9 mixins
 * on those calls and on renderItem still apply). The same head also draws a 1.21.8 entry's renderBack override
 * ({@link CbSelectionLists.LegacyRenderBack}) where 1.21.8's renderItem drew it, before the selection box.
 */
@Mixin(class_350.class)
public abstract class CbSelectionListSelectedRouteMixin {

    @Unique
    private class_350.class_351<?> cb$rowEntry;

    @Unique
    private boolean cb$legacySelected() {
        return this instanceof CbSelectionLists.LegacyIsSelectedItem || CbMixedMembers.mixed(CbSelectionLists.IS_SELECTED_ITEM);
    }

    @Shadow
    protected abstract class_350.class_351<?> method_37019();

    @Inject(method = "method_44397(Lnet/minecraft/class_332;IIFLnet/minecraft/class_350$class_351;)V", at = @At("HEAD"))
    private void cb$row(class_332 graphics, int mouseX, int mouseY, float delta, class_350.class_351<?> entry, CallbackInfo ci) {
        this.cb$rowEntry = entry;
        if (entry instanceof CbSelectionLists.LegacyRenderBack back) {
            // 1.21.8's renderItem drew renderBack first, with the entry's index and row and hovered = it is the hovered one
            int index = ((class_350<?>) (Object) this).method_25396().indexOf(entry);
            back.method_49568(graphics, index, entry.method_73382(), entry.method_73380(), entry.method_25368(), entry.method_73384(),
                mouseX, mouseY, this.method_37019() == entry, delta);
        }
    }

    @ModifyExpressionValue(method = "method_44397(Lnet/minecraft/class_332;IIFLnet/minecraft/class_350$class_351;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_350;method_73379()Z"))
    private boolean cb$selectable(boolean original) {
        if (!this.cb$legacySelected()) {
            return original;
        }
        int index = ((class_350<?>) (Object) this).method_25396().indexOf(this.cb$rowEntry);
        return index >= 0 && ((CbSelectionLists.LegacyHeader) this).cb$isSelectedItem(index);
    }

    @ModifyExpressionValue(method = "method_44397(Lnet/minecraft/class_332;IIFLnet/minecraft/class_350$class_351;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_350;method_25334()Lnet/minecraft/class_350$class_351;"))
    private class_350.class_351<?> cb$selected(class_350.class_351<?> original) {
        return this.cb$legacySelected() ? this.cb$rowEntry : original;
    }
}
