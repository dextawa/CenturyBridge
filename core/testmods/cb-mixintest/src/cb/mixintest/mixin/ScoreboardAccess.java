package cb.mixintest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.class_269.class)
public interface ScoreboardAccess {
    /** Scoreboard.getObjective(name) (method_1165, the named objective or null): gone in 1.20.2, instance-redirected by step 1 */
    @Invoker("getOrCreateObjective")
    net.minecraft.class_266 cbtest$getOrCreate(String name);

    /** getDisplaySlotName: gone in 1.20.2, static-redirected by step 1 */
    @Invoker("getDisplaySlotName")
    static String cbtest$slotName(int slot) {
        throw new AssertionError();
    }

    /** DISPLAY_SLOT_LIST: gone in 1.20.2, a field getter in step 1 */
    @Accessor("DISPLAY_SLOT_LIST")
    static int cbtest$listSlotId() {
        throw new AssertionError();
    }

    /** addObjective: reshaped by step 2 (1.20.3 takes display-auto-update and a number format) */
    @Invoker("addObjective")
    net.minecraft.class_266 cbtest$add(String name, net.minecraft.class_274 criterion, net.minecraft.class_2561 title,
                                       net.minecraft.class_274.class_275 render);
}
