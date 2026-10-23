package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.class_1792;
import net.minecraft.class_1856;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_9887;
import top.dext.centurybridge.step.v1_21_4.facade.IngredientInfo;
import top.dext.centurybridge.step.v1_21_4.facade.SlotInfo;

/**
 * 1.21.3's recipe placement contents on 1.21.4, with 1.21.3's bodies:
 * Ingredient.items() (a list; 1.21.4 answers a stream), StackedItemContents.convertIngredientContents (the
 * holders sorted by item id), PlacementInfo.ingredientToContents / unpackedIngredients / slotInfo (rebuilt
 * from 1.21.4's ingredients and slot index list, -1 for an empty slot). {@link #infos} hands 1.21.4's
 * ingredient infos (an Ingredient) to a 1.21.3 override as 1.21.3's records.
 */
public final class CbRestRecipes {

    private CbRestRecipes() {
    }

    /** Ingredient.items() of 1.21.3: the holders as a list */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List listOf(Stream stream) {
        return stream == null ? null : stream.toList();
    }

    /** StackedItemContents.convertIngredientContents(holders) */
    public static IngredientInfo<class_6880<class_1792>> method_61539(Stream<class_6880<class_1792>> holders) {
        return new IngredientInfo<>(holders.sorted(Comparator.comparingInt(h -> class_7923.field_41178.method_10206(h.comp_349()))).toList());
    }

    /** PlacementInfo.ingredientToContents(ingredient) */
    public static IngredientInfo<class_6880<class_1792>> method_64674(class_1856 ingredient) {
        return method_61539(ingredient.method_8105());
    }

    /** PlacementInfo.unpackedIngredients() */
    public static List<IngredientInfo<class_6880<class_1792>>> method_64676(class_9887 info) {
        List<IngredientInfo<class_6880<class_1792>>> out = new ArrayList<>();
        for (class_1856 ingredient : info.method_64675()) {
            out.add(method_64674(ingredient));
        }
        return List.copyOf(out);
    }

    /** PlacementInfo.slotInfo() */
    public static List<Optional<SlotInfo>> method_61681(class_9887 info) {
        List<Optional<SlotInfo>> out = new ArrayList<>();
        for (int i : info.method_65800()) {
            out.add(i >= 0 ? Optional.of(new SlotInfo(i)) : Optional.empty());
        }
        return List.copyOf(out);
    }

    /** 1.21.4's ingredient infos as 1.21.3's records (adaptive): an Ingredient becomes its contents, a record stays */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List infos(List list) {
        if (list == null) {
            return null;
        }
        List out = new ArrayList(list.size());
        for (Object o : list) {
            out.add(o instanceof class_1856 ingredient ? method_64674(ingredient) : o);
        }
        return out;
    }

}
