package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * authlib 4.0.43's AbuseReport had public mutable fields; 5.0.47 made it a record with private final ones. A
 * 1.20.1 mod's field write lands here (rt/v1_20_2/CbLibAuthlibRecords): the record's own field, made writable.
 */
@Mixin(com.mojang.authlib.minecraft.report.AbuseReport.class)
public interface CbLibAuthlibAbuseReportFields {
    @Mutable
    @Accessor("opinionComments")
    void cb$setOpinionComments(String value);

    @Mutable
    @Accessor("reason")
    void cb$setReason(String value);

    @Mutable
    @Accessor("evidence")
    void cb$setEvidence(com.mojang.authlib.minecraft.report.ReportEvidence value);

    @Mutable
    @Accessor("reportedEntity")
    void cb$setReportedEntity(com.mojang.authlib.minecraft.report.ReportedEntity value);

    @Mutable
    @Accessor("createdTime")
    void cb$setCreatedTime(java.time.Instant value);
}
