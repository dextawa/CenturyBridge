package top.dext.centurybridge.step.v26_1.rt;

import java.util.Optional;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.dialog.body.ItemBody;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.crafting.display.SlotDisplay;

/**
 * Records 1.21.11 built from an ItemStack that 26.1 builds from an ItemStackTemplate (an immutable, non-empty item,
 * count and component patch): HoverEvent.ShowItem, ItemBody, UseRemainder, SlotDisplay.ItemStackSlotDisplay. Their
 * 1.21.11 constructors take the stack's template; their 1.21.11 accessors answer a stack made from it (ItemStackTemplate
 * create(): same item, count and components). An empty stack has no template: 26.1 refuses it where 1.21.11's record
 * took it, but every 1.21.11 codec of these records (ItemStack.CODEC, MAP_CODEC, STRICT_CODEC, STREAM_CODEC) refused
 * to encode one as well.
 */
public final class CbWallItemTemplates {

    private CbWallItemTemplates() {
    }

    private static ItemStackTemplate template(ItemStack stack) {
        return ItemStackTemplate.fromNonEmptyStack(stack);
    }

    /** 1.21.11 new HoverEvent.ShowItem(stack) (it kept a copy; the template is one) */
    public static HoverEvent.ShowItem showItem(ItemStack stack) {
        return new HoverEvent.ShowItem(template(stack));
    }

    /** 1.21.11 HoverEvent.ShowItem.item() */
    public static ItemStack showItemStack(HoverEvent.ShowItem event) {
        return event.item().create();
    }

    /** 1.21.11 new ItemBody(item, description, showDecorations, showTooltip, width, height) */
    public static ItemBody itemBody(ItemStack item, Optional<PlainMessage> description, boolean showDecorations, boolean showTooltip,
                                    int width, int height) {
        return new ItemBody(template(item), description, showDecorations, showTooltip, width, height);
    }

    /** 1.21.11 ItemBody.item() */
    public static ItemStack itemBodyStack(ItemBody body) {
        return body.item().create();
    }

    /** 1.21.11 new UseRemainder(convertInto) */
    public static UseRemainder useRemainder(ItemStack convertInto) {
        return new UseRemainder(template(convertInto));
    }

    /** 1.21.11 UseRemainder.convertInto() */
    public static ItemStack useRemainderStack(UseRemainder remainder) {
        return remainder.convertInto().create();
    }

    /** 1.21.11 new SlotDisplay.ItemStackSlotDisplay(stack) */
    public static SlotDisplay.ItemStackSlotDisplay itemStackSlotDisplay(ItemStack stack) {
        return new SlotDisplay.ItemStackSlotDisplay(template(stack));
    }

    /** 1.21.11 SlotDisplay.ItemStackSlotDisplay.stack() */
    public static ItemStack slotDisplayStack(SlotDisplay.ItemStackSlotDisplay display) {
        return display.stack().create();
    }
}
