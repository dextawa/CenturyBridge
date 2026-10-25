package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ResolutionContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;

/**
 * 1.21.11 WrittenBookContent members 26.1 reshaped: resolving a book took the command source and the player it is
 * resolved for (ComponentUtils.updateForEntity(source, page, player, 0), the source's registries judging the page
 * length); 26.1 takes a ResolutionContext and the registries -- the context of that source with the player as its
 * entity (as rt/CbWallStatics.updateForEntity). tryCraftCopy() is 26.1's craftCopy() behind 1.21.11's generation
 * check: a copy of a copy (generation 2) cannot be copied again.
 */
public final class CbWallBooks {

    /** 1.21.11 WrittenBookContent.MAX_CRAFTABLE_GENERATION */
    public static final int MAX_CRAFTABLE_GENERATION = 2;

    private CbWallBooks() {
    }

    private static ResolutionContext context(CommandSourceStack source, Player player) {
        return ResolutionContext.builder().withSource(source).withEntityOverride(player).build();
    }

    /** 1.21.11 WrittenBookContent.tryCraftCopy(): null past the last craftable generation */
    public static WrittenBookContent tryCraftCopy(WrittenBookContent content) {
        return content.generation() >= MAX_CRAFTABLE_GENERATION ? null : content.craftCopy();
    }

    /** 1.21.11 WrittenBookContent.resolve(source, player) */
    public static WrittenBookContent resolve(WrittenBookContent content, CommandSourceStack source, Player player) {
        return content.resolve(context(source, player), source.registryAccess());
    }

    /** 1.21.11 WrittenBookContent.resolveForItem(stack, source, player) */
    public static boolean resolveForItem(ItemStack stack, CommandSourceStack source, Player player) {
        return WrittenBookContent.resolveForItem(stack, context(source, player), source.registryAccess());
    }
}
