package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.util.CompilableString;
import top.dext.centurybridge.step.v26_1.mixin.CbWallCompilableStringAccess;

/**
 * 1.21.11's SelectorPattern (a selector's text and the selector parsed from it), which 26.1 replaced everywhere
 * (SelectorContents, ScoreContents, Component.selector / score) by CompilableString&lt;EntitySelector&gt;, the
 * same pair: classRenames name it so, and its members are these.
 */
public final class CbWallSelectors {

    /** 1.21.11 SelectorPattern.CODEC: the text, parsed as SelectorPattern.parse parses it */
    public static final Codec<CompilableString<EntitySelector>> CODEC =
        Codec.STRING.comapFlatMap(CbWallSelectors::parse, CompilableString::source);

    private CbWallSelectors() {
    }

    /** new SelectorPattern(text, selector) */
    public static CompilableString<EntitySelector> create(String text, EntitySelector selector) {
        return CbWallCompilableStringAccess.cb$new(text, selector);
    }

    /** SelectorPattern.parse(text): 1.21.11's body -- an entity selector parser allowing selectors, no trailing check */
    public static DataResult<CompilableString<EntitySelector>> parse(String text) {
        try {
            EntitySelectorParser parser = new EntitySelectorParser(new StringReader(text), true);
            return DataResult.success(create(text, parser.parse()));
        } catch (CommandSyntaxException e) {
            return DataResult.error(() -> "Invalid selector component: " + text + ": " + e.getMessage());
        }
    }

    /** SelectorPattern.text() */
    public static String text(CompilableString<EntitySelector> pattern) {
        return pattern.source();
    }

    /** SelectorPattern.resolved() */
    public static EntitySelector resolved(CompilableString<EntitySelector> pattern) {
        return pattern.compiled();
    }

    /** SelectorPattern.CODEC */
    public static Codec<CompilableString<EntitySelector>> field_53684() {
        return CODEC;
    }
}
