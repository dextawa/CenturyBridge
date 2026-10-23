package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The stored components of 1.20.2's Advancement record, read past its accessors: a 1.20.1 subclass
 * overriding getRewards() and the like answers the accessors itself (adapterHooks), so its own
 * super.getRewards() reads what the record holds (CbAdvSubclass.super_*).
 */
@Mixin(net.minecraft.class_161.class)
public interface CbAdvRecordFields {

    @Accessor("comp_1913")
    java.util.Optional<net.minecraft.class_185> cb$display();

    @Accessor("comp_1914")
    net.minecraft.class_170 cb$rewards();

    @Accessor("comp_1915")
    java.util.Map<String, net.minecraft.class_175<?>> cb$criteria();

    @Accessor("comp_1916")
    net.minecraft.class_8782 cb$requirements();

    @Accessor("comp_1917")
    boolean cb$telemetry();

    @Accessor("comp_1918")
    java.util.Optional<net.minecraft.class_2561> cb$name();
}
