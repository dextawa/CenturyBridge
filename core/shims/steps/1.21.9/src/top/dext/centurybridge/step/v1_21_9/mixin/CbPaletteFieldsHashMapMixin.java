package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_2359;
import net.minecraft.class_2835;
import net.minecraft.class_2814;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbPalettes;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * HashMapPalette's registry and resize handler, the two fields 1.21.8's HashMapPalette kept (javap -p 1.21.8 class_2814: field_12821 / field_12825,
 * set by its constructor) and 1.21.9's dropped. They keep 1.21.8's names (a 1.21.8 accessor of either reaches them)
 * and are filled where the palette is made with them known (rt/CbPalettes).
 */
@Mixin(value = class_2814.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbPaletteFieldsHashMapMixin implements CbPalettes.Legacy {

    /** HashMapPalette.registry */
    private class_2359<?> field_12821;

    /** HashMapPalette.resizeHandler */
    private class_2835<?> field_12825;

    @Override
    public class_2359<?> cb$registry() {
        return this.field_12821;
    }

    @Override
    public class_2835<?> cb$resize() {
        return this.field_12825;
    }

    @Override
    public void cb$legacy(class_2359<?> registry, class_2835<?> resize) {
        this.field_12821 = registry;
        this.field_12825 = resize;
    }
}
