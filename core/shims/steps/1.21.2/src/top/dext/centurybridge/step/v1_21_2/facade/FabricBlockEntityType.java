package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_2586;
import net.minecraft.class_2591;

/**
 * Holder of 1.21.1 Fabric API's FabricBlockEntityType.Builder (fabric-object-builder-api-v1; classRenames), which
 * 1.21.1 injected into BlockEntityType.Builder and 1.21.2's Fabric API dropped with that class. FabricBlockEntityType
 * itself is still Fabric API's on 1.21.2.
 */
public final class FabricBlockEntityType {
    private FabricBlockEntityType() {
    }

    /**
     * 1.21.1's FabricBlockEntityType.Builder: build() is build(null) -- the vanilla builder's (BlockEntityTypeBuilder);
     * 1.21.1's default threw ("Implemented in Mixin") for any other implementation, and so does this one.
     */
    public interface Builder<T extends class_2586> {
        default class_2591<T> build() {
            throw new AssertionError("Implemented in Mixin");
        }
    }
}
