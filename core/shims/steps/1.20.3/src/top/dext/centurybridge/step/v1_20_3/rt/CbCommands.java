package top.dext.centurybridge.step.v1_20_3.rt;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

/**
 * 1.20.2's command API on 1.20.3: CommandSourceStack's brigadier ResultConsumer and return-value consumer
 * (1.20.3 has one CommandResultCallback, told (success, result)), Commands.performCommand and
 * performPrefixedCommand answering the result (1.20.3 answers nothing and queues a nested call), and
 * ScoreHolderArgument answering names (1.20.3 answers ScoreHolders, under the same descriptors for the
 * collections).
 */
public final class CbCommands {

    // ------------------------------------------------------------------ CommandSourceStack callbacks (class_2168)

    /**
     * The command context a result is reported from: 1.20.3's command step hands its brigadier result consumer
     * the context of each command it runs; the ExecuteCommand mixin keeps it here while the callback is told.
     */
    private static final ThreadLocal<CommandContext<?>> REPORTING = new ThreadLocal<>();

    public static ResultConsumer<Object> cbReporting(ResultConsumer<Object> consumer) {
        return (context, success, result) -> {
            CommandContext<?> outer = REPORTING.get();
            REPORTING.set(context);
            try {
                consumer.onCommandComplete(context, success, result);
            } finally {
                REPORTING.set(outer);
            }
        };
    }

    /**
     * a 1.20.2 ResultConsumer attached to a source, as 1.20.3's callback: it gets the context of the command
     * whose result this is -- the one 1.20.3's command step is reporting, else (a result 1.20.3 reports
     * outside a command step, a return or a function's) a context of the source it was attached to
     */
    public static final class CbLegacyResultCallback implements net.minecraft.class_8935 {
        final ResultConsumer<net.minecraft.class_2168> consumer;
        volatile net.minecraft.class_2168 source;

        CbLegacyResultCallback(ResultConsumer<net.minecraft.class_2168> consumer) {
            this.consumer = consumer;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onResult(boolean success, int result) {
            CommandContext<?> reporting = REPORTING.get();
            CommandContext<net.minecraft.class_2168> context = reporting != null && reporting.getSource() instanceof net.minecraft.class_2168
                ? (CommandContext<net.minecraft.class_2168>) reporting : contextOf(source);
            consumer.onCommandComplete(context, success, result);
        }
    }

    private static CommandContext<net.minecraft.class_2168> contextOf(net.minecraft.class_2168 source) {
        return new CommandContext<>(source, "", Map.of(), null, null, List.of(), StringRange.at(0), null, null, false);
    }

    /** the callback a source carries, as 1.20.2's ResultConsumer */
    private static ResultConsumer<net.minecraft.class_2168> asConsumer(net.minecraft.class_8935 callback) {
        if (callback instanceof CbLegacyResultCallback legacy) {
            return legacy.consumer;
        }
        return (context, success, result) -> callback.onResult(success, result);
    }

    /** was CommandSourceStack.withCallback(ResultConsumer) (method_9231) */
    public static net.minecraft.class_2168 method_9231(net.minecraft.class_2168 source, ResultConsumer<net.minecraft.class_2168> consumer) {
        CbLegacyResultCallback callback = new CbLegacyResultCallback(consumer);
        net.minecraft.class_2168 with = source.method_9231(callback);
        callback.source = with;
        return with;
    }

    /** was CommandSourceStack.withCallback(ResultConsumer, BinaryOperator) (method_9209): 1.20.2's merge of the two */
    public static net.minecraft.class_2168 method_9209(net.minecraft.class_2168 source, ResultConsumer<net.minecraft.class_2168> consumer,
                                                       BinaryOperator<ResultConsumer<net.minecraft.class_2168>> merge) {
        return method_9231(source, merge.apply(asConsumer(source.method_54881()), consumer));
    }

    /**
     * was CommandSourceStack.onCommandComplete(context, success, result) (method_9215): 1.20.2 told the source's
     * ResultConsumer -- a 1.20.2 consumer is told with this very context; any other callback the result
     */
    public static void method_9215(net.minecraft.class_2168 source, CommandContext<net.minecraft.class_2168> context, boolean success, int result) {
        net.minecraft.class_8935 callback = source.method_54881();
        net.minecraft.class_8935 consumer = callback instanceof CbReturnValueCallback r ? r.callback : callback;
        if (consumer instanceof CbLegacyResultCallback legacy) {
            legacy.consumer.onCommandComplete(context, success, result);
        } else {
            consumer.onResult(success, result);
        }
    }

    /**
     * the protected 1.20.2 constructor's ResultConsumer and return-value consumer, as 1.20.3's one callback (the
     * constructor reshape's post static fills in the source the consumer gets)
     */
    public static net.minecraft.class_8935 cbCallback(ResultConsumer<net.minecraft.class_2168> consumer, IntConsumer returnValueConsumer) {
        net.minecraft.class_8935 callback = consumer == null ? net.minecraft.class_8935.field_47158 : new CbLegacyResultCallback(consumer);
        return returnValueConsumer == null ? callback : new CbReturnValueCallback(callback, returnValueConsumer);
    }

    public static void cbSourceBuilt(net.minecraft.class_2168 built, net.minecraft.class_2165 output, net.minecraft.class_243 position,
                                     net.minecraft.class_241 rotation, net.minecraft.class_3218 level, int permission, String textName,
                                     net.minecraft.class_2561 displayName, net.minecraft.server.MinecraftServer server,
                                     net.minecraft.class_1297 entity, boolean silent, ResultConsumer<net.minecraft.class_2168> consumer,
                                     net.minecraft.class_2183.class_2184 anchor, net.minecraft.class_7448 signingContext,
                                     net.minecraft.class_7620 chatChainer, IntConsumer returnValueConsumer) {
        net.minecraft.class_8935 callback = built.method_54881();
        if (callback instanceof CbReturnValueCallback r) {
            callback = r.callback;
        }
        if (callback instanceof CbLegacyResultCallback legacy) {
            legacy.source = built;
        }
    }

    /**
     * 1.20.2's return-value consumer on a source, chained to the callback: 1.20.3 reports a return to the same
     * callback as any successful result, so the consumer hears every success the source reports
     */
    public static final class CbReturnValueCallback implements net.minecraft.class_8935 {
        final net.minecraft.class_8935 callback;
        final IntConsumer returnValues;

        CbReturnValueCallback(net.minecraft.class_8935 callback, IntConsumer returnValues) {
            this.callback = callback;
            this.returnValues = returnValues;
        }

        @Override
        public void onResult(boolean success, int result) {
            callback.onResult(success, result);
            if (success) {
                returnValues.accept(result);
            }
        }
    }

    /** was CommandSourceStack.withReturnValueConsumer(IntConsumer) (method_51411) */
    public static net.minecraft.class_2168 method_51411(net.minecraft.class_2168 source, IntConsumer returnValues) {
        net.minecraft.class_8935 callback = source.method_54881();
        if (callback instanceof CbReturnValueCallback r) {
            callback = r.callback;
        }
        return source.method_9231(new CbReturnValueCallback(callback, returnValues));
    }

    /** was CommandSourceStack.getReturnValueConsumer() (method_51413): the one set, else 1.20.2's no-op */
    public static IntConsumer method_51413(net.minecraft.class_2168 source) {
        return source.method_54881() instanceof CbReturnValueCallback r ? r.returnValues : value -> {
        };
    }

    // ------------------------------------------------------------------ Commands (class_2170)

    /**
     * was Commands.performCommand(ParseResults, String) (method_9249), an int in 1.20.2: the command run now
     * (1.20.3 queues a call made while commands run), its result -- the sum of what its forks answered -- or 0
     * when it failed (1.20.3 reports the failure to the source, as 1.20.2 did)
     */
    public static int method_9249(net.minecraft.class_2170 commands, ParseResults<net.minecraft.class_2168> parse, String command) {
        int[] sum = {0};
        net.minecraft.class_8935 capture = (success, result) -> {
            if (success) {
                sum[0] += result;
            }
        };
        net.minecraft.class_2168 source = parse.getContext().getSource();
        ParseResults<net.minecraft.class_2168> capturing = new ParseResults<>(
            parse.getContext().withSource(source.method_9231(net.minecraft.class_8935.chain(source.method_54881(), capture))),
            parse.getReader(), parse.getExceptions());
        // 1.20.3 queues a call made while commands run on this thread: cleared for the call, it runs now
        net.minecraft.class_8854<net.minecraft.class_2168> outer = top.dext.centurybridge.step.v1_20_3.mixin.CbCommandsAccess.cb$currentContext().get();
        top.dext.centurybridge.step.v1_20_3.mixin.CbCommandsAccess.cb$currentContext().set(null);
        try {
            commands.method_9249(capturing, command);
        } finally {
            top.dext.centurybridge.step.v1_20_3.mixin.CbCommandsAccess.cb$currentContext().set(outer);
        }
        return sum[0];
    }

    /** was Commands.performPrefixedCommand(source, command) (method_44252), an int in 1.20.2: as {@link #method_9249} */
    public static int method_44252(net.minecraft.class_2170 commands, net.minecraft.class_2168 source, String command) {
        String line = command.startsWith("/") ? command.substring(1) : command;
        ParseResults<net.minecraft.class_2168> parse = commands.method_9235().parse(line, source);
        return method_9249(commands, parse, line);
    }

    /** a 1.20.2 Commands subclass's performCommand answer, which 1.20.3's void method drops */
    public static void cbDropResult(int result) {
    }

    // ------------------------------------------------------------------ ScoreHolderArgument (class_2233)
    //
    // 1.20.3 kept the shapes of ScoreHolderArgument's getNames family -- a Collection, a Supplier of one --
    // and made the elements ScoreHolders where 1.20.2 had names. A holder's name is its scoreboard name
    // (ScoreHolder.getScoreboardName); a name stands for ScoreHolder.forNameOnly(name), the holder 1.20.3's
    // own name arguments make of a plain name. Calls are converted in place (the step's same-shape
    // callReshapes), a 1.20.2 Result class's getNames sits behind a converting bridge (convertHooks), a 1.20.2
    // Result lambda behind cbScoreHolderResult. Erasure cannot tell where a value came from, so each converter
    // remembers what it converted and the opposite one hands that back: a value crossing twice -- a 1.20.2
    // result's names that vanilla passes on to a 1.20.2 caller, vanilla's holders a 1.20.2 override hands to
    // super -- is the value it was. An element already of the wanted kind is left as it is.

    /** a holder as its name; a name as it is */
    private static Object nameOf(Object element) {
        return element instanceof net.minecraft.class_9015 holder ? holder.method_5820() : element;
    }

    /** a name as its holder; a holder as it is */
    private static Object holderOf(Object element) {
        return element instanceof String name ? net.minecraft.class_9015.method_55422(name) : element;
    }

    /**
     * A collection converted for the other release: its elements as converted when it was made (getNames
     * answers a fresh, unmodifiable collection in both releases' vanilla paths) and the collection they came
     * from, which the opposite converter hands back.
     */
    private static final class CbConvertedNames<E> extends java.util.AbstractList<E> {
        final Collection<?> from;
        /** true: names made from holders; false: holders made from names */
        final boolean names;
        private final Object[] elements;

        CbConvertedNames(Collection<?> from, boolean names) {
            this.from = from;
            this.names = names;
            Object[] in = from.toArray();
            for (int i = 0; i < in.length; i++) {
                in[i] = names ? nameOf(in[i]) : holderOf(in[i]);
            }
            this.elements = in;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E get(int index) {
            return (E) elements[index];
        }

        @Override
        public int size() {
            return elements.length;
        }
    }

    /** 1.20.3's holders as the names 1.20.2 answered */
    @SuppressWarnings("unchecked")
    public static Collection<String> cbNames(Collection<?> holders) {
        if (holders instanceof CbConvertedNames<?> c) {
            return (Collection<String>) (c.names ? c : c.from);
        }
        return holders == null ? null : new CbConvertedNames<>(holders, true);
    }

    /** 1.20.2's names as the holders 1.20.3 answers */
    @SuppressWarnings("unchecked")
    public static Collection<net.minecraft.class_9015> cbHolders(Collection<?> names) {
        if (names instanceof CbConvertedNames<?> c) {
            return (Collection<net.minecraft.class_9015>) (c.names ? c.from : c);
        }
        return names == null ? null : new CbConvertedNames<>(names, false);
    }

    /** a supplier of one release's collection as the other's: each collection it gives, converted */
    private static final class CbConvertedSupplier<E> implements Supplier<Collection<E>> {
        final Supplier<?> from;
        final boolean names;

        CbConvertedSupplier(Supplier<?> from, boolean names) {
            this.from = from;
            this.names = names;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<E> get() {
            Collection<?> got = (Collection<?>) from.get();
            return (Collection<E>) (names ? cbNames(got) : cbHolders(got));
        }
    }

    /** a 1.20.3 supplier of default holders as 1.20.2's supplier of default names */
    @SuppressWarnings("unchecked")
    public static Supplier<Collection<String>> cbNameSupplier(Supplier<?> holders) {
        if (holders instanceof CbConvertedSupplier<?> s) {
            return (Supplier<Collection<String>>) (s.names ? s : s.from);
        }
        return holders == null ? null : new CbConvertedSupplier<>(holders, true);
    }

    /** a 1.20.2 supplier of default names as 1.20.3's supplier of default holders */
    @SuppressWarnings("unchecked")
    public static Supplier<Collection<net.minecraft.class_9015>> cbHolderSupplier(Supplier<?> names) {
        if (names instanceof CbConvertedSupplier<?> s) {
            return (Supplier<Collection<net.minecraft.class_9015>>) (s.names ? s.from : s);
        }
        return names == null ? null : new CbConvertedSupplier<>(names, false);
    }

    /** 1.20.2's ScoreHolderArgument.Result -- names in and out -- as a 1.20.2 lambda or method reference built it */
    @FunctionalInterface
    public interface CbLegacyScoreHolderResult {
        Collection<String> getNames(net.minecraft.class_2168 source, Supplier<Collection<String>> defaults)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException;
    }

    /** a 1.20.2 Result lambda as 1.20.3's Result (lambdaAdapters): vanilla's default holders in, its names out as holders */
    public static net.minecraft.class_2233.class_2234 cbScoreHolderResult(CbLegacyScoreHolderResult legacy) {
        return (source, defaults) -> cbHolders(legacy.getNames(source, cbNameSupplier(defaults)));
    }

    /** was ScoreHolderArgument.getName(context, name) (method_9452): the holder's scoreboard name */
    public static String cbName(net.minecraft.class_9015 holder) {
        return holder.method_5820();
    }

    // ------------------------------------------------------------------ FunctionArgument.Result (class_2284$class_2285)

    /** marks a 1.20.2 FunctionArgument.Result given 1.20.3's unwrapToCollection (adapterHooks) */
    public interface CbLegacyFunctionArgumentResult {
    }

    /**
     * FunctionArgument.Result.unwrapToCollection(context), abstract since 1.20.3, for a 1.20.2 result: its
     * unwrap -- a single function as a collection of one, a tag's functions as they are -- as 1.20.3's own
     * results answer it
     */
    public static com.mojang.datafixers.util.Pair<net.minecraft.class_2960, Collection<net.minecraft.class_2158<net.minecraft.class_2168>>>
            cbUnwrapToCollection(net.minecraft.class_2284.class_2285 result, CommandContext<net.minecraft.class_2168> context)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        com.mojang.datafixers.util.Pair<net.minecraft.class_2960, com.mojang.datafixers.util.Either<net.minecraft.class_2158<net.minecraft.class_2168>,
            Collection<net.minecraft.class_2158<net.minecraft.class_2168>>>> unwrapped = result.method_9770(context);
        return com.mojang.datafixers.util.Pair.of(unwrapped.getFirst(),
            unwrapped.getSecond().map(java.util.Collections::singleton, functions -> functions));
    }

    private CbCommands() {
    }
}
