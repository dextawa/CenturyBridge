package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.class_2165;
import net.minecraft.class_2168;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_5244;
import net.minecraft.class_8641;
import net.minecraft.class_8643;
import net.minecraft.class_8839;
import net.minecraft.class_8868;
import net.minecraft.server.MinecraftServer;

/**
 * CommandFunction.instantiate between 1.20.4 and 1.20.5 (area s4fin).
 *
 * 1.20.4 instantiated a function with (macro arguments, dispatcher, source) -- the source its macro
 * lines were parsed with, which the caller supplied. 1.20.5 drops the source: a function's lines are
 * parsed with the source it was compiled with (ServerFunctionLibrary's compilation source: no
 * entity, no position, the server's function permission level). A 1.20.4 call drops its source (a
 * 1.20.4 MacroFunction parsed each argument combination once and cached it, whatever source asked
 * later, so the source never was the function's to keep). A 1.20.4 implementation, which 1.20.5
 * calls without one, gets that compilation source -- the one 1.20.5 parses functions with.
 */
public final class CbFinFunctions {

    private CbFinFunctions() {
    }

    /** ServerFunctionLibrary's compilation source on the running server (null without one) */
    public static class_2168 cbCompilationSource() {
        MinecraftServer server = CbRegistries.server();
        if (server == null) {
            return null;
        }
        return new class_2168(class_2165.field_17395, class_243.field_1353, class_241.field_1340, null,
            server.method_21714(), "", class_5244.field_39003, null, null);
    }

    /** MacroFunction.instantiate(arguments, dispatcher, source) of 1.20.4: 1.20.5's, without the source */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_8868 method_54427(class_8641 function, class_2487 arguments, CommandDispatcher dispatcher,
            class_8839 source) throws class_8643 {
        return function.method_52595(arguments, dispatcher);
    }

    /** a 1.20.4 CommandFunction's instantiate(arguments, dispatcher, source) (the adapter the override hook adds) */
    public interface LegacyInstantiate {
        class_8868<?> method_52595(class_2487 arguments, CommandDispatcher<?> dispatcher, Object source) throws class_8643;
    }

    /** a 1.20.4 MacroFunction subclass's typed instantiate(arguments, dispatcher, source) */
    public interface LegacyMacroInstantiate {
        class_8868<?> method_54427(class_2487 arguments, CommandDispatcher<?> dispatcher, class_8839<?> source) throws class_8643;
    }

    /** 1.20.5 instantiates a 1.20.4 function without a source: its own instantiate with the compilation source */
    public static class_8868<?> cbInstantiate(LegacyInstantiate function, class_2487 arguments, CommandDispatcher<?> dispatcher)
            throws class_8643 {
        return function.method_52595(arguments, dispatcher, cbCompilationSource());
    }

    /** the same for a 1.20.4 MacroFunction subclass's typed override */
    public static class_8868<?> cbInstantiateMacro(LegacyMacroInstantiate function, class_2487 arguments,
            CommandDispatcher<?> dispatcher) throws class_8643 {
        return function.method_54427(arguments, dispatcher, cbCompilationSource());
    }
}
