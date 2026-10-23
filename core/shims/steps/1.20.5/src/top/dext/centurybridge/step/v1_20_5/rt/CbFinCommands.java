package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import top.dext.centurybridge.step.v1_20_5.facade.ItemParserTagResult;

/**
 * 1.20.4's command argument members (area s4fin commands/arguments) on 1.20.5's:
 * <ul>
 * <li>Item arguments. 1.20.4 wrote an item as {@code id{nbt}} (or {@code #tag{nbt}} where tags were allowed)
 *     and carried the stack's NBT; 1.20.5 writes {@code id[components]} and carries components. 1.20.4's static
 *     parsers parse 1.20.4's syntax again, and the NBT goes into and out of the components through the stack's
 *     1.20.4 NBT view (CbItemTags), exactly as an ItemStack's setTag / getTag do.</li>
 * <li>ResourceLocationArgument.getPredicate / getItemModifier: the server's loot registries (1.20.5 keeps
 *     predicates and item modifiers there), failing with 1.20.4's messages.</li>
 * </ul>
 */
public final class CbFinCommands {

    private static final DynamicCommandExceptionType UNKNOWN_ITEM = new DynamicCommandExceptionType(
        id -> net.minecraft.class_2561.method_54159("argument.item.id.invalid", id));
    private static final DynamicCommandExceptionType UNKNOWN_TAG = new DynamicCommandExceptionType(
        id -> net.minecraft.class_2561.method_54159("arguments.item.tag.unknown", id));
    private static final DynamicCommandExceptionType UNKNOWN_PREDICATE = new DynamicCommandExceptionType(
        id -> net.minecraft.class_2561.method_54159("predicate.unknown", id));
    private static final DynamicCommandExceptionType UNKNOWN_ITEM_MODIFIER = new DynamicCommandExceptionType(
        id -> net.minecraft.class_2561.method_54159("item_modifier.unknown", id));

    // ---------------------------------------------------------------- NBT <-> components

    /** an item's 1.20.4 NBT as the components it sets (a stack of the item given the tag, its explicit components) */
    public static net.minecraft.class_9323 components(net.minecraft.class_6880<net.minecraft.class_1792> item, net.minecraft.class_2487 tag) {
        net.minecraft.class_1799 stack = new net.minecraft.class_1799(item);
        if (tag != null) {
            CbItemTags.setTag(stack, tag.method_10553());
        }
        net.minecraft.class_9323.class_9324 b = net.minecraft.class_9323.method_57827();
        for (java.util.Map.Entry<net.minecraft.class_9331<?>, Optional<?>> e : stack.method_57380().method_57846()) {
            if (e.getValue().isPresent()) {
                put(b, e.getKey(), e.getValue().get());
            }
        }
        return b.method_57838();
    }

    @SuppressWarnings("unchecked")
    private static <T> void put(net.minecraft.class_9323.class_9324 b, net.minecraft.class_9331<T> type, Object value) {
        b.method_57840(type, (T) value);
    }

    /** the 1.20.4 NBT of the item with those components (null for none), as ItemStack.getTag() reads it */
    public static net.minecraft.class_2487 tag(net.minecraft.class_6880<net.minecraft.class_1792> item, net.minecraft.class_9323 components) {
        net.minecraft.class_1799 stack = new net.minecraft.class_1799(item);
        stack.method_57365(components);
        net.minecraft.class_2487 tag = CbItemTags.getTag(stack);
        return tag == null ? null : tag.method_10553();
    }

    /** ItemParser.ItemResult.nbt() */
    public static net.minecraft.class_2487 comp_629(net.minecraft.class_2291.class_7215 result) {
        return tag(result.comp_628(), result.comp_2439());
    }

    /** ItemInput.test(stack)'s 1.20.4 body: the same item, and the input's NBT contained in the stack's */
    public static boolean test(net.minecraft.class_6880<net.minecraft.class_1792> item, net.minecraft.class_9323 components, net.minecraft.class_1799 stack) {
        return stack.method_41406(item) && net.minecraft.class_2512.method_10687(tag(item, components), CbItemTags.getTag(stack), true);
    }

    // ---------------------------------------------------------------- 1.20.4's item syntax

    /** ItemParser.parseForItem(items, reader): {@code id{nbt}} */
    public static net.minecraft.class_2291.class_7215 parseForItem(net.minecraft.class_7225<net.minecraft.class_1792> items, StringReader reader)
            throws CommandSyntaxException {
        net.minecraft.class_6880<net.minecraft.class_1792> item = readItem(items, reader);
        net.minecraft.class_2487 tag = readNbt(reader);
        return new net.minecraft.class_2291.class_7215(item, components(item, tag));
    }

    /** ItemParser.parseForTesting(items, reader): {@code id{nbt}} or {@code #tag{nbt}} */
    public static Either<net.minecraft.class_2291.class_7215, ItemParserTagResult> parseForTesting(
            net.minecraft.class_7225<net.minecraft.class_1792> items, StringReader reader) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '#') {
            int start = reader.getCursor();
            reader.expect('#');
            net.minecraft.class_2960 id = net.minecraft.class_2960.method_12835(reader);
            Optional<? extends net.minecraft.class_6885.class_6888<net.minecraft.class_1792>> tag =
                items.method_46733(net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41197, id));
            if (tag.isEmpty()) {
                reader.setCursor(start);
                throw UNKNOWN_TAG.createWithContext(reader, id.toString());
            }
            return Either.right(new ItemParserTagResult(tag.get(), readNbt(reader)));
        }
        net.minecraft.class_6880<net.minecraft.class_1792> item = readItem(items, reader);
        net.minecraft.class_2487 nbt = readNbt(reader);
        return Either.left(new net.minecraft.class_2291.class_7215(item, components(item, nbt)));
    }

    private static net.minecraft.class_6880<net.minecraft.class_1792> readItem(net.minecraft.class_7225<net.minecraft.class_1792> items, StringReader reader)
            throws CommandSyntaxException {
        int start = reader.getCursor();
        net.minecraft.class_2960 id = net.minecraft.class_2960.method_12835(reader);
        Optional<? extends net.minecraft.class_6880<net.minecraft.class_1792>> item =
            items.method_46746(net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41197, id));
        if (item.isEmpty()) {
            reader.setCursor(start);
            throw UNKNOWN_ITEM.createWithContext(reader, id.toString());
        }
        return item.get();
    }

    private static net.minecraft.class_2487 readNbt(StringReader reader) throws CommandSyntaxException {
        return reader.canRead() && reader.peek() == '{' ? new net.minecraft.class_2522(reader).method_10727() : null;
    }

    /** ItemParser.fillSuggestions(items, builder, allowTags): item ids (and #tags where allowed); after an id, its NBT's brace */
    public static CompletableFuture<Suggestions> fillSuggestions(net.minecraft.class_7225<net.minecraft.class_1792> items, SuggestionsBuilder builder,
                                                                  boolean allowTags) {
        String rest = builder.getRemaining();
        if (rest.indexOf('{') >= 0) {
            return builder.buildFuture();
        }
        if (allowTags) {
            net.minecraft.class_2172.method_9258(items.method_42020().map(t -> t.method_40251().comp_327())::iterator, builder, "#");
        }
        return net.minecraft.class_2172.method_9257(items.method_42017().map(h -> h.method_40237().method_29177()), builder);
    }

    // ---------------------------------------------------------------- loot arguments

    /** ResourceLocationArgument.getPredicate(context, name) */
    public static net.minecraft.class_5341 getPredicate(CommandContext<net.minecraft.class_2168> context, String name) throws CommandSyntaxException {
        net.minecraft.class_2960 id = net.minecraft.class_2232.method_9443(context, name);
        return loot(context, net.minecraft.class_7924.field_50081, id).orElseThrow(() -> UNKNOWN_PREDICATE.create(id));
    }

    /** ResourceLocationArgument.getItemModifier(context, name) */
    public static net.minecraft.class_117 getItemModifier(CommandContext<net.minecraft.class_2168> context, String name) throws CommandSyntaxException {
        net.minecraft.class_2960 id = net.minecraft.class_2232.method_9443(context, name);
        return loot(context, net.minecraft.class_7924.field_50080, id).orElseThrow(() -> UNKNOWN_ITEM_MODIFIER.create(id));
    }

    private static <T> Optional<T> loot(CommandContext<net.minecraft.class_2168> context,
                                        net.minecraft.class_5321<? extends net.minecraft.class_2378<T>> registry, net.minecraft.class_2960 id) {
        net.minecraft.class_7225.class_7874 provider = context.getSource().method_9211().method_58576().method_58289();
        return provider.method_46762(registry).method_46746(net.minecraft.class_5321.method_29179(registry, id)).map(net.minecraft.class_6880::comp_349);
    }

    // ---------------------------------------------------------------- ArgumentSignatures

    /** ArgumentSignatures.get(name): the named argument's signature, or null */
    public static net.minecraft.class_7469 get(net.minecraft.class_7450 signatures, String name) {
        for (net.minecraft.class_7450.class_7599 e : signatures.comp_912()) {
            if (e.comp_913().equals(name)) {
                return e.comp_914();
            }
        }
        return null;
    }

    private CbFinCommands() {
    }
}
