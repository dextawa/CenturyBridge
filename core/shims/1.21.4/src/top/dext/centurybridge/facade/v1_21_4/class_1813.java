package top.dext.centurybridge.facade.v1_21_4;

/**
 * Facade for 1.20.1 net/minecraft/class_1813 (MusicDiscItem), deleted by 1.21
 * (music discs became a JUKEBOX_PLAYABLE data component, no item subclass).
 * Corpus disc items both {@code new class_1813(...)} and {@code extends} it, so
 * class_1813 is wired as a classRename (rewrites every ref incl. NEW and super).
 * Extends the real 1.21.4 Item so the disc registers and is held/given as a
 * genuine item; the old (comparatorOutput, sound, Properties, lengthTicks) ctor
 * and the getters are preserved off stored fields.
 *
 * Playback note: 1.21.4 drives jukebox playback off a JUKEBOX_PLAYABLE component
 * referencing a *registered* JukeboxSong, which a legacy disc has no registered
 * holder for; rather than fabricate a half-wired component (1.21.4's
 * JukeboxPlayable takes an internal SongOrKey + BlockPos, not a direct Holder),
 * the disc registers faithfully and its own sound/length stay queryable, with
 * actual jukebox playback a ledgered degrade. Member names are intermediary tokens.
 */
public class class_1813 extends net.minecraft.class_1792 {

    private final net.minecraft.class_3414 field_sound;
    private final int field_lengthTicks;
    private final int field_comparator;

    protected class_1813(int comparatorOutput, net.minecraft.class_3414 sound,
            net.minecraft.class_1792.class_1793 props, int lengthTicks) {
        super(props);
        this.field_sound = sound;
        this.field_lengthTicks = lengthTicks;
        this.field_comparator = comparatorOutput;
    }

    /** getAnalogOutput() */
    public int method_8010() {
        return field_comparator;
    }

    /** getSound() */
    public net.minecraft.class_3414 method_8009() {
        return field_sound;
    }

    /** getLengthInTicks() */
    public int method_44369() {
        return field_lengthTicks;
    }

    /** getDescription() -> empty */
    public net.minecraft.class_5250 method_8011() {
        return net.minecraft.class_2561.method_43473();
    }
}
