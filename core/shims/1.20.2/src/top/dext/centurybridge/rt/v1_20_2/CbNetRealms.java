package top.dext.centurybridge.rt.v1_20_2;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.util.UndashedUuid;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_2588;
import net.minecraft.class_310;
import net.minecraft.class_4325;
import net.minecraft.class_4341;
import net.minecraft.class_4345;
import net.minecraft.class_4355;
import net.minecraft.class_437;
import net.minecraft.class_4410;
import net.minecraft.class_4416;
import net.minecraft.class_4427;
import net.minecraft.class_4431;
import net.minecraft.class_4871;
import net.minecraft.class_4877;
import net.minecraft.class_5244;
import org.slf4j.Logger;
import top.dext.centurybridge.shims.v1_20_2.CbNetRealmsClientAccessor;

/**
 * Realms bridges of the 1.20.1 -> 1.20.2 networking area (s1net): the Realms client, its errors, the
 * service DTOs whose UUIDs became {@link UUID}s, RealmsUtil, RealmsScreen's dropped constants, the main
 * screen's play, and the long-running-task / reset-world / upload screens whose constructors and task
 * protocol changed. Client-only: every class these touch is in the client jar alone.
 */
@Environment(EnvType.CLIENT)
public final class CbNetRealms {

    private static final Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    // ---------------------------------------------------------------- RealmsScreen constants

    // RealmsScreen's constants 1.20.2 dropped, with their 1.20.1 values (fieldRedirects)

    /** RealmsScreen.COMPONENT_HEIGHT */
    public static final int field_33056 = 20;
    /** RealmsScreen.COLOR_WHITE */
    public static final int field_33059 = 16777215;
    /** RealmsScreen.COLOR_GRAY */
    public static final int field_33060 = 10526880;
    /** RealmsScreen.COLOR_DARK_GREEN */
    public static final int field_33064 = 6077788;
    /** RealmsScreen.COLOR_RED */
    public static final int field_33065 = 16711680;
    /** RealmsScreen.COLOR_BLACK */
    public static final int field_33037 = -1073741824;
    /** RealmsScreen.COLOR_BUTTON_YELLOW */
    public static final int field_33043 = 16777120;
    /** RealmsScreen.UPDATE_BREAKS_ADVENTURE_URL */
    public static final String field_33044 = "https://www.minecraft.net/realms/adventure-maps-in-1-9";

    private static final String S0 = "\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583";
    private static final String S1 = "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584";
    private static final String S2 = "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585";
    private static final String S3 = "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586";
    private static final String S4 = "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587";
    private static final String S5 = "_ _ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588";
    private static final String S6 = "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _";
    private static final String S7 = "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _";
    private static final String S8 = "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _";
    private static final String S9 = "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _";
    private static final String S10 = "\u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _ _";

    /**
     * RealmsLongRunningMcoTaskScreen.SYMBOLS, the frames of 1.20.1's progress bar (1.20.2 draws
     * LoadingDotsWidget instead); the same twenty strings in the same order.
     */
    public static final String[] field_19908 = {
        S0, S1, S2, S3, S4, S5, S4, S3, S2, S1, S0, S6, S7, S8, S9, S10, S9, S8, S7, S6
    };

    // ---------------------------------------------------------------- Realms environment

    /**
     * RealmsClient.switchToStage(): 1.20.1 kept the environment in a mutable static
     * (currentEnvironment) that every later request's URL was built from; 1.20.2 keeps it in
     * ENVIRONMENT, which the layer's access widener leaves writable, so switching works as it did.
     */
    public static void method_21001() {
        CbNetRealmsClientAccessor.cb$setEnvironment(class_4341.class_4343.field_19587);
    }

    /** RealmsClient.switchToProd() */
    public static void method_21008() {
        CbNetRealmsClientAccessor.cb$setEnvironment(class_4341.class_4343.field_19586);
    }

    /** RealmsClient.switchToLocal() */
    public static void method_21012() {
        CbNetRealmsClientAccessor.cb$setEnvironment(class_4341.class_4343.field_19588);
    }

    // ---------------------------------------------------------------- UUIDs

    // The Realms service sends UUIDs undashed. 1.20.1's DTOs kept those strings as they came (an absent
    // one as "" or null, per field); 1.20.2 parses them into UUIDs, an absent one into the nil UUID.

    /** an API UUID string, dashed or not, as 1.20.2 holds it: null stays null, "" (absent) is the nil UUID */
    public static UUID cbUuid(String id) {
        if (id == null) {
            return null;
        }
        return id.isEmpty() ? class_156.field_25140 : UndashedUuid.fromStringLenient(id);
    }

    /** a UUID in the service's undashed form, as 1.20.1 held it */
    public static String cbUndashed(UUID id) {
        return id == null ? null : UndashedUuid.toString(id);
    }

    /** a UUID as the 1.20.1 field read it, an absent one (the nil UUID) as {@code absent} */
    private static String undashed(UUID id, String absent) {
        return id == null ? null : id.equals(class_156.field_25140) ? absent : UndashedUuid.toString(id);
    }

    /** PlayerInfo.getUuid(): 1.20.1 parsed an absent "uuid" as null (1.20.2: the nil UUID) */
    public static String cbPlayerUuid(UUID id) {
        return undashed(id, null);
    }

    /** PendingInvite.worldOwnerUuid read: 1.20.1 parsed an absent one as "" */
    public static String field_22583(class_4871 invite) {
        return undashed(invite.field_22583, "");
    }

    /** PendingInvite.worldOwnerUuid written */
    public static void field_22583(class_4871 invite, String id) {
        invite.field_22583 = cbUuid(id);
    }

    /**
     * RealmsServer.ownerUUID read: 1.20.1 parsed an absent one as "". (1.20.2 also starts a new
     * RealmsServer at the nil UUID, where 1.20.1 started at null; both read as "" here.)
     */
    public static String field_22605(class_4877 server) {
        return undashed(server.field_22605, "");
    }

    /** RealmsServer.ownerUUID written */
    public static void field_22605(class_4877 server, String id) {
        server.field_22605 = cbUuid(id);
    }

    // ---------------------------------------------------------------- RealmsUtil

    /** the session service's profile for an API UUID string; null when it has none */
    private static ProfileResult profile(String id) {
        return class_310.method_1551().method_1495().fetchProfile(UndashedUuid.fromStringLenient(id), false);
    }

    /**
     * RealmsUtil.uuidToName(uuid): the name the session service gives that player. 1.20.1 looked the
     * profile up (fillProfileProperties, cached for an hour); 1.20.2's session service looks it up and
     * caches it itself. Null when the lookup gives nothing, as 1.20.1's unfilled profile had no name.
     */
    public static String method_21568(String id) {
        ProfileResult r = profile(id);
        return r == null ? null : r.profile().getName();
    }

    /**
     * RealmsUtil.getGameProfile(uuid): that player's profile. When the lookup gives nothing, 1.20.1
     * answered the bare UUID with no name; 1.20.2's authlib refuses a nameless profile, so the name is
     * empty instead.
     */
    public static GameProfile method_48973(String id) {
        ProfileResult r = profile(id);
        return r != null ? r.profile() : new GameProfile(UndashedUuid.fromStringLenient(id), "");
    }

    // ---------------------------------------------------------------- RealmsError / RealmsServiceException

    private static final ThreadLocal<String> BODY = new ThreadLocal<>();

    /**
     * The response text RealmsClient.execute is about to parse into the RealmsError of the exception it
     * throws next (CbNetRealmsClientBridgeHooks); 1.20.2 keeps only the parsed result, 1.20.1 kept the text.
     */
    public static void cbRememberBody(String body) {
        BODY.set(body);
    }

    /** the text remembered for the exception being made on this thread, cleared; null when none */
    public static String cbTakeBody() {
        String b = BODY.get();
        BODY.remove();
        return b;
    }

    /** 1.20.1 RealmsError.parse: a JSON object's errorMsg ("" when absent) and errorCode (-1), else null */
    private static CbNetLegacyRealmsError parse1201(int httpCode, String text) {
        if (Strings.isNullOrEmpty(text)) {
            return null;
        }
        try {
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            return new CbNetLegacyRealmsError(httpCode, text, class_4431.method_21547("errorMsg", json, ""),
                class_4431.method_21545("errorCode", json, -1));
        } catch (Exception e) {
            LOGGER.error("Could not parse RealmsError: {}", e.getMessage());
            LOGGER.error("The error was: {}", text);
            return null;
        }
    }

    /** 1.20.1 RealmsClient.getHttpCodeDescription: the raw response 1.20.1 kept when the body did not parse */
    private static String httpCodeDescription(int code) {
        return code == 429 ? class_1074.method_4662("mco.errorMessage.serviceBusy") : "Unknown error";
    }

    /** RealmsError.parse(json), 1.20.1's: a RealmsError for a JSON object, null otherwise */
    public static class_4345 method_30162(String json) {
        return parse1201(-1, json);
    }

    /** new RealmsServiceException(httpResultCode, rawResponse): 1.20.1's failure with no RealmsError */
    public static class_4345 cbServiceError(int httpCode, String rawResponse) {
        return new CbNetLegacyRealmsError(httpCode, rawResponse, null, -1);
    }

    /** new RealmsServiceException(httpResultCode, rawResponse, realmsError) */
    public static class_4345 cbServiceError(int httpCode, String rawResponse, class_4345 error) {
        if (error == null) {
            return new CbNetLegacyRealmsError(httpCode, rawResponse, null, -1);
        }
        return new CbNetLegacyRealmsError(httpCode, rawResponse, method_21036(error), method_21037(error));
    }

    /**
     * What 1.20.1 made of a failure 1.20.2 describes as {@code error}, its response text {@code body}
     * when RealmsClient parsed one: RealmsClient.execute 1.20.1 kept the text as rawResponse with the
     * RealmsError parsed from it, or the code's description when it did not parse; an authentication
     * failure kept the WWW-Authenticate header, a connection failure "Could not connect to Realms: "
     * and the message, a version check "Could not check compatible version, got response: " and the
     * response, a retry "Retry operation".
     */
    public static CbNetLegacyRealmsError cbLegacyView(class_4345 error, String body) {
        if (error instanceof CbNetLegacyRealmsError legacy) {
            return legacy;
        }
        int http = httpCodeOf(error);
        if (body != null) {
            CbNetLegacyRealmsError parsed = parse1201(http, body);
            return parsed != null ? parsed : new CbNetLegacyRealmsError(http, httpCodeDescription(http), null, -1);
        }
        if (error instanceof class_4345.class_8652 auth) {
            return new CbNetLegacyRealmsError(401, auth.comp_1595(), null, -1);
        }
        if (error instanceof class_4345.class_8655 raw) {
            CbNetLegacyRealmsError parsed = parse1201(http, raw.comp_1603());
            return parsed != null ? parsed : new CbNetLegacyRealmsError(http, httpCodeDescription(http), null, -1);
        }
        if (error instanceof class_4345.class_8653 custom) {
            class_2561 payload = custom.comp_1597();
            if (payload == null) {
                return new CbNetLegacyRealmsError(http, httpCodeDescription(http), null, -1);
            }
            if (payload == class_4345.class_8653.field_45236) {
                return new CbNetLegacyRealmsError(http, "Retry operation", null, -1);
            }
            if (payload.method_10851() instanceof class_2588 t && t.method_11023().length == 1) {
                String arg = String.valueOf(t.method_11023()[0]);
                if (t.method_11022().equals("mco.errorMessage.realmsService.connectivity")) {
                    return new CbNetLegacyRealmsError(http, "Could not connect to Realms: " + arg, null, -1);
                }
                if (t.method_11022().equals("mco.errorMessage.realmsService.unknownCompatibility")) {
                    return new CbNetLegacyRealmsError(http, "Could not check compatible version, got response: " + arg, null, -1);
                }
            }
            return new CbNetLegacyRealmsError(http, payload.getString(), null, -1);
        }
        if (error instanceof class_4345.class_8654 json) {
            // only RealmsClient parses one of these, and its text is remembered above; built elsewhere,
            // the text is gone and the parsed fields are all there is
            return new CbNetLegacyRealmsError(http, json.method_21036(),
                json.comp_1601() == null ? "" : json.comp_1601(), json.comp_1599() == http ? -1 : json.comp_1599());
        }
        return new CbNetLegacyRealmsError(http, error.method_21036(), null, -1);
    }

    private static int httpCodeOf(class_4345 error) {
        if (error instanceof class_4345.class_8654 json) {
            return json.comp_1598();
        }
        if (error instanceof class_4345.class_8655 raw) {
            return raw.comp_1602();
        }
        if (error instanceof class_4345.class_8653 custom) {
            return custom.comp_1596();
        }
        if (error instanceof class_4345.class_8652) {
            return 401;
        }
        return error.method_21037();
    }

    /**
     * RealmsServiceException.realmsError read: 1.20.1 left it null when the service's answer parsed
     * into no RealmsError (no body, not JSON, an authentication or connection failure); 1.20.2 always
     * has one. The 1.20.1 view of the exception answers.
     */
    public static class_4345 field_36320(class_4355 e) {
        CbNetLegacyRealmsError v = ((CbNetRealmsAccess.Failure) e).cb$legacy();
        return v.parsed() ? v : null;
    }

    /**
     * RealmsError.getErrorMessage(): the service's own message ("" when the JSON had none). 1.20.2
     * kept the name for logMessage(), a formatted log line.
     */
    public static String method_21036(class_4345 error) {
        if (error instanceof CbNetLegacyRealmsError legacy) {
            return legacy.parsed() ? legacy.errorMsg() : "";
        }
        if (error instanceof class_4345.class_8654 json) {
            return json.comp_1601() == null ? "" : json.comp_1601();
        }
        return "";
    }

    /**
     * RealmsError.getErrorCode(): the service's code, -1 when the JSON had none. 1.20.2's errorCode()
     * answers the HTTP status in that case.
     */
    public static int method_21037(class_4345 error) {
        if (error instanceof CbNetLegacyRealmsError legacy) {
            return legacy.parsed() ? legacy.serviceCode() : -1;
        }
        if (error instanceof class_4345.class_8654 json) {
            return json.comp_1599() == json.comp_1598() ? -1 : json.comp_1599();
        }
        return error.method_21037();
    }

    // ---------------------------------------------------------------- RealmsMainScreen.play

    /**
     * RealmsMainScreen.play(server, screen) on a main screen: an instance method in 1.20.1, so a call
     * reached a subclass's override; 1.20.2's is static. A screen overriding it gets the call, any
     * other plays the 1.20.2 way.
     */
    public static void method_20853(class_4325 self, class_4877 server, class_437 screen) {
        if (self instanceof CbNetLegacyRealmsPlay legacy) {
            legacy.method_20853(server, screen);
        } else {
            class_4325.method_20853(server, screen);
        }
    }

    /**
     * super.play(server, screen) in a subclass's override: vanilla's play, with the override marked
     * as running so that vanilla's own dispatch to it (CbNetRealmsMainScreenBridgeHooks) does not run it
     * a second time.
     */
    public static void cbSuperPlay(class_4325 self, class_4877 server, class_437 screen) {
        boolean entered = Guard.cbEnter(screen);
        try {
            class_4325.method_20853(server, screen);
        } finally {
            if (entered) {
                Guard.cbExit(screen);
            }
        }
    }

    // ---------------------------------------------------------------- long-running tasks

    /**
     * getTitle() of a 1.20.1 task (synthesized, adapterHooks): 1.20.2's screen asks for it when it is
     * made. A 1.20.1 task titled its screen through setTitle instead, which starts out empty.
     */
    public static class_2561 cbTaskTitle(CbNetLegacyTask task) {
        class_2561 t = ((CbNetRealmsAccess.Task) task).cb$title();
        return t != null ? t : class_5244.field_39003;
    }

    // ---------------------------------------------------------------- reset-world / upload screens

    /** 1.20.1 RealmsResetWorldScreen's slot when made with no slot: -1, the active world */
    public static int cbNoSlot() {
        return -1;
    }

    /** the title 1.20.1's RealmsResetWorldScreen(lastScreen, server, ...) gave itself */
    public static class_2561 cbResetWorldTitle() {
        return class_2561.method_43471("mco.reset.world.title");
    }

    /** its subtitle */
    public static class_2561 cbResetWorldWarning() {
        return class_2561.method_43471("mco.reset.world.warning");
    }

    /** its subtitle's color */
    public static int cbResetWorldWarningColor() {
        return 16711680;
    }

    /** the title of the screen its reset task ran in */
    public static class_2561 cbResettingTitle() {
        return class_2561.method_43471("mco.reset.world.resetting.screen.title");
    }

    /**
     * After 1.20.1's RealmsResetWorldScreen(lastScreen, server[, title], resetWorldRunnable, callback):
     * the callback the upload screens run when the player leaves them, and the back button's label
     * (Cancel, as those constructors had it).
     */
    public static void cbLegacyResetScreen(class_4410 screen, Runnable callback) {
        cbLegacyResetScreen(screen, class_5244.field_24335, callback);
    }

    /** After 1.20.1's RealmsResetWorldScreen(lastScreen, server, title, subtitle, color, buttonTitle, resetWorldRunnable, callback) */
    public static void cbLegacyResetScreen(class_4410 screen, class_2561 buttonTitle, Runnable callback) {
        ((CbNetRealmsAccess.Callback) screen).cb$setLegacyCallback(callback);
        ((CbNetRealmsAccess.ResetScreen) screen).cb$setLegacyButtonTitle(buttonTitle);
    }

    /** After 1.20.1's RealmsSelectFileToUploadScreen(worldId, slot, lastScreen, callback) */
    public static void cbLegacyCallback(class_4416 screen, Runnable callback) {
        ((CbNetRealmsAccess.Callback) screen).cb$setLegacyCallback(callback);
    }

    /** After 1.20.1's RealmsUploadScreen(worldId, slot, lastScreen, level, callback) */
    public static void cbLegacyCallback(class_4427 screen, Runnable callback) {
        ((CbNetRealmsAccess.Callback) screen).cb$setLegacyCallback(callback);
    }

    /**
     * RealmsMainScreen.updateTeaserImages(resources): 1.20.1 filled the purchase popup's carousel with the
     * images under textures/gui/images; 1.20.2 moved the popup to AddRealmPopupScreen, whose
     * updateCarouselImages(resources) is the same body (same directory, same filter, the list its carousel
     * draws). Called where 1.20.1 called it, on resource reload.
     */
    public static void method_23765(net.minecraft.class_3300 resources) {
        net.minecraft.class_8656.method_52685(resources);
    }

    private CbNetRealms() {
    }
}
