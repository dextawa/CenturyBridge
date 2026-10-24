package top.dext.centurybridge.rt.v1_20_2;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.net.InetAddress;

/** authlib 4.0.43's MinecraftSessionService shapes that 5.0.47 changed, which a converted mod's session service still declares */
public interface CbLibSessionOld {
    void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException;

    GameProfile hasJoinedServer(GameProfile user, String serverId, InetAddress address) throws AuthenticationUnavailableException;

    GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure);
}
