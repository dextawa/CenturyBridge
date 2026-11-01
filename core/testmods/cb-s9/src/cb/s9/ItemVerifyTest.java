package cb.s9;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.class_10630;
import net.minecraft.class_1304;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_9323;
import net.minecraft.class_9326;
import net.minecraft.class_9334;

/**
 * 1.21.8 Item.verifyComponentsAfterLoad(stack): vanilla calls it after ItemStack construction, applyComponents(patch),
 * applyComponents(map), a validated applyValidatedChanges(patch) and EntityEquipment.set (1.21.8 call points, given
 * back by the layer's hooks); a 1.21.8 mixin on it sees each. The native cb-s9n mixin on 1.21.9's applyComponents(patch)
 * RETURN must still run (cb.s9n.applies) and the component must still be applied.
 */
public final class ItemVerifyTest {
    public static volatile class_1792 WATCHED;
    public static final AtomicInteger CALLS = new AtomicInteger();

    private ItemVerifyTest() {
    }

    static void run() {
        S9TestMod.run("item: verifyComponentsAfterLoad call points", () -> {
            WATCHED = class_1802.field_8600;
            CALLS.set(0);
            class_1799 stack = new class_1799(class_1802.field_8600);
            S9TestMod.check(CALLS.get() == 1, "new ItemStack(..) verifies once, got " + CALLS.get());
            int nativeBefore = Integer.getInteger("cb.s9n.applies", 0);
            stack.method_57366(class_9326.method_57841().method_57854(class_9334.field_49631, class_2561.method_43470("cb")).method_57852());
            int nativeAfter = Integer.getInteger("cb.s9n.applies", -1);
            S9TestMod.check(CALLS.get() == 2 && "cb".equals(stack.method_58694(class_9334.field_49631).getString()),
                "applyComponents(patch) applies the name and verifies, got calls " + CALLS.get());
            S9TestMod.check(nativeAfter == nativeBefore + 1,
                "the native 1.21.9 applyComponents(patch) RETURN mixin still runs once, got " + nativeBefore + " -> " + nativeAfter);
            stack.method_57365(class_9323.method_57827().method_57840(class_9334.field_50071, 16).method_57838());
            S9TestMod.check(CALLS.get() == 3, "applyComponents(map) verifies, got " + CALLS.get());
            stack.method_59692(class_9326.method_57841().method_57854(class_9334.field_50071, 8).method_57852());
            S9TestMod.check(CALLS.get() == 4 && stack.method_7914() == 8, "a valid applyValidatedChanges(patch) verifies, got " + CALLS.get());
            stack.method_59692(class_9326.method_57841().method_57854(class_9334.field_50072, 10).method_57852());
            S9TestMod.check(CALLS.get() == 4, "a rejected applyValidatedChanges(patch) (max_damage on a stack of 8) does not verify, got " + CALLS.get());
            new class_10630().method_66660(class_1304.field_6169, stack);
            S9TestMod.check(CALLS.get() == 5, "EntityEquipment.set(slot, stack) verifies, got " + CALLS.get());
            WATCHED = null;
        });
    }
}
