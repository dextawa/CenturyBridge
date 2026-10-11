package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;

/**
 * Facade for 1.20.1 net/minecraft/class_1813 (MusicDiscItem), deleted on 26.2
 * (music discs are a data component / JukeboxSong, no item subclass). Corpus
 * disc items both {@code new class_1813(...)} directly and {@code extends} it,
 * so class_1813 is wired as a classRename (rewrites every ref incl. NEW and
 * super), not superRenames (which misses direct instantiation). Extends the
 * real 26.2 Item so the subclass/instance registers as a genuine item.
 *
 * Faithful playback: 1.20.1 discs carried their sound + length on the item and
 * the jukebox read them off the item class. 26.2 drives playback off a
 * JUKEBOX_PLAYABLE data component holding a JukeboxSong. The old
 * (comparatorOutput, sound, Properties, lengthTicks) constructor rebuilds that
 * component from a direct (unregistered) Holder<JukeboxSong> and attaches it to
 * the Properties before super(Properties), so a legacy disc actually plays when
 * inserted into a 26.2 jukebox. Member names are intermediary tokens.
 */
public class class_1813 extends Item {

    private final SoundEvent field_sound;
    private final int field_lengthTicks;
    private final int field_comparator;

    public class_1813(int comparatorOutput, SoundEvent sound, Item.Properties props, int lengthTicks) {
        super(withJukebox(props, comparatorOutput, sound, lengthTicks));
        this.field_sound = sound;
        this.field_lengthTicks = lengthTicks;
        this.field_comparator = comparatorOutput;
    }

    private static Item.Properties withJukebox(Item.Properties props, int comparatorOutput,
            SoundEvent sound, int lengthTicks) {
        try {
            JukeboxSong song = new JukeboxSong(
                Holder.direct(sound), Component.empty(),
                lengthTicks / 20.0f, comparatorOutput);
            JukeboxPlayable playable = new JukeboxPlayable(Holder.direct(song));
            return props.component(DataComponents.JUKEBOX_PLAYABLE, playable);
        } catch (Throwable t) {
            // never let a disc fail to construct over a component edge case
            return props;
        }
    }

    /** getAnalogOutput() */
    public int method_8010() {
        return field_comparator;
    }

    /** getSound() */
    public SoundEvent method_8009() {
        return field_sound;
    }

    /** getLengthInTicks()-ish (method_44369) */
    public int method_44369() {
        return field_lengthTicks;
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
