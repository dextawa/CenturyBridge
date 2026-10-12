package top.dext.centurybridge.shims.v1_20_2;

import com.mojang.serialization.Lifecycle;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.compat.fapi.event.registry.FabricRegistry;
import top.dext.centurybridge.compat.fapi.impl.registry.sync.CbFapiRegistryAliases;

/**
 * Registry aliases on SimpleRegistry, as 1.20.1's registry sync SimpleRegistryMixin had them: addAlias
 * with 1.20.1's checks (not already an alias, not a registered id, no cycle, registry not frozen; a
 * missing target is only warned about) and chain collapsing; the id and key lookups (get, containsId,
 * get / getEntry / getOrCreateEntry / contains by key) resolve an alias first; and registering an id
 * that is an alias is refused right after the entry is added, where 1.20.1's entry-added listener
 * refused it.
 */
@Mixin(net.minecraft.class_2370.class)
abstract class CbFapiSimpleRegistryAliases<T> implements FabricRegistry {

    @Shadow @Final private Map<net.minecraft.class_2960, net.minecraft.class_6880.class_6883<T>> field_11107;
    @Shadow @Final net.minecraft.class_5321<? extends net.minecraft.class_2378<T>> field_41126;

    @Shadow
    protected abstract void method_45939();

    @Unique
    private final Map<net.minecraft.class_2960, net.minecraft.class_2960> cb$aliases = new HashMap<>();

    @Override
    public void addAlias(net.minecraft.class_2960 old, net.minecraft.class_2960 newId) {
        Objects.requireNonNull(old, "alias cannot be null");
        Objects.requireNonNull(newId, "aliased id cannot be null");
        if (cb$aliases.containsKey(old)) {
            throw new IllegalArgumentException("Tried adding %s as an alias for %s, but it is already an alias (for %s) in registry %s"
                .formatted(old, newId, cb$aliases.get(old), field_41126));
        }
        if (field_11107.containsKey(old)) {
            throw new IllegalArgumentException("Tried adding %s as an alias, but it is already present in registry %s"
                .formatted(old, field_41126));
        }
        if (old.equals(cb$aliases.get(newId))) {
            throw new IllegalArgumentException("Making %1$s an alias of %2$s would create a cycle, as %2$s is already an alias of %1$s (registry %3$s)"
                .formatted(old, newId, field_41126));
        }
        if (!field_11107.containsKey(newId)) {
            CbFapiRegistryAliases.warnMissingTarget(old, newId, field_41126);
        }
        method_45939();
        net.minecraft.class_2960 target = cb$aliases.getOrDefault(newId, newId);
        for (Map.Entry<net.minecraft.class_2960, net.minecraft.class_2960> e : cb$aliases.entrySet()) {
            if (old.equals(e.getValue())) {
                e.setValue(target);
            }
        }
        cb$aliases.put(old, target);
        CbFapiRegistryAliases.debugAdded(old, newId, field_41126);
    }

    @ModifyVariable(method = {"method_10223", "method_10250"}, at = @At("HEAD"), argsOnly = true)
    private net.minecraft.class_2960 cb$aliasIdentifierParameter(net.minecraft.class_2960 original) {
        return cb$aliases.getOrDefault(original, original);
    }

    @ModifyVariable(method = {"method_29107", "method_40264", "method_44298", "method_35842"}, at = @At("HEAD"), argsOnly = true)
    private net.minecraft.class_5321<T> cb$aliasRegistryKeyParameter(net.minecraft.class_5321<T> original) {
        net.minecraft.class_2960 aliased = cb$aliases.get(original.method_29177());
        return aliased == null ? original
            : net.minecraft.class_5321.method_29179(net.minecraft.class_5321.method_29180(original.method_41185()), aliased);
    }

    @Inject(method = "method_10272", at = @At("RETURN"))
    private void cb$refuseAliasRegistration(net.minecraft.class_5321<T> key, T entry, Lifecycle lifecycle,
                                            CallbackInfoReturnable<net.minecraft.class_6880.class_6883<T>> cir) {
        net.minecraft.class_2960 id = key.method_29177();
        if (cb$aliases.containsKey(id)) {
            throw new IllegalArgumentException("Tried registering %s to registry %s, but it is already an alias (for %s)"
                .formatted(id, field_41126, cb$aliases.get(id)));
        }
    }
}
