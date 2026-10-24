package top.dext.centurybridge.facade.v1_20_2.authlib;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import org.apache.commons.lang3.Validate;
import top.dext.centurybridge.facade.v1_20_2.AuthlibAgent;
import top.dext.centurybridge.rt.v1_20_2.CbLibAuthlib;

/**
 * authlib 4.0.43's com.mojang.authlib.legacy.LegacyAuthenticationService (gone in 5.0.47). The request helpers 4.0.43's
 * HttpAuthenticationService had are declared here, so a subclass's override of them is what this service's logins call.
 */
public class LegacyAuthenticationService extends HttpAuthenticationService {
    protected LegacyAuthenticationService(Proxy proxy) {
        super(proxy);
    }

    public LegacyUserAuthentication createUserAuthentication(AuthlibAgent agent) {
        Validate.notNull(agent);
        if (agent != AuthlibAgent.MINECRAFT) {
            throw new IllegalArgumentException("Legacy authentication cannot handle anything but Minecraft");
        }
        return new LegacyUserAuthentication(this);
    }

    @Override
    public LegacyMinecraftSessionService createMinecraftSessionService() {
        return new LegacyMinecraftSessionService(this);
    }

    @Override
    public GameProfileRepository createProfileRepository() {
        throw new UnsupportedOperationException("Legacy authentication service has no profile repository");
    }

    protected HttpURLConnection createUrlConnection(URL url) throws IOException {
        return CbLibAuthlib.createUrlConnection(this, url);
    }

    public String performPostRequest(URL url, String post, String contentType) throws IOException {
        return CbLibAuthlib.performPostRequest(this, url, post, contentType);
    }

    public String performGetRequest(URL url) throws IOException {
        return CbLibAuthlib.performGetRequest(this, url);
    }

    public String performGetRequest(URL url, String authentication) throws IOException {
        return CbLibAuthlib.performGetRequest(this, url, authentication);
    }
}
