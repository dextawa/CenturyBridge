package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2370;
import net.minecraft.class_6862;
import net.minecraft.class_6885;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRegistries;

/** MappedRegistry: whether it froze, and its registration-time tag creation (CbItemRegistries.getOrCreateTag) */
@Mixin(class_2370.class)
public abstract class CbItemRegistryMixin<T> implements CbItemRegistries.Frozen {

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
}
