package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * authlib 4.0.43's ReportEvidence had public mutable fields; 5.0.47 made it a record with private final ones. A
 * 1.20.1 mod's field write lands here (rt/v1_20_2/CbLibAuthlibRecords): the record's own field, made writable.
 */
@Mixin(com.mojang.authlib.minecraft.report.ReportEvidence.class)
public interface CbLibAuthlibReportEvidenceFields {
    @Mutable
    @Accessor("messages")
    void cb$setMessages(java.util.List<com.mojang.authlib.minecraft.report.ReportChatMessage> value);
}
