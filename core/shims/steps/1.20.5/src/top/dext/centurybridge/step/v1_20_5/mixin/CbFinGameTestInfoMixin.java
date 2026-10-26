package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Collection;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_2470;
import net.minecraft.class_2633;
import net.minecraft.class_3218;
import net.minecraft.class_3341;
import net.minecraft.class_4517;
import net.minecraft.class_4518;
import net.minecraft.class_4525;
import net.minecraft.class_9118;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinGameTests;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * GameTestInfo: 1.20.4's public members 1.20.5 removed, with 1.20.4's bodies (a subclass may override and
 * call them; vanilla 1.20.5 calls none of them).
 * <ul>
 * <li>getStructureBlockPos() / getStructureSize(): of the structure block at the info's structure block
 * position, null when there is none;</li>
 * <li>spawnStructure(northWestCorner): 1.20.5's prepareTestStructure() spawns at the info's own corner; 1.20.4's
 * spawned at the given one (the structure block, the space cleared around it, testStructureLoaded to the
 * listeners). The corner becomes the info's 1.20.5 north-west corner too;</li>
 * <li>clearStructure(): clears the structure's bounding box;</li>
 * <li>setRerunUntilFailed / isRerunUntilFailed: 1.20.4's report listener reran a passing test while the flag
 * was set and stopped at its first failure; 1.20.5's reads the same from RetryOptions -- unlimited tries,
 * halting on failure (RetryOptions(0, true)) -- so the flag is the info's retry options.</li>
 * </ul>
 */
@Mixin(value = class_4517.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbFinGameTestInfoMixin implements CbFinGameTests.LegacyInfo {

    @Shadow
    private class_2338 field_20560;

    @Shadow
    private class_2338 field_48468;

    @Shadow
    @Final
    private class_3218 field_20561;

    @Shadow
    @Final
    private Collection<class_4518> field_20562;

    @Shadow
    @Final
    @Mutable
    private class_9118 field_48469;

    @Shadow
    private class_2633 field_27805;

    @Shadow
    public abstract class_2470 method_29402();

    /** 1.20.4's private getStructureBlockEntity(): the block entity at the structure block position */
    private class_2633 cb$structureBlockAt() {
        return (class_2633) this.field_20561.method_8321(this.field_20560);
    }

    /** was getStructureBlockPos() */
    public class_2338 method_54843() {
        class_2633 block = this.cb$structureBlockAt();
        return block == null ? null : class_4525.method_54849(block);
    }

    /** was getStructureSize() */
    public class_2382 method_36061() {
        class_2633 block = this.cb$structureBlockAt();
        return block == null ? null : block.method_11349();
    }

    /** was spawnStructure(northWestCorner) */
    @Override
    public void method_22166(class_2338 pos) {
        class_4517 self = (class_4517) (Object) this;
        this.field_48468 = pos;
        this.field_27805 = class_4525.method_54903(self, pos, this.method_29402(), this.field_20561);
        this.field_20560 = this.field_27805.method_11016();
        class_4525.method_22248(this.field_20560, new class_2338(1, 0, -1), this.method_29402(), this.field_20561);
        this.field_20562.forEach(listener -> listener.method_22188(self));
    }

    /** was clearStructure() */
    public void method_32240() {
        if (this.field_27805 == null) {
            throw new IllegalStateException("Expected structure to be initialized, but it was null");
        }
        class_3341 box = class_4525.method_29410(this.field_27805);
        class_4525.method_22246(box, this.field_20561);
    }

    /** was setRerunUntilFailed(rerun) */
    public void method_54146(boolean rerun) {
        this.field_48469 = rerun ? new class_9118(0, true) : class_9118.method_56245();
    }

    /** was isRerunUntilFailed() */
    public boolean method_54145() {
        return this.field_48469.method_56247() && this.field_48469.comp_2214();
    }
}
