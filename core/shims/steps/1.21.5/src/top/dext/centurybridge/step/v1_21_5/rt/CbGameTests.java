package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_4516;

/**
 * 1.21.4's GameTestHelper / GameTestAssertException shapes on 1.21.5 (javap -c -p both). 1.21.5 made every assertion
 * message a Component (GameTestHelper's String / Supplier&lt;String&gt; message parameters became Component /
 * Function&lt;X, Component&gt; / Supplier&lt;Component&gt;; the assertion exceptions carry a Component and the game tick they
 * failed at) and getBlockEntity / assertBlockEntityData take the expected block entity class. The converters below
 * are the callReshapes' and superCtorReshapes' ops: a 1.21.4 message is the literal text 1.21.4 showed
 * (Component.literal), a supplier's text is asked when 1.21.5 asks for the component; the expected class is
 * BlockEntity, so 1.21.5's class check accepts what 1.21.4's unchecked cast accepted. An assertion exception a mod
 * builds itself has no tick (1.21.4 kept none): 0.
 */
public final class CbGameTests {

    private CbGameTests() {
    }

    /** a 1.21.4 message as 1.21.5's component */
    public static class_2561 text(String message) {
        return class_2561.method_43470(message);
    }

    /** a 1.21.4 message supplier as 1.21.5's message function (the value tested is not part of 1.21.4's message) */
    public static Function<Object, class_2561> textOf(Supplier<String> message) {
        return x -> class_2561.method_43470(message.get());
    }

    /** a 1.21.4 message supplier as 1.21.5's component supplier */
    public static Supplier<class_2561> textSupplier(Supplier<String> message) {
        return () -> class_2561.method_43470(message.get());
    }

    /** the block entity class 1.21.4's unchecked getBlockEntity accepted: any */
    public static Class<?> anyBlockEntity() {
        return net.minecraft.class_2586.class;
    }

    /** the game tick of an assertion exception a mod builds (1.21.4 kept none) */
    public static int noTick() {
        return 0;
    }

    /** a 1.21.4 tick (long) as 1.21.5's int */
    public static int tick(long tick) {
        return (int) tick;
    }

    /** new GameTestAssertException(String) */
    public static net.minecraft.class_4512 newAssert(String message) {
        return new net.minecraft.class_4512(text(message), 0);
    }

    /** new GameTestAssertPosException(String, BlockPos, BlockPos, long) */
    public static net.minecraft.class_4513 newAssertPos(String message, class_2338 absolutePos, class_2338 relativePos, long tick) {
        return new net.minecraft.class_4513(text(message), absolutePos, relativePos, (int) tick);
    }

    /** GameTestHelper.assertBlock(BlockPos, Predicate, String): 1.21.4 asked assertBlock(pos, predicate, () -> message) */
    public static void method_35991(class_4516 helper, class_2338 pos, Predicate<class_2248> predicate, String message) {
        helper.method_35992(pos, predicate, b -> text(message));
    }
}
