package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 AbstractFurnaceScreen(menu, recipeBook, inventory, title, texture): one sheet held the background
 * and, at (176, 0) and (176, 14), the flame and the arrow drawBackground cut from it. 1.20.2's constructor
 * also takes the two progress sprites and draws them from the GUI atlas. A 1.20.1 subclass's super call is
 * reshaped to pass {@link #LIT} and {@link #BURN} -- markers, not sprites -- and CbGuiFurnaceScreenBridge
 * draws a marked progress the 1.20.1 way: from the screen's own sheet, at 1.20.1's sizes (the menu's
 * integer progress, computed as 1.20.1's menu computed it). A mod sheet with its own flame keeps it.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiFurnace {

    public static final net.minecraft.class_2960 LIT = new net.minecraft.class_2960("centurybridge", "legacy_sheet/furnace_lit_progress");
    public static final net.minecraft.class_2960 BURN = new net.minecraft.class_2960("centurybridge", "legacy_sheet/furnace_burn_progress");

    /** superCtorReshapes push: the lit-progress marker */
    public static net.minecraft.class_2960 cbLegacyLit() {
        return LIT;
    }

    /** superCtorReshapes push: the burn-progress marker */
    public static net.minecraft.class_2960 cbLegacyBurn() {
        return BURN;
    }

    /** 1.20.1 AbstractFurnaceMenu.getLitProgress(): fuel left * 13 / fuel total (200 when unknown) */
    public static int cbLitProgress(net.minecraft.class_1720 menu) {
        net.minecraft.class_3913 data = ((top.dext.centurybridge.shims.v1_20_2.CbGuiFurnaceMenuAccessor) menu).cb$data();
        int total = data.method_17390(1);
        if (total == 0) {
            total = 200;
        }
        return data.method_17390(0) * 13 / total;
    }

    /** 1.20.1 AbstractFurnaceMenu.getBurnProgress(): cook time * 24 / cook total, 0 while either is 0 */
    public static int cbBurnProgress(net.minecraft.class_1720 menu) {
        net.minecraft.class_3913 data = ((top.dext.centurybridge.shims.v1_20_2.CbGuiFurnaceMenuAccessor) menu).cb$data();
        int done = data.method_17390(2);
        int total = data.method_17390(3);
        return total != 0 && done != 0 ? done * 24 / total : 0;
    }

    private CbGuiFurnace() {
    }
}
