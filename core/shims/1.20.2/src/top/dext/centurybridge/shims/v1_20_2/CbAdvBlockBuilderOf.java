package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * BlockPredicate.Builder.of(Iterable) of 1.20.1 (1.20.2 takes a Collection): 1.20.1 kept
 * ImmutableSet.copyOf(blocks); 1.20.2's builder holds the blocks' holder set.
 */
@Mixin(net.minecraft.class_4550.class_4710.class)
public abstract class CbAdvBlockBuilderOf {

    @Shadow
    private java.util.Optional<net.minecraft.class_6885<net.minecraft.class_2248>> field_21572;

    /** of(Iterable blocks) */
    public net.minecraft.class_4550.class_4710 method_37214(Iterable<net.minecraft.class_2248> blocks) {
        this.field_21572 = java.util.Optional.of(net.minecraft.class_6885.method_40244(net.minecraft.class_2248::method_40142,
            com.google.common.collect.ImmutableSet.copyOf(blocks)));
        return (net.minecraft.class_4550.class_4710) (Object) this;
    }
}
