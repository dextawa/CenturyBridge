package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 1.21.11 ItemInput members 26.1 dropped (its ItemInput is a record of the item and its component changes),
 * 1.21.11's bodies on 26.1's record.
 */
public final class CbWallItemInputs {

    private CbWallItemInputs() {
    }

    private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType(
        (name, max) -> Component.translatableEscape("arguments.item.overstacked", name, max));

    /** getItem() */
    public static Item getItem(ItemInput input) {
        return input.item().value();
    }

    /** createItemStack(count, checkSize): the stack with the changes; over the item's maximum stack size refused when checked */
    public static ItemStack createItemStack(ItemInput input, int count, boolean checkSize) throws CommandSyntaxException {
        ItemStack stack = new ItemStack(input.item(), count);
        stack.applyComponentsAndValidate(input.components());
        if (checkSize && count > stack.getMaxStackSize()) {
            throw ERROR_STACK_TOO_BIG.create(itemName(input), stack.getMaxStackSize());
        }
        return stack;
    }

    /** serialize(registries): the item's id, and its component changes in brackets (id=snbt, !id for a removed one) */
    public static String serialize(ItemInput input, HolderLookup.Provider registries) {
        StringBuilder out = new StringBuilder(itemName(input));
        DynamicOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        String components = input.components().entrySet().stream().flatMap(entry -> component(ops, entry)).collect(Collectors.joining(","));
        if (!components.isEmpty()) {
            out.append('[').append(components).append(']');
        }
        return out.toString();
    }

    private static String itemName(ItemInput input) {
        return input.item().unwrapKey().<Object>map(key -> key.identifier()).orElseGet(() -> "unknown[" + input.item() + "]").toString();
    }

    private static Stream<String> component(DynamicOps<Tag> ops, Map.Entry<DataComponentType<?>, Optional<?>> entry) {
        DataComponentType<?> type = entry.getKey();
        Identifier id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type);
        if (id == null) {
            return Stream.empty();
        }
        Optional<?> value = entry.getValue();
        if (value.isPresent()) {
            TypedDataComponent<?> typed = TypedDataComponent.createUnchecked(type, value.get());
            return typed.encodeValue(ops).result().stream().map(tag -> id + "=" + tag);
        }
        return Stream.of("!" + id);
    }
}
