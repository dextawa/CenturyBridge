package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * authlib 4.0.43's ReportChatMessage had public mutable fields; 5.0.47 made it a record with private final ones. A
 * 1.20.1 mod's field write lands here (rt/v1_20_2/CbLibAuthlibRecords): the record's own field, made writable.
 */
@Mixin(com.mojang.authlib.minecraft.report.ReportChatMessage.class)
public interface CbLibAuthlibReportChatMessageFields {
    @Mutable
    @Accessor("index")
    void cb$setIndex(int value);

    @Mutable
    @Accessor("profileId")
    void cb$setProfileId(java.util.UUID value);

    @Mutable
    @Accessor("sessionId")
    void cb$setSessionId(java.util.UUID value);

    @Mutable
    @Accessor("timestamp")
    void cb$setTimestamp(java.time.Instant value);

    @Mutable
    @Accessor("salt")
    void cb$setSalt(long value);

    @Mutable
    @Accessor("lastSeen")
    void cb$setLastSeen(java.util.List<java.nio.ByteBuffer> value);

    @Mutable
    @Accessor("message")
    void cb$setMessage(String value);

    @Mutable
    @Accessor("signature")
    void cb$setSignature(java.nio.ByteBuffer value);

    @Mutable
    @Accessor("messageReported")
    void cb$setMessageReported(boolean value);
}
