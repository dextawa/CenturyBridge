package top.dext.centurybridge.facade.v1_21_4;

/**
 * Facade for 1.20.1 net/minecraft/class_2342 (BlockSource / the position source a
 * DispenserBehavior reads), an INTERFACE mods implement to drive custom dispenser
 * behaviour (create's ContraptionBlockSource). On 1.20.2 it became a final record,
 * so a legacy `implements class_2342` fails to load ("implements a class"). This
 * facade re-supplies it as an interface (extending the still-interface class_2374
 * Position) via superRenames, so the mod's implementing class loads and keeps
 * supplying its own coordinates/level/block accessors. The four own methods beyond
 * Position are re-declared here; member names are intermediary tokens.
 */
public interface class_2342 extends net.minecraft.class_2374 {

    /** getBlockPos() */
    net.minecraft.class_2338 method_10122();

    /** getBlockState() */
    net.minecraft.class_2680 method_10120();

    /** getBlockEntity() */
    <T extends net.minecraft.class_2586> T method_10121();

    /** getLevel() */
    net.minecraft.class_3218 method_10207();
}
