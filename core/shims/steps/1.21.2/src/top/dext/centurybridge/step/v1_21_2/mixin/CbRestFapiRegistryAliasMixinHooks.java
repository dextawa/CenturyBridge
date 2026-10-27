package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Map;
import net.minecraft.class_2370;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_9248;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplit;

/**
 * The injectors into 1.21.2's own MappedRegistry methods that go with {@link CbRestFapiRegistryAliasMixin} (its
 * restored 0.116 member stays there, at {@code CbVanillaReads.PRIORITY}): the lookups' id and key parameters resolved
 * through the alias map at their start, and registering an id that is an alias refused.
 */
@Mixin(class_2370.class)
public abstract class CbRestFapiRegistryAliasMixinHooks<T> {

    @Shadow
    @Final
    private class_5321<? extends class_2378<T>> field_41126;

    @ModifyVariable(method = {"method_10223", "method_63535", "method_10250"}, at = @At("HEAD"), argsOnly = true)
    private class_2960 cb$aliasId(class_2960 original) {
        return ((CbPrioritySplit.RegistryAliases) (Object) this).cb$v1_21_2$aliases().getOrDefault(original, original);
    }

    @ModifyVariable(method = {"method_29107", "method_44298", "method_35842", "method_57058"}, at = @At("HEAD"), argsOnly = true)
    private class_5321<T> cb$aliasKey(class_5321<T> original) {
        if (original == null) {
            return null;
        }
        class_2960 aliased = ((CbPrioritySplit.RegistryAliases) (Object) this).cb$v1_21_2$aliases().get(original.method_29177());
        return aliased == null ? original : class_5321.method_29179(this.field_41126, aliased);
    }

    @Inject(method = "method_10272", at = @At("HEAD"))
    private void cb$refuseAliasId(class_5321<T> key, T value, class_9248 info, CallbackInfoReturnable<class_6880.class_6883<T>> cir) {
        Map<class_2960, class_2960> aliases = ((CbPrioritySplit.RegistryAliases) (Object) this).cb$v1_21_2$aliases();
        if (aliases.containsKey(key.method_29177())) {
            throw new IllegalArgumentException("Tried registering %s to registry %s, but it is already an alias (for %s)"
                .formatted(key.method_29177(), this.field_41126, aliases.get(key.method_29177())));
        }
    }
}
