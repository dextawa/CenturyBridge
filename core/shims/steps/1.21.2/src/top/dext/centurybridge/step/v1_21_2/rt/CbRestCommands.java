package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import org.slf4j.Logger;
import java.util.stream.Stream;

/**
 * 1.21.1's command and command-text API on 1.21.2.
 *
 * <ul>
 * <li>Selector and score text contents: 1.21.1 classes holding the pattern / name string and the selector
 * parsed from it (null when it did not parse); 1.21.2 records holding a ParsedSelector (the string and the
 * selector) and, for a score, Either a ParsedSelector or the plain name -- exactly what 1.21.2's own codec
 * makes of the string (Codec.either(ParsedSelector.CODEC, STRING): the selector when it parses). A pattern
 * that does not parse keeps its string with a null selector, which 1.21.1 resolved to empty text
 * (CbRestSelectorContentsMixin answers the same on 1.21.2).</li>
 * <li>Recipe and advancement ids from a command source: 1.21.1 read them from the server's managers, as
 * here; recipes are keyed by ResourceKey on 1.21.2.</li>
 * <li>sendSystemMessage of entities and players, which 1.21.2 took off Entity with its CommandSource
 * implementation (1.21.2's command source of an entity is CommandSource.NULL; ServerPlayer's message
 * method is a new member).</li>
 * </ul>
 */
public final class CbRestCommands {

    private static final Logger LOGGER = LogUtils.getLogger();

    private CbRestCommands() {
    }

    // ------------------------------------------------------------------ SelectorContents (class_2579)

    /**
     * new SelectorContents(pattern, separator): 1.21.1 parsed the pattern (logging a warning and keeping a
     * null selector when it did not parse). 1.21.2's ParsedSelector of the same string; for a pattern that
     * does not parse, the string with a null selector (1.21.2's ParsedSelector compares by the string alone,
     * as 1.21.1's contents compared by pattern).
     */
    public static net.minecraft.class_2579 newSelectorContents(String pattern, Optional<net.minecraft.class_2561> separator) {
        return new net.minecraft.class_2579(parsedSelector(pattern), separator);
    }

    /** Component.selector(pattern, separator): a mutable component of those contents, as 1.21.1 */
    public static net.minecraft.class_5250 method_43467(String pattern, Optional<net.minecraft.class_2561> separator) {
        return net.minecraft.class_5250.method_43477(newSelectorContents(pattern, separator));
    }

    /** ParsedSelector of the string, as 1.21.1's contents parsed it: null selector when it does not parse */
    static net.minecraft.class_10104 parsedSelector(String pattern) {
        try {
            return new net.minecraft.class_10104(pattern,
                new net.minecraft.class_2303(new com.mojang.brigadier.StringReader(pattern), true).method_9882());
        } catch (CommandSyntaxException e) {
            LOGGER.warn("Invalid selector component: {}: {}", pattern, e.getMessage());
            return new net.minecraft.class_10104(pattern, null);
        }
    }

    /** SelectorContents.getPattern() */
    public static String selectorPattern(net.minecraft.class_2579 contents) {
        return contents.comp_3080().comp_3067();
    }

    /** SelectorContents.getSelector(): null when the pattern did not parse */
    public static net.minecraft.class_2300 selectorSelector(net.minecraft.class_2579 contents) {
        return contents.comp_3080().comp_3068();
    }

    /** SelectorContents.getSeparator() */
    public static Optional<net.minecraft.class_2561> selectorSeparator(net.minecraft.class_2579 contents) {
        return contents.comp_3081();
    }

    // ------------------------------------------------------------------ ScoreContents (class_2578)

    /**
     * new ScoreContents(name, objective): 1.21.1 kept the name and the selector parsed from it (null when it
     * did not parse) and resolved through the selector when there was one. 1.21.2: the ParsedSelector when
     * the name parses, else the plain name -- the same choice.
     */
    public static net.minecraft.class_2578 newScoreContents(String name, String objective) {
        net.minecraft.class_10104 parsed = parsedQuietly(name);
        return new net.minecraft.class_2578(parsed != null ? Either.left(parsed) : Either.right(name), objective);
    }

    private static net.minecraft.class_10104 parsedQuietly(String name) {
        try {
            return new net.minecraft.class_10104(name,
                new net.minecraft.class_2303(new com.mojang.brigadier.StringReader(name), true).method_9882());
        } catch (CommandSyntaxException e) {
            return null;
        }
    }

    /** ScoreContents.getName(): the string it was made of */
    public static String scoreName(net.minecraft.class_2578 contents) {
        return contents.comp_3078().map(net.minecraft.class_10104::comp_3067, n -> n);
    }

    /** ScoreContents.getSelector(): the parsed selector, null for a plain name */
    public static net.minecraft.class_2300 scoreSelector(net.minecraft.class_2578 contents) {
        return contents.comp_3078().left().map(net.minecraft.class_10104::comp_3068).orElse(null);
    }

    /** ScoreContents.getObjective() */
    public static String scoreObjective(net.minecraft.class_2578 contents) {
        return contents.comp_3079();
    }

    // ------------------------------------------------------------------ recipes and advancements

    private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType(
        id -> net.minecraft.class_2561.method_54159("recipe.notFound", id));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ADVANCEMENT = new DynamicCommandExceptionType(
        id -> net.minecraft.class_2561.method_54159("advancement.advancementNotFound", id));

    /** ResourceLocationArgument.getRecipe(context, name): the server's recipe of that id, as 1.21.1 */
    public static net.minecraft.class_8786<?> method_9442(CommandContext<net.minecraft.class_2168> context, String name)
            throws CommandSyntaxException {
        net.minecraft.class_2960 id = net.minecraft.class_2232.method_9443(context, name);
        return context.getSource().method_9211().method_3772()
            .method_8130(net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_52178, id))
            .orElseThrow(() -> ERROR_UNKNOWN_RECIPE.create(id));
    }

    /** ResourceLocationArgument.getAdvancement(context, name): the server's advancement of that id, as 1.21.1 */
    public static net.minecraft.class_8779 method_9439(CommandContext<net.minecraft.class_2168> context, String name)
            throws CommandSyntaxException {
        net.minecraft.class_2960 id = net.minecraft.class_2232.method_9443(context, name);
        net.minecraft.class_8779 advancement = context.getSource().method_9211().method_3851().method_12896(id);
        if (advancement == null) {
            throw ERROR_UNKNOWN_ADVANCEMENT.create(id);
        }
        return advancement;
    }

    /** the server's recipe ids (1.21.1's RecipeManager.getRecipeIds) */
    static Stream<net.minecraft.class_2960> recipeIds(net.minecraft.server.MinecraftServer server) {
        return server.method_3772().method_8126().stream().map(h -> h.comp_1932().method_29177());
    }

    /**
     * SharedSuggestionProvider.getRecipeNames(): a command source stack's are the server's recipes (1.21.1's
     * body); a client's suggestion provider answered the recipes its server had sent, which a 1.21.2 server
     * no longer sends -- CbRestClient answers it where the recipes are known to the client (an integrated
     * server's).
     */
    public static Stream<net.minecraft.class_2960> recipeNames(net.minecraft.class_2172 provider) {
        if (provider instanceof net.minecraft.class_2168 stack) {
            return recipeIds(stack.method_9211());
        }
        if (provider instanceof LegacySuggestions legacy) {
            return legacy.method_9273();
        }
        return CbRestClient.recipeNames(provider);
    }

    /** a mod's own SharedSuggestionProvider implementing 1.21.1's getRecipeNames (adapterHooks) */
    public interface LegacySuggestions {
        Stream<net.minecraft.class_2960> method_9273();
    }

    /**
     * SuggestionProviders.ALL_RECIPES: 1.21.1 registered it as "all_recipes", which the client resolved
     * against the recipes the server had sent. 1.21.2 sends none; an unregistered provider is sent to the
     * client as ask_server (SuggestionProviders.getName), so the client asks and the server answers from its
     * recipes -- the same suggestions.
     */
    private static final SuggestionProvider<net.minecraft.class_2168> ALL_RECIPES = (context, builder) ->
        net.minecraft.class_2172.method_9257(recipeIds(context.getSource().method_9211()), builder);

    /** SuggestionProviders.ALL_RECIPES (see above): one provider, as 1.21.1's constant */
    public static SuggestionProvider<net.minecraft.class_2168> field_10932() {
        return ALL_RECIPES;
    }

    // ------------------------------------------------------------------ commands

    /** WorldCoordinates.current(): "~ ~ ~", 1.21.1's body (three relative coordinates of offset 0) */
    public static net.minecraft.class_2280 method_9751() {
        return new net.minecraft.class_2280(new net.minecraft.class_2278(true, 0.0), new net.minecraft.class_2278(true, 0.0),
            new net.minecraft.class_2278(true, 0.0));
    }

    /**
     * GameRuleCommand.register(dispatcher): 1.21.1 offered every game rule type; 1.21.2's register(dispatcher,
     * context) offers those the context's features enable, so the context enables them all.
     */
    public static void method_13392(CommandDispatcher<net.minecraft.class_2168> dispatcher) {
        net.minecraft.class_3065.method_13392(dispatcher, allFeatures(net.minecraft.class_7225.class_7874.method_46761(Stream.empty())));
    }

    private static net.minecraft.class_7157 allFeatures(net.minecraft.class_7225.class_7874 lookups) {
        return net.minecraft.class_7157.method_46722(lookups, net.minecraft.class_7701.field_40180.method_45383());
    }

    /**
     * CommandBuildContext.enabledFeatures() of a mod's own 1.21.1 CommandBuildContext: 1.21.1 asked a
     * context for nothing but its lookups (pre-filtered by whoever made it) and filtered nothing by feature
     * again; the one 1.21.2 reader (GameRuleCommand) then offers every rule, as 1.21.1 did.
     */
    public static net.minecraft.class_7699 enabledFeatures(net.minecraft.class_7157 context) {
        return net.minecraft.class_7701.field_40180.method_45383();
    }

    // ------------------------------------------------------------------ sendSystemMessage (class_2165.method_43496)

    /** an entity class of a mod declaring 1.21.1's sendSystemMessage(Component) (adapterHooks) */
    public interface SystemMessageTarget {
        void method_43496(net.minecraft.class_2561 message);
    }

    /**
     * source.sendSystemMessage(message) of 1.21.1, keyed on CommandSource, which every 1.21.1 entity was:
     * a ServerPlayer's goes to 1.21.2's ServerPlayer.sendSystemMessage(Component) (1.21.1's body, and where
     * a mod's override now lives, overrideRenames); an entity whose mod class declares the method answers
     * itself; other entities take 1.21.1's bodies ({@link #entitySystemMessage}); any other command source
     * is still one on 1.21.2.
     */
    public static void sendSystemMessage(net.minecraft.class_2165 source, net.minecraft.class_2561 message) {
        if (source instanceof net.minecraft.class_3222 player) {
            player.method_64398(message);
        } else if (source instanceof SystemMessageTarget target) {
            target.method_43496(message);
        } else if (source instanceof net.minecraft.class_1297 entity) {
            entitySystemMessage(entity, message);
        } else {
            source.method_43496(message);
        }
    }

    /**
     * 1.21.1's sendSystemMessage bodies of entities that are not server players: LocalPlayer and RemotePlayer
     * added it to the chat, Entity's did nothing.
     */
    static void entitySystemMessage(net.minecraft.class_1297 entity, net.minecraft.class_2561 message) {
        if (entity.method_37908().method_8608()) {
            CbRestClient.playerSystemMessage(entity, message);
        }
    }

    /** super.sendSystemMessage(message) reaching Entity's 1.21.1 body, which did nothing */
    public static void entitySuperSystemMessage(net.minecraft.class_1297 entity, net.minecraft.class_2561 message) {
    }
}
