package top.dext.centurybridge.rt.v1_20_2;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.authlib.yggdrasil.request.JoinMinecraftServerRequest;
import com.mojang.authlib.yggdrasil.request.TelemetryEventsRequest;
import com.mojang.authlib.yggdrasil.response.BlockListResponse;
import com.mojang.authlib.yggdrasil.response.HasJoinedMinecraftServerResponse;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.authlib.yggdrasil.response.UserAttributesResponse;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import top.dext.centurybridge.shims.v1_20_2.CbLibAuthlibAbuseReportClientInfoFields;
import top.dext.centurybridge.shims.v1_20_2.CbLibAuthlibAbuseReportFields;
import top.dext.centurybridge.shims.v1_20_2.CbLibAuthlibAbuseReportRealmInfoFields;
import top.dext.centurybridge.shims.v1_20_2.CbLibAuthlibAbuseReportRequestFields;
import top.dext.centurybridge.shims.v1_20_2.CbLibAuthlibAbuseReportThirdPartyServerInfoFields;
import top.dext.centurybridge.shims.v1_20_2.CbLibAuthlibJoinMinecraftServerRequestFields;
import top.dext.centurybridge.shims.v1_20_2.CbLibAuthlibReportChatMessageFields;
import top.dext.centurybridge.shims.v1_20_2.CbLibAuthlibReportEvidenceFields;
import top.dext.centurybridge.shims.v1_20_2.CbLibAuthlibReportedEntityFields;

/**
 * authlib 4.0.43's Yggdrasil request, response and abuse-report classes, which 5.0.47 made final records
 * (core/segments/damage-lib/1.20.1__1.20.2.tsv). A 1.20.1 mod keeps the record (the type 1.20.2's game and authlib hand
 * around); what 4.0.43 let it do with the class is done on the record:
 * <ul>
 * <li>4.0.43's constructors build the record: a no-argument constructor (Gson's, the fields unset) gives every component
 * its default (null, 0, false), as the class's fields were; a constructor 5.0.47 widened gets 4.0.43's meaning for the new
 * component (a 4.0.43 abuse report is a chat report: AbuseReport.chat, report type "chat", the only kind 4.0.43 sent);</li>
 * <li>a public field read is the component's accessor; a write sets the record's own field, made writable by the layer's
 * mixin (CbLibAuthlib*Fields), so every holder of the object sees it, as with 4.0.43's mutable object;</li>
 * <li>a getter 5.0.47 renamed to the component name is renamed (wiring methodRenames); the rest are below.</li>
 * </ul>
 * A record cannot be subclassed, so a 4.0.43 subclass of these classes cannot load (wiring ledger).
 */
public final class CbLibAuthlibRecords {
    private CbLibAuthlibRecords() {
    }

    // ------------------------------------------------------------------ constructors

    /** new AbuseReport(opinionComments, reason, evidence, reportedEntity, createdTime): 4.0.43's reports were chat reports */
    public static AbuseReport abuseReport(String opinionComments, String reason, ReportEvidence evidence, ReportedEntity reportedEntity,
            Instant createdTime) {
        return AbuseReport.chat(opinionComments, reason, evidence, reportedEntity, createdTime);
    }

    /**
     * new AbuseReportRequest(version, id, report, clientInfo, thirdPartyServerInfo, realmInfo): report type "chat", the
     * report type 1.20.2 sends for a chat report (ReportType.CHAT), the only kind 4.0.43 had
     */
    public static AbuseReportRequest abuseReportRequest(int version, UUID id, AbuseReport report, AbuseReportRequest.ClientInfo clientInfo,
            AbuseReportRequest.ThirdPartyServerInfo thirdPartyServerInfo, AbuseReportRequest.RealmInfo realmInfo) {
        return new AbuseReportRequest(version, id, report, clientInfo, thirdPartyServerInfo, realmInfo, "chat");
    }

    /** new JoinMinecraftServerRequest(): its fields unset (a 4.0.43 caller filled them in afterwards) */
    public static JoinMinecraftServerRequest joinMinecraftServerRequest() {
        return new JoinMinecraftServerRequest(null, null, null);
    }

    /** new BlockListResponse() (Gson's constructor): nothing set */
    public static BlockListResponse blockListResponse() {
        return new BlockListResponse(null);
    }

    /** new HasJoinedMinecraftServerResponse(): nothing set (5.0.47's profile actions too: 4.0.43 had none) */
    public static HasJoinedMinecraftServerResponse hasJoinedMinecraftServerResponse() {
        return new HasJoinedMinecraftServerResponse(null, null, null);
    }

    /** new KeyPairResponse(): nothing set */
    public static KeyPairResponse keyPairResponse() {
        return new KeyPairResponse(null, null, null, null);
    }

    /** new MinecraftProfilePropertiesResponse(): nothing set */
    public static MinecraftProfilePropertiesResponse minecraftProfilePropertiesResponse() {
        return new MinecraftProfilePropertiesResponse(null, null, null, null);
    }

    /** new MinecraftTexturesPayload(): nothing set (timestamp 0, not public) */
    public static MinecraftTexturesPayload minecraftTexturesPayload() {
        return new MinecraftTexturesPayload(0L, null, null, false, null);
    }

    /** new ProfileSearchResultsResponse(): nothing set */
    public static ProfileSearchResultsResponse profileSearchResultsResponse() {
        return new ProfileSearchResultsResponse(null);
    }

    /** new UserAttributesResponse(): nothing set */
    public static UserAttributesResponse userAttributesResponse() {
        return new UserAttributesResponse(null, null, null);
    }

    /** new UserAttributesResponse.BanStatus(): nothing set */
    public static UserAttributesResponse.BanStatus banStatus() {
        return new UserAttributesResponse.BanStatus(null);
    }

    /** new UserAttributesResponse.BanStatus.BannedScope(): nothing set */
    public static UserAttributesResponse.BanStatus.BannedScope bannedScope() {
        return new UserAttributesResponse.BanStatus.BannedScope(null, null, null, null);
    }

    /** new UserAttributesResponse.Privileges(): no privilege set (each getter answers false, in 4.0.43 and 5.0.47 alike) */
    public static UserAttributesResponse.Privileges privileges() {
        return new UserAttributesResponse.Privileges(null, null, null, null, null);
    }

    /**
     * outer.new Privilege(): 4.0.43's inner class kept its Privileges only as the enclosing instance (never read) and started
     * disabled; 5.0.47's record has no enclosing instance
     */
    public static UserAttributesResponse.Privileges.Privilege privilege(UserAttributesResponse.Privileges outer) {
        return new UserAttributesResponse.Privileges.Privilege(false);
    }

    /** new UserAttributesResponse.ProfanityFilterPreferences(): the filter off (4.0.43's unset boolean) */
    public static UserAttributesResponse.ProfanityFilterPreferences profanityFilterPreferences() {
        return new UserAttributesResponse.ProfanityFilterPreferences(false);
    }

    // ------------------------------------------------------------------ getters whose shape changed

    /** KeyPairResponse.getPrivateKey(): 4.0.43 read keyPair.privateKey (a NullPointerException with no key pair, as there) */
    public static String getPrivateKey(KeyPairResponse self) {
        return self.keyPair().privateKey();
    }

    /** KeyPairResponse.getPublicKey(): keyPair.publicKey */
    public static String getPublicKey(KeyPairResponse self) {
        return self.keyPair().publicKey();
    }

    /**
     * ProfileSearchResultsResponse.getProfiles(): 4.0.43 held the profiles as an array, 5.0.47 as a list; the list's profiles
     * in a new array (null when none were set)
     */
    public static GameProfile[] getProfiles(ProfileSearchResultsResponse self) {
        List<GameProfile> profiles = self.profiles();
        return profiles == null ? null : profiles.toArray(new GameProfile[0]);
    }

    // ------------------------------------------------------------------ 4.0.43's Response getters on its subclasses
    // 4.0.43's six response classes extended Response (error, errorMessage, cause from the answer's JSON). authlib handed them
    // out only when the answer carried no error (YggdrasilAuthenticationService.makeRequest threw for one, MinecraftClient
    // threw on an error status), so on those the fields were unset: null, which these answer. 5.0.47's records have no such
    // fields: a record a mod read from an error answer itself (its own Gson) has lost the error, where 4.0.43's object kept it.

    /** BlockListResponse.getError(): unset on an answer authlib returned */
    public static String getError(BlockListResponse self) {
        return null;
    }

    /** BlockListResponse.getErrorMessage(): unset on an answer authlib returned */
    public static String getErrorMessage(BlockListResponse self) {
        return null;
    }

    /** BlockListResponse.getCause(): unset on an answer authlib returned */
    public static String getCause(BlockListResponse self) {
        return null;
    }

    /** HasJoinedMinecraftServerResponse.getError(): unset on an answer authlib returned */
    public static String getError(HasJoinedMinecraftServerResponse self) {
        return null;
    }

    /** HasJoinedMinecraftServerResponse.getErrorMessage(): unset on an answer authlib returned */
    public static String getErrorMessage(HasJoinedMinecraftServerResponse self) {
        return null;
    }

    /** HasJoinedMinecraftServerResponse.getCause(): unset on an answer authlib returned */
    public static String getCause(HasJoinedMinecraftServerResponse self) {
        return null;
    }

    /** KeyPairResponse.getError(): unset on an answer authlib returned */
    public static String getError(KeyPairResponse self) {
        return null;
    }

    /** KeyPairResponse.getErrorMessage(): unset on an answer authlib returned */
    public static String getErrorMessage(KeyPairResponse self) {
        return null;
    }

    /** KeyPairResponse.getCause(): unset on an answer authlib returned */
    public static String getCause(KeyPairResponse self) {
        return null;
    }

    /** MinecraftProfilePropertiesResponse.getError(): unset on an answer authlib returned */
    public static String getError(MinecraftProfilePropertiesResponse self) {
        return null;
    }

    /** MinecraftProfilePropertiesResponse.getErrorMessage(): unset on an answer authlib returned */
    public static String getErrorMessage(MinecraftProfilePropertiesResponse self) {
        return null;
    }

    /** MinecraftProfilePropertiesResponse.getCause(): unset on an answer authlib returned */
    public static String getCause(MinecraftProfilePropertiesResponse self) {
        return null;
    }

    /** ProfileSearchResultsResponse.getError(): unset on an answer authlib returned */
    public static String getError(ProfileSearchResultsResponse self) {
        return null;
    }

    /** ProfileSearchResultsResponse.getErrorMessage(): unset on an answer authlib returned */
    public static String getErrorMessage(ProfileSearchResultsResponse self) {
        return null;
    }

    /** ProfileSearchResultsResponse.getCause(): unset on an answer authlib returned */
    public static String getCause(ProfileSearchResultsResponse self) {
        return null;
    }

    /** UserAttributesResponse.getError(): unset on an answer authlib returned */
    public static String getError(UserAttributesResponse self) {
        return null;
    }

    /** UserAttributesResponse.getErrorMessage(): unset on an answer authlib returned */
    public static String getErrorMessage(UserAttributesResponse self) {
        return null;
    }

    /** UserAttributesResponse.getCause(): unset on an answer authlib returned */
    public static String getCause(UserAttributesResponse self) {
        return null;
    }

    // ------------------------------------------------------------------ public fields

    // AbuseReport
    /** AbuseReport.opinionComments (read) */
    public static String opinionComments(AbuseReport self) {
        return self.opinionComments();
    }

    /** AbuseReport.opinionComments (write) */
    public static void set_opinionComments(AbuseReport self, String value) {
        ((CbLibAuthlibAbuseReportFields) (Object) self).cb$setOpinionComments(value);
    }

    /** AbuseReport.reason (read) */
    public static String reason(AbuseReport self) {
        return self.reason();
    }

    /** AbuseReport.reason (write) */
    public static void set_reason(AbuseReport self, String value) {
        ((CbLibAuthlibAbuseReportFields) (Object) self).cb$setReason(value);
    }

    /** AbuseReport.evidence (read) */
    public static ReportEvidence evidence(AbuseReport self) {
        return self.evidence();
    }

    /** AbuseReport.evidence (write) */
    public static void set_evidence(AbuseReport self, ReportEvidence value) {
        ((CbLibAuthlibAbuseReportFields) (Object) self).cb$setEvidence(value);
    }

    /** AbuseReport.reportedEntity (read) */
    public static ReportedEntity reportedEntity(AbuseReport self) {
        return self.reportedEntity();
    }

    /** AbuseReport.reportedEntity (write) */
    public static void set_reportedEntity(AbuseReport self, ReportedEntity value) {
        ((CbLibAuthlibAbuseReportFields) (Object) self).cb$setReportedEntity(value);
    }

    /** AbuseReport.createdTime (read) */
    public static Instant createdTime(AbuseReport self) {
        return self.createdTime();
    }

    /** AbuseReport.createdTime (write) */
    public static void set_createdTime(AbuseReport self, Instant value) {
        ((CbLibAuthlibAbuseReportFields) (Object) self).cb$setCreatedTime(value);
    }

    // ReportChatMessage
    /** ReportChatMessage.index (read) */
    public static int index(ReportChatMessage self) {
        return self.index();
    }

    /** ReportChatMessage.index (write) */
    public static void set_index(ReportChatMessage self, int value) {
        ((CbLibAuthlibReportChatMessageFields) (Object) self).cb$setIndex(value);
    }

    /** ReportChatMessage.profileId (read) */
    public static UUID profileId(ReportChatMessage self) {
        return self.profileId();
    }

    /** ReportChatMessage.profileId (write) */
    public static void set_profileId(ReportChatMessage self, UUID value) {
        ((CbLibAuthlibReportChatMessageFields) (Object) self).cb$setProfileId(value);
    }

    /** ReportChatMessage.sessionId (read) */
    public static UUID sessionId(ReportChatMessage self) {
        return self.sessionId();
    }

    /** ReportChatMessage.sessionId (write) */
    public static void set_sessionId(ReportChatMessage self, UUID value) {
        ((CbLibAuthlibReportChatMessageFields) (Object) self).cb$setSessionId(value);
    }

    /** ReportChatMessage.timestamp (read) */
    public static Instant timestamp(ReportChatMessage self) {
        return self.timestamp();
    }

    /** ReportChatMessage.timestamp (write) */
    public static void set_timestamp(ReportChatMessage self, Instant value) {
        ((CbLibAuthlibReportChatMessageFields) (Object) self).cb$setTimestamp(value);
    }

    /** ReportChatMessage.salt (read) */
    public static long salt(ReportChatMessage self) {
        return self.salt();
    }

    /** ReportChatMessage.salt (write) */
    public static void set_salt(ReportChatMessage self, long value) {
        ((CbLibAuthlibReportChatMessageFields) (Object) self).cb$setSalt(value);
    }

    /** ReportChatMessage.lastSeen (read) */
    public static List<ByteBuffer> lastSeen(ReportChatMessage self) {
        return self.lastSeen();
    }

    /** ReportChatMessage.lastSeen (write) */
    public static void set_lastSeen(ReportChatMessage self, List<ByteBuffer> value) {
        ((CbLibAuthlibReportChatMessageFields) (Object) self).cb$setLastSeen(value);
    }

    /** ReportChatMessage.message (read) */
    public static String message(ReportChatMessage self) {
        return self.message();
    }

    /** ReportChatMessage.message (write) */
    public static void set_message(ReportChatMessage self, String value) {
        ((CbLibAuthlibReportChatMessageFields) (Object) self).cb$setMessage(value);
    }

    /** ReportChatMessage.signature (read) */
    public static ByteBuffer signature(ReportChatMessage self) {
        return self.signature();
    }

    /** ReportChatMessage.signature (write) */
    public static void set_signature(ReportChatMessage self, ByteBuffer value) {
        ((CbLibAuthlibReportChatMessageFields) (Object) self).cb$setSignature(value);
    }

    /** ReportChatMessage.messageReported (read) */
    public static boolean messageReported(ReportChatMessage self) {
        return self.messageReported();
    }

    /** ReportChatMessage.messageReported (write) */
    public static void set_messageReported(ReportChatMessage self, boolean value) {
        ((CbLibAuthlibReportChatMessageFields) (Object) self).cb$setMessageReported(value);
    }

    // ReportEvidence
    /** ReportEvidence.messages (read) */
    public static List<ReportChatMessage> messages(ReportEvidence self) {
        return self.messages();
    }

    /** ReportEvidence.messages (write) */
    public static void set_messages(ReportEvidence self, List<ReportChatMessage> value) {
        ((CbLibAuthlibReportEvidenceFields) (Object) self).cb$setMessages(value);
    }

    // ReportedEntity
    /** ReportedEntity.profileId (read) */
    public static UUID profileId(ReportedEntity self) {
        return self.profileId();
    }

    /** ReportedEntity.profileId (write) */
    public static void set_profileId(ReportedEntity self, UUID value) {
        ((CbLibAuthlibReportedEntityFields) (Object) self).cb$setProfileId(value);
    }

    // AbuseReportRequest
    /** AbuseReportRequest.version (read) */
    public static int version(AbuseReportRequest self) {
        return self.version();
    }

    /** AbuseReportRequest.version (write) */
    public static void set_version(AbuseReportRequest self, int value) {
        ((CbLibAuthlibAbuseReportRequestFields) (Object) self).cb$setVersion(value);
    }

    /** AbuseReportRequest.id (read) */
    public static UUID id(AbuseReportRequest self) {
        return self.id();
    }

    /** AbuseReportRequest.id (write) */
    public static void set_id(AbuseReportRequest self, UUID value) {
        ((CbLibAuthlibAbuseReportRequestFields) (Object) self).cb$setId(value);
    }

    /** AbuseReportRequest.report (read) */
    public static AbuseReport report(AbuseReportRequest self) {
        return self.report();
    }

    /** AbuseReportRequest.report (write) */
    public static void set_report(AbuseReportRequest self, AbuseReport value) {
        ((CbLibAuthlibAbuseReportRequestFields) (Object) self).cb$setReport(value);
    }

    /** AbuseReportRequest.clientInfo (read) */
    public static AbuseReportRequest.ClientInfo clientInfo(AbuseReportRequest self) {
        return self.clientInfo();
    }

    /** AbuseReportRequest.clientInfo (write) */
    public static void set_clientInfo(AbuseReportRequest self, AbuseReportRequest.ClientInfo value) {
        ((CbLibAuthlibAbuseReportRequestFields) (Object) self).cb$setClientInfo(value);
    }

    /** AbuseReportRequest.thirdPartyServerInfo (read) */
    public static AbuseReportRequest.ThirdPartyServerInfo thirdPartyServerInfo(AbuseReportRequest self) {
        return self.thirdPartyServerInfo();
    }

    /** AbuseReportRequest.thirdPartyServerInfo (write) */
    public static void set_thirdPartyServerInfo(AbuseReportRequest self, AbuseReportRequest.ThirdPartyServerInfo value) {
        ((CbLibAuthlibAbuseReportRequestFields) (Object) self).cb$setThirdPartyServerInfo(value);
    }

    /** AbuseReportRequest.realmInfo (read) */
    public static AbuseReportRequest.RealmInfo realmInfo(AbuseReportRequest self) {
        return self.realmInfo();
    }

    /** AbuseReportRequest.realmInfo (write) */
    public static void set_realmInfo(AbuseReportRequest self, AbuseReportRequest.RealmInfo value) {
        ((CbLibAuthlibAbuseReportRequestFields) (Object) self).cb$setRealmInfo(value);
    }

    // AbuseReportRequest.ClientInfo
    /** AbuseReportRequest.ClientInfo.clientVersion (read) */
    public static String clientVersion(AbuseReportRequest.ClientInfo self) {
        return self.clientVersion();
    }

    /** AbuseReportRequest.ClientInfo.clientVersion (write) */
    public static void set_clientVersion(AbuseReportRequest.ClientInfo self, String value) {
        ((CbLibAuthlibAbuseReportClientInfoFields) (Object) self).cb$setClientVersion(value);
    }

    /** AbuseReportRequest.ClientInfo.locale (read) */
    public static String locale(AbuseReportRequest.ClientInfo self) {
        return self.locale();
    }

    /** AbuseReportRequest.ClientInfo.locale (write) */
    public static void set_locale(AbuseReportRequest.ClientInfo self, String value) {
        ((CbLibAuthlibAbuseReportClientInfoFields) (Object) self).cb$setLocale(value);
    }

    // AbuseReportRequest.RealmInfo
    /** AbuseReportRequest.RealmInfo.realmId (read) */
    public static String realmId(AbuseReportRequest.RealmInfo self) {
        return self.realmId();
    }

    /** AbuseReportRequest.RealmInfo.realmId (write) */
    public static void set_realmId(AbuseReportRequest.RealmInfo self, String value) {
        ((CbLibAuthlibAbuseReportRealmInfoFields) (Object) self).cb$setRealmId(value);
    }

    /** AbuseReportRequest.RealmInfo.slotId (read) */
    public static int slotId(AbuseReportRequest.RealmInfo self) {
        return self.slotId();
    }

    /** AbuseReportRequest.RealmInfo.slotId (write) */
    public static void set_slotId(AbuseReportRequest.RealmInfo self, int value) {
        ((CbLibAuthlibAbuseReportRealmInfoFields) (Object) self).cb$setSlotId(value);
    }

    // AbuseReportRequest.ThirdPartyServerInfo
    /** AbuseReportRequest.ThirdPartyServerInfo.address (read) */
    public static String address(AbuseReportRequest.ThirdPartyServerInfo self) {
        return self.address();
    }

    /** AbuseReportRequest.ThirdPartyServerInfo.address (write) */
    public static void set_address(AbuseReportRequest.ThirdPartyServerInfo self, String value) {
        ((CbLibAuthlibAbuseReportThirdPartyServerInfoFields) (Object) self).cb$setAddress(value);
    }

    // JoinMinecraftServerRequest
    /** JoinMinecraftServerRequest.accessToken (read) */
    public static String accessToken(JoinMinecraftServerRequest self) {
        return self.accessToken();
    }

    /** JoinMinecraftServerRequest.accessToken (write) */
    public static void set_accessToken(JoinMinecraftServerRequest self, String value) {
        ((CbLibAuthlibJoinMinecraftServerRequestFields) (Object) self).cb$setAccessToken(value);
    }

    /** JoinMinecraftServerRequest.selectedProfile (read) */
    public static UUID selectedProfile(JoinMinecraftServerRequest self) {
        return self.selectedProfile();
    }

    /** JoinMinecraftServerRequest.selectedProfile (write) */
    public static void set_selectedProfile(JoinMinecraftServerRequest self, UUID value) {
        ((CbLibAuthlibJoinMinecraftServerRequestFields) (Object) self).cb$setSelectedProfile(value);
    }

    /** JoinMinecraftServerRequest.serverId (read) */
    public static String serverId(JoinMinecraftServerRequest self) {
        return self.serverId();
    }

    /** JoinMinecraftServerRequest.serverId (write) */
    public static void set_serverId(JoinMinecraftServerRequest self, String value) {
        ((CbLibAuthlibJoinMinecraftServerRequestFields) (Object) self).cb$setServerId(value);
    }

    // TelemetryEventsRequest
    /** TelemetryEventsRequest.events (read) */
    public static List<TelemetryEventsRequest.Event> events(TelemetryEventsRequest self) {
        return self.events();
    }

    // TelemetryEventsRequest.Event
    /** TelemetryEventsRequest.Event.source (read) */
    public static String source(TelemetryEventsRequest.Event self) {
        return self.source();
    }

    /** TelemetryEventsRequest.Event.name (read) */
    public static String name(TelemetryEventsRequest.Event self) {
        return self.name();
    }

    /** TelemetryEventsRequest.Event.timestamp (read) */
    public static long timestamp(TelemetryEventsRequest.Event self) {
        return self.timestamp();
    }

    /** TelemetryEventsRequest.Event.data (read) */
    public static JsonObject data(TelemetryEventsRequest.Event self) {
        return self.data();
    }
}
