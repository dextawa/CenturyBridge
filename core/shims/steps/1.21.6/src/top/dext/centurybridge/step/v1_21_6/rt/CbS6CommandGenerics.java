package top.dext.centurybridge.step.v1_21_6.rt;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;

/**
 * The command-related generic-signature changes of 1.21.6, for 1.21.5 code.
 *
 * <p>AVAILABLE_SOUNDS and SUMMONABLE_ENTITIES are registered providers, not server-only providers. Their delegates
 * cast the context source to SharedSuggestionProvider and call only that interface in both releases (javap of
 * SuggestionProviders' three suggestion bodies); 1.21.6 widened the fields' declarations to the type their bodies
 * already used. A CommandSourceStack is a SharedSuggestionProvider, so the 1.21.5 call contract needs no wrapper.
 * Keeping the registered object is also significant: SuggestionProviders.getName recognises that object's registry
 * wrapper, which a facade would hide.</p>
 *
 * <p>ClientPacketListener.commands is likewise the same dispatcher object. ClientSuggestionProvider implements
 * SharedSuggestionProvider in both releases, so a command registered against V's broad source domain accepts W's
 * client source, and a V override's dispatcher accepts every source W can hand it. The two override/call converters
 * therefore pass the dispatcher itself in both directions. This preserves its identity, root, result consumer and
 * registrations instead of making a detached generic facade.</p>
 *
 * <p>This does not claim that an explicitly ClientSuggestionProvider-typed command accepts every broad V source.
 * The cb-s6cmd control registers such a command on raw V and on W: both throw ClassCastException when it is executed
 * with CommandSourceStack, so that is not a regression introduced by this boundary. Commands actually registered
 * through V's SharedSuggestionProvider view execute with that broad source on both runtimes.</p>
 */
public final class CbS6CommandGenerics {

    private CbS6CommandGenerics() {
    }

    /** SuggestionProviders.AVAILABLE_SOUNDS, with 1.21.5's source type. */
    public static SuggestionProvider<net.minecraft.class_2168> field_10934() {
        return serverProvider(net.minecraft.class_2321.field_10934);
    }

    /** SuggestionProviders.SUMMONABLE_ENTITIES, with 1.21.5's source type. */
    public static SuggestionProvider<net.minecraft.class_2168> field_10935() {
        return serverProvider(net.minecraft.class_2321.field_10935);
    }

    /**
     * A provider that accepts a SharedSuggestionProvider also accepts its CommandSourceStack subtype. The registered
     * provider is deliberately returned unchanged (and null, although vanilla's constants are non-null, stays null).
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static SuggestionProvider<net.minecraft.class_2168> serverProvider(
            SuggestionProvider<net.minecraft.class_2172> provider) {
        return (SuggestionProvider) provider;
    }

    /** W -> V side of ClientPacketListener.getCommands: the live dispatcher, with V's source type. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CommandDispatcher dispatcherToV(CommandDispatcher dispatcher) {
        return dispatcher;
    }

    /** V -> W side of a converted getCommands override: the same live dispatcher, not a copy or a second root. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CommandDispatcher dispatcherToW(CommandDispatcher dispatcher) {
        return dispatcher;
    }
}
