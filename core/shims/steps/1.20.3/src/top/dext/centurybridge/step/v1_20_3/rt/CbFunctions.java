package top.dext.centurybridge.step.v1_20_3.rt;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import top.dext.centurybridge.step.v1_20_3.facade.class_2158$class_2160;
import top.dext.centurybridge.step.v1_20_3.facade.class_2158$class_2161;
import top.dext.centurybridge.step.v1_20_3.facade.class_2158$class_2162;
import top.dext.centurybridge.step.v1_20_3.facade.class_2158$class_8642;
import top.dext.centurybridge.step.v1_20_3.facade.class_2991$class_2992;
import top.dext.centurybridge.step.v1_20_3.facade.class_2991$class_6346;
import top.dext.centurybridge.step.v1_20_3.facade.class_3062$class_8601;
import top.dext.centurybridge.step.v1_20_3.mixin.CbCommandsAccess;
import top.dext.centurybridge.step.v1_20_3.mixin.CbExecutionContextAccess;
import top.dext.centurybridge.step.v1_20_3.mixin.CbMacroFunctionAccess;
import top.dext.centurybridge.step.v1_20_3.mixin.CbMacroEntryAccess;
import top.dext.centurybridge.step.v1_20_3.mixin.CbPlainTextEntryAccess;

/**
 * 1.20.2's function API on 1.20.3's command execution. 1.20.2 ran a function as a queue of entries
 * (CommandFunction.Entry: a command, a call of another function, a macro line) in ServerFunctionManager;
 * 1.20.3 runs functions and commands alike as entry actions in an ExecutionContext. Here: the 1.20.2
 * entries of a function and back ({@link #method_9193}, {@link #actions}), instantiation, macro lines,
 * running a parsed command or a function the 1.20.2 way -- at once, answering a count or a result --
 * and the 1.20.2 tracer.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbFunctions {

    // ------------------------------------------------------------------ running in an execution context

    /**
     * Runs {@code body} in a new execution context and to its end before returning, as 1.20.2 ran a command
     * or a function at the call. 1.20.3's Commands.executeCommandInContext queues into the context already
     * running on this thread instead; it is cleared for the call and put back after.
     */
    static void runNow(net.minecraft.class_2168 source, Consumer<net.minecraft.class_8854<net.minecraft.class_2168>> body) {
        ThreadLocal<net.minecraft.class_8854<net.minecraft.class_2168>> current = CbCommandsAccess.cb$currentContext();
        net.minecraft.class_8854<net.minecraft.class_2168> outer = current.get();
        current.set(null);
        try {
            net.minecraft.class_2170.method_54313(source, body);
        } finally {
            current.set(outer);
        }
    }

    /** what of a context's command budget was spent: the commands and function calls it ran */
    static int spent(net.minecraft.class_8854<?> context) {
        CbExecutionContextAccess a = (CbExecutionContextAccess) context;
        return a.cb$commandLimit() - a.cb$commandQuota();
    }

    // ------------------------------------------------------------------ the 1.20.2 tracer

    /** a 1.20.2 tracer as 1.20.3's: the depth of the last command or call stands for the error's, which 1.20.3 dropped */
    static net.minecraft.class_6346 tracer(class_2991$class_6346 legacy) {
        if (legacy == null) {
            return null;
        }
        return new net.minecraft.class_6346() {
            private int depth;

            @Override
            public void method_36349(int depth, String command) {
                this.depth = depth;
                legacy.method_36349(depth, command);
            }

            @Override
            public void method_36350(int depth, String command, int result) {
                this.depth = depth;
                legacy.method_36350(depth, command, result);
            }

            @Override
            public void method_36352(String message) {
                legacy.method_36352(depth, message);
            }

            @Override
            public void method_36351(int depth, net.minecraft.class_2960 function, int size) {
                this.depth = depth;
                legacy.method_36351(depth, function, size);
            }

            @Override
            public void close() {
            }
        };
    }

    /** 1.20.3's tracer as a 1.20.2 one (an entry a 1.20.2 mod wrote, run from 1.20.3's execution) */
    static class_2991$class_6346 legacyTracer(net.minecraft.class_6346 tracer) {
        if (tracer == null) {
            return null;
        }
        return new class_2991$class_6346() {
            @Override
            public void method_36349(int depth, String command) {
                tracer.method_36349(depth, command);
            }

            @Override
            public void method_36350(int depth, String command, int result) {
                tracer.method_36350(depth, command, result);
            }

            @Override
            public void method_36352(int depth, String message) {
                tracer.method_36352(message);
            }

            @Override
            public void method_36351(int depth, net.minecraft.class_2960 function, int size) {
                tracer.method_36351(depth, function, size);
            }
        };
    }

    // ------------------------------------------------------------------ a parsed command

    /** 1.20.3's entry action for a parsed command line, as CommandFunction.parseCommand builds it */
    private static net.minecraft.class_8856<net.minecraft.class_2168> commandAction(ParseResults<net.minecraft.class_2168> parse) {
        String input = parse.getReader().getString();
        Optional<ContextChain<net.minecraft.class_2168>> chain = ContextChain.tryFlatten(parse.getContext().build(input));
        if (chain.isPresent()) {
            return new net.minecraft.class_8858.class_8861<>(input, chain.get());
        }
        CommandSyntaxException unknown = CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader());
        return (source, context, frame) -> source.method_54495(unknown, false, context.method_54396());
    }

    /**
     * A 1.20.2 CommandEntry's command, run with {@code source} now: its result, the sum of what its forks
     * answered (as 1.20.2's dispatcher summed them), 0 when it failed; the tracer hears it at {@code depth}
     */
    public static int runParsed(net.minecraft.class_2168 source, ParseResults<net.minecraft.class_2168> parse, class_2991$class_6346 legacy, int depth) {
        int[] sum = {0};
        net.minecraft.class_8935 capture = (success, result) -> {
            if (success) {
                sum[0] += result;
            }
        };
        net.minecraft.class_8856<net.minecraft.class_2168> action = commandAction(parse);
        runNow(source, context -> {
            context.method_54394(tracer(legacy));
            // each fork reports its result to its source's callback, which carries the capture; the frame's own
            // callback (what a return in it answers) is not counted again
            net.minecraft.class_8937 frame = new net.minecraft.class_8937(depth, net.minecraft.class_8935.field_47158, context.method_54890(depth));
            context.method_54392(new net.minecraft.class_8847<>(frame, action.bind(source.method_9231(
                net.minecraft.class_8935.chain(source.method_54881(), capture)))));
        });
        return sum[0];
    }

    // ------------------------------------------------------------------ entries <-> actions

    /** a 1.20.3 entry action as a 1.20.2 entry: it runs now, in its own execution, at the entry's depth */
    public static final class ActionEntry implements class_2158$class_2161 {
        final net.minecraft.class_8856<net.minecraft.class_2168> action;

        ActionEntry(net.minecraft.class_8856<net.minecraft.class_2168> action) {
            this.action = action;
        }

        @Override
        public void method_9198(net.minecraft.class_2991 manager, net.minecraft.class_2168 source, Deque<class_2991$class_2992> queue,
                                int depth, int maxChainLength, class_2991$class_6346 legacy) {
            runNow(source, context -> {
                context.method_54394(tracer(legacy));
                net.minecraft.class_8937 frame = new net.minecraft.class_8937(depth, net.minecraft.class_8935.field_47158, context.method_54890(depth));
                context.method_54392(new net.minecraft.class_8847<>(frame, action.bind(source)));
            });
        }

        @Override
        public String toString() {
            return action.toString();
        }
    }

    /**
     * was CommandFunction.getEntries() (method_9193): a 1.20.2 function's own; a 1.20.3 plain function's
     * actions, each as an entry; a 1.20.3 macro function's lines -- a macro line as a MacroEntry of its
     * template's segments and variables, as 1.20.2's macro function kept them
     */
    public static class_2158$class_2161[] method_9193(net.minecraft.class_2158 function) {
        if (function instanceof CbCommandFunction legacy) {
            return legacy.method_9193();
        }
        if (function instanceof net.minecraft.class_8868 instantiated) {
            List<net.minecraft.class_8856> actions = instantiated.comp_1995();
            class_2158$class_2161[] out = new class_2158$class_2161[actions.size()];
            for (int i = 0; i < out.length; i++) {
                out[i] = new ActionEntry(actions.get(i));
            }
            return out;
        }
        if (function instanceof net.minecraft.class_8641 macro) {
            List<?> lines = ((CbMacroFunctionAccess) macro).cb$entries();
            class_2158$class_2161[] out = new class_2158$class_2161[lines.size()];
            for (int i = 0; i < out.length; i++) {
                Object line = lines.get(i);
                if (line instanceof CbMacroEntryAccess m) {
                    net.minecraft.class_8873 template = m.cb$template();
                    out[i] = new class_2158$class_8642(template.comp_1996(), template.comp_1997());
                } else {
                    out[i] = new ActionEntry(((CbPlainTextEntryAccess) line).cb$action());
                }
            }
            return out;
        }
        throw new IllegalArgumentException("not a function of entries: " + function.comp_1994() + " (" + function.getClass().getName() + ")");
    }

    /**
     * a 1.20.2 function's entries as 1.20.3's actions: a command as the action 1.20.3 builds for a function
     * line, a call as 1.20.3's function call, a macro line as 1.20.2's error, a 1.20.3 action as itself, and
     * an entry a 1.20.2 mod wrote run the 1.20.2 way ({@link #legacyAction})
     */
    public static List<net.minecraft.class_8856<net.minecraft.class_2168>> actions(class_2158$class_2161[] entries,
                                                                                  CommandDispatcher<net.minecraft.class_2168> dispatcher) {
        List<net.minecraft.class_8856<net.minecraft.class_2168>> out = new ArrayList<>(entries.length);
        for (class_2158$class_2161 entry : entries) {
            if (entry instanceof ActionEntry a) {
                out.add(a.action);
            } else if (entry instanceof class_2158$class_2160 c) {
                out.add(commandAction(c.field_9811));
            } else if (entry instanceof class_2158$class_8642) {
                out.add((source, context, frame) -> {
                    throw new IllegalStateException("Tried to execute an uninstantiated macro");
                });
            } else if (entry instanceof class_2158$class_2162 call) {
                out.add(callAction(call, dispatcher));
            } else {
                out.add(legacyAction(entry));
            }
        }
        return out;
    }

    /** a 1.20.2 FunctionEntry as 1.20.3's call of the function, one frame deeper */
    private static net.minecraft.class_8856<net.minecraft.class_2168> callAction(class_2158$class_2162 call,
                                                                                CommandDispatcher<net.minecraft.class_2168> dispatcher) {
        net.minecraft.class_2158 function = call.cb$function();
        return (source, context, frame) -> {
            net.minecraft.class_8868 instance;
            try {
                instance = function.method_52595(null, dispatcher, source);
            } catch (net.minecraft.class_8643 e) {
                source.method_54491(new com.mojang.brigadier.exceptions.SimpleCommandExceptionType(e.method_52600()), e.method_52600(),
                    false, context.method_54396());
                return;
            }
            new net.minecraft.class_8862<>(instance, net.minecraft.class_8935.field_47158, false).execute(source, context, frame);
        };
    }

    /**
     * an entry a 1.20.2 mod wrote, run 1.20.2's way inside 1.20.3's execution: the entry with a 1.20.2
     * queue, then the queue -- front first, each queued entry at its depth -- until it is empty or the chain
     * limit has run
     */
    private static net.minecraft.class_8856<net.minecraft.class_2168> legacyAction(class_2158$class_2161 entry) {
        return (source, context, frame) -> {
            net.minecraft.class_2991 manager = source.method_9211().method_3740();
            int limit = commandLimit(source.method_9211());
            class_2991$class_6346 legacy = legacyTracer(context.method_54396());
            Deque<class_2991$class_2992> queue = new ArrayDeque<>();
            queue.push(new class_2991$class_2992(source, frame.comp_2022(), entry));
            for (int ran = 0; !queue.isEmpty() && ran < limit; ran++) {
                queue.removeFirst().method_12914(manager, queue, limit, legacy);
            }
        };
    }

    // ------------------------------------------------------------------ CommandFunction

    /**
     * was CommandFunction.instantiate(arguments, dispatcher, source) (method_52595) of 1.20.2: a 1.20.2
     * function's own; a 1.20.3 one's instance, which for a 1.20.3 plain or macro function is a function too
     */
    public static net.minecraft.class_2158 method_52595(net.minecraft.class_2158 function, net.minecraft.class_2487 arguments,
                                                        CommandDispatcher<net.minecraft.class_2168> dispatcher, net.minecraft.class_2168 source)
            throws net.minecraft.class_8643 {
        if (function instanceof CbCommandFunction legacy) {
            return legacy.method_52595(arguments, dispatcher, source);
        }
        net.minecraft.class_8868 instance = function.method_52595(arguments, dispatcher, source);
        if (instance instanceof net.minecraft.class_2158 asFunction) {
            return asFunction;
        }
        List<net.minecraft.class_8856> actions = instance.comp_1995();
        class_2158$class_2161[] entries = new class_2158$class_2161[actions.size()];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = new ActionEntry(actions.get(i));
        }
        return new CbCommandFunction(instance.comp_1994(), entries);
    }

    /** was CommandFunction.parseMacro(line, lineNumber) (method_52594), 1.20.2's body */
    public static class_2158$class_8642 method_52594(String line, int lineNumber) {
        com.google.common.collect.ImmutableList.Builder<String> segments = com.google.common.collect.ImmutableList.builder();
        com.google.common.collect.ImmutableList.Builder<String> parameters = com.google.common.collect.ImmutableList.builder();
        int length = line.length();
        int start = 0;
        int i = line.indexOf('$');
        while (i != -1) {
            if (i == length - 1 || line.charAt(i + 1) != '(') {
                i = line.indexOf('$', i + 1);
                continue;
            }
            segments.add(line.substring(start, i));
            int end = line.indexOf(')', i + 1);
            if (end == -1) {
                throw new IllegalArgumentException("Unterminated macro variable in macro '" + line + "' on line " + lineNumber);
            }
            String name = line.substring(i + 2, end);
            if (!validVariableName(name)) {
                throw new IllegalArgumentException("Invalid macro variable name '" + name + "' on line " + lineNumber);
            }
            parameters.add(name);
            start = end + 1;
            i = line.indexOf('$', start);
        }
        if (start == 0) {
            throw new IllegalArgumentException("Macro without variables on line " + lineNumber);
        }
        if (start != length) {
            segments.add(line.substring(start));
        }
        return new class_2158$class_8642(segments.build(), parameters.build());
    }

    private static boolean validVariableName(String name) {
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------ ServerFunctionManager

    /** getCommandLimit(), 1.20.2's: the maxCommandChainLength game rule */
    static int commandLimit(net.minecraft.server.MinecraftServer server) {
        return server.method_3767().method_8356(net.minecraft.class_1928.field_19408);
    }

    /**
     * A function instance run the 1.20.2 way: while commands are already running on this thread (1.20.2:
     * while its function queue ran) it is queued to run after the current one and the answer is -1 -- with a
     * tracer it is refused, as 1.20.2 refused nested tracing; else it runs now, the tracer hearing it, the
     * result callback getting what it returns, and the answer is how many of its commands and calls ran
     * (1.20.3's own count of them, the top-level call left out as 1.20.2 left it out).
     */
    private static int run(net.minecraft.class_8868 instance, net.minecraft.class_2168 source, class_2991$class_6346 legacy,
                           net.minecraft.class_8935 result) {
        net.minecraft.class_8854<net.minecraft.class_2168> running = CbCommandsAccess.cb$currentContext().get();
        if (running != null) {
            if (legacy != null) {
                net.minecraft.class_6346 tracer = running.method_54396();
                if (tracer != null) {
                    tracer.method_36352(net.minecraft.class_2561.method_43471("commands.debug.function.noRecursion").getString());
                }
                return -1;
            }
            net.minecraft.class_8854.method_54395(running, instance, source, result);
            return -1;
        }
        net.minecraft.class_8854<?>[] ran = {null};
        runNow(source, context -> {
            ran[0] = context;
            context.method_54394(tracer(legacy));
            net.minecraft.class_8854.method_54395(context, instance, source, result);
        });
        return ran[0] == null ? 0 : Math.max(spent(ran[0]) - 1, 0);
    }

    /**
     * execute(function, source, tracer, arguments) of 1.20.2 (an int): the function instantiated (failing as
     * 1.20.2 failed) and run 1.20.2's way ({@link #run}); 0 when it was queued
     */
    public static int execute(net.minecraft.class_2991 manager, net.minecraft.class_2158 function, net.minecraft.class_2168 source,
                              class_2991$class_6346 legacy, net.minecraft.class_2487 arguments) throws net.minecraft.class_8643 {
        net.minecraft.class_8868 instance = function.method_52595(arguments, manager.method_12900(), source);
        return Math.max(run(instance, source, legacy, net.minecraft.class_8935.field_47158), 0);
    }

    /**
     * was FunctionCommand.runFunction(source, function, arguments) (method_52312), 1.20.2's body: the function
     * run with the source's output suppressed and at most permission level 2; what it returned, else how many
     * of its commands ran (0 when it was queued)
     */
    public static class_3062$class_8601 method_52312(net.minecraft.class_2168 source, net.minecraft.class_2158 function,
                                                    net.minecraft.class_2487 arguments) throws net.minecraft.class_8643 {
        int[] returned = {0};
        boolean[] didReturn = {false};
        net.minecraft.class_8935 capture = (success, result) -> {
            if (success) {
                returned[0] = result;
                didReturn[0] = true;
            }
        };
        net.minecraft.class_2168 runAs = source.method_9217().method_9230(2);
        net.minecraft.class_2991 manager = source.method_9211().method_3740();
        net.minecraft.class_8868 instance = function.method_52595(arguments, manager.method_12900(), runAs);
        int ran = run(instance, runAs, null, capture);
        return didReturn[0] ? new class_3062$class_8601(returned[0], true) : new class_3062$class_8601(Math.max(ran, 0), false);
    }

    private CbFunctions() {
    }
}
