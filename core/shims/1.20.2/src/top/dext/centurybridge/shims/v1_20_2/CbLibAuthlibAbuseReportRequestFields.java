package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * authlib 4.0.43's AbuseReportRequest had public mutable fields; 5.0.47 made it a record with private final ones. A
 * 1.20.1 mod's field write lands here (rt/v1_20_2/CbLibAuthlibRecords): the record's own field, made writable.
 */
@Mixin(com.mojang.authlib.yggdrasil.request.AbuseReportRequest.class)
public interface CbLibAuthlibAbuseReportRequestFields {
    @Mutable
    @Accessor("version")
    void cb$setVersion(int value);

    @Mutable
    @Accessor("id")
    void cb$setId(java.util.UUID value);

    @Mutable
    @Accessor("report")
    void cb$setReport(com.mojang.authlib.minecraft.report.AbuseReport value);

    @Mutable
    @Accessor("clientInfo")
    void cb$setClientInfo(com.mojang.authlib.yggdrasil.request.AbuseReportRequest.ClientInfo value);

    @Mutable
    @Accessor("thirdPartyServerInfo")
    void cb$setThirdPartyServerInfo(com.mojang.authlib.yggdrasil.request.AbuseReportRequest.ThirdPartyServerInfo value);

    @Mutable
    @Accessor("realmInfo")
    void cb$setRealmInfo(com.mojang.authlib.yggdrasil.request.AbuseReportRequest.RealmInfo value);
}
