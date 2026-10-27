package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21.rt.CbReportComments;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * 1.20.6's FilledProfileResults.getComment() (method_16062), restored with 1.20.6's body (javap -c -p): the 15 profiler
 * comments in a local array, one picked by Util.getNanos() modulo its length, "Witty comment unavailable :(" on any
 * throwable. 1.21 moved the array into ReportType.PROFILE's comment list (class_9813.field_52182, the same 15 strings in
 * the same order) and the pick into ReportType.getErrorComment (method_60927). Route: CbReportCommentRouteMixin, when a
 * converted mod mixed this member (CbMixedMembers); handed to it through CbReportComments from
 * the static initializer this mixin merges into FilledProfileResults (the member stays private, as in 1.20.6).
 */
@Mixin(value = net.minecraft.class_3692.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbProfileResultsCommentRestoredMixin {

    /** the hand-over to CbReportCommentRouteMixin, run by FilledProfileResults's static init (merged), not an injector */
    @Unique
    private static final boolean cb$v1_21$published = CbReportComments.profile(CbProfileResultsCommentRestoredMixin::method_16062);

    @CbVanillaReads("CbReportCommentRouteMixin: ReportType.getErrorComment of ReportType.PROFILE answers through it when a "
        + "converted mod mixed it (CbMixedMembers); body 1.20.6's")
    private static String method_16062() {
        String[] strings = new String[]{
            "I'd Rather Be Surfing",
            "Shiny numbers!",
            "Am I not running fast enough? :(",
            "I'm working as hard as I can!",
            "Will I ever be good enough for you? :(",
            "Speedy. Zoooooom!",
            "Hello world",
            "40% better than a crash report.",
            "Now with extra numbers",
            "Now with less numbers",
            "Now with the same numbers",
            "You should add flames to things, it makes them go faster!",
            "Do you feel the need for... optimization?",
            "*cracks redstone whip*",
            "Maybe if you treated it better then it'll have more motivation to work faster! Poor server.",
        };
        try {
            return strings[(int) (net.minecraft.class_156.method_648() % (long) strings.length)];
        } catch (Throwable t) {
            return "Witty comment unavailable :(";
        }
    }
}
