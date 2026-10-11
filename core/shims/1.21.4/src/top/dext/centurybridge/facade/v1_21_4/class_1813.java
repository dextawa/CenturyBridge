package top.dext.centurybridge.facade.v1_21_4;

/**
 * Facade for 1.20.1 net/minecraft/class_1813 (MusicDiscItem), deleted by 1.21
 * (music discs became a JUKEBOX_PLAYABLE data component, no item subclass).
 * Corpus disc items both {@code new class_1813(...)} and {@code extends} it, so
 * class_1813 is wired as a classRename (rewrites every ref incl. NEW and super).
 * Extends the real 1.21.4 Item so the disc registers and is held/given as a
 * genuine item. What the 1.20.1 class kept is kept the same way: the sound, the
 * comparator output, the length (the constructor takes seconds and stores ticks),
 * and the sound -> disc table bySound reads (mods with an access widener fill it
 * themselves, as Architectury's record item does).
 *
 * Not carried: playback. A 1.21.4 jukebox plays what a JUKEBOX_PLAYABLE component
 * names, and the client resolves the song through the synced jukebox_song registry
 * by raw id, so a song that is not registered there never sounds on a client; a
 * legacy disc gets no component and the jukebox does not take it.
 */
public class class_1813 extends net.minecraft.class_1792 {

    /** MUSIC_DISCS */
    public static final java.util.Map<net.minecraft.class_3414, class_1813> field_8901 = new java.util.HashMap<>();
    /** comparatorOutput */
    public final int field_8902;
    /** sound */
    public final net.minecraft.class_3414 field_8900;
    /** lengthInTicks */
    public final int field_39480;

    public class_1813(int comparatorOutput, net.minecraft.class_3414 sound,
            net.minecraft.class_1792.class_1793 props, int lengthInSeconds) {
        super(props);
        this.field_8902 = comparatorOutput;
        this.field_8900 = sound;
        this.field_39480 = lengthInSeconds * 20;
        field_8901.put(this.field_8900, this);
    }

    /** getComparatorOutput() */
    public int method_8010() {
        return field_8902;
    }

    /** getSound() */
    public net.minecraft.class_3414 method_8009() {
        return field_8900;
    }

    /** getLengthInTicks() */
    public int method_44369() {
        return field_39480;
    }

    /** getDescription() */
    public net.minecraft.class_5250 method_8011() {
        return net.minecraft.class_2561.method_43471(this.method_7876() + ".desc");
    }

    /** bySound(sound) */
    public static class_1813 method_8012(net.minecraft.class_3414 sound) {
        return field_8901.get(sound);
    }
}
