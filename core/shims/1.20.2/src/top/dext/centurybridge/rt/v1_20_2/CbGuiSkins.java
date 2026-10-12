package top.dext.centurybridge.rt.v1_20_2;

/**
 * Static parts of the 1.20.1 player-skin API on 1.20.2: AbstractClientPlayer's skin texture helpers,
 * the SkinManager and User constructors' old arguments. Each body is 1.20.1's.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiSkins {

    /**
     * was net/minecraft/class_742.method_3120 AbstractClientPlayer.registerSkinTexture(id, playerName):
     * unless a texture is registered under the id, register the player's skin from the legacy skin
     * server, the UUID's default skin standing in until it loads
     */
    public static void method_3120(net.minecraft.class_2960 id, String playerName) {
        net.minecraft.class_1060 textures = net.minecraft.class_310.method_1551().method_1531();
        net.minecraft.class_1044 texture = textures.method_34590(id, net.minecraft.class_1047.method_4540());
        if (texture == net.minecraft.class_1047.method_4540()) {
            texture = new net.minecraft.class_1046(null,
                String.format(java.util.Locale.ROOT, "http://skins.minecraft.net/MinecraftSkins/%s.png", net.minecraft.class_3544.method_15440(playerName)),
                net.minecraft.class_1068.method_4648(net.minecraft.class_4844.method_43344(playerName)).comp_1626(), true, null);
            textures.method_4616(id, texture);
        }
    }

    /** was net/minecraft/class_742.method_3124 AbstractClientPlayer.getSkinLocation(playerName): skins/sha1(name) */
    public static net.minecraft.class_2960 method_3124(String playerName) {
        return new net.minecraft.class_2960("skins/" + com.google.common.hash.Hashing.sha1()
            .hashUnencodedChars(net.minecraft.class_3544.method_15440(playerName)));
    }

    /** superCtorReshapes conv: 1.20.1 SkinManager's cache directory, as 1.20.2 takes it */
    public static java.nio.file.Path cbPath(java.io.File dir) {
        return dir.toPath();
    }

    /** superCtorReshapes push: the executor 1.20.2's own SkinManager is given (the client) */
    public static java.util.concurrent.Executor cbExecutor() {
        return net.minecraft.class_310.method_1551();
    }

    /**
     * 1.20.1 SkinManager's insecureSkinCache: a profile's textures property decoded without signature checks,
     * kept 15 s per value, on a dummy profile (NIL_UUID where 1.20.1 had null: authlib 5's GameProfile
     * rejects a null id); anything that fails reads as no textures
     */
    public static com.google.common.cache.LoadingCache<String, java.util.Map<com.mojang.authlib.minecraft.MinecraftProfileTexture.Type,
            com.mojang.authlib.minecraft.MinecraftProfileTexture>> cbInsecureSkinCache(com.mojang.authlib.minecraft.MinecraftSessionService sessions) {
        return com.google.common.cache.CacheBuilder.newBuilder().expireAfterAccess(java.time.Duration.ofSeconds(15L))
            .build(new com.google.common.cache.CacheLoader<>() {
                @Override
                public java.util.Map<com.mojang.authlib.minecraft.MinecraftProfileTexture.Type, com.mojang.authlib.minecraft.MinecraftProfileTexture> load(String value) {
                    com.mojang.authlib.GameProfile p = new com.mojang.authlib.GameProfile(net.minecraft.class_156.field_25140, "dummy_mcdummyface");
                    p.getProperties().put("textures", new com.mojang.authlib.properties.Property("textures", value, ""));
                    try {
                        return sessions.getTextures(p, false);
                    } catch (Throwable t) {
                        return com.google.common.collect.ImmutableMap.of();
                    }
                }
            });
    }

    private static final ThreadLocal<String> PENDING_UUID = new ThreadLocal<>();

    /**
     * superCtorReshapes conv: 1.20.1 User kept the UUID as the string it was given and parsed it on
     * demand (getProfileId: null when it is no UUID); 1.20.2 keeps the parsed UUID. The string rides to the
     * constructor (CbGuiUserBridge keeps it, so getUuid() returns it as 1.20.1 did).
     */
    public static java.util.UUID cbUuid(String uuid) {
        PENDING_UUID.set(uuid);
        return cbParseUuid(uuid);
    }

    /** 1.20.1 User.getProfileId: the string read as a UUID with or without dashes, null when it is none */
    public static java.util.UUID cbParseUuid(String uuid) {
        try {
            return com.mojang.util.UndashedUuid.fromStringLenient(uuid);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    /** taken by the User constructor that finishes next on this thread */
    public static String cbTakeUuid() {
        String s = PENDING_UUID.get();
        if (s != null) {
            PENDING_UUID.remove();
        }
        return s;
    }

    private CbGuiSkins() {
    }
}
