package cb.swtrade.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 1.20.6 SuspiciousStewForEmerald (class_3853$class_4166, package-private, opened by the mod's access widener) constructor; a constructor invoker answers the target class */
@Mixin(targets = "net.minecraft.class_3853$class_4166")
public interface StewInvoker {
    @Invoker(value = "<init>", remap = false)
    static net.minecraft.class_3853.class_4166 cbswtrade$new(net.minecraft.class_6880<net.minecraft.class_1291> effect, int duration, int xp) {
        throw new AssertionError();
    }
}
