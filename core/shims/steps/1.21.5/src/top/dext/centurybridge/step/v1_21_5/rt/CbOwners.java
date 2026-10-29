package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.class_10583;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1321;
import net.minecraft.class_1496;
import net.minecraft.class_1924;
import net.minecraft.class_6025;

/**
 * 1.21.4's owner members of OwnableEntity / TamableAnimal on 1.21.5, which keeps the owner as an EntityReference
 * (class_10583: the owner's UUID, resolved against the level; javap 1.21.5 class_6025 / class_1321: getOwnerReference
 * method_66287, setOwnerReference method_66288 storing Optional.ofNullable(ref)).
 */
public final class CbOwners {
    private CbOwners() {
    }

    /** OwnableEntity.getOwnerUUID(): the reference's UUID, null without an owner */
    public static UUID ownerUuid(class_6025 owned) {
        class_10583<class_1309> ref = owned.method_66287();
        return ref == null ? null : ref.method_66263();
    }

    /** TamableAnimal.getOwnerUUID() */
    public static UUID ownerUuidTamable(class_1321 animal) {
        return ownerUuid(animal);
    }

    /** TamableAnimal.setOwnerUUID(uuid): a reference to that UUID, none for null (1.21.4 stored Optional.ofNullable(uuid)) */
    public static void setOwnerUuid(class_1321 animal, UUID uuid) {
        animal.method_66288(reference(uuid));
    }

    /** OwnableEntity.level(): the entity's level (every 1.21.4 implementer was an Entity, whose level() answered it) */
    public static class_1924 level(class_6025 owned) {
        return owned instanceof class_1297 e ? e.method_37908() : null;
    }

    /**
     * a converted AbstractHorse subclass's super.getOwnerUUID(): 1.21.4's body, the owner field (1.21.5 class_1496.field_55705
     * holds the reference)
     */
    public static UUID ownerUuidHorse(class_1496 horse) {
        class_10583<class_1309> ref = horse.field_55705;
        return ref == null ? null : ref.method_66263();
    }

    /**
     * AbstractHorse.setOwnerUUID(uuid), a call or a converted subclass's super call: 1.21.4's body, the UUID into the owner
     * field (1.21.5 keeps a reference to it there). 1.21.5 has no virtual owner setter taking a UUID, so a call typed as a
     * vanilla horse does not enter a converted subclass's override (one naming the subclass does)
     */
    public static void setOwnerUuidHorse(class_1496 horse, UUID uuid) {
        horse.field_55705 = reference(uuid);
    }

    /** the reference a 1.21.4 override's owner UUID stands for (1.21.5 getOwnerReference), none for null */
    public static class_10583<class_1309> reference(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        // EntityReference's codec decodes a UUID to new EntityReference(uuid) (javap -c 1.21.5 class_10583 <clinit>:
        // UUIDUtil.CODEC.xmap). The constructor itself goes private at 1.21.11 (EntityReference.of there); the codec and
        // UUIDUtil.CODEC stay through 26.2, so the climbed layer keeps linking
        com.mojang.serialization.Codec<class_10583<class_1309>> codec = class_10583.method_66253();
        return codec.parse(com.mojang.serialization.JsonOps.INSTANCE,
            net.minecraft.class_4844.field_25122.encodeStart(com.mojang.serialization.JsonOps.INSTANCE, uuid).getOrThrow()).getOrThrow();
    }

    /** the owner UUID 1.21.4's setOwnerUUID took for a 1.21.5 owner reference, null for none */
    public static UUID uuidOf(class_10583<class_1309> ref) {
        return ref == null ? null : ref.method_66263();
    }

    /** the owner UUID 1.21.4's AbstractHorse.setOwnerUUID took where 1.21.5 sets the owner entity, null for none */
    public static UUID uuidOfEntity(class_1309 owner) {
        return owner == null ? null : owner.method_5667();
    }

    /**
     * a converted TamableAnimal subclass's super.getOwnerUUID(): 1.21.4's body, the owner in the synched data (1.21.5
     * keeps the reference there: TamableAnimal.DATA_OWNERUUID_ID, field_6320, the layer's access widener), read without
     * a virtual call so the subclass's own override is not entered again
     */
    public static UUID ownerUuidTamableSuper(class_1321 animal) {
        Optional<class_10583<class_1309>> ref = animal.method_5841().method_12789(class_1321.field_6320);
        return ref.map(class_10583::method_66263).orElse(null);
    }

    /** a converted TamableAnimal subclass's super.setOwnerUUID(uuid): 1.21.4's body, Optional.ofNullable into the synched data */
    public static void setOwnerUuidTamableSuper(class_1321 animal, UUID uuid) {
        animal.method_5841().method_12778(class_1321.field_6320, Optional.ofNullable(reference(uuid)));
    }

    /** AbstractHorse.getOwnerUUID() called on a horse: through 1.21.5's getOwnerReference, which a converted override answers */
    public static UUID ownerUuidHorseCall(class_1496 horse) {
        return uuidOf(horse.method_66287());
    }
}
