package top.dext.centurybridge.step.v1_21.rt;

/**
 * Music discs. 1.20.6's disc was a RecordItem holding its sound, comparator output and length, found by
 * its sound (RecordItem.getBySound) and recognised by the jukebox by its class; 1.21's disc is any item
 * with the jukebox_playable component, naming a JukeboxSong (sound, description, length, comparator
 * output) of each world's minecraft:jukebox_song registry, loaded from datapacks and sent to clients.
 *
 * <p>A 1.20.6 mod's RecordItem (the facade {@link top.dext.centurybridge.step.v1_21.facade.class_1813})
 * carries the component naming the song keyed as its sound ({@link #songKey}), and its song is added to
 * each server world's jukebox_song registry as it loads ({@link Songs}, a CbLegacyRegistries stand-in):
 * 1.21's jukebox, comparator and "now playing" then play it as 1.20.6's did from the disc. The tooltip line
 * stays the disc's own (appendHoverText, 1.20.6's), so the component does not add a second one. A
 * datapack that defines the same key keeps its song.
 */
public final class CbItemRecords {

    private CbItemRecords() {
    }

    /** the jukebox songs of 1.20.6 mods' discs, under the jukebox_song registry key */
    static final class Songs extends CbLegacyRegistries.Legacy<top.dext.centurybridge.step.v1_21.facade.class_1813, net.minecraft.class_9793> {
        @SuppressWarnings({"unchecked", "rawtypes"})
        Songs() {
            super((net.minecraft.class_5321) net.minecraft.class_7924.field_52176, false);
        }

        @Override
        protected net.minecraft.class_9793 recordFor(top.dext.centurybridge.step.v1_21.facade.class_1813 disc,
                net.minecraft.class_5321<top.dext.centurybridge.step.v1_21.facade.class_1813> key, net.minecraft.class_6903.class_7863 lookup) {
            return new net.minecraft.class_9793(net.minecraft.class_7923.field_41172.method_47983(disc.method_8009()), disc.method_8011(),
                disc.method_44369() / 20.0F, disc.method_8010());
        }

        /** a datapack's song stands for no 1.20.6 disc */
        @Override
        protected top.dext.centurybridge.step.v1_21.facade.class_1813 newFacade(net.minecraft.class_5321<net.minecraft.class_9793> key) {
            return null;
        }
    }

    private static final class Instance {
        static final Songs SONGS = new Songs();
    }

    /** the jukebox song of a 1.20.6 disc: keyed as its sound, 1.20.6's identity of a disc (RecordItem.getBySound) */
    public static net.minecraft.class_5321<net.minecraft.class_9793> songKey(net.minecraft.class_3414 sound) {
        return net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_52176, sound.method_14833());
    }

    /** the properties of a 1.20.6 disc, with the component naming its song (no tooltip line of its own) */
    public static net.minecraft.class_1792.class_1793 withSong(net.minecraft.class_1792.class_1793 properties, net.minecraft.class_3414 sound) {
        return properties.method_57349(net.minecraft.class_9334.field_52175,
            new net.minecraft.class_9792(new net.minecraft.class_9791<>(songKey(sound)), false));
    }

    /** a 1.20.6 disc was made: its song joins the worlds that load from now on (the first disc of a sound defines it) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void track(top.dext.centurybridge.step.v1_21.facade.class_1813 disc) {
        net.minecraft.class_5321 key = songKey(disc.method_8009());
        synchronized (Instance.SONGS) {
            if (!Instance.SONGS.method_35842(key)) {
                Instance.SONGS.cbRegister(key, disc);
            }
        }
    }
}
