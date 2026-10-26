package top.dext.centurybridge.step.v1_21_2.mixin;

import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.class_2370;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.facade.fapi.FabricRegistry;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * Registry aliases of 1.21.1's Fabric API (0.116's SimpleRegistryMixin, Apache-2.0, FabricMC), on 1.21.2's
 * MappedRegistry: addAlias with 0.116's checks and invariant (every alias points to a non-alias id), the id
 * and key parameters of the lookups 0.116 aliased resolved through the alias map at their start (1.21.2's
 * names of the same lookups: get / getValue / containsKey by id, getValue / getOrCreateHolderOrThrow /
 * containsKey / registrationInfo by key; 1.21.2's getOptional(key) reaches getValue(key)), and registering an
 * id that is an alias refused, as 0.116 refused it when the entry was added.
 * The injectors into 1.21.2's lookups and register are in CbRestFapiRegistryAliasMixinHooks (default priority).
 */
@Mixin(value = class_2370.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestFapiRegistryAliasMixin<T> implements FabricRegistry, CbPrioritySplit.RegistryAliases {

    @Unique
    private static final Logger CB_LOGGER = LogUtils.getLogger();

    @Shadow
    @Final
    private class_5321<? extends class_2378<T>> field_41126;

    @Shadow
    @Final
    private Map<class_2960, class_6880.class_6883<T>> field_11107;

    @Shadow
    private void method_45939() {
    }

    @Unique
    private final Map<class_2960, class_2960> cb$aliases = new HashMap<>();

    @Override
    public Map<class_2960, class_2960> cb$v1_21_2$aliases() {
        return this.cb$aliases;
    }

    @Override
    public void addAlias(class_2960 old, class_2960 newId) {
        Objects.requireNonNull(old, "alias cannot be null");
        Objects.requireNonNull(newId, "aliased id cannot be null");
        if (this.cb$aliases.containsKey(old)) {
            throw new IllegalArgumentException("Tried adding %s as an alias for %s, but it is already an alias (for %s) in registry %s"
                .formatted(old, newId, this.cb$aliases.get(old), this.field_41126));
        }
        if (this.field_11107.containsKey(old)) {
            throw new IllegalArgumentException("Tried adding %s as an alias, but it is already present in registry %s".formatted(old, this.field_41126));
        }
        if (old.equals(this.cb$aliases.get(newId))) {
            throw new IllegalArgumentException(
                "Making %1$s an alias of %2$s would create a cycle, as %2$s is already an alias of %1$s (registry %3$s)".formatted(old, newId, this.field_41126));
        }
        if (!this.field_11107.containsKey(newId)) {
            CB_LOGGER.warn("Adding {} as an alias for {}, but the latter doesn't exist in registry {}", old, newId, this.field_41126);
        }
        method_45939();
        class_2960 deepest = this.cb$aliases.getOrDefault(newId, newId);
        for (Map.Entry<class_2960, class_2960> entry : this.cb$aliases.entrySet()) {
            if (old.equals(entry.getValue())) {
                entry.setValue(deepest);
            }
        }
        this.cb$aliases.put(old, deepest);
        CB_LOGGER.debug("Adding alias {} for {} in registry {}", old, newId, this.field_41126);
    }
}
