package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_155;
import net.minecraft.class_34;
import net.minecraft.class_5315;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.20.2's LevelSummary.markVersionInList() (a cover): whether the world list marks the world's version -- when
 * opening it is a downgrade (1.20.2's askToOpenWorld, 1.20.3's isDowngrade), when a snapshot game shows a world of
 * a release, or when the world wants a backup. 1.20.3's list asks shouldBackup() alone at those places.
 */
@Mixin(class_34.class)
public abstract class CbWorldLevelSummaryMixin {

    @Shadow
    @Final
    private class_5315 field_25023;

    @Shadow
    public abstract boolean method_54550();

    @Shadow
    public abstract class_34.class_5781 method_33405();

    public boolean method_256() {
        return this.method_54550()
            || !class_155.method_16673().method_48022() && !this.field_25023.method_29027()
            || this.method_33405().method_33406();
    }
}
