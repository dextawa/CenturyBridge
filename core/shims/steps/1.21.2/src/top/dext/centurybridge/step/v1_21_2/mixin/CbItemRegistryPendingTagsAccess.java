package top.dext.centurybridge.step.v1_21_2.mixin;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.class_2370;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRegistries;

/** MappedRegistry's PendingTags (class_2370$3): its registry, load result and named sets (CbItemRegistries.bindTags) */
@Mixin(targets = "net.minecraft.class_2370$3")
public interface CbItemRegistryPendingTagsAccess extends CbItemRegistries.PendingTagsAccess {

    @Override
    @Accessor("field_53689")
    class_2370<?> cb$registry();

    @Override
    @SuppressWarnings("rawtypes")
    @Accessor("field_54031")
    Map cb$pending();

    @Override
    @SuppressWarnings("rawtypes")
    @Accessor("field_53688")
    ImmutableMap cb$named();

    @Override
    @SuppressWarnings("rawtypes")
    @Accessor("field_53688")
    @Mutable
    void cb$setNamed(ImmutableMap named);
}
