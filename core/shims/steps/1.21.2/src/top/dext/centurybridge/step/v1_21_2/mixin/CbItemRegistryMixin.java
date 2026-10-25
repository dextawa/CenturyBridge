package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Map;
import net.minecraft.class_2370;
import net.minecraft.class_6862;
import net.minecraft.class_6885;
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

    @Override
    public boolean cb$frozen() {
        return this.field_36463;
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
