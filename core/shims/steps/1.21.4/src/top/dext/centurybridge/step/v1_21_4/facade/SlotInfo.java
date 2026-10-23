package top.dext.centurybridge.step.v1_21_4.facade;

/**
 * 1.21.3's PlacementInfo.SlotInfo (net/minecraft/class_9887$class_9888, classRenames): the ingredient index a
 * recipe slot takes (placerOutputPosition, comp_2937). 1.21.4 keeps the same indices as an IntList
 * (PlacementInfo.slotsToIngredientIndex, -1 for an empty slot); CbRestRecipes.slotInfo rebuilds 1.21.3's list.
 * A record's members with 1.21.3's names, all public.
 */
public final class SlotInfo {
    public final int comp_2937;

    public SlotInfo(int placerOutputPosition) {
        this.comp_2937 = placerOutputPosition;
    }

    public int comp_2937() {
        return this.comp_2937;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SlotInfo other && this.comp_2937 == other.comp_2937;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.comp_2937);
    }

    @Override
    public String toString() {
        return "SlotInfo[placerOutputPosition=" + this.comp_2937 + "]";
    }
}
