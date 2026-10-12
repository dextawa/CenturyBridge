package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's SkullBlockEntity owner on 1.20.5: 1.20.4 kept the owner as a GameProfile, 1.20.5 as a
 * ResolvableProfile wrapping one (setOwner resolves it in both, updateOwnerProfile). The owner's
 * profile is the wrapped GameProfile; a profile becomes the ResolvableProfile made from it. No
 * owner stays null both ways.
 */
public final class CbBlockSkull {

    /** ResolvableProfile -> GameProfile */
    public static com.mojang.authlib.GameProfile gameProfile(net.minecraft.class_9296 profile) {
        return profile == null ? null : profile.comp_2413();
    }

    /** GameProfile -> ResolvableProfile */
    public static net.minecraft.class_9296 profile(com.mojang.authlib.GameProfile profile) {
        return profile == null ? null : new net.minecraft.class_9296(profile);
    }

    /** 1.20.4's Util.isBlank (gone in 1.20.5): null, empty, or only whitespace / space characters */
    private static boolean isBlank(String s) {
        return s == null || s.isEmpty() || s.chars().allMatch(c -> Character.isWhitespace(c) || Character.isSpaceChar(c));
    }

    /**
     * 1.20.4's SkullBlockEntity.getOrResolveGameProfile(tag), on an item's tag: a "SkullOwner"
     * compound is read as a profile (CbProfiles); a non-blank "SkullOwner" name is taken off the tag
     * and fetched, the profile written back under "SkullOwner" when it arrives; otherwise null.
     */
    public static com.mojang.authlib.GameProfile method_52589(net.minecraft.class_2487 tag) {
        if (tag.method_10573("SkullOwner", 10)) {
            return CbProfiles.readGameProfile(tag.method_10562("SkullOwner"));
        }
        if (tag.method_10573("SkullOwner", 8)) {
            String name = tag.method_10558("SkullOwner");
            if (!isBlank(name)) {
                tag.method_10551("SkullOwner");
                resolve(tag, name);
            }
        }
        return null;
    }

    /** 1.20.4's SkullBlockEntity.resolveGameProfile(tag): fetch a non-blank "SkullOwner" name and write the profile back */
    public static void method_52590(net.minecraft.class_2487 tag) {
        String name = tag.method_10558("SkullOwner");
        if (!isBlank(name)) {
            resolve(tag, name);
        }
    }

    /** 1.20.4's private resolveGameProfile(tag, name), on 1.20.5's profile fetch */
    private static void resolve(net.minecraft.class_2487 tag, String name) {
        net.minecraft.class_2631.method_52580(name).thenAccept(found -> tag.method_10566("SkullOwner",
            CbProfiles.writeGameProfile(new net.minecraft.class_2487(),
                found.orElse(new com.mojang.authlib.GameProfile(net.minecraft.class_156.field_25140, name)))));
    }

    private CbBlockSkull() {
    }
}
