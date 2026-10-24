#!/usr/bin/env python3
"""gen_lib_authlib.py: step 1 (1.20.1 -> 1.20.2) wiring for authlib 4.0.43's request / response / abuse-report classes,
which authlib 5.0.47 made final records (core/segments/damage-lib/1.20.1__1.20.2.tsv).

Writes core/segments/shims-1.20.2.json.d/lib-authlib-gen.json (never hand-edit it; edit the tables below and rerun):
  - ctorRedirects: 4.0.43's constructors -> rt/v1_20_2/CbLibAuthlibRecords statics building the record;
  - methodRenames: a getter 5.0.47 renamed to its component's accessor with the same descriptor;
  - instanceRedirects: getters whose shape changed, and 4.0.43's Response getters on its six subclasses;
  - instanceFieldGetters / instanceFieldSetters: 4.0.43's public fields -> the accessor / the record's own field made
    writable by the layer's mixins (shims/v1_20_2/CbLibAuthlib*Fields);
  - ledger: the records themselves (a 4.0.43 subclass cannot load; its rows are extend / super-ctor / override /
    call-inherited).
The tables were read off both jars' bytecode (javap -p -c of authlib-4.0.43.jar and authlib-5.0.47.jar).

usage: python3 core/tools/gen_lib_authlib.py [--no-ledger]   (--no-ledger: measure what the ledger would cover)
"""
import json
import os
import sys

RT = "top/dext/centurybridge/rt/v1_20_2/CbLibAuthlibRecords"
REP = "com/mojang/authlib/minecraft/report/"
REQ = "com/mojang/authlib/yggdrasil/request/"
RES = "com/mojang/authlib/yggdrasil/response/"
OUT = "core/segments/shims-1.20.2.json.d/lib-authlib-gen.json"

# 4.0.43 constructor (owner, descriptor) -> static name in CbLibAuthlibRecords
CTORS = [
    (REP + "AbuseReport", "(Ljava/lang/String;Ljava/lang/String;Lcom/mojang/authlib/minecraft/report/ReportEvidence;"
     "Lcom/mojang/authlib/minecraft/report/ReportedEntity;Ljava/time/Instant;)V", "abuseReport"),
    (REQ + "AbuseReportRequest", "(ILjava/util/UUID;Lcom/mojang/authlib/minecraft/report/AbuseReport;"
     "Lcom/mojang/authlib/yggdrasil/request/AbuseReportRequest$ClientInfo;"
     "Lcom/mojang/authlib/yggdrasil/request/AbuseReportRequest$ThirdPartyServerInfo;"
     "Lcom/mojang/authlib/yggdrasil/request/AbuseReportRequest$RealmInfo;)V", "abuseReportRequest"),
    (REQ + "JoinMinecraftServerRequest", "()V", "joinMinecraftServerRequest"),
    (RES + "BlockListResponse", "()V", "blockListResponse"),
    (RES + "HasJoinedMinecraftServerResponse", "()V", "hasJoinedMinecraftServerResponse"),
    (RES + "KeyPairResponse", "()V", "keyPairResponse"),
    (RES + "MinecraftProfilePropertiesResponse", "()V", "minecraftProfilePropertiesResponse"),
    (RES + "MinecraftTexturesPayload", "()V", "minecraftTexturesPayload"),
    (RES + "ProfileSearchResultsResponse", "()V", "profileSearchResultsResponse"),
    (RES + "UserAttributesResponse", "()V", "userAttributesResponse"),
    (RES + "UserAttributesResponse$BanStatus", "()V", "banStatus"),
    (RES + "UserAttributesResponse$BanStatus$BannedScope", "()V", "bannedScope"),
    (RES + "UserAttributesResponse$Privileges", "()V", "privileges"),
    (RES + "UserAttributesResponse$Privileges$Privilege", "(Lcom/mojang/authlib/yggdrasil/response/UserAttributesResponse$Privileges;)V",
     "privilege"),
    (RES + "UserAttributesResponse$ProfanityFilterPreferences", "()V", "profanityFilterPreferences"),
]

# getter renamed to the accessor: (owner, old name, descriptor, new name)
RENAMES = [
    (RES + "BlockListResponse", "getBlockedProfiles", "()Ljava/util/Set;", "blockedProfiles"),
    (RES + "ErrorResponse", "getPath", "()Ljava/lang/String;", "path"),
    (RES + "ErrorResponse", "getError", "()Ljava/lang/String;", "error"),
    (RES + "ErrorResponse", "getErrorMessage", "()Ljava/lang/String;", "errorMessage"),
    (RES + "ErrorResponse", "getDetails", "()Ljava/util/Map;", "details"),
    (RES + "HasJoinedMinecraftServerResponse", "getId", "()Ljava/util/UUID;", "id"),
    (RES + "HasJoinedMinecraftServerResponse", "getProperties", "()Lcom/mojang/authlib/properties/PropertyMap;", "properties"),
    (RES + "KeyPairResponse", "getPublicKeySignature", "()Ljava/nio/ByteBuffer;", "publicKeySignature"),
    (RES + "KeyPairResponse", "getExpiresAt", "()Ljava/lang/String;", "expiresAt"),
    (RES + "KeyPairResponse", "getRefreshedAfter", "()Ljava/lang/String;", "refreshedAfter"),
    (RES + "MinecraftProfilePropertiesResponse", "getId", "()Ljava/util/UUID;", "id"),
    (RES + "MinecraftProfilePropertiesResponse", "getName", "()Ljava/lang/String;", "name"),
    (RES + "MinecraftProfilePropertiesResponse", "getProperties", "()Lcom/mojang/authlib/properties/PropertyMap;", "properties"),
    (RES + "MinecraftTexturesPayload", "getTimestamp", "()J", "timestamp"),
    (RES + "MinecraftTexturesPayload", "getProfileId", "()Ljava/util/UUID;", "profileId"),
    (RES + "MinecraftTexturesPayload", "getProfileName", "()Ljava/lang/String;", "profileName"),
    (RES + "MinecraftTexturesPayload", "getTextures", "()Ljava/util/Map;", "textures"),
    (RES + "UserAttributesResponse", "getPrivileges", "()Lcom/mojang/authlib/yggdrasil/response/UserAttributesResponse$Privileges;",
     "privileges"),
    (RES + "UserAttributesResponse", "getProfanityFilterPreferences",
     "()Lcom/mojang/authlib/yggdrasil/response/UserAttributesResponse$ProfanityFilterPreferences;", "profanityFilterPreferences"),
    (RES + "UserAttributesResponse", "getBanStatus", "()Lcom/mojang/authlib/yggdrasil/response/UserAttributesResponse$BanStatus;",
     "banStatus"),
    (RES + "UserAttributesResponse$BanStatus", "getBannedScopes", "()Ljava/util/Map;", "bannedScopes"),
    (RES + "UserAttributesResponse$BanStatus$BannedScope", "getBanId", "()Ljava/util/UUID;", "banId"),
    (RES + "UserAttributesResponse$BanStatus$BannedScope", "getExpires", "()Ljava/time/Instant;", "expires"),
    (RES + "UserAttributesResponse$BanStatus$BannedScope", "getReason", "()Ljava/lang/String;", "reason"),
    (RES + "UserAttributesResponse$BanStatus$BannedScope", "getReasonMessage", "()Ljava/lang/String;", "reasonMessage"),
    (RES + "UserAttributesResponse$ProfanityFilterPreferences", "isEnabled", "()Z", "enabled"),
]

# getters bridged by a static of the same name (owner, name, descriptor)
STATIC_GETTERS = [
    (RES + "KeyPairResponse", "getPrivateKey", "()Ljava/lang/String;"),
    (RES + "KeyPairResponse", "getPublicKey", "()Ljava/lang/String;"),
    (RES + "ProfileSearchResultsResponse", "getProfiles", "()[Lcom/mojang/authlib/GameProfile;"),
]
RESPONSE_SUBCLASSES = ["BlockListResponse", "HasJoinedMinecraftServerResponse", "KeyPairResponse",
                       "MinecraftProfilePropertiesResponse", "ProfileSearchResultsResponse", "UserAttributesResponse"]
for r in RESPONSE_SUBCLASSES:
    for g in ("getError", "getErrorMessage", "getCause"):
        STATIC_GETTERS.append((RES + r, g, "()Ljava/lang/String;"))

# public fields: owner -> [(name, descriptor)], and whether 4.0.43's field was writable (not final)
FIELDS = [
    (REP + "AbuseReport", True, [("opinionComments", "Ljava/lang/String;"), ("reason", "Ljava/lang/String;"),
                                 ("evidence", "Lcom/mojang/authlib/minecraft/report/ReportEvidence;"),
                                 ("reportedEntity", "Lcom/mojang/authlib/minecraft/report/ReportedEntity;"),
                                 ("createdTime", "Ljava/time/Instant;")]),
    (REP + "ReportChatMessage", True, [("index", "I"), ("profileId", "Ljava/util/UUID;"), ("sessionId", "Ljava/util/UUID;"),
                                       ("timestamp", "Ljava/time/Instant;"), ("salt", "J"), ("lastSeen", "Ljava/util/List;"),
                                       ("message", "Ljava/lang/String;"), ("signature", "Ljava/nio/ByteBuffer;"),
                                       ("messageReported", "Z")]),
    (REP + "ReportEvidence", True, [("messages", "Ljava/util/List;")]),
    (REP + "ReportedEntity", True, [("profileId", "Ljava/util/UUID;")]),
    (REQ + "AbuseReportRequest", True, [("version", "I"), ("id", "Ljava/util/UUID;"),
                                        ("report", "Lcom/mojang/authlib/minecraft/report/AbuseReport;"),
                                        ("clientInfo", "Lcom/mojang/authlib/yggdrasil/request/AbuseReportRequest$ClientInfo;"),
                                        ("thirdPartyServerInfo",
                                         "Lcom/mojang/authlib/yggdrasil/request/AbuseReportRequest$ThirdPartyServerInfo;"),
                                        ("realmInfo", "Lcom/mojang/authlib/yggdrasil/request/AbuseReportRequest$RealmInfo;")]),
    (REQ + "AbuseReportRequest$ClientInfo", True, [("clientVersion", "Ljava/lang/String;"), ("locale", "Ljava/lang/String;")]),
    (REQ + "AbuseReportRequest$RealmInfo", True, [("realmId", "Ljava/lang/String;"), ("slotId", "I")]),
    (REQ + "AbuseReportRequest$ThirdPartyServerInfo", True, [("address", "Ljava/lang/String;")]),
    (REQ + "JoinMinecraftServerRequest", True, [("accessToken", "Ljava/lang/String;"), ("selectedProfile", "Ljava/util/UUID;"),
                                                ("serverId", "Ljava/lang/String;")]),
    (REQ + "TelemetryEventsRequest", False, [("events", "Ljava/util/List;")]),
    (REQ + "TelemetryEventsRequest$Event", False, [("source", "Ljava/lang/String;"), ("name", "Ljava/lang/String;"),
                                                   ("timestamp", "J"), ("data", "Lcom/google/gson/JsonObject;")]),
]

RECORDS = [REP + c for c in ("AbuseReport", "ReportChatMessage", "ReportEvidence", "ReportedEntity")] \
    + [REQ + c for c in ("AbuseReportRequest", "AbuseReportRequest$ClientInfo", "AbuseReportRequest$RealmInfo",
                         "AbuseReportRequest$ThirdPartyServerInfo", "JoinMinecraftServerRequest", "TelemetryEventsRequest",
                         "TelemetryEventsRequest$Event")] \
    + [RES + c for c in ("BlockListResponse", "ErrorResponse", "HasJoinedMinecraftServerResponse", "KeyPairResponse",
                         "MinecraftProfilePropertiesResponse", "MinecraftTexturesPayload", "ProfileSearchResultsResponse",
                         "UserAttributesResponse", "UserAttributesResponse$BanStatus",
                         "UserAttributesResponse$BanStatus$BannedScope", "UserAttributesResponse$Privileges",
                         "UserAttributesResponse$Privileges$Privilege", "UserAttributesResponse$ProfanityFilterPreferences")]

LEDGER_BASE = ("authlib 5.0.47 made {cls} a final record (javap -p of authlib-5.0.47.jar: public final class ... extends "
               "java.lang.Record), where 4.0.43's was an open class: a 4.0.43 subclass cannot load (its extend, super-ctor, "
               "override and inherited-call sites). A facade superclass would split the type: 1.20.2's game and authlib take and "
               "return the record ({where}), so the subclass could not stand where 4.0.43 code passed it. Every other use is "
               "bridged (rt/v1_20_2/CbLibAuthlibRecords, methodRenames, the record's fields made writable by "
               "shims/v1_20_2/CbLibAuthlib*Fields)")
WHERE = {
    "report": "UserApiService.reportAbuse, AbuseReportSender, ChatReportBuilder",
    "request": "UserApiService.reportAbuse, YggdrasilMinecraftSessionService, YggdrassilTelemetrySession",
    "response": "YggdrasilUserApiService, YggdrasilMinecraftSessionService, YggdrasilGameProfileRepository, MinecraftClient",
}
RESPONSE_NOTE = ("; 4.0.43's class also extended Response, which 5.0.47 dropped (a facade now): a value of this record "
                 "is not a Response, so YggdrasilAuthenticationService.makeRequest(url, input, <this class>) cannot answer "
                 "(CbLibAuthlibLogin refuses it by name)")


def main():
    out = {"_basis_lib_authlib_gen": "generated by core/tools/gen_lib_authlib.py -- do not edit; authlib 4.0.43 classes that 5.0.47 made "
                     "final records, step 1 library front (slib)",
           "ctorRedirects": {}, "methodRenames": {}, "instanceRedirects": {}, "instanceFieldGetters": {},
           "instanceFieldSetters": {}, "ledger": {}}
    for owner, desc, name in CTORS:
        out["ctorRedirects"][f"{owner}.<init>{desc}"] = f"{RT}#{name}"
    for owner, name, desc, new in RENAMES:
        out["methodRenames"][f"{owner}.{name}{desc}"] = new
    for owner, name, desc in STATIC_GETTERS:
        out["instanceRedirects"][f"{owner}.{name}{desc}"] = RT
    for owner, writable, fields in FIELDS:
        for name, desc in fields:
            out["instanceFieldGetters"][f"{owner}.{name}:{desc}"] = f"{RT}#{name}"
            if writable:
                out["instanceFieldSetters"][f"{owner}.{name}:{desc}"] = f"{RT}#set_{name}"
    for cls in RECORDS if "--no-ledger" not in sys.argv else []:
        kind = "report" if "/report/" in cls else "request" if "/request/" in cls else "response"
        reason = LEDGER_BASE.format(cls=cls.rsplit("/", 1)[1], where=WHERE[kind])
        if cls.rsplit("/", 1)[1] in RESPONSE_SUBCLASSES:
            reason += RESPONSE_NOTE
        out["ledger"][cls] = reason
    with open(OUT, "w", encoding="utf-8", newline="\n") as f:
        json.dump(out, f, indent=1, ensure_ascii=False)
        f.write("\n")
    print(f"{OUT}: {sum(len(v) for k, v in out.items() if isinstance(v, dict))} entries")


if __name__ == "__main__":
    os.chdir(os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", ".."))
    main()
