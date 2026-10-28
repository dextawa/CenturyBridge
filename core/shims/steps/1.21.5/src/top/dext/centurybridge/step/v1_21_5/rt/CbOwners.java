package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.UUID;
import net.minecraft.class_10583;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1321;
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
        animal.method_66288(uuid == null ? null : new class_10583<>(uuid));
    }

    /** OwnableEntity.level(): the entity's level (every 1.21.4 implementer was an Entity, whose level() answered it) */
    public static class_1924 level(class_6025 owned) {
        return owned instanceof class_1297 e ? e.method_37908() : null;
    }
}
