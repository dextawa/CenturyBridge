package top.dext.centurybridge.step.v1_21_4.rt;

import net.minecraft.class_1299;
import net.minecraft.class_1747;
import net.minecraft.class_1767;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2487;
import net.minecraft.class_2573;
import net.minecraft.class_2586;
import net.minecraft.class_2587;
import net.minecraft.class_2593;
import net.minecraft.class_2625;
import net.minecraft.class_2636;
import net.minecraft.class_3722;
import net.minecraft.class_5819;
import net.minecraft.class_7225;
import net.minecraft.class_7923;
import net.minecraft.class_8172;
import net.minecraft.class_8961;
import net.minecraft.class_8963;
import net.minecraft.class_8966;
import top.dext.centurybridge.step.v1_21_4.mixin.CbRestBannerAccess;
import top.dext.centurybridge.step.v1_21_4.mixin.CbRestBedAccess;
import top.dext.centurybridge.step.v1_21_4.mixin.CbRestStackAccess;
import top.dext.centurybridge.step.v1_21_4.mixin.CbRestTrialDataInvoker;

/**
 * 1.21.3 block-entity (and ItemStack) members gone in 1.21.4, with 1.21.3's bodies over 1.21.4's members:
 * BlockEntity.saveToItem and onlyOpCanSetNbt, DecoratedPotBlockEntity.setFromItem / getPotAsItem,
 * BannerBlockEntity.fromItem(stack, color), BedBlockEntity.setColor, TrialSpawnerData.setEntityId,
 * ItemStack.clearComponents.
 * <p>
 * A mod's call answers what 1.21.3's virtual call did: a 1.21.3 subclass's own override when it has one
 * (the interfaces below, given to such a class by adapterHooks), otherwise 1.21.3's body ({@code super*}),
 * which is also what the mod's super call runs. onlyOpCanSetNbt: 1.21.3's BlockItem.updateCustomBlockEntityTag
 * asked the block entity, 1.21.4 asks its type (the same answer for vanilla's); CbRestBlockEntityNbtMixin asks
 * a mod override there again.
 */
public final class CbRestBlockEntities {

    private CbRestBlockEntities() {
    }

    public interface SaveToItem { void method_38240(class_1799 stack, class_7225.class_7874 registries); }
    public interface OnlyOp { boolean method_11011(); }
    public interface PotSetFromItem { void method_49196(class_1799 stack); }
    public interface PotAsItem { class_1799 method_52578(); }
    public interface BannerFromItem { void method_10913(class_1799 stack, class_1767 color); }
    public interface BedSetColor { void method_11019(class_1767 color); }
    public interface TrialSetEntityId { void method_55189(class_8963 spawner, class_5819 random, class_1299<?> type); }

    // ---- ItemStack.clearComponents ----

    /** 1.21.3 ItemStack.clearComponents(): components.clearPatch() */
    public static void method_61656(class_1799 stack) {
        ((CbRestStackAccess) (Object) stack).cb$components().method_62714();
    }

    // ---- BlockEntity.saveToItem ----

    public static void method_38240(class_2586 be, class_1799 stack, class_7225.class_7874 registries) {
        if (be instanceof SaveToItem a) {
            a.method_38240(stack, registries);
        } else {
            superSaveToItem(be, stack, registries);
        }
    }

    /** 1.21.3's body: the custom data written into the stack's block-entity data, the stack's patch replaced by the block entity's components */
    public static void superSaveToItem(class_2586 be, class_1799 stack, class_7225.class_7874 registries) {
        class_2487 tag = be.method_58692(registries);
        be.method_57569(tag);
        method_61656(stack);
        class_1747.method_57338(stack, be.method_11017(), tag);
        stack.method_57365(be.method_57590());
    }

    // ---- BlockEntity.onlyOpCanSetNbt ----

    public static boolean method_11011(class_2586 be) {
        return be instanceof OnlyOp a ? a.method_11011() : be.method_11017().method_65166();
    }

    public static boolean method_11011(class_2593 be) {
        return method_11011((class_2586) be);
    }

    public static boolean method_11011(class_3722 be) {
        return method_11011((class_2586) be);
    }

    public static boolean method_11011(class_2625 be) {
        return method_11011((class_2586) be);
    }

    public static boolean method_11011(class_2636 be) {
        return method_11011((class_2586) be);
    }

    public static boolean method_11011(class_8961 be) {
        return method_11011((class_2586) be);
    }

    /** BlockEntity.onlyOpCanSetNbt()'s 1.21.3 body */
    public static boolean superOnlyOp(class_2586 be) {
        return false;
    }

    /** CommandBlockEntity / LecternBlockEntity / SignBlockEntity / SpawnerBlockEntity / TrialSpawnerBlockEntity: true in 1.21.3 */
    public static boolean superOnlyOp(class_2593 be) {
        return true;
    }

    public static boolean superOnlyOp(class_3722 be) {
        return true;
    }

    public static boolean superOnlyOp(class_2625 be) {
        return true;
    }

    public static boolean superOnlyOp(class_2636 be) {
        return true;
    }

    public static boolean superOnlyOp(class_8961 be) {
        return true;
    }

    // ---- DecoratedPotBlockEntity ----

    public static void method_49196(class_8172 be, class_1799 stack) {
        if ((Object) be instanceof PotSetFromItem a) {
            a.method_49196(stack);
        } else {
            superSetFromItem(be, stack);
        }
    }

    /** 1.21.3's body: applyComponentsFromItemStack(stack) */
    public static void superSetFromItem(class_8172 be, class_1799 stack) {
        be.method_58683(stack);
    }

    public static class_1799 method_52578(class_8172 be) {
        return (Object) be instanceof PotAsItem a ? a.method_52578() : superGetPotAsItem(be);
    }

    /** 1.21.3's body: a decorated pot stack carrying every component of the block entity */
    public static class_1799 superGetPotAsItem(class_8172 be) {
        class_1799 stack = class_1802.field_42699.method_7854();
        stack.method_57365(be.method_57590());
        return stack;
    }

    // ---- BannerBlockEntity.fromItem / BedBlockEntity.setColor ----

    public static void method_10913(class_2573 be, class_1799 stack, class_1767 color) {
        if ((Object) be instanceof BannerFromItem a) {
            a.method_10913(stack, color);
        } else {
            superFromItem(be, stack, color);
        }
    }

    /** 1.21.3's body: the base color, then applyComponentsFromItemStack(stack) */
    public static void superFromItem(class_2573 be, class_1799 stack, class_1767 color) {
        ((CbRestBannerAccess) be).cb$setBaseColor(color);
        be.method_58683(stack);
    }

    public static void method_11019(class_2587 be, class_1767 color) {
        if ((Object) be instanceof BedSetColor a) {
            a.method_11019(color);
        } else {
            superSetColor(be, color);
        }
    }

    /** 1.21.3's body: the color field */
    public static void superSetColor(class_2587 be, class_1767 color) {
        ((CbRestBedAccess) be).cb$setColor(color);
    }

    // ---- TrialSpawnerData.setEntityId ----

    public static void method_55189(class_8966 data, class_8963 spawner, class_5819 random, class_1299<?> type) {
        if ((Object) data instanceof TrialSetEntityId a) {
            a.method_55189(spawner, random, type);
        } else {
            superSetEntityId(data, spawner, random, type);
        }
    }

    /** 1.21.3's body: the next spawn data's entity id */
    public static void superSetEntityId(class_8966 data, class_8963 spawner, class_5819 random, class_1299<?> type) {
        ((CbRestTrialDataInvoker) data).cb$getOrCreateNextSpawnData(spawner, random).method_38093()
            .method_10582("id", class_7923.field_41177.method_10221(type).toString());
    }
}
