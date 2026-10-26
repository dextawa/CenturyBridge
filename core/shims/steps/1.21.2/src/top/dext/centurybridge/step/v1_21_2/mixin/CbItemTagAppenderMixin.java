package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Collection;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.class_2474;
import net.minecraft.class_3495;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.facade.fapi.FabricProvidedTagBuilder;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemDatagenTags;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * Fabric API 0.116's FabricProvidedTagBuilder on 1.21.2's TagsProvider.TagAppender (0.116 injected it there): a
 * FabricTagBuilder answers with its own setReplace / forceAddTag (0.106 kept them) and the removals 0.106 dropped
 * (CbItemDatagenTags); any other appender fails as 0.116's interface defaults did. Also hands its TagBuilder to
 * CbItemDatagenTags.
 */
@Mixin(value = class_2474.class_5124.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemTagAppenderMixin<T> implements FabricProvidedTagBuilder<T>, CbItemDatagenTags.Appender {

    @Shadow
    @Final
    private class_3495 field_23960;

    @Override
    public class_3495 cb$builder() {
        return this.field_23960;
    }

    @SuppressWarnings("unchecked")
    private FabricTagProvider<T>.FabricTagBuilder cb$fabric() {
        Object self = this;
        if (self instanceof FabricTagProvider<?>.FabricTagBuilder b) {
            return (FabricTagProvider<T>.FabricTagBuilder) b;
        }
        throw new AssertionError("Implemented via mixin");
    }

    @Override
    public FabricProvidedTagBuilder<T> setReplace(boolean replace) {
        this.cb$fabric().setReplace(replace);
        return this;
    }

    @Override
    public FabricProvidedTagBuilder<T> forceAddTag(class_6862<T> tag) {
        this.cb$fabric().forceAddTag(tag);
        return this;
    }

    @Override
    public FabricProvidedTagBuilder<T> remove(class_5321<T> key) {
        CbItemDatagenTags.remove(this.cb$fabric(), key);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public FabricProvidedTagBuilder<T> remove(class_5321<T>... keys) {
        CbItemDatagenTags.remove(this.cb$fabric(), keys);
        return this;
    }

    @Override
    public FabricProvidedTagBuilder<T> removeAll(Collection<class_5321<T>> keys) {
        CbItemDatagenTags.removeAll(this.cb$fabric(), keys);
        return this;
    }

    @Override
    public FabricProvidedTagBuilder<T> removeAll(Stream<class_5321<T>> keys) {
        CbItemDatagenTags.removeAll(this.cb$fabric(), keys);
        return this;
    }

    @Override
    public FabricProvidedTagBuilder<T> removeTag(class_6862<T> tag) {
        CbItemDatagenTags.removeTag(this.cb$fabric(), tag);
        return this;
    }
}
