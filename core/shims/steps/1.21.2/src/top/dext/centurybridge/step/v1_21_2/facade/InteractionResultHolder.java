package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1269;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemResults;

/**
 * 1.21.1's InteractionResultHolder (net/minecraft/class_1271, classRenames), gone in 1.21.2: a result
 * and the object it carries (Item.use's stack the hand holds afterwards). 1.21.1's fields and bodies;
 * the result constants are 1.21.1's, as 1.21.2 results ({@link CbItemResults}). Item.use's holder and
 * 1.21.2's result convert in CbItemResults (holder / result).
 */
public class InteractionResultHolder<T> {
    /** result (private in 1.21.1, public here: a mod widened to it reaches the facade) */
    public final class_1269 field_5815;
    /** object */
    public final T field_5816;

    public InteractionResultHolder(class_1269 result, T object) {
        this.field_5815 = result;
        this.field_5816 = object;
    }

    /** getResult() */
    public class_1269 method_5467() {
        return this.field_5815;
    }

    /** getObject() */
    public T method_5466() {
        return this.field_5816;
    }

    /** success(object) */
    public static <T> InteractionResultHolder<T> method_22427(T object) {
        return new InteractionResultHolder<>(CbItemResults.of(CbItemResults.SUCCESS), object);
    }

    /** consume(object) */
    public static <T> InteractionResultHolder<T> method_22428(T object) {
        return new InteractionResultHolder<>(CbItemResults.of(CbItemResults.CONSUME), object);
    }

    /** pass(object) */
    public static <T> InteractionResultHolder<T> method_22430(T object) {
        return new InteractionResultHolder<>(CbItemResults.of(CbItemResults.PASS), object);
    }

    /** fail(object) */
    public static <T> InteractionResultHolder<T> method_22431(T object) {
        return new InteractionResultHolder<>(CbItemResults.of(CbItemResults.FAIL), object);
    }

    /** sidedSuccess(object, isClientSide): success on the client, consume on the server */
    public static <T> InteractionResultHolder<T> method_29237(T object, boolean clientSide) {
        return clientSide ? method_22427(object) : method_22428(object);
    }
}
