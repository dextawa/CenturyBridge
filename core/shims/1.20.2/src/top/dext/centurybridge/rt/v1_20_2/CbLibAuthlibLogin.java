package top.dext.centurybridge.rt.v1_20_2;

import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.Environment;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.exceptions.UserBannedException;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.lang3.StringUtils;
import top.dext.centurybridge.facade.v1_20_2.AuthlibAgent;
import top.dext.centurybridge.facade.v1_20_2.authlib.LegacyAuthenticationService;
import top.dext.centurybridge.facade.v1_20_2.authlib.Response;
import top.dext.centurybridge.facade.v1_20_2.authlib.UserAuthentication;
import top.dext.centurybridge.facade.v1_20_2.authlib.UserMigratedException;
import top.dext.centurybridge.facade.v1_20_2.authlib.YggdrasilUserAuthentication;

/**
 * authlib 4.0.43's username/password login, which 5.0.47 removed (core/segments/damage-lib/1.20.1__1.20.2.tsv): the
 * authentication service's createUserAuthentication and makeRequest, and the client token 4.0.43's services were built with.
 * The removed classes are facades (facade/v1_20_2/authlib); this class is what stands for the removed service methods.
 * <p>
 * A converted mod's service may still declare 4.0.43's methods (makeRequest, performPostRequest, createUserAuthentication...):
 * the JVM no longer dispatches to them, so each bridge for a 4.0.43 virtual call first looks for such an override
 * (CbLibOverrides) and calls it, as 4.0.43's own virtual call did.
 */
public final class CbLibAuthlibLogin {
    private CbLibAuthlibLogin() {
    }

    private record Built(String clientToken, Environment environment) {
    }

    private static final Map<YggdrasilAuthenticationService, Built> BUILT = Collections.synchronizedMap(new WeakHashMap<>());

    /** what a 4.0.43 constructor gave the service: its client token and environment (5.0.47 keeps neither for login) */
    public static void remember(YggdrasilAuthenticationService service, String clientToken, Environment environment) {
        BUILT.put(service, new Built(clientToken, environment));
    }

    /** a mod subclass's super(proxy, clientToken) */
    public static void rememberToken(YggdrasilAuthenticationService service, String clientToken) {
        remember(service, clientToken, null);
    }

    /** a mod subclass's super(proxy, clientToken, environment) */
    public static void rememberTokenAndEnvironment(YggdrasilAuthenticationService service, String clientToken, Environment environment) {
        remember(service, clientToken, environment);
    }

    private static String clientToken(YggdrasilAuthenticationService service) {
        Built built = BUILT.get(service);
        return built == null ? null : built.clientToken();
    }

    private static final java.lang.reflect.Field ENVIRONMENT;

    static {
        java.lang.reflect.Field f;
        try {
            f = YggdrasilAuthenticationService.class.getDeclaredField("environment");
            f.setAccessible(true);
        } catch (ReflectiveOperationException | RuntimeException e) {
            f = null;
        }
        ENVIRONMENT = f;
    }

    /** the service's environment: the one its 4.0.43 constructor named, else the one 5.0.47 determined (its private field) */
    private static Environment environment(YggdrasilAuthenticationService service) {
        Built built = BUILT.get(service);
        if (built != null && built.environment() != null) {
            return built.environment();
        }
        if (ENVIRONMENT != null) {
            try {
                Environment env = (Environment) ENVIRONMENT.get(service);
                if (env != null) {
                    return env;
                }
            } catch (IllegalAccessException ignored) {
                // fall through
            }
        }
        return YggdrasilEnvironment.PROD.getEnvironment();
    }

    // ------------------------------------------------------------------ createUserAuthentication

    private static final MethodType CREATE_USER_AUTHENTICATION = MethodType.methodType(UserAuthentication.class, AuthlibAgent.class);

    /**
     * service.createUserAuthentication(agent): a legacy service's own; a mod service's override (4.0.43 declared it on the
     * interface, so every 4.0.43 service had one); the Yggdrasil service's 4.0.43 body: a login for its client token and
     * environment ("Missing client token" for a service built without one, as 4.0.43 refused)
     */
    public static UserAuthentication createUserAuthentication(AuthenticationService service, AuthlibAgent agent) {
        if (service instanceof LegacyAuthenticationService legacy) {
            return legacy.createUserAuthentication(agent);
        }
        MethodHandle override = CbLibOverrides.find(service, Object.class, "createUserAuthentication", CREATE_USER_AUTHENTICATION);
        if (override != null) {
            try {
                return (UserAuthentication) override.invoke(service, agent);
            } catch (Throwable t) {
                throw CbLibOverrides.rethrow(t);
            }
        }
        if (service instanceof YggdrasilAuthenticationService yggdrasil) {
            return superCreateUserAuthentication(yggdrasil, agent);
        }
        throw new AbstractMethodError(service.getClass().getName() + ".createUserAuthentication(Agent)");
    }

    /** YggdrasilAuthenticationService's own createUserAuthentication (a subclass's super call) */
    public static UserAuthentication superCreateUserAuthentication(YggdrasilAuthenticationService service, AuthlibAgent agent) {
        String clientToken = clientToken(service);
        if (clientToken == null) {
            throw new IllegalStateException("Missing client token");
        }
        return new YggdrasilUserAuthentication(service, clientToken, agent, environment(service));
    }

    // ------------------------------------------------------------------ the request helpers, dispatching to 4.0.43 overrides

    private static final MethodType CREATE_URL_CONNECTION = MethodType.methodType(HttpURLConnection.class, URL.class);
    private static final MethodType PERFORM_POST = MethodType.methodType(String.class, URL.class, String.class, String.class);
    private static final MethodType PERFORM_GET = MethodType.methodType(String.class, URL.class, String.class);

    public static HttpURLConnection createUrlConnection(HttpAuthenticationService service, URL url) throws IOException {
        MethodHandle override = CbLibOverrides.find(service, HttpAuthenticationService.class, "createUrlConnection", CREATE_URL_CONNECTION);
        if (override != null) {
            try {
                return (HttpURLConnection) override.invoke(service, url);
            } catch (Throwable t) {
                throw CbLibOverrides.rethrow(t);
            }
        }
        return CbLibAuthlib.createUrlConnection(service, url);
    }

    public static String performPostRequest(HttpAuthenticationService service, URL url, String post, String contentType) throws IOException {
        MethodHandle override = CbLibOverrides.find(service, HttpAuthenticationService.class, "performPostRequest", PERFORM_POST);
        if (override != null) {
            try {
                return (String) override.invoke(service, url, post, contentType);
            } catch (Throwable t) {
                throw CbLibOverrides.rethrow(t);
            }
        }
        return CbLibAuthlib.performPostRequest(service, url, post, contentType);
    }

    public static String performGetRequest(HttpAuthenticationService service, URL url, String authentication) throws IOException {
        MethodHandle override = CbLibOverrides.find(service, HttpAuthenticationService.class, "performGetRequest", PERFORM_GET);
        if (override != null) {
            try {
                return (String) override.invoke(service, url, authentication);
            } catch (Throwable t) {
                throw CbLibOverrides.rethrow(t);
            }
        }
        return CbLibAuthlib.performGetRequest(service, url, authentication);
    }

    // ------------------------------------------------------------------ makeRequest

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapper.create();
    private static final MethodType MAKE_REQUEST = MethodType.methodType(Response.class, URL.class, Object.class, Class.class, String.class);
    private static final MethodType MAKE_REQUEST_3 = MethodType.methodType(Response.class, URL.class, Object.class, Class.class);

    /** service.makeRequest(url, input, classOfT): a mod service's override, else 4.0.43's body */
    @SuppressWarnings("unchecked")
    public static <T extends Response> T makeRequest(YggdrasilAuthenticationService service, URL url, Object input, Class<T> classOfT)
            throws AuthenticationException {
        MethodHandle override = CbLibOverrides.find(service, YggdrasilAuthenticationService.class, "makeRequest", MAKE_REQUEST_3);
        if (override != null) {
            try {
                return (T) override.invoke(service, url, input, classOfT);
            } catch (Throwable t) {
                throw CbLibOverrides.rethrow(t);
            }
        }
        return makeRequest(service, url, input, classOfT, null);
    }

    /** service.makeRequest(url, input, classOfT, authentication): a mod service's override, else 4.0.43's body */
    @SuppressWarnings("unchecked")
    public static <T extends Response> T makeRequest(YggdrasilAuthenticationService service, URL url, Object input, Class<T> classOfT,
            String authentication) throws AuthenticationException {
        MethodHandle override = CbLibOverrides.find(service, YggdrasilAuthenticationService.class, "makeRequest", MAKE_REQUEST);
        if (override != null) {
            try {
                return (T) override.invoke(service, url, input, classOfT, authentication);
            } catch (Throwable t) {
                throw CbLibOverrides.rethrow(t);
            }
        }
        return superMakeRequest(service, url, input, classOfT, authentication);
    }

    /** super.makeRequest(url, input, classOfT) */
    public static <T extends Response> T superMakeRequest(YggdrasilAuthenticationService service, URL url, Object input, Class<T> classOfT)
            throws AuthenticationException {
        return makeRequest(service, url, input, classOfT, null);
    }

    /**
     * 4.0.43's YggdrasilAuthenticationService.makeRequest body: GET when there is no input, else the input POSTed as JSON (the
     * service's ObjectMapper: ObjectMapper.create(), which 5.0.47 keeps); the answer read as classOfT; a Yggdrasil error turned
     * into 4.0.43's exception for it; a failed connection or unreadable answer is "Cannot contact authentication server"
     */
    public static <T extends Response> T superMakeRequest(YggdrasilAuthenticationService service, URL url, Object input, Class<T> classOfT,
            String authentication) throws AuthenticationException {
        try {
            String jsonResult = input == null
                ? performGetRequest(service, url, authentication)
                : performPostRequest(service, url, OBJECT_MAPPER.writeValueAsString(input), "application/json");
            T result = OBJECT_MAPPER.readValue(jsonResult, classOfT);
            if (result == null) {
                return null;
            }
            if (StringUtils.isNotBlank(result.getError())) {
                if ("UserMigratedException".equals(result.getCause())) {
                    throw new UserMigratedException(result.getErrorMessage());
                }
                if ("ForbiddenOperationException".equals(result.getError())) {
                    throw new InvalidCredentialsException(result.getErrorMessage());
                }
                if ("InsufficientPrivilegesException".equals(result.getError())) {
                    throw new InsufficientPrivilegesException(result.getErrorMessage());
                }
                if ("multiplayer.access.banned".equals(result.getError())) {
                    throw new UserBannedException();
                }
                throw new AuthenticationException(result.getErrorMessage());
            }
            return result;
        } catch (IOException | IllegalStateException | MinecraftClientException e) {
            throw new AuthenticationUnavailableException("Cannot contact authentication server", e);
        }
    }
}
