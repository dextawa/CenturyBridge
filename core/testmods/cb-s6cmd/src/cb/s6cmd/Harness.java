package cb.s6cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.stream.Stream;
import net.minecraft.class_2168;
import net.minecraft.class_2172;
import net.minecraft.class_2321;
import net.minecraft.class_2960;
import net.minecraft.class_634;
import net.minecraft.class_637;

/**
 * 1.21.5 bytecode exercising the s6cmd generic closures. Run the unconverted jar on the 1.21.5 code jar as the
 * V-native control, then convert it one step and run this main class on the 1.21.6 code jar with that step's layer;
 * both runs must print the same 17 passing checks. No Fabric or Minecraft bootstrap is needed: Unsafe only bypasses
 * constructors of the listener/source shells, while every member under test is the real vanilla or converted bytecode.
 * The remapped code jar lacks stack-map frames in some unchanged interface defaults, so this standalone run uses
 * -noverify; all checkcast instructions and the behavior under test still execute.
 */
public final class Harness {
    private static int checks;
    private static sun.misc.Unsafe unsafe;

    private Harness() {
    }

    public static void main(String[] args) throws Exception {
        Field uf = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        uf.setAccessible(true);
        unsafe = (sun.misc.Unsafe) uf.get(null);

        suggestionProviders();
        vanillaDispatcherCall();
        convertedDispatcherOverride();
        System.out.println("CB-S6CMD ok " + checks + " checks");
    }

    /** Both field reads keep the registered provider; AVAILABLE_SOUNDS is invoked with the old source type. */
    private static void suggestionProviders() throws Exception {
        TestServerSource source = allocate(TestServerSource.class);
        CommandContext<class_2168> context = new CommandContext<>(source, "", Collections.emptyMap(), null, null,
            Collections.emptyList(), StringRange.at(0), null, null, false);

        SuggestionProvider<class_2168> sounds = class_2321.field_10934;
        check("minecraft:available_sounds".equals(providerName(sounds)), "AVAILABLE_SOUNDS registry identity", providerName(sounds));
        Suggestions soundSuggestions = sounds.getSuggestions(context, new SuggestionsBuilder("", 0)).join();
        check(has(soundSuggestions, "cb_s6cmd:test_sound"), "AVAILABLE_SOUNDS old source behavior", soundSuggestions);

        SuggestionProvider<class_2168> entities = class_2321.field_10935;
        check("minecraft:summonable_entities".equals(providerName(entities)), "SUMMONABLE_ENTITIES registry identity",
            providerName(entities));
        // Both fields use the same registered-wrapper conversion; executing the entity registry requires a full game.
        check(entities == class_2321.field_10935, "SUMMONABLE_ENTITIES repeated-read identity", entities);
    }

    /**
     * Run the same dispatcher domain checks on the raw V runtime and on converted V bytecode on W. In particular, the
     * explicitly narrow command is a control, not a V-compatible command: both releases must reject the broad server
     * source with CCE. The commands registered through the V-shaped view are the actual compatibility contract and
     * must accept that same broad source on both releases.
     */
    private static void vanillaDispatcherCall() throws Exception {
        CommandDispatcher<class_637> nativeDispatcher = new CommandDispatcher<>();
        class_637 clientSource = allocate(class_637.class);
        TestServerSource broadSource = allocate(TestServerSource.class);
        nativeDispatcher.register(LiteralArgumentBuilder.<class_637>literal("cb_s6cmd_native").executes(context -> {
            class_637 narrowSource = context.getSource();
            check(narrowSource == clientSource, "native narrow command source", narrowSource);
            return 11;
        }));

        class_634 listener = allocate(class_634.class);
        Field commands = class_634.class.getDeclaredField("field_3696");
        commands.setAccessible(true);
        commands.set(listener, nativeDispatcher);
        CommandDispatcher<class_2172> oldView = readCommands(listener);
        check((Object) oldView == nativeDispatcher, "vanilla getter identity", oldView);
        check(oldView.execute("cb_s6cmd_native", clientSource) == 11, "native narrow command through old call", "11");
        expectNarrowSourceRejection(oldView, broadSource);

        oldView.register(LiteralArgumentBuilder.<class_2172>literal("cb_s6cmd_mutation").executes(context -> {
            check(context.getSource() == clientSource, "old registration source", context.getSource());
            return 12;
        }));
        check(nativeDispatcher.execute("cb_s6cmd_mutation", clientSource) == 12,
            "old registration mutates native root", "12");

        oldView.register(LiteralArgumentBuilder.<class_2172>literal("cb_s6cmd_v_broad").executes(context -> {
            class_2172 source = context.getSource();
            check(source == broadSource, "V-native broad command source", source);
            check(source.method_9254().anyMatch(id -> id.equals(class_2960.method_60655("cb_s6cmd", "test_sound"))),
                "V-native broad command interface dispatch", source);
            return 14;
        }));
        check(oldView.execute("cb_s6cmd_v_broad", broadSource) == 14,
            "V-native command set with broad source", "14");
    }

    /** A class_637 command is outside the broad-source contract on V too; record the parity instead of hiding it. */
    private static void expectNarrowSourceRejection(CommandDispatcher<class_2172> dispatcher, class_2168 broadSource)
            throws Exception {
        try {
            dispatcher.execute("cb_s6cmd_native", broadSource);
            throw new AssertionError("CB-S6CMD FAIL narrow command accepted a broad source");
        } catch (ClassCastException expected) {
            check(String.valueOf(expected.getMessage()).contains("class_637"),
                "narrow command rejects broad source with CCE (V/W control parity)", expected);
        }
    }

    /** A converted override returns a broad V dispatcher; the call site and override bridge must undo each other. */
    private static void convertedDispatcherOverride() throws Exception {
        CommandDispatcher<class_2172> legacyDispatcher = new CommandDispatcher<>();
        LegacyListener.commands = legacyDispatcher;
        LegacyListener listener = allocate(LegacyListener.class);
        CommandDispatcher<class_2172> oldView = readCommands(listener);
        check(oldView == legacyDispatcher, "override call/return round trip identity", oldView);

        TestServerSource source = allocate(TestServerSource.class);
        oldView.register(LiteralArgumentBuilder.<class_2172>literal("cb_s6cmd_override").executes(context -> {
            check(context.getSource() == source, "broad override source identity", context.getSource());
            return 13;
        }));
        check(oldView.execute("cb_s6cmd_override", source) == 13, "broad override execution", "13");
        check(oldView.getRoot() == legacyDispatcher.getRoot(), "override root identity", oldView.getRoot());
    }

    /** The one assigned call row: conversion adds its W -> V return conversion here. */
    private static CommandDispatcher<class_2172> readCommands(class_634 listener) {
        return listener.method_2886();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String providerName(SuggestionProvider<?> provider) {
        return String.valueOf(class_2321.method_10027((SuggestionProvider) provider));
    }

    private static boolean has(Suggestions suggestions, String text) {
        return suggestions.getList().stream().anyMatch(s -> s.getText().equals(text));
    }

    private static void check(boolean condition, String label, Object detail) {
        if (!condition) {
            throw new AssertionError("CB-S6CMD FAIL " + label + ": " + detail);
        }
        checks++;
        System.out.println("CB-S6CMD pass " + label + " (" + detail + ")");
    }

    private static <T> T allocate(Class<T> type) throws InstantiationException {
        return type.cast(unsafe.allocateInstance(type));
    }

    /** Constructor bypassed: AVAILABLE_SOUNDS asks only for this old interface method. */
    private static final class TestServerSource extends class_2168 {
        private TestServerSource() {
            super(null, null, null, null, 0, "cb_s6cmd", null, null, null);
        }

        @Override
        public Stream<class_2960> method_9254() {
            return Stream.of(class_2960.method_60655("cb_s6cmd", "test_sound"));
        }
    }

    /** The assigned override row: conversion renames this body and puts the W-facing bridge in its place. */
    private static final class LegacyListener extends class_634 {
        private static CommandDispatcher<class_2172> commands;

        private LegacyListener() {
            super(null, null, null);
        }

        @Override
        public CommandDispatcher<class_2172> method_2886() {
            return commands;
        }
    }
}
