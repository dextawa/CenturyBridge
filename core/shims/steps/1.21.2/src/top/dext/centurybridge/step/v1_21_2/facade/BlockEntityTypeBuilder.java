package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import java.util.Set;
import net.minecraft.class_2248;
import net.minecraft.class_2586;
import net.minecraft.class_2591;

/**
 * 1.21.1's BlockEntityType.Builder (net/minecraft/class_2591$class_2592; classRenames), gone in 1.21.2 (whose
 * BlockEntityType registers from the factory and the blocks directly). 1.21.1's bodies: {@code of} keeps the factory
 * and an immutable copy of the blocks, {@code build} makes the type, the datafixer type going where 1.21.1 put it --
 * a field nothing read, which 1.21.2's type no longer has (CbLevelBlockEntityTypes). 1.21.1's Fabric API injected
 * FabricBlockEntityType.Builder into it, whose build() is build(null). Its members are public: a mod
 * that widened them reached them by these names, and access wideners never reach a CenturyBridge class.
 */
public final class BlockEntityTypeBuilder<T extends class_2586> implements FabricBlockEntityType.Builder<T> {
    public final class_2591.class_5559<? extends T> field_11915;
    public final Set<class_2248> field_19316;

    public BlockEntityTypeBuilder(class_2591.class_5559<? extends T> factory, Set<class_2248> validBlocks) {
        this.field_11915 = factory;
        this.field_19316 = validBlocks;
    }

    public static <T extends class_2586> BlockEntityTypeBuilder<T> method_20528(class_2591.class_5559<? extends T> factory,
                                                                                class_2248... validBlocks) {
        return new BlockEntityTypeBuilder<>(factory, ImmutableSet.copyOf(validBlocks));
    }

    public class_2591<T> method_11034(Type<?> dataType) {
        return new class_2591<>(this.field_11915, this.field_19316);
    }

    /** FabricBlockEntityType.Builder.build(): build(null), as 1.21.1 Fabric API's mixin implemented it */
    @Override
    public class_2591<T> build() {
        return this.method_11034(null);
    }
}
