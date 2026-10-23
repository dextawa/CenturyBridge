package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_2350;
import net.minecraft.class_2680;

/**
 * 1.21.1's Block.BlockStatePairKey (net/minecraft/class_2248$class_2249; classRenames), gone in 1.21.2 (whose face
 * occlusion cache keys on shapes): a key of two states and a direction, equal by identity of each. 1.21.1's bodies.
 */
public final class BlockStatePairKey {
    public final class_2680 field_10652;
    public final class_2680 field_10654;
    public final class_2350 field_10653;

    public BlockStatePairKey(class_2680 first, class_2680 second, class_2350 direction) {
        this.field_10652 = first;
        this.field_10654 = second;
        this.field_10653 = direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof BlockStatePairKey k && this.field_10652 == k.field_10652 && this.field_10654 == k.field_10654
            && this.field_10653 == k.field_10653;
    }

    @Override
    public int hashCode() {
        int h = this.field_10652.hashCode();
        h = 31 * h + this.field_10654.hashCode();
        return 31 * h + this.field_10653.hashCode();
    }
}
