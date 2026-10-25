package top.dext.centurybridge.step.v1_21_9.rt;

import com.mojang.authlib.GameProfile;
import java.lang.ref.WeakReference;
import net.minecraft.class_11560;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.9's NameAndId where 1.21.8 passed a GameProfile (player lists, ops / bans / whitelist entries, the server's
 * op-level and singleplayer-owner checks, the user cache).
 *
 * A 1.21.8 call converts its profile to the id and name 1.21.9 keeps (NameAndId(GameProfile)), all 1.21.9 reads.
 * A 1.21.8 override is handed a profile back: the online player's own GameProfile (what 1.21.8's callers passed --
 * PlayerList checked the connecting or online player's profile) when a player with that id is on the server, else
 * a profile of just the id and name (1.21.8's lists stored and reloaded only those two).
 */
public final class CbProfiles {

    private CbProfiles() {
    }

    private static volatile WeakReference<MinecraftServer> server = new WeakReference<>(null);

    /** the running server (CbProfilesServerMixin, at the end of MinecraftServer's constructor) */
    public static void cbServer(MinecraftServer s) {
        server = new WeakReference<>(s);
    }

    public static class_11560 toNameAndId(GameProfile profile) {
        return profile == null ? null : new class_11560(profile);
    }

    public static GameProfile toProfile(class_11560 id) {
        if (id == null) {
            return null;
        }
        MinecraftServer s = server.get();
        if (s != null && s.method_3760() != null) {
            class_3222 p = s.method_3760().method_14602(id.comp_4422());
            if (p != null) {
                return p.method_7334();
            }
        }
        return new GameProfile(id.comp_4422(), id.comp_4423());
    }

    /** MinecraftServer.ANONYMOUS_PLAYER_PROFILE (1.21.8: a GameProfile; 1.21.9: the same id and name as NameAndId) */
    public static GameProfile field_35641() {
        return new GameProfile(MinecraftServer.field_35641.comp_4422(), MinecraftServer.field_35641.comp_4423());
    }
}
