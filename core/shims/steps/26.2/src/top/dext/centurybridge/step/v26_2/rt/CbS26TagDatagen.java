package top.dext.centurybridge.step.v26_2.rt;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import top.dext.centurybridge.step.v26_2.facade.TagAppender;
import top.dext.centurybridge.step.v26_2.facade.fapi.FabricTagAppender;

/**
 * The 26.1 layer's 1.21.11 FabricProvidedTagBuilder.remove / removeAll / removeTag entry points
 * (top/dext/centurybridge/step/v26_1/rt/CbFapiTagDatagen), carried on to 26.2 (staticRedirects, s26g-tags.json).
 * Their appenders are 26.1.2 TagAppenders, and on 26.2 a converted mod's 26.1.2 appenders are the layer's
 * (facade/TagAppender: s26g-tags.json renames the type), not vanilla's TagAppender$1 whose builder CbFapiTagDatagen reads
 * (its mixin CbFapiTagAppenderAccess): left there, every removal would end in its AssertionError.
 * <p>1.21.11's semantics, as CbFapiTagDatagen kept them: the receiver's own override first; else the builder-backed
 * appender records the removal on its TagBuilder; any other appender -- vanilla's mapped one included -- had only the
 * interface default, AssertionError("Implemented via mixin"). Here the call is the facade's interface member, virtual:
 * a mod's override answers (javac bridged its remove(ResourceKey) to the erased member), the builder-backed appender
 * records through Fabric API's TagBuilderHooks (written by 26.2's TagsProviderMixin as "fabric:remove", as
 * CbFapiTagDatagen's renamed hooks were on 26.1.2). The facade's mapped appender implements the members too (26.1.2's
 * Fabric API did); 1.21.11's did not, so it keeps 1.21.11's AssertionError here.
 */
public final class CbS26TagDatagen {
    private CbS26TagDatagen() {
    }

    /** the class of the facade's mapped appender (26.1.2's TagAppender$2) */
    private static final Class<?> MAPPED = TagAppender.forBuilder(new TagBuilder()).map(Function.identity()).getClass();

    @SuppressWarnings("rawtypes")
    private static FabricTagAppender check(FabricTagAppender appender) {
        if (appender.getClass() == MAPPED) {
            throw new AssertionError("Implemented via mixin");
        }
        return appender;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static TagAppender remove(FabricTagAppender appender, ResourceKey key) {
        return check(appender).remove(key);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static TagAppender remove(FabricTagAppender appender, ResourceKey[] keys) {
        return check(appender).remove((Object[]) keys);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static TagAppender removeAll(FabricTagAppender appender, Collection keys) {
        return check(appender).removeAll(keys);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static TagAppender removeAll(FabricTagAppender appender, Stream keys) {
        return check(appender).removeAll(keys);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static TagAppender removeTag(FabricTagAppender appender, TagKey tag) {
        return check(appender).removeTag(tag);
    }
}
