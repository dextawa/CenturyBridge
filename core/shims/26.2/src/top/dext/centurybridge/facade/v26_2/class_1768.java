package top.dext.centurybridge.facade.v26_2;

/** Facade for 1.20.1 net/minecraft/class_1768 (DyeableLeatherItem), removed on
 *  26.2 (dyeing is a data component). Legacy dyeable items implement it and read
 *  getColor; default to the vanilla undyed leather color so they load and query.
 *  Member names are intermediary tokens. */
public interface class_1768 {
    /** getColor(stack) (method_7800) */
    default int method_7800(net.minecraft.world.item.ItemStack stack) {
        return 0xA06540;
    }
}
