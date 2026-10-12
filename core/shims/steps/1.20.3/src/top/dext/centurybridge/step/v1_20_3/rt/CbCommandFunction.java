package top.dext.centurybridge.step.v1_20_3.rt;

import top.dext.centurybridge.step.v1_20_3.facade.class_2158$class_2161;

/**
 * 1.20.2's CommandFunction class -- an id and its entries -- as a 1.20.3 CommandFunction (an interface since
 * 1.20.3). What 1.20.2 code made with new CommandFunction(id, entries) (ctorRedirects) and what a 1.20.2
 * subclass extends (superRenames): the 1.20.2 members, and 1.20.3's id() and instantiate(..), which runs the
 * 1.20.2 instantiate (a subclass's own, a macro's) and gives 1.20.3's execution the entries as its actions
 * ({@link CbFunctions#actions}).
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CbCommandFunction implements net.minecraft.class_2158 {

    /** entries */
    public final class_2158$class_2161[] field_9805;
    /** id */
    public final net.minecraft.class_2960 field_9806;

    public CbCommandFunction(net.minecraft.class_2960 id, class_2158$class_2161[] entries) {
        this.field_9806 = id;
        this.field_9805 = entries;
    }

    /** new CommandFunction(id, entries) of 1.20.2 code (ctorRedirects) */
    public static net.minecraft.class_2158 cbNew(net.minecraft.class_2960 id, class_2158$class_2161[] entries) {
        return new CbCommandFunction(id, entries);
    }

    /** getId() */
    public net.minecraft.class_2960 method_9194() {
        return field_9806;
    }

    /** getEntries() */
    public class_2158$class_2161[] method_9193() {
        return field_9805;
    }

    /** instantiate(arguments, dispatcher, source) of 1.20.2: a plain function is its own instance */
    public net.minecraft.class_2158 method_52595(net.minecraft.class_2487 arguments,
                                                 com.mojang.brigadier.CommandDispatcher<net.minecraft.class_2168> dispatcher,
                                                 net.minecraft.class_2168 source) throws net.minecraft.class_8643 {
        return this;
    }

    // ------------------------------------------------------------------ 1.20.3's CommandFunction

    @Override
    public net.minecraft.class_2960 comp_1994() {
        return method_9194();
    }

    @Override
    public net.minecraft.class_8868 method_52595(net.minecraft.class_2487 arguments, com.mojang.brigadier.CommandDispatcher dispatcher,
                                                 Object source) throws net.minecraft.class_8643 {
        net.minecraft.class_2158 instance = method_52595(arguments, (com.mojang.brigadier.CommandDispatcher<net.minecraft.class_2168>) dispatcher,
            (net.minecraft.class_2168) source);
        if (instance instanceof CbCommandFunction legacy) {
            return new net.minecraft.class_8872(legacy.method_9194(),
                CbFunctions.actions(legacy.method_9193(), (com.mojang.brigadier.CommandDispatcher<net.minecraft.class_2168>) dispatcher));
        }
        return instance.method_52595(arguments, dispatcher, source);
    }
}
