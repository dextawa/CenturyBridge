package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The value a HoverEvent.TypedHoverEvent (package-private record, 1.20.3) carries: what an action's own
 * codec decodes "contents" into, read back to build the HoverEvent 1.20.2's Action.deserialize returned.
 */
@Mixin(targets = "net/minecraft/class_2568$class_8826")
public interface CbTypedHoverEventAccess {

    @Accessor("comp_1986")
    Object cb$value();
}
