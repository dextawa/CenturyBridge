package top.dext.centurybridge.step.v1_21.rt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tags 1.20.6's vanilla data defined and 1.21's dropped: ItemTags.MUSIC_DISCS (1.21 tells a disc by its
 * jukebox_playable component) and DamageTypeTags.BREEZE_IMMUNE_TO (1.21's breeze deflects projectiles in
 * code). The keys are what 1.20.6 named; when a registry binds its tags and the world's data does not
 * define one of them, it gets 1.20.6's vanilla contents (MappedRegistry.bindTags, CbLegacyRegistryTagsMixin):
 * music_discs = #creeper_drop_music_discs + pigstep, otherside, 5, relic; breeze_immune_to = arrow, trident.
 * A datapack that defines the tag keeps its own.
 */
public final class CbItemTags {

    private CbItemTags() {
    }

    private static final net.minecraft.class_6862<net.minecraft.class_1792> MUSIC_DISCS =
        net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41197, net.minecraft.class_2960.method_60656("music_discs"));
    private static final net.minecraft.class_6862<net.minecraft.class_1792> CREEPER_DROP_MUSIC_DISCS =
        net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41197, net.minecraft.class_2960.method_60656("creeper_drop_music_discs"));
    private static final net.minecraft.class_6862<net.minecraft.class_8110> BREEZE_IMMUNE_TO =
        net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_42534, net.minecraft.class_2960.method_60656("breeze_immune_to"));

    /** ItemTags.MUSIC_DISCS (fieldGetter) */
    public static net.minecraft.class_6862<net.minecraft.class_1792> field_15541() {
        return MUSIC_DISCS;
    }

    /** DamageTypeTags.BREEZE_IMMUNE_TO (fieldGetter) */
    public static net.minecraft.class_6862<net.minecraft.class_8110> field_47214() {
        return BREEZE_IMMUNE_TO;
    }

    /** the tags a registry binds, with the dropped tags 1.20.6's vanilla data gave it added (see the class comment) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map withDroppedTags(net.minecraft.class_2378<?> registry, Map tags) {
        net.minecraft.class_5321<?> key = registry.method_30517();
        if (key.equals(net.minecraft.class_7924.field_41197) && !tags.containsKey(MUSIC_DISCS)) {
            List holders = new ArrayList((List) tags.getOrDefault(CREEPER_DROP_MUSIC_DISCS, List.of()));
            for (String disc : new String[] {"music_disc_pigstep", "music_disc_otherside", "music_disc_5", "music_disc_relic"}) {
                add(registry, holders, disc);
            }
            Map out = new LinkedHashMap(tags);
            out.put(MUSIC_DISCS, holders);
            return out;
        }
        if (key.equals(net.minecraft.class_7924.field_42534) && !tags.containsKey(BREEZE_IMMUNE_TO)) {
            List holders = new ArrayList();
            add(registry, holders, "arrow");
            add(registry, holders, "trident");
            Map out = new LinkedHashMap(tags);
            out.put(BREEZE_IMMUNE_TO, holders);
            return out;
        }
        return tags;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void add(net.minecraft.class_2378 registry, List holders, String id) {
        net.minecraft.class_5321 k = net.minecraft.class_5321.method_29179(registry.method_30517(), net.minecraft.class_2960.method_60656(id));
        ((java.util.Optional) registry.method_40264(k)).ifPresent(h -> {
            if (!holders.contains(h)) {
                holders.add(h);
            }
        });
    }
}
