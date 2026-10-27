package top.dext.centurybridge.step.v1_21.rt;

import java.util.function.Supplier;

/**
 * The restored 1.20.6 comment members, CrashReport.getErrorComment (CbCrashReportCommentRestoredMixin) and
 * FilledProfileResults.getComment (CbProfileResultsCommentRestoredMixin). They stay private statics of their game classes,
 * as in 1.20.6, so each restorer hands its member over from the static initializer it merges into its class, and
 * CbReportCommentRouteMixin asks through here. A hand-over from the class's own static init needs no injector and no
 * @Invoker: an @Invoker of a restored member is stripped where the climb crosses the 1.21.11 -> 26.1 wall (the wall step
 * looks the target up in 26.1's class, which does not declare it). Null until the class has loaded; a report of that kind
 * is made by that class, so it has loaded by then.
 */
public final class CbReportComments {
    public static volatile Supplier<String> CRASH;
    public static volatile Supplier<String> PROFILE;

    private CbReportComments() {
    }

    /** CrashReport's static init: its restored getErrorComment */
    public static boolean crash(Supplier<String> comment) {
        CRASH = comment;
        return true;
    }

    /** FilledProfileResults' static init: its restored getComment */
    public static boolean profile(Supplier<String> comment) {
        PROFILE = comment;
        return true;
    }
}
