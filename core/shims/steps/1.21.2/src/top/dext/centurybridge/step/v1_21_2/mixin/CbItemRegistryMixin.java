package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Map;
import net.minecraft.class_2370;
import net.minecraft.class_6862;
import net.minecraft.class_6885;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRegistries;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * MappedRegistry: whether it froze, and its registration-time tag creation (CbItemRegistries.getOrCreateTag).
 *
 * <p>1.21.1's bindTags(map), resetTags() and getOrCreateTag(tag) restored as members, so a mod's mixin on them has a
 * method to inject into and a mod's call reaches it (CbItemRegistries dispatches through the Legacy* interfaces).
 * bindTags is on vanilla's path: CbItemRegistryPendingTagsMixin hands every PendingTags.apply() -- 1.21.2's binding of
 * a tag load result, where 1.21.1's ReloadableServerResources and client TagCollector called bindTags -- to it.
 * resetTags (1.21.1's reader: the client's RegistryDataCollector, for registries the server sent no tags for; 1.21.2
 * binds only those it got) and getOrCreateTag (1.21.1's readers resolve tags through 1.21.2's holder lookups now) are
 * restored for mods only: their vanilla paths are unmixed.
 */
@Mixin(value = class_2370.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemRegistryMixin<T> implements CbItemRegistries.Frozen, CbItemRegistries.LegacyBindTags,
        CbItemRegistries.LegacyResetTags, CbItemRegistries.LegacyGetOrCreateTag {

    @Shadow
    private boolean field_36463;

    @Shadow
    abstract class_6885.class_6888<T> method_62690(class_6862<T> tag);

    @Shadow
    @Final
    private Map<class_6862<T>, class_6885.class_6888<T>> field_53686;

    /** the registry's tag state: unbound from the constructor, bound by freeze() and by PendingTags.apply() */
    @Shadow
    class_2370.class_10105<T> field_53687;

    /** bindAllTagsToEmpty's per-set step: the set bound to List.of() */
    @Shadow
    private static void method_62688(class_6885.class_6888<?> set) {
        throw new AssertionError();
    }

    @Override
    public boolean cb$frozen() {
        return this.field_36463;
    }

    /** CbItemRegistries.method_40276: before a 1.21.1 caller's freeze, the still-unbound sets bound as 1.21.1 created them */
    @Override
    public void cb$bindUnboundTagsToEmpty() {
        for (class_6885.class_6888<T> set : this.field_53686.values()) {
            if (!set.method_62680()) {
                method_62688(set);
            }
        }
    }

    /** CbItemRegistries.method_40276: a 1.21.1 caller's refreeze of a registry a mod unfroze (only then: unfrozen, bound) */
    @Override
    public void cb$unbindTagStateForRefreeze() {
        if (!this.field_36463 && this.field_53687.method_62701()) {
            this.field_53687 = class_2370.class_10105.method_62697();
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public class_6885.class_6888 cb$tagForRegistration(class_6862 tag) {
        return this.method_62690(tag);
    }

    /** 1.21.1's bindTags(map): the registry's tags bound to the map's holders (CbItemRegistries.bindTags) */
    @CbVanillaReads("CbItemRegistryPendingTagsMixin: PendingTags.apply() (every tag load result vanilla binds) goes through it")
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void method_40257(Map tags) {
        CbItemRegistries.bindTags((class_2370) (Object) this, tags);
    }

    /** 1.21.1's resetTags(): every known tag bound to nothing */
    @Override
    public void method_40278() {
        CbItemRegistries.super_method_40278((class_2370<?>) (Object) this);
    }

    /** 1.21.1's getOrCreateTag(tag) */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public class_6885.class_6888 method_40260(class_6862 tag) {
        return CbItemRegistries.super_method_40260((class_2370) (Object) this, tag);
    }
}
