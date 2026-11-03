package top.dext.centurybridge.step.v1_21_5.facade;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.4 ModelResourceLocation (class_1091, gone in 1.21.5; javap -c -p 1.21.4): record (id, variant), the variant
 * lower-cased (Locale.ROOT, method_45911) by the constructor, getVariant (method_4740), toString "id#variant", record
 * equality over both components. A final class with public members (a facade's members must be public: access wideners
 * never reach a CenturyBridge class). 1.21.5 keys block models by BlockState; rt/CbModelLocations answers
 * ModelManager.getModel with one.
 */
@Environment(EnvType.CLIENT)
public final class ModelResourceLocation {
    /** id */
    public final net.minecraft.class_2960 comp_2875;
    /** variant */
    public final String comp_2876;

    public ModelResourceLocation(net.minecraft.class_2960 id, String variant) {
        this.comp_2875 = id;
        this.comp_2876 = method_45911(variant);
    }

    /** lowercaseVariant */
    public static String method_45911(String variant) {
        return variant.toLowerCase(java.util.Locale.ROOT);
    }

    public net.minecraft.class_2960 comp_2875() {
        return this.comp_2875;
    }

    public String comp_2876() {
        return this.comp_2876;
    }

    /** getVariant */
    public String method_4740() {
        return this.comp_2876;
    }

    @Override
    public String toString() {
        return this.comp_2875 + "#" + this.comp_2876;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ModelResourceLocation m && Objects.equals(this.comp_2875, m.comp_2875)
            && Objects.equals(this.comp_2876, m.comp_2876);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.comp_2875) + Objects.hashCode(this.comp_2876);
    }
}
