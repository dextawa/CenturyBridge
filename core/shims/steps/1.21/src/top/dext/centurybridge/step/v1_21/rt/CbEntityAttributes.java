package top.dext.centurybridge.step.v1_21.rt;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_5819;

/**
 * Attribute modifiers. 1.21 names a modifier by a ResourceLocation id (AttributeModifier(id, amount,
 * operation)); 1.20.6 named it by a UUID and carried a display name besides.
 *
 * <p>UUID to id is the mapping 1.21's own data fixer applies to the modifiers of a 1.20.6 save:
 * {@code net.minecraft.util.datafix.fixes.AttributeModifierIdFix} (class_9785), the lambda in
 * {@code fixModifiers} reads the uuid and looks it up in the static {@code ID_MAP} (the table below
 * is that map, copied entry by entry from class_9785's static initializer in the 1.21 client jar, in
 * its order). A uuid the map does not know becomes {@code "minecraft:" + uuid.toString().toLowerCase(Locale.ROOT)}.
 * So a modifier a 1.20.6 mod adds at run time and the same modifier read back from a 1.20.6 save get the
 * same id.
 *
 * <p>The fixer has one more step that is not applied here: a modifier whose uuid is unknown but whose
 * name is one of five vanilla names ("Random spawn bonus" and the zombie reinforcement/leader names,
 * {@code NAME_MAP}) gets that vanilla id. Those five were vanilla's own modifiers with random uuids. At
 * run time a 1.20.6 caller names a modifier by its uuid alone (getModifier / removeModifier /
 * hasModifier take a UUID), so the id has to be a function of the uuid alone, or a mod could not find
 * the modifier it added.
 *
 * <p>Back from an id to a UUID (1.20.6's AttributeModifier.id()): an id of the form
 * {@code minecraft:<uuid>} is that uuid; an id in ID_MAP is its first uuid in the map's order (two
 * vanilla uuids share {@code minecraft:baby}, {@code minecraft:attacking} and
 * {@code minecraft:enchantment.soul_speed}; either maps back to the same id, so the round trip
 * holds); any other id (a modifier 1.21 code made) gets a name-based UUID, remembered so that the
 * UUID maps back to the id it came from for the rest of the session.
 */
public final class CbEntityAttributes {

    /** 1.20.6 Attribute.MAX_NAME_LENGTH (a constant 1.21 dropped with the translation-key length check) */
    public static final int MAX_NAME_LENGTH = 64;

    /** class_9785.ID_MAP of the 1.21 client jar, same entries, same order */
    private static final Map<UUID, String> ID_MAP = ImmutableMap.<UUID, String>builder()
        .put(UUID.fromString("736565d2-e1a7-403d-a3f8-1aeb3e302542"), "minecraft:creative_mode_block_range")
        .put(UUID.fromString("98491ef6-97b1-4584-ae82-71a8cc85cf73"), "minecraft:creative_mode_entity_range")
        .put(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"), "minecraft:effect.speed")
        .put(UUID.fromString("7107DE5E-7CE8-4030-940E-514C1F160890"), "minecraft:effect.slowness")
        .put(UUID.fromString("AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3"), "minecraft:effect.haste")
        .put(UUID.fromString("55FCED67-E92A-486E-9800-B47F202C4386"), "minecraft:effect.mining_fatigue")
        .put(UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9"), "minecraft:effect.strength")
        .put(UUID.fromString("C0105BF3-AEF8-46B0-9EBC-92943757CCBE"), "minecraft:effect.jump_boost")
        .put(UUID.fromString("22653B89-116E-49DC-9B6B-9971489B5BE5"), "minecraft:effect.weakness")
        .put(UUID.fromString("5D6F0BA2-1186-46AC-B896-C61C5CEE99CC"), "minecraft:effect.health_boost")
        .put(UUID.fromString("EAE29CF0-701E-4ED6-883A-96F798F3DAB5"), "minecraft:effect.absorption")
        .put(UUID.fromString("03C3C89D-7037-4B42-869F-B146BCB64D2E"), "minecraft:effect.luck")
        .put(UUID.fromString("CC5AF142-2BD2-4215-B636-2605AED11727"), "minecraft:effect.unluck")
        .put(UUID.fromString("6555be74-63b3-41f1-a245-77833b3c2562"), "minecraft:evil")
        .put(UUID.fromString("1eaf83ff-7207-4596-b37a-d7a07b3ec4ce"), "minecraft:powder_snow")
        .put(UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D"), "minecraft:sprinting")
        .put(UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0"), "minecraft:attacking")
        .put(UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667"), "minecraft:baby")
        .put(UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F"), "minecraft:covered")
        .put(UUID.fromString("9e362924-01de-4ddd-a2b2-d0f7a405a174"), "minecraft:suffocating")
        .put(UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E"), "minecraft:drinking")
        .put(UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836"), "minecraft:baby")
        .put(UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718"), "minecraft:attacking")
        .put(UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), "minecraft:armor.boots")
        .put(UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), "minecraft:armor.leggings")
        .put(UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), "minecraft:armor.chestplate")
        .put(UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"), "minecraft:armor.helmet")
        .put(UUID.fromString("C1C72771-8B8E-BA4A-ACE0-81A93C8928B2"), "minecraft:armor.body")
        .put(UUID.fromString("b572ecd2-ac0c-4071-abde-9594af072a37"), "minecraft:enchantment.fire_protection")
        .put(UUID.fromString("40a9968f-5c66-4e2f-b7f4-2ec2f4b3e450"), "minecraft:enchantment.blast_protection")
        .put(UUID.fromString("07a65791-f64d-4e79-86c7-f83932f007ec"), "minecraft:enchantment.respiration")
        .put(UUID.fromString("60b1b7db-fffd-4ad0-817c-d6c6a93d8a45"), "minecraft:enchantment.aqua_affinity")
        .put(UUID.fromString("11dc269a-4476-46c0-aff3-9e17d7eb6801"), "minecraft:enchantment.depth_strider")
        .put(UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038"), "minecraft:enchantment.soul_speed")
        .put(UUID.fromString("b9716dbd-50df-4080-850e-70347d24e687"), "minecraft:enchantment.soul_speed")
        .put(UUID.fromString("92437d00-c3a7-4f2e-8f6c-1f21585d5dd0"), "minecraft:enchantment.swift_sneak")
        .put(UUID.fromString("5d3d087b-debe-4037-b53e-d84f3ff51f17"), "minecraft:enchantment.sweeping_edge")
        .put(UUID.fromString("3ceb37c0-db62-46b5-bd02-785457b01d96"), "minecraft:enchantment.efficiency")
        .put(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"), "minecraft:base_attack_damage")
        .put(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), "minecraft:base_attack_speed")
        .build();

    /** the first uuid of each ID_MAP id, in the map's order */
    private static final Map<String, UUID> UUID_OF_VANILLA_ID;

    static {
        Map<String, UUID> back = new java.util.LinkedHashMap<>();
        for (Map.Entry<UUID, String> e : ID_MAP.entrySet()) {
            back.putIfAbsent(e.getValue(), e.getKey());
        }
        UUID_OF_VANILLA_ID = Map.copyOf(back);
    }

    /** name-based UUIDs handed out for ids no 1.20.6 uuid stands for, back to their ids */
    private static final ConcurrentMap<UUID, class_2960> DERIVED = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * the 1.20.6 name a modifier was built with, by identity: AttributeModifier is a record whose
     * equality no longer sees a name, so two modifiers equal in 1.21 may carry different 1.20.6 names
     */
    private static final ConcurrentMap<class_1322, String> NAMES = new MapMaker().weakKeys().makeMap();

    /** the 1.21 id for a 1.20.6 modifier uuid (see the class comment) */
    public static class_2960 idOf(UUID uuid) {
        class_2960 derived = DERIVED.get(uuid);
        if (derived != null) {
            return derived;
        }
        String vanilla = ID_MAP.get(uuid);
        return class_2960.method_60654(vanilla != null ? vanilla : "minecraft:" + uuid.toString().toLowerCase(Locale.ROOT));
    }

    /** the 1.20.6 uuid for a 1.21 modifier id: the inverse of {@link #idOf} (see the class comment) */
    public static UUID uuidOf(class_2960 id) {
        if (id.method_12836().equals("minecraft")) {
            String path = id.method_12832();
            UUID vanilla = UUID_OF_VANILLA_ID.get(id.toString());
            if (vanilla != null) {
                return vanilla;
            }
            if (path.length() == 36) {
                try {
                    UUID u = UUID.fromString(path);
                    if (u.toString().equals(path)) {
                        return u;
                    }
                } catch (IllegalArgumentException notAUuid) {
                    // an ordinary path
                }
            }
        }
        UUID derived = UUID.nameUUIDFromBytes(("centurybridge:attribute_modifier/" + id).getBytes(StandardCharsets.UTF_8));
        DERIVED.putIfAbsent(derived, id);
        return derived;
    }

    /** new AttributeModifier(name, amount, operation) of 1.20.6: a fresh random uuid, as 1.20.6 drew it */
    public static class_1322 newModifier(String name, double amount, class_1322.class_1323 operation) {
        return newModifier(class_3532.method_15378(class_5819.method_43053()), name, amount, operation);
    }

    /** new AttributeModifier(uuid, name, amount, operation) of 1.20.6 */
    public static class_1322 newModifier(UUID uuid, String name, double amount, class_1322.class_1323 operation) {
        class_1322 modifier = new class_1322(idOf(uuid), amount, operation);
        NAMES.put(modifier, name);
        return modifier;
    }

    /** 1.20.6 AttributeModifier.name(): the name it was built with; a modifier 1.21 code made has none, its id names it */
    public static String name(class_1322 modifier) {
        String name = NAMES.get(modifier);
        return name != null ? name : modifier.comp_2447().toString();
    }

    /** 1.20.6 AttributeInstance.hasModifier(modifier) asked for the modifier's id: the id of a 1.21 modifier */
    public static class_2960 idOfModifier(class_1322 modifier) {
        return modifier.comp_2447();
    }

    /** the pre-record id getter, mapped through the same data-fixer table as comp_2447 */
    public static UUID uuidOfModifier(class_1322 modifier) {
        return uuidOf(modifier.comp_2447());
    }

    public static double amount(class_1322 modifier) {
        return modifier.comp_2449();
    }

    public static class_1322.class_1323 operation(class_1322 modifier) {
        return modifier.comp_2450();
    }

    /**
     * The 1.20.6 hasModifier(AttributeModifier) a subclass overrides, as the override hook calls it: 1.21
     * asks hasModifier(id). The subclass's method is handed the instance's own modifier of that id -- what a
     * 1.20.6 caller holding the modifier passed -- and, when there is none, a modifier carrying the id alone
     * (1.20.6's own body read nothing but the id).
     */
    public interface LegacyHasModifier {
        boolean method_6196(class_1322 modifier);
    }

    /** hasModifier(id) of 1.21 on a 1.20.6 AttributeInstance subclass (see {@link LegacyHasModifier}) */
    public static boolean hasModifier(LegacyHasModifier self, class_2960 id) {
        class_1322 present = ((class_1324) self).method_6199(id);
        return self.method_6196(present != null ? present : new class_1322(id, 0.0, class_1322.class_1323.field_6328));
    }

    /** the 1.20.6 removeModifier(UUID) a subclass overrides: void, where 1.21's answers whether it removed one */
    public interface LegacyRemoveModifier {
        void method_6200(UUID uuid);
    }

    /**
     * removeModifier(id) of 1.21 on a 1.20.6 AttributeInstance subclass: the subclass's removeModifier(UUID)
     * runs, and the answer is 1.21's -- whether a modifier of that id was there before and is gone after.
     */
    public static boolean removeModifier(LegacyRemoveModifier self, class_2960 id) {
        class_1324 instance = (class_1324) self;
        boolean before = instance.method_6199(id) != null;
        self.method_6200(uuidOf(id));
        return before && instance.method_6199(id) == null;
    }

    private CbEntityAttributes() {
    }
}
