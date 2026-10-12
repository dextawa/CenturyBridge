package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.Objects;

/**
 * Item methods 1.20.4 handed the stack's tag, which 1.20.5 hands the stack (verifyTagAfterLoad became
 * verifyComponentsAfterLoad; SpawnEggItem.getType / spawnsEntity take the stack): a 1.20.4 override is
 * reached through an override hook whose bridge passes the stack's 1.20.4 tag (CbItemTags) -- the tag
 * 1.20.4 passed, and like it null for a stack without one; a 1.20.4 call with a tag does what that
 * class's 1.20.4 method did with it. Bodies read off 1.20.4's bytecode.
 */
public final class CbItemTagHooks {

    /** a mod class declaring 1.20.4's Item.verifyTagAfterLoad(CompoundTag) */
    public interface VerifyTag {
        void method_7860(net.minecraft.class_2487 tag);
    }

    /** a mod class declaring 1.20.4's SpawnEggItem.getType(CompoundTag) */
    public interface EggType {
        net.minecraft.class_1299<?> method_8015(net.minecraft.class_2487 tag);
    }

    /** a mod class declaring 1.20.4's SpawnEggItem.spawnsEntity(CompoundTag, EntityType) */
    public interface EggSpawns {
        boolean method_8018(net.minecraft.class_2487 tag, net.minecraft.class_1299<?> type);
    }

    // ---------------------------------------------------------------- verifyTagAfterLoad

    /**
     * verifyComponentsAfterLoad(stack) of a mod class declaring 1.20.4's verifyTagAfterLoad (the override
     * hook's bridge): 1.20.4 ran it on a loaded stack's tag, when the stack had one.
     */
    public static void verifyAfterLoad(VerifyTag item, net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 tag = CbItemTags.getTag(stack);
        if (tag != null) {
            item.method_7860(tag);
        }
    }

    /** item.verifyTagAfterLoad(tag): the class's own override, else the 1.20.4 body of the vanilla class it is */
    public static void verifyTagAfterLoad(net.minecraft.class_1792 item, net.minecraft.class_2487 tag) {
        if (item instanceof VerifyTag v) {
            v.method_7860(tag);
        } else if (item instanceof net.minecraft.class_1809 head) {
            superVerifyTagAfterLoad(head, tag);
        }
        // any other vanilla item: 1.20.4's Item.verifyTagAfterLoad had an empty body
    }

    /** super.verifyTagAfterLoad(tag) in a mod subclass of Item: 1.20.4's body was empty */
    public static void superVerifyTagAfterLoad(net.minecraft.class_1792 item, net.minecraft.class_2487 tag) {
    }

    /**
     * ... of PlayerHeadItem, 1.20.4's body (SkullBlockEntity.resolveGameProfile): a "SkullOwner" name gets
     * its profile looked up, and written in its place when it arrives (a bare name profile if none).
     */
    public static void superVerifyTagAfterLoad(net.minecraft.class_1809 item, net.minecraft.class_2487 tag) {
        String name = tag.method_10558("SkullOwner");
        if (name != null && !name.isEmpty()) {
            net.minecraft.class_2631.method_52580(name).thenAccept(profile -> tag.method_10566("SkullOwner",
                CbProfiles.writeGameProfile(new net.minecraft.class_2487(),
                    profile.orElse(new com.mojang.authlib.GameProfile(net.minecraft.class_156.field_25140, name)))));
        }
    }

    // ---------------------------------------------------------------- spawn eggs

    /** egg.getType(tag): the class's own override, else 1.20.4's body */
    public static net.minecraft.class_1299<?> eggType(net.minecraft.class_1826 egg, net.minecraft.class_2487 tag) {
        return egg instanceof EggType t ? t.method_8015(tag) : superEggType(egg, tag);
    }

    /** SpawnEggItem.getType(tag)'s 1.20.4 body: EntityTag's id when it names a type, else the egg's default */
    public static net.minecraft.class_1299<?> superEggType(net.minecraft.class_1826 egg, net.minecraft.class_2487 tag) {
        net.minecraft.class_1299<?> dflt = ((top.dext.centurybridge.step.v1_20_5.mixin.CbSpawnEggAccessor) egg).cbS4$defaultType();
        if (tag != null && tag.method_10573("EntityTag", 10)) {
            net.minecraft.class_2487 entity = tag.method_10562("EntityTag");
            if (entity.method_10573("id", 8)) {
                return net.minecraft.class_1299.method_5898(entity.method_10558("id")).orElse(dflt);
            }
        }
        return dflt;
    }

    /** egg.spawnsEntity(tag, type): the class's own override, else 1.20.4's body */
    public static boolean eggSpawns(net.minecraft.class_1826 egg, net.minecraft.class_2487 tag, net.minecraft.class_1299<?> type) {
        return egg instanceof EggSpawns s ? s.method_8018(tag, type) : superEggSpawns(egg, tag, type);
    }

    /** SpawnEggItem.spawnsEntity(tag, type)'s 1.20.4 body: getType(tag) -- the egg's own where it overrides it -- is the type */
    public static boolean superEggSpawns(net.minecraft.class_1826 egg, net.minecraft.class_2487 tag, net.minecraft.class_1299<?> type) {
        return Objects.equals(eggType(egg, tag), type);
    }

    /** 1.20.5's getType(stack) of a mod class declaring 1.20.4's getType(tag) (the override hook's bridge) */
    public static net.minecraft.class_1299<?> eggTypeOf(EggType egg, net.minecraft.class_1799 stack) {
        return egg.method_8015(CbItemTags.getTag(stack));
    }

    /** 1.20.5's spawnsEntity(stack, type) of a mod class declaring 1.20.4's spawnsEntity(tag, type) */
    public static boolean eggSpawnsOf(EggSpawns egg, net.minecraft.class_1799 stack, net.minecraft.class_1299<?> type) {
        return egg.method_8018(CbItemTags.getTag(stack), type);
    }

    private CbItemTagHooks() {
    }
}
