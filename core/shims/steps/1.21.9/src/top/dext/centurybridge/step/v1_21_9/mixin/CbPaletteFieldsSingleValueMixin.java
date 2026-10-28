package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_2359;
import net.minecraft.class_2835;
import net.minecraft.class_6564;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbPalettes;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * SingleValuePalette's registry and resize handler, the two fields 1.21.8's SingleValuePalette kept (javap -p 1.21.8 class_6564: field_34573 / field_34575,
 * set by its constructor) and 1.21.9's dropped. They keep 1.21.8's names (a 1.21.8 accessor of either reaches them)
 * and are filled where the palette is made with them known (rt/CbPalettes).
 */
@Mixin(value = class_6564.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbPaletteFieldsSingleValueMixin implements CbPalettes.Legacy {

    /** SingleValuePalette.registry */
    private class_2359<?> field_34573;

    /** SingleValuePalette.resizeHandler */
    private class_2835<?> field_34575;

    @Override
    public class_2359<?> cb$registry() {
        return this.field_34573;
    }

    @Override
    public class_2835<?> cb$resize() {
        return this.field_34575;
    }

    @Override
    public void cb$legacy(class_2359<?> registry, class_2835<?> resize) {
        this.field_34573 = registry;
        this.field_34575 = resize;
    }
}
