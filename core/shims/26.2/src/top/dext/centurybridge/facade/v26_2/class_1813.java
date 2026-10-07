package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvent;

/**
 * Facade for 1.20.1 net/minecraft/class_1813 (MusicDiscItem), deleted on 26.2
 * (music discs are a data component / JukeboxSong, no item subclass). 14 corpus
 * disc items extend it. Reached via superRenames; extends the real 26.2 Item so
 * the subclass registers as a genuine item. The old (comparatorOutput, sound,
 * Properties, lengthTicks) constructor forwards to Item(Properties), keeping the
 * sound for the legacy getters. Member names are intermediary tokens.
 */
public class class_1813 extends Item {

    private final SoundEvent field_sound;

    protected class_1813(int comparatorOutput, SoundEvent sound, Item.Properties props, int lengthTicks) {
        super(props);
        this.field_sound = sound;
    }

    /** getAnalogOutput() */
    public int method_8010() {
        return 15;
    }

    /** getSound() */
    public SoundEvent method_8009() {
        return field_sound;
    }

    /** getLengthInTicks()-ish (method_44369) */
    public int method_44369() {
        return 0;
    }

    /** getDisplayName() (method_8011) -> empty component */
    public net.minecraft.network.chat.MutableComponent method_8011() {
        return net.minecraft.network.chat.Component.empty();
    }

    /** useOn(context) -> pass (jukebox interaction is data-driven on 26.2) */
    public net.minecraft.world.InteractionResult method_7884(net.minecraft.world.item.context.UseOnContext ctx) {
        return net.minecraft.world.InteractionResult.PASS;
    }

    /** appendHoverText(stack, level, tooltip, flag) -> no extra tooltip */
    public void method_7851(ItemStack stack, net.minecraft.world.level.Level level,
            java.util.List tooltip, net.minecraft.world.item.TooltipFlag flag) {
    }
}
