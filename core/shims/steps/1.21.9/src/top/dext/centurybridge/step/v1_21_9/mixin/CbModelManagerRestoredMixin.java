package top.dext.centurybridge.step.v1_21_9.mixin;

import java.util.Objects;
import net.minecraft.class_1059;
import net.minecraft.class_1092;
import net.minecraft.class_11697;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_9.rt.CbAtlases;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8 ModelManager's atlas members, which 1.21.9 dropped when the model atlases moved into AtlasManager (javap -c
 * both): 1.21.8's ModelManager owned an AtlasSet of the model atlases and stitched them at its mip level; 1.21.9's is
 * given the AtlasManager that owns them. getAtlas(location) answers that AtlasManager's atlas at the location (a
 * NullPointerException for an unknown one, as 1.21.8's AtlasSet lookup threw), updateMaxMipLevel(level) sets the
 * AtlasManager's (1.21.9's updateMaxMipLevel, the level its next stitch uses), close() closes its atlases.
 * 1.21.9 never calls them.
 */
@Mixin(value = class_1092.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbModelManagerRestoredMixin implements AutoCloseable {

    @Shadow
    @Final
    private class_11697 field_61870;

    /** ModelManager.getAtlas(location) */
    public class_1059 method_24153(class_2960 location) {
        return Objects.requireNonNull(CbAtlases.byLocation(this.field_61870, location));
    }

    /** ModelManager.updateMaxMipLevel(level) */
    public void method_24152(int level) {
        this.field_61870.method_73024(level);
    }

    /** ModelManager.close() */
    @Override
    public void close() {
        this.field_61870.close();
    }
}
