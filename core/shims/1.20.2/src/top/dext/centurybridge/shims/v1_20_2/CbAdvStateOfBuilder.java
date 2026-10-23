package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The state of 1.20.2's Advancement.Builder that 1.20.1's builder exposed through its own methods
 * (parentId, display, requirements and their strategy): CbAdvBuilders reads it to port canBuild,
 * serializeToNetwork and the like.
 */
@Mixin(net.minecraft.class_161.class_162.class)
public interface CbAdvStateOfBuilder {

    @Accessor("field_1149")
    java.util.Optional<net.minecraft.class_2960> cb$parent();

    @Accessor("field_1147")
    java.util.Optional<net.minecraft.class_185> cb$display();

    @Accessor("field_1150")
    java.util.Optional<net.minecraft.class_8782> cb$requirements();

    @Accessor("field_1151")
    net.minecraft.class_8782.class_8797 cb$strategy();

    @Accessor("field_44783")
    boolean cb$telemetry();
}
