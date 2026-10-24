package cb.slib;

import com.google.gson.JsonObject;
import com.mojang.authlib.Agent;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.UserType;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.authlib.yggdrasil.request.JoinMinecraftServerRequest;
import com.mojang.authlib.yggdrasil.request.TelemetryEventsRequest;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.authlib.yggdrasil.response.UserAttributesResponse;
import java.net.Proxy;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;

/**
 * A 1.20.1-era mod (compiled against authlib 4.0.43) for slib's step-1 authlib login and record bridges: the removed
 * username/password login (YggdrasilUserAuthentication over a service whose HTTP is a mod override, as a 4.0.43 subclass
 * could), and the request / response / report classes 5.0.47 made records (constructors, getters, public field writes).
 * Prints "CB-SLIB ok|FAIL ..." and "CB-SLIB LOGIN DONE passed/checks".
 */
public class SlibAuthlibLoginTest implements ModInitializer {
    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-SLIB " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) FAILS.add(what);
    }

    static void run(String what, Runnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            check(false, what + " threw " + e);
            e.printStackTrace(System.out);
        }
    }

    /** a service whose POSTs are answered locally: the login's requests reach this override, as in 4.0.43 */
    static class LocalService extends YggdrasilAuthenticationService {
        final List<String> posts = new ArrayList<>();
        String answer;

        LocalService(String clientToken) {
            super(Proxy.NO_PROXY, clientToken);
        }

        @Override
        public String performPostRequest(URL url, String post, String contentType) {
            posts.add(url.getPath() + " " + post);
            return answer;
        }
    }

    @Override
    public void onInitialize() {
        UUID id = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
        run("login", () -> {
            LocalService service = new LocalService("client-token-1");
            AuthenticationService asInterface = service;
            UserAuthentication auth = asInterface.createUserAuthentication(Agent.MINECRAFT);
            check(auth instanceof YggdrasilUserAuthentication, "createUserAuthentication gives a YggdrasilUserAuthentication");
            check(!auth.canLogIn() && !auth.isLoggedIn(), "a fresh login cannot log in and is not logged in");
            auth.setUsername("user@example.com");
            auth.setPassword("pw");
            check(auth.canLogIn(), "with username and password it can log in");
            service.answer = "{\"accessToken\":\"at-1\",\"clientToken\":\"client-token-1\",\"selectedProfile\":{\"id\":\""
                + "069a79f444e94726a5befca90e38aaf5\",\"name\":\"Notch\"},\"availableProfiles\":[{\"id\":\""
                + "069a79f444e94726a5befca90e38aaf5\",\"name\":\"Notch\"}],\"user\":{\"id\":\"user-id-1\"}}";
            try {
                auth.logIn();
            } catch (AuthenticationException e) {
                check(false, "logIn threw " + e);
                return;
            }
            check(service.posts.size() == 1 && service.posts.get(0).startsWith("/authenticate ")
                && service.posts.get(0).contains("\"username\":\"user@example.com\"")
                && service.posts.get(0).contains("\"clientToken\":\"client-token-1\"")
                && service.posts.get(0).contains("\"name\":\"Minecraft\""), "logIn POSTs 4.0.43's authenticate request: " + service.posts);
            check(auth.isLoggedIn() && auth.canPlayOnline(), "logged in and can play online");
            check("at-1".equals(auth.getAuthenticatedToken()) && "user-id-1".equals(auth.getUserID()), "token and user id from the answer");
            check(auth.getSelectedProfile() != null && "Notch".equals(auth.getSelectedProfile().getName())
                && id.equals(auth.getSelectedProfile().getId()), "selected profile from the answer");
            check(auth.getUserType() == UserType.MOJANG, "user type MOJANG");
            Map<String, Object> saved = auth.saveForStorage();
            check("at-1".equals(saved.get("accessToken")) && "user@example.com".equals(saved.get("username")), "saveForStorage: " + saved);
            auth.logOut();
            check(!auth.isLoggedIn() && auth.getAuthenticatedToken() == null, "logOut");
            service.answer = "{\"error\":\"ForbiddenOperationException\",\"errorMessage\":\"Invalid credentials.\"}";
            auth.setUsername("user@example.com");
            auth.setPassword("wrong");
            try {
                auth.logIn();
                check(false, "a refused login throws");
            } catch (InvalidCredentialsException e) {
                check("Invalid credentials.".equals(e.getMessage()), "a refused login throws InvalidCredentialsException(message)");
            } catch (AuthenticationException e) {
                check(false, "a refused login threw " + e);
            }
            YggdrasilAuthenticationService noToken = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
            try {
                noToken.createUserAuthentication(Agent.MINECRAFT);
                check(false, "a service without a client token refuses a login");
            } catch (IllegalStateException e) {
                check("Missing client token".equals(e.getMessage()), "a service without a client token: Missing client token");
            }
        });
        run("records", () -> {
            ReportedEntity entity = new ReportedEntity(id);
            List<ReportChatMessage> messages = new ArrayList<>();
            ReportChatMessage message = new ReportChatMessage(0, id, id, Instant.EPOCH, 7L, new ArrayList<>(), "hi", null, true);
            messages.add(message);
            ReportEvidence evidence = new ReportEvidence(messages);
            AbuseReport report = new AbuseReport("comment", "reason-a", evidence, entity, Instant.EPOCH);
            check("reason-a".equals(report.reason) && report.evidence == evidence && report.reportedEntity == entity, "AbuseReport fields read");
            report.reason = "reason-b";
            message.message = "changed";
            message.salt = 9L;
            check("reason-b".equals(report.reason) && "changed".equals(report.evidence.messages.get(0).message)
                && report.evidence.messages.get(0).salt == 9L, "AbuseReport / ReportChatMessage field writes seen by every holder");
            AbuseReportRequest request = new AbuseReportRequest(1, id, report, new AbuseReportRequest.ClientInfo("1.20.1", "en_us"), null, null);
            request.realmInfo = new AbuseReportRequest.RealmInfo("realm", 2);
            request.clientInfo.locale = "de_de";
            String json = ObjectMapper.create().writeValueAsString(request);
            check(json.contains("\"reason\":\"reason-b\"") && json.contains("\"realmId\":\"realm\"") && json.contains("\"locale\":\"de_de\"")
                && json.contains("\"reportType\":\"chat\""), "the request serializes with the writes and report type chat: " + json);
            JoinMinecraftServerRequest join = new JoinMinecraftServerRequest();
            join.accessToken = "at";
            join.selectedProfile = id;
            join.serverId = "srv";
            check("at".equals(join.accessToken) && id.equals(join.selectedProfile) && "srv".equals(join.serverId), "JoinMinecraftServerRequest fields");
            JsonObject data = new JsonObject();
            TelemetryEventsRequest.Event event = new TelemetryEventsRequest.Event("src", "name", Instant.ofEpochSecond(5), data);
            check(event.timestamp == 5L && "src".equals(event.source) && event.data == data, "TelemetryEventsRequest.Event fields (epoch seconds, as 4.0.43)");
            KeyPairResponse keyPair = new KeyPairResponse();
            check(keyPair.getExpiresAt() == null && keyPair.getError() == null, "a new KeyPairResponse has nothing set");
            try {
                keyPair.getPrivateKey();
                check(false, "getPrivateKey without a key pair throws");
            } catch (NullPointerException e) {
                check(true, "getPrivateKey without a key pair: NullPointerException, as 4.0.43");
            }
            UserAttributesResponse.Privileges privileges = new UserAttributesResponse.Privileges();
            check(!privileges.getOnlineChat() && !privileges.getTelemetry(), "unset privileges are false");
            check(new UserAttributesResponse().getBanStatus() == null && !new UserAttributesResponse.ProfanityFilterPreferences().isEnabled(),
                "UserAttributesResponse getters");
            check(new ProfileSearchResultsResponse().getProfiles() == null, "ProfileSearchResultsResponse with no profiles: null");
        });
        System.out.println("CB-SLIB LOGIN DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILED: " + FAILS));
    }
}
