package top.dext.centurybridge.step.v1_20_3.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_5699;
import net.minecraft.class_7923;

/**
 * 1.20.2 CraftingRecipeCodecs (gone in 1.20.3, whose ItemStack carries codecs of its own with other error
 * messages): its three codecs, built as 1.20.2 built them.
 */
public class CraftingRecipeCodecs {

    /** 1.20.2: an item id that must not be air (private there) */
    public static final Codec<class_1792> field_46094 = class_5699.method_48112(class_7923.field_41178.method_39673(),
        item -> item == class_1802.field_8162
            ? DataResult.error(() -> "Crafting result must not be minecraft:air") : DataResult.success(item));

    /** 1.20.2: {"item": .., "count": ..} with count optional, default 1, positive */
    public static final Codec<class_1799> field_46092 = RecordCodecBuilder.create(i -> i.group(
        field_46094.fieldOf("item").forGetter(class_1799::method_7909),
        class_5699.method_53049(class_5699.field_33442, "count", 1).forGetter(class_1799::method_7947)
    ).apply(i, class_1799::new));

    /** 1.20.2: the same, refusing an empty stack (package-private there) */
    public static final Codec<class_1799> field_46093 = class_5699.method_48112(field_46092,
        stack -> stack.method_7960()
            ? DataResult.error(() -> "Empty ingredient not allowed here") : DataResult.success(stack));

    public CraftingRecipeCodecs() {
    }
}
