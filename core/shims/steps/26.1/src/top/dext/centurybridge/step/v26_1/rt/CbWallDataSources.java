package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.contents.data.BlockDataSource;
import net.minecraft.network.chat.contents.data.EntityDataSource;
import top.dext.centurybridge.step.v26_1.mixin.CbWallCompilableStringAccess;

/**
 * NBT component data sources: 1.21.11's BlockDataSource(posPattern, compiledPos) and EntityDataSource(selectorPattern,
 * compiledSelector) are 26.1's records over one CompilableString (the text with what it compiles to). 1.21.11's
 * one-argument constructors compiled the text themselves and kept null when it did not parse; such a source resolves
 * to nothing, as 1.21.11's did (mixin/CbWallBlockDataSourceMixin, CbWallEntityDataSourceMixin).
 */
public final class CbWallDataSources {

    private CbWallDataSources() {
    }

    /** 1.21.11 BlockDataSource.compilePos(text): a block position argument, or null when it does not parse */
    private static Coordinates compilePos(String text) {
        try {
            return BlockPosArgument.blockPos().parse(new StringReader(text));
        } catch (CommandSyntaxException e) {
            return null;
        }
    }

    /** 1.21.11 EntityDataSource.compileSelector(text): a selector parser allowing selectors, or null when it does not parse */
    private static EntitySelector compileSelector(String text) {
        try {
            return new EntitySelectorParser(new StringReader(text), true).parse();
        } catch (CommandSyntaxException e) {
            return null;
        }
    }

    /** 1.21.11 new BlockDataSource(posPattern) */
    public static BlockDataSource blockSource(String posPattern) {
        return blockSource(posPattern, compilePos(posPattern));
    }

    /** 1.21.11 new BlockDataSource(posPattern, compiledPos) */
    public static BlockDataSource blockSource(String posPattern, Coordinates compiledPos) {
        return new BlockDataSource(CbWallCompilableStringAccess.cb$new(posPattern, compiledPos));
    }

    /** 1.21.11 BlockDataSource.posPattern() */
    public static String posPattern(BlockDataSource source) {
        return source.coordinates().source();
    }

    /** 1.21.11 BlockDataSource.compiledPos() */
    public static Coordinates compiledPos(BlockDataSource source) {
        return source.coordinates().compiled();
    }

    /** 1.21.11 new EntityDataSource(selectorPattern) */
    public static EntityDataSource entitySource(String selectorPattern) {
        return entitySource(selectorPattern, compileSelector(selectorPattern));
    }

    /** 1.21.11 new EntityDataSource(selectorPattern, compiledSelector) */
    public static EntityDataSource entitySource(String selectorPattern, EntitySelector compiledSelector) {
        return new EntityDataSource(CbWallCompilableStringAccess.cb$new(selectorPattern, compiledSelector));
    }

    /** 1.21.11 EntityDataSource.selectorPattern() */
    public static String selectorPattern(EntityDataSource source) {
        return source.selector().source();
    }

    /** 1.21.11 EntityDataSource.compiledSelector() */
    public static EntitySelector compiledSelector(EntityDataSource source) {
        return source.selector().compiled();
    }
}
