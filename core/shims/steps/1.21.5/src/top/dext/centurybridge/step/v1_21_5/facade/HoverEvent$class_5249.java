package top.dext.centurybridge.step.v1_21_5.facade;

import com.mojang.serialization.Codec;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_6880;
import net.minecraft.class_9326;

/**
 * 1.21.4's HoverEvent.ItemStackInfo (net/minecraft/class_2568$class_5249), gone in 1.21.5: a show_item hover event holds
 * the ItemStack itself there (HoverEvent.ShowItem, class_2568$class_10612). Converted references name this class
 * (classRenames). Every public / protected 1.21.4 member is here, public (a facade's members stand in for the class's;
 * access wideners never reach a CenturyBridge class). Bodies from javap -c -p 1.21.4 class_2568$class_5249: the item
 * holder, count and component patch, the stack built from them once and cached; FULL_CODEC is ItemStack.CODEC xmapped,
 * CODEC falls back to ItemStack.SIMPLE_ITEM_CODEC; equals / hashCode over item, count and patch.
 */
public class HoverEvent$class_5249 {
    /** FULL_CODEC */
    public static final Codec<HoverEvent$class_5249> field_46608 =
        class_1799.field_24671.xmap(HoverEvent$class_5249::new, HoverEvent$class_5249::method_27683);
    /** SIMPLE_CODEC (private in 1.21.4) */
    public static final Codec<HoverEvent$class_5249> field_49672 =
        class_1799.field_49267.xmap(HoverEvent$class_5249::new, HoverEvent$class_5249::method_27683);
    /** CODEC */
    public static final Codec<HoverEvent$class_5249> field_46609 = Codec.withAlternative(field_46608, field_49672);

    /** item */
    public final class_6880<class_1792> field_24355;
    /** count */
    public final int field_24356;
    /** components */
    public final class_9326 field_49673;
    /** itemStack (built on first use) */
    public class_1799 field_24358;

    public HoverEvent$class_5249(class_6880<class_1792> item, int count, class_9326 components) {
        this.field_24355 = item;
        this.field_24356 = count;
        this.field_49673 = components;
    }

    public HoverEvent$class_5249(class_1799 stack) {
        this(stack.method_41409(), stack.method_7947(), stack.method_57380());
    }

    /** getItemStack() */
    public class_1799 method_27683() {
        if (this.field_24358 == null) {
            this.field_24358 = new class_1799(this.field_24355, this.field_24356, this.field_49673);
        }
        return this.field_24358;
    }

    /**
     * legacyCreate(text, ops) (private in 1.21.4): the text as SNBT (1.21.4 TagParser.parseTag, 1.21.5
     * TagParser.parseCompoundFully), read with ItemStack.CODEC through NbtOps (with the ops' registries when given)
     */
    public static com.mojang.serialization.DataResult<HoverEvent$class_5249> method_54203(net.minecraft.class_2561 text,
            net.minecraft.class_6903<?> ops) {
        try {
            net.minecraft.class_2487 tag = net.minecraft.class_2522.method_67315(text.getString());
            com.mojang.serialization.DynamicOps<net.minecraft.class_2520> nbt = ops != null
                ? ops.method_57110(net.minecraft.class_2509.field_11560) : net.minecraft.class_2509.field_11560;
            return class_1799.field_24671.parse(nbt, tag).map(HoverEvent$class_5249::new);
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return com.mojang.serialization.DataResult.error(() -> method_54200(e));
        }
    }

    /** the legacyCreate error message (private in 1.21.4) */
    public static String method_54200(com.mojang.brigadier.exceptions.CommandSyntaxException e) {
        return "Failed to parse item tag: " + e.getMessage();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HoverEvent$class_5249 other = (HoverEvent$class_5249) o;
        return this.field_24356 == other.field_24356 && this.field_24355.equals(other.field_24355)
            && this.field_49673.equals(other.field_49673);
    }

    @Override
    public int hashCode() {
        int h = this.field_24355.hashCode();
        h = 31 * h + this.field_24356;
        return 31 * h + this.field_49673.hashCode();
    }
}
