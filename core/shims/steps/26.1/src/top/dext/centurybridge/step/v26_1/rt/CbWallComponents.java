package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JavaOps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ResolutionContext;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.util.CompilableString;
import net.minecraft.world.entity.Entity;

/**
 * 1.21.11's ComponentContents.resolve(source, entity, depth) on 26.1's resolve(context, depth): 26.1 gathers the
 * command source and the entity to resolve for (1.21.11's entity argument, its entity override) into a
 * resolution context. A 1.21.11 contents class answers 26.1's resolve with its own, given the context's source and
 * entity (the hook's guard lets its super call reach the implementation above it); a mod's call resolves with a
 * context of its source and entity.
 * <p>
 * NbtContents: 26.1 keeps the path compiled with its source text, and adds a "plain" flag 1.21.11's lacked
 * (false: 1.21.11's resolved the way 26.1's non-plain does).
 */
public final class CbWallComponents {

    private CbWallComponents() {
    }

    /** 1.21.11's resolve(source, entity, depth), declared by a mod's contents class */
    public interface LegacyResolve {
        MutableComponent resolve(CommandSourceStack source, Entity entity, int depth) throws CommandSyntaxException;
    }

    /** 26.1's resolve(context, depth) of a mod's contents class: its 1.21.11 resolve */
    public static MutableComponent resolve(LegacyResolve self, ResolutionContext context, int depth) throws CommandSyntaxException {
        return self.resolve(context.source(), context.defaultScoreboardEntity(), depth);
    }

    /** a mod's call of 1.21.11's resolve(source, entity, depth) (its super call included, through the hook's guard) */
    public static MutableComponent callResolve(ComponentContents contents, CommandSourceStack source, Entity entity, int depth)
        throws CommandSyntaxException {
        return contents.resolve(ResolutionContext.builder().withSource(source).withEntityOverride(entity).build(), depth);
    }

    // ---- NbtContents ----

    /**
     * the compiled path of 1.21.11's path text (26.1's codec compiles it the same way); a path that does not compile is
     * kept uncompiled, as 1.21.11 kept it (its compiled path null), and resolves to nothing (mixin/CbWallNbtContentsMixin)
     */
    public static CompilableString<NbtPathArgument.NbtPath> nbtPath(String path) {
        return NbtContents.NBT_PATH_CODEC.parse(JavaOps.INSTANCE, path).result()
            .orElseGet(() -> top.dext.centurybridge.step.v26_1.mixin.CbWallCompilableStringAccess.cb$new(path, null));
    }

    /** 1.21.11 had no plain flag: its contents resolved as 26.1's non-plain ones */
    public static boolean notPlain() {
        return false;
    }

    /** NbtContents.getNbtPath() */
    public static String getNbtPath(NbtContents contents) {
        return contents.nbtPath().source();
    }

    /** NbtContents.compiledNbtPath */
    public static NbtPathArgument.NbtPath compiledNbtPath(NbtContents contents) {
        return contents.nbtPath().compiled();
    }
}
