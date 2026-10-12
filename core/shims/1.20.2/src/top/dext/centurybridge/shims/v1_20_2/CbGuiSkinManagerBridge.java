package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * SkinManager's 1.20.1 API on 1.20.2's SkinManager: registerTexture, registerSkins (with its callback),
 * getInsecureSkinInformation and getInsecureSkinLocation, removed when 1.20.2 moved to SkinTextures
 * futures. The 1.20.1 bodies, unchanged where 1.20.2 kept the API: a profile texture becomes the
 * PlayerSkinTexture 1.20.1 made (same id -- skins|capes|elytra/sha1(hash) --, same cache file, the default
 * skin standing in until it loads), which is also what 1.20.2's own texture cache registers; the textures
 * of a profile come from the session service exactly as 1.20.1 asked for them (authlib's getTextures kept
 * its 1.20.1 shape), the one profile-filling call taking authlib's current form (fetchProfile, whose
 * properties land on the profile as fillProfileProperties put them there).
 *
 * The texture manager, cache directory and session service are the ones this SkinManager was built with.
 */
@Mixin(net.minecraft.class_1071.class)
public abstract class CbGuiSkinManagerBridge {

    @Unique
    private net.minecraft.class_1060 cb$textures;
    @Unique
    private java.nio.file.Path cb$dir;
    @Unique
    private com.mojang.authlib.minecraft.MinecraftSessionService cb$sessions;
    @Unique
    private com.google.common.cache.LoadingCache<String, java.util.Map<com.mojang.authlib.minecraft.MinecraftProfileTexture.Type,
        com.mojang.authlib.minecraft.MinecraftProfileTexture>> cb$insecure;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$keep(net.minecraft.class_1060 textures, java.nio.file.Path dir, com.mojang.authlib.minecraft.MinecraftSessionService sessions,
                         java.util.concurrent.Executor executor, CallbackInfo ci) {
        this.cb$textures = textures;
        this.cb$dir = dir;
        this.cb$sessions = sessions;
        this.cb$insecure = top.dext.centurybridge.rt.v1_20_2.CbGuiSkins.cbInsecureSkinCache(sessions);
    }

    /** 1.20.1 registerTexture(texture, type) */
    public net.minecraft.class_2960 method_4656(com.mojang.authlib.minecraft.MinecraftProfileTexture texture,
                                               com.mojang.authlib.minecraft.MinecraftProfileTexture.Type type) {
        return this.cb$register(texture, type, null);
    }

    /** 1.20.1 registerTexture(texture, type, callback) */
    @Unique
    private net.minecraft.class_2960 cb$register(com.mojang.authlib.minecraft.MinecraftProfileTexture texture,
                                                 com.mojang.authlib.minecraft.MinecraftProfileTexture.Type type,
                                                 top.dext.centurybridge.facade.v1_20_2.class_1071$class_1072 callback) {
        String hash = com.google.common.hash.Hashing.sha1().hashUnencodedChars(texture.getHash()).toString();
        String folder = switch (type) {
            case SKIN -> "skins";
            case CAPE -> "capes";
            case ELYTRA -> "elytra";
        };
        net.minecraft.class_2960 id = new net.minecraft.class_2960(folder + "/" + hash);
        net.minecraft.class_1044 existing = this.cb$textures.method_34590(id, net.minecraft.class_1047.method_4540());
        if (existing == net.minecraft.class_1047.method_4540()) {
            java.io.File file = this.cb$dir.resolve(hash.length() > 2 ? hash.substring(0, 2) : "xx").resolve(hash).toFile();
            this.cb$textures.method_4616(id, new net.minecraft.class_1046(file, texture.getUrl(), net.minecraft.class_1068.method_4649(),
                type == com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN, () -> {
                    if (callback != null) {
                        callback.onSkinTextureAvailable(type, id, texture);
                    }
                }));
        } else if (callback != null) {
            callback.onSkinTextureAvailable(type, id, texture);
        }
        return id;
    }

    /** 1.20.1 registerSkins(profile, callback, requireSecure): skin and cape, loaded off-thread, registered on the render thread */
    public void method_4652(com.mojang.authlib.GameProfile profile, top.dext.centurybridge.facade.v1_20_2.class_1071$class_1072 callback,
                            boolean requireSecure) {
        net.minecraft.class_156.method_18349().execute(() -> {
            java.util.Map<com.mojang.authlib.minecraft.MinecraftProfileTexture.Type, com.mojang.authlib.minecraft.MinecraftProfileTexture> map =
                com.google.common.collect.Maps.newHashMap();
            try {
                map.putAll(this.cb$sessions.getTextures(profile, requireSecure));
            } catch (com.mojang.authlib.minecraft.InsecurePublicKeyException ignored) {
                // as 1.20.1: an unsigned textures property counts as none
            }
            if (map.isEmpty()) {
                profile.getProperties().clear();
                net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
                if (profile.getId().equals(mc.method_1548().method_44717())) {
                    profile.getProperties().putAll(mc.method_53462().getProperties());
                    try {
                        map.putAll(this.cb$sessions.getTextures(profile, false));
                    } catch (com.mojang.authlib.minecraft.InsecurePublicKeyException ignored) {
                        // not thrown when the signature is not required
                    }
                } else {
                    com.mojang.authlib.yggdrasil.ProfileResult fetched = this.cb$sessions.fetchProfile(profile.getId(), requireSecure);
                    if (fetched != null) {
                        profile.getProperties().putAll(fetched.profile().getProperties());
                    }
                    try {
                        map.putAll(this.cb$sessions.getTextures(profile, requireSecure));
                    } catch (com.mojang.authlib.minecraft.InsecurePublicKeyException ignored) {
                        // as 1.20.1
                    }
                }
            }
            net.minecraft.class_310.method_1551().execute(() -> com.mojang.blaze3d.systems.RenderSystem.recordRenderCall(() ->
                com.google.common.collect.ImmutableList.of(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN,
                        com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.CAPE)
                    .forEach(type -> {
                        if (map.containsKey(type)) {
                            this.cb$register(map.get(type), type, callback);
                        }
                    })));
        });
    }

    /** 1.20.1 getInsecureSkinInformation(profile) */
    public java.util.Map<com.mojang.authlib.minecraft.MinecraftProfileTexture.Type, com.mojang.authlib.minecraft.MinecraftProfileTexture>
            method_4654(com.mojang.authlib.GameProfile profile) {
        com.mojang.authlib.properties.Property property = com.google.common.collect.Iterables.getFirst(profile.getProperties().get("textures"), null);
        if (property == null) {
            return com.google.common.collect.ImmutableMap.of();
        }
        return this.cb$insecure.getUnchecked(property.value());
    }

    /** 1.20.1 getInsecureSkinLocation(profile) */
    public net.minecraft.class_2960 method_44705(com.mojang.authlib.GameProfile profile) {
        com.mojang.authlib.minecraft.MinecraftProfileTexture skin = this.method_4654(profile)
            .get(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN);
        if (skin != null) {
            return this.method_4656(skin, com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN);
        }
        java.util.UUID id = profile.getId() != null ? profile.getId() : net.minecraft.class_4844.method_43344(profile.getName());
        return net.minecraft.class_1068.method_4648(id).comp_1626();
    }
}
