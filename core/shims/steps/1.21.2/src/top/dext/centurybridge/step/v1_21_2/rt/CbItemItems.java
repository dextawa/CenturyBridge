package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7923;

/**
 * 1.21.1's Items.registerBlock / registerItem helpers taking a built item (1.21.2's take a factory and Properties,
 * building the item with its key). 1.21.1's bodies: a block item first enters Item.BY_BLOCK for its blocks, then the
 * item is registered under the block's id / the given id or key. An item built before it had a key got a
 * placeholder from CbPendingIds, which the registration backfills.
 */
public final class CbItemItems {

    /** registerBlock(blockItem): under its block's id */
    public static class_1792 method_7993(class_1747 item) {
        return method_7992(item.method_7711(), item);
    }

    /** registerBlock(block, item): under the block's id */
    public static class_1792 method_7992(class_2248 block, class_1792 item) {
        return method_7990(class_7923.field_41175.method_10221(block), item);
    }

    /** registerItem(name, item): under minecraft:name */
    public static class_1792 method_7991(String name, class_1792 item) {
        return method_7990(class_2960.method_60656(name), item);
    }

    /** registerItem(id, item) */
    public static class_1792 method_7990(class_2960 id, class_1792 item) {
        return method_51348(class_5321.method_29179(net.minecraft.class_7924.field_41197, id), item);
    }

    /** registerItem(key, item) */
    public static class_1792 method_51348(class_5321<class_1792> key, class_1792 item) {
        if (item instanceof class_1747 bi) {
            bi.method_7713(class_1792.field_8003, item);
        }
        return class_2378.method_39197(class_7923.field_41178, key, item);
    }

    private CbItemItems() {
    }
}
