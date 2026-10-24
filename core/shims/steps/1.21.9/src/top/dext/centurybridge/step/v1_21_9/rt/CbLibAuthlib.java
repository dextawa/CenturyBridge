package top.dext.centurybridge.step.v1_21_9.rt;

import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import java.util.Objects;
import java.util.UUID;

/**
 * authlib 6.0.58 (1.21.8) members that authlib 7.0.61 (1.21.9) changed or removed, with 6.0.58's semantics read off
 * its bytecode: the library front of this step (core/segments/damage-lib/1.21.8__1.21.9.tsv).
 * <ul>
 * <li>GameProfile became a record whose two-argument constructor shares the immutable PropertyMap.EMPTY; 6.0.58's
 * gave every profile its own mutable map ({@link #gameProfile}). getId/getName/getProperties are the record's
 * id/name/properties (wiring: methodRenames).</li>
 * <li>PropertyMap() is gone and every PropertyMap immutable: {@link CbLibMutablePropertyMap}.</li>
 * <li>Environment gained profilesHost: 6.0.58's profile repository looked names up on servicesHost, 7.0.61's on
 * profilesHost (same path, /minecraft/profile/lookup/...), so a 6.0.58 environment's profiles host is its services
 * host ({@link #environment}).</li>
 * <li>ProfileLookupCallback.onProfileLookupSucceeded(GameProfile) became (name, id): 6.0.58's repository handed the
 * profile it deserialized from the lookup response, id and name with an empty mutable property map.</li>
 * <li>MinecraftProfilePropertiesResponse.toProfile() became profile(): 6.0.58 built a new profile and copied the
 * response's properties into its own mutable map; 7.0.61 shares the response's map.</li>
 * </ul>
 */
public final class CbLibAuthlib {
    private CbLibAuthlib() {
    }

    /** new GameProfile(id, name) as 6.0.58 built it: null checks with its messages, a fresh mutable property map */
    public static GameProfile gameProfile(UUID id, String name) {
        return new GameProfile(Objects.requireNonNull(id, "Profile ID must not be null"),
            Objects.requireNonNull(name, "Profile name must not be null"), new CbLibMutablePropertyMap());
    }

    /** new PropertyMap() */
    public static PropertyMap propertyMap() {
        return new CbLibMutablePropertyMap();
    }

    /** new Environment(sessionHost, servicesHost, name) */
    public static Environment environment(String sessionHost, String servicesHost, String name) {
        return new Environment(sessionHost, servicesHost, servicesHost, name);
    }

    /** MinecraftProfilePropertiesResponse.toProfile() */
    public static GameProfile toProfile(MinecraftProfilePropertiesResponse response) {
        GameProfile profile = gameProfile(response.id(), response.name());
        profile.properties().putAll(response.properties());
        return profile;
    }

    /**
     * the 7.0.61 success shape synthesized in a converted mod's ProfileLookupCallback: its own 6.0.58 method, handed the
     * profile 6.0.58's repository built (the looked-up id and name, empty mutable properties)
     */
    public static void profileLookupSucceeded(CbLibProfileLookupOld self, String name, UUID id) {
        self.onProfileLookupSucceeded(gameProfile(id, name));
    }

    /**
     * a mod's call of the 6.0.58 shape on any callback: a converted mod's own implementation takes the profile itself,
     * any other callback the 7.0.61 shape with the profile's name and id
     */
    public static void onProfileLookupSucceeded(ProfileLookupCallback callback, GameProfile profile) {
        if (callback instanceof CbLibProfileLookupOld old) {
            old.onProfileLookupSucceeded(profile);
        } else {
            callback.onProfileLookupSucceeded(profile.name(), profile.id());
        }
    }
}
