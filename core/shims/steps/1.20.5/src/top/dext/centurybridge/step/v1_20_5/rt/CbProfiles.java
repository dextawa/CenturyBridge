package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's NbtUtils.readGameProfile / writeGameProfile, which 1.20.5 dropped with the NBT form of a
 * profile (1.20.5 keeps it as a ResolvableProfile component): the NBT a 1.20.4 mod reads and writes
 * -- a skull owner, "SkullOwner" on a player head item -- has exactly this shape. Shared by every
 * area of this step. Both bodies are 1.20.4's, read off its bytecode (NbtUtils.method_10683 /
 * method_10684), on 1.20.5's CompoundTag API.
 */
public final class CbProfiles {

    /**
     * 1.20.4's readGameProfile(tag): the "Id" UUID (the nil UUID without one), the "Name" string
     * ("" without one), and every "Properties" entry -- a compound of lists of {Value, Signature?}
     * by property name; null when building it throws anything, as 1.20.4 caught every Throwable there.
     */
    public static com.mojang.authlib.GameProfile readGameProfile(net.minecraft.class_2487 tag) {
        java.util.UUID id = tag.method_25928("Id") ? tag.method_25926("Id") : net.minecraft.class_156.field_25140;
        String name = tag.method_10558("Name");
        try {
            com.mojang.authlib.GameProfile profile = new com.mojang.authlib.GameProfile(id, name);
            if (tag.method_10573("Properties", 10)) {
                net.minecraft.class_2487 properties = tag.method_10562("Properties");
                for (String key : properties.method_10541()) {
                    net.minecraft.class_2499 list = properties.method_10554(key, 10);
                    for (int i = 0; i < list.size(); i++) {
                        net.minecraft.class_2487 p = list.method_10602(i);
                        String value = p.method_10558("Value");
                        if (p.method_10573("Signature", 8)) {
                            profile.getProperties().put(key, new com.mojang.authlib.properties.Property(key, value,
                                p.method_10558("Signature")));
                        } else {
                            profile.getProperties().put(key, new com.mojang.authlib.properties.Property(key, value));
                        }
                    }
                }
            }
            return profile;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * 1.20.4's writeGameProfile(tag, profile): "Name" unless empty, "Id" unless the nil UUID, and
     * "Properties" when there are any; the tag itself is answered.
     */
    public static net.minecraft.class_2487 writeGameProfile(net.minecraft.class_2487 tag, com.mojang.authlib.GameProfile profile) {
        if (!profile.getName().isEmpty()) {
            tag.method_10582("Name", profile.getName());
        }
        if (!profile.getId().equals(net.minecraft.class_156.field_25140)) {
            tag.method_25927("Id", profile.getId());
        }
        if (!profile.getProperties().isEmpty()) {
            net.minecraft.class_2487 properties = new net.minecraft.class_2487();
            for (String key : profile.getProperties().keySet()) {
                net.minecraft.class_2499 list = new net.minecraft.class_2499();
                for (com.mojang.authlib.properties.Property property : profile.getProperties().get(key)) {
                    net.minecraft.class_2487 p = new net.minecraft.class_2487();
                    p.method_10582("Value", property.value());
                    String signature = property.signature();
                    if (signature != null) {
                        p.method_10582("Signature", signature);
                    }
                    list.add(p);
                }
                properties.method_10566(key, list);
            }
            tag.method_10566("Properties", properties);
        }
        return tag;
    }

    private CbProfiles() {
    }
}
