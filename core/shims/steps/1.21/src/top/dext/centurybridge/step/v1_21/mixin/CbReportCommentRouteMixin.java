package top.dext.centurybridge.step.v1_21.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.dext.centurybridge.step.v1_21.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21.rt.CbReportComments;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * ReportType.getErrorComment (class_9813.method_60927): 1.21's home of the comment pick 1.20.6 kept in
 * CrashReport.getErrorComment (method_573) and FilledProfileResults.getComment (method_16062). When a converted mod mixed
 * one of those (CbMixedMembers), the CRASH / PROFILE report types answer what the restored member answers
 * (CbCrashReportCommentRestoredMixin / CbProfileResultsCommentRestoredMixin, handed over through CbReportComments). The route
 * modifies 1.21's return value (MixinExtras @ModifyReturnValue) instead of cancelling at HEAD: 1.21's body and every
 * other mod's injector into it still run; at CbVanillaReads.ROUTE_FIRST it goes before them, so a native 1.21 mod's
 * RETURN injector sees the routed answer. The types 1.20.6 did not have (test, network protocol error, chunk IO error)
 * and every unmixed case keep 1.21's answer.
 */
@Mixin(value = net.minecraft.class_9813.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbReportCommentRouteMixin {

    private static final String CRASH = "net/minecraft/class_128.method_573()Ljava/lang/String;";
    private static final String PROFILE = "net/minecraft/class_3692.method_16062()Ljava/lang/String;";

    @ModifyReturnValue(method = "method_60927()Ljava/lang/String;", at = @At("RETURN"))
    private String cb$v1_21$legacyComment(String comment) {
        Object self = this;
        if (self == net.minecraft.class_9813.field_52181 && CbReportComments.CRASH != null && CbMixedMembers.mixed(CRASH)) {
            return CbReportComments.CRASH.get();
        }
        if (self == net.minecraft.class_9813.field_52182 && CbReportComments.PROFILE != null && CbMixedMembers.mixed(PROFILE)) {
            return CbReportComments.PROFILE.get();
        }
        return comment;
    }
}
