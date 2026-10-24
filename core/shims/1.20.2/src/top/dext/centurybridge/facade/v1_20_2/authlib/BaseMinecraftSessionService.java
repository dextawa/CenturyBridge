package top.dext.centurybridge.facade.v1_20_2.authlib;

import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.net.InetAddress;
import java.util.UUID;
import top.dext.centurybridge.rt.v1_20_2.CbLibAuthlib;
import top.dext.centurybridge.rt.v1_20_2.CbLibSessionOld;

/**
 * authlib 4.0.43's com.mojang.authlib.minecraft.BaseMinecraftSessionService (gone in 5.0.47). Its subclasses implement 4.0.43's
 * session shapes (CbLibSessionOld); 5.0.47's shapes reach them as they reach any converted 4.0.43 session service
 * (CbLibAuthlib's bridges).
 */
public abstract class BaseMinecraftSessionService implements MinecraftSessionService, CbLibSessionOld {
    private final AuthenticationService authenticationService;

    protected BaseMinecraftSessionService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    @Override
    public void joinServer(UUID profileId, String authenticationToken, String serverId) throws AuthenticationException {
        CbLibAuthlib.joinServerBridge(this, profileId, authenticationToken, serverId);
    }

    @Override
    public ProfileResult hasJoinedServer(String profileName, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        return CbLibAuthlib.hasJoinedServerBridge(this, profileName, serverId, address);
    }

    @Override
    public ProfileResult fetchProfile(UUID profileId, boolean requireSecure) {
        return CbLibAuthlib.fetchProfileBridge(this, profileId, requireSecure);
    }
}
