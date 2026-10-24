package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * authlib 4.0.43's RealmInfo had public mutable fields; 5.0.47 made it a record with private final ones. A
 * 1.20.1 mod's field write lands here (rt/v1_20_2/CbLibAuthlibRecords): the record's own field, made writable.
 */
@Mixin(com.mojang.authlib.yggdrasil.request.AbuseReportRequest.RealmInfo.class)
public interface CbLibAuthlibAbuseReportRealmInfoFields {
    @Mutable
    @Accessor("realmId")
    void cb$setRealmId(String value);

    @Mutable
    @Accessor("slotId")
    void cb$setSlotId(int value);
}
