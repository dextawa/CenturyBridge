package top.dext.centurybridge.step.v1_20_5.facade;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockBanner;

/**
 * 1.20.4 BannerPattern.Builder (net/minecraft/class_2582$class_3750), which 1.20.5 replaced with
 * BannerPatternLayers.Builder: patterns and colors gathered into 1.20.4's NBT list of
 * {Pattern: hash name, Color: dye id}. 1.20.4's bodies, on 1.20.4's patterns (class_2582 here).
 */
public class class_2582$class_3750 {

    /** patterns */
    public final List<Pair<net.minecraft.class_6880<class_2582>, net.minecraft.class_1767>> field_16548 = new java.util.ArrayList<>();

    public class_2582$class_3750() {
    }

    /** addPattern(key, color): the pattern registered under the key */
    public class_2582$class_3750 method_43720(net.minecraft.class_5321<class_2582> key, net.minecraft.class_1767 color) {
        return this.method_16376(CbBlockBanner.registry().method_40290(key), color);
    }

    /** addPattern(holder, color) */
    public class_2582$class_3750 method_16376(net.minecraft.class_6880<class_2582> pattern, net.minecraft.class_1767 color) {
        return this.method_31653(Pair.of(pattern, color));
    }

    /** addPattern(pair) */
    public class_2582$class_3750 method_31653(Pair<net.minecraft.class_6880<class_2582>, net.minecraft.class_1767> pair) {
        this.field_16548.add(pair);
        return this;
    }

    /** toListTag(): {Pattern: the pattern's hash name, Color: the dye's id} for each, in order */
    public net.minecraft.class_2499 method_16375() {
        net.minecraft.class_2499 list = new net.minecraft.class_2499();
        for (Pair<net.minecraft.class_6880<class_2582>, net.minecraft.class_1767> pair : this.field_16548) {
            net.minecraft.class_2487 tag = new net.minecraft.class_2487();
            tag.method_10582("Pattern", pair.getFirst().comp_349().field_11824);
            tag.method_10569("Color", pair.getSecond().method_7789());
            list.add(tag);
        }
        return list;
    }
}
