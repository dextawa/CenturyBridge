package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21.rt.CbReportComments;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * 1.20.6's CrashReport.getErrorComment() (method_573), restored with 1.20.6's body (javap -c -p): the 34 witty comments
 * in a local array, one picked by Util.getNanos() modulo its length, "Witty comment unavailable :(" on any throwable.
 * 1.21 moved the array into ReportType.CRASH's comment list (class_9813.field_52181, the same 34 strings in the same
 * order, D:/tmp/s121/crashcmp.py) and the pick into ReportType.getErrorComment (method_60927, the same formula).
 * Route: CbReportCommentRouteMixin, when a converted mod mixed this member (CbMixedMembers); handed to it through CbReportComments from
 * the static initializer this mixin merges into CrashReport (the member stays private, as in 1.20.6).
 */
@Mixin(value = net.minecraft.class_128.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbCrashReportCommentRestoredMixin {

    /** the hand-over to CbReportCommentRouteMixin, run by CrashReport's static init (merged), not an injector */
    @Unique
    private static final boolean cb$v1_21$published = CbReportComments.crash(CbCrashReportCommentRestoredMixin::method_573);

    @CbVanillaReads("CbReportCommentRouteMixin: ReportType.getErrorComment of ReportType.CRASH answers through it when a "
        + "converted mod mixed it (CbMixedMembers); body 1.20.6's")
    private static String method_573() {
        String[] strings = new String[]{
            "Who set us up the TNT?",
            "Everything's going to plan. No, really, that was supposed to happen.",
            "Uh... Did I do that?",
            "Oops.",
            "Why did you do that?",
            "I feel sad now :(",
            "My bad.",
            "I'm sorry, Dave.",
            "I let you down. Sorry :(",
            "On the bright side, I bought you a teddy bear!",
            "Daisy, daisy...",
            "Oh - I know what I did wrong!",
            "Hey, that tickles! Hehehe!",
            "I blame Dinnerbone.",
            "You should try our sister game, Minceraft!",
            "Don't be sad. I'll do better next time, I promise!",
            "Don't be sad, have a hug! <3",
            "I just don't know what went wrong :(",
            "Shall we play a game?",
            "Quite honestly, I wouldn't worry myself about that.",
            "I bet Cylons wouldn't have this problem.",
            "Sorry :(",
            "Surprise! Haha. Well, this is awkward.",
            "Would you like a cupcake?",
            "Hi. I'm Minecraft, and I'm a crashaholic.",
            "Ooh. Shiny.",
            "This doesn't make any sense!",
            "Why is it breaking :(",
            "Don't do that.",
            "Ouch. That hurt :(",
            "You're mean.",
            "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]",
            "There are four lights!",
            "But it works on my machine.",
        };
        try {
            return strings[(int) (net.minecraft.class_156.method_648() % (long) strings.length)];
        } catch (Throwable t) {
            return "Witty comment unavailable :(";
        }
    }
}
