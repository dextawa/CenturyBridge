package top.dext.centurybridge.shims.v1_20_2;

import com.mojang.serialization.Lifecycle;
import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbPrioritySplit;

/**
 * The injectors into 1.20.2's own SimpleRegistry methods that go with {@link CbFapiSimpleRegistryAliases} (its
 * restored 1.20.1 member stays there, at {@code CbVanillaReads.PRIORITY}): the id and key lookups resolve an alias
 * first, and registering an id that is an alias is refused right after the entry is added.
 */
@Mixin(net.minecraft.class_2370.class)
abstract class CbFapiSimpleRegistryAliasesHooks<T> {

    @Shadow @Final net.minecraft.class_5321<? extends net.minecraft.class_2378<T>> field_41126;

    @ModifyVariable(method = {"method_10223", "method_10250"}, at = @At("HEAD"), argsOnly = true)
    private net.minecraft.class_2960 cb$aliasIdentifierParameter(net.minecraft.class_2960 original) {
        return ((CbPrioritySplit.RegistryAliases) (Object) this).cb$v1_20_2$aliases().getOrDefault(original, original);
    }

    @ModifyVariable(method = {"method_29107", "method_40264", "method_44298", "method_35842"}, at = @At("HEAD"), argsOnly = true)
    private net.minecraft.class_5321<T> cb$aliasRegistryKeyParameter(net.minecraft.class_5321<T> original) {
        net.minecraft.class_2960 aliased = ((CbPrioritySplit.RegistryAliases) (Object) this).cb$v1_20_2$aliases().get(original.method_29177());
        return aliased == null ? original
            : net.minecraft.class_5321.method_29179(net.minecraft.class_5321.method_29180(original.method_41185()), aliased);
    }

    @Inject(method = "method_10272", at = @At("RETURN"))
    private void cb$refuseAliasRegistration(net.minecraft.class_5321<T> key, T entry, Lifecycle lifecycle,
                                            CallbackInfoReturnable<net.minecraft.class_6880.class_6883<T>> cir) {
        net.minecraft.class_2960 id = key.method_29177();
        Map<net.minecraft.class_2960, net.minecraft.class_2960> aliases = ((CbPrioritySplit.RegistryAliases) (Object) this).cb$v1_20_2$aliases();
        if (aliases.containsKey(id)) {
            throw new IllegalArgumentException("Tried registering %s to registry %s, but it is already an alias (for %s)"
                .formatted(id, field_41126, aliases.get(id)));
        }
    }
}
