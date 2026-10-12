package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's PlayerDataStorage API on 1.20.5's: load(player) answered the loaded (and data-fixed) tag or
 * null where 1.20.5's load answers an Optional (and falls back to the .dat_old backup), and
 * getSeenPlayers() -- gone in 1.20.5 -- listed the player data files.
 */
public final class CbLevelStorage {

    /** a mod class declaring 1.20.4's getSeenPlayers() (the converter adds it to every one) */
    public interface SeenPlayers {
        String[] method_263();
    }

    /** playerDataStorage.load(player): 1.20.5's load, null where it answers nothing */
    public static net.minecraft.class_2487 load(net.minecraft.class_29 storage, net.minecraft.class_1657 player) {
        return storage.method_55789(player).orElse(null);
    }

    /**
     * super.load(player) in a mod subclass: PlayerDataStorage's own load. The subclass's 1.20.4 override
     * is reached from 1.20.5's load through a guarded synthesized forward; with the guard taken here,
     * that forward passes the call on to super's -- vanilla's -- instead of back into the override.
     */
    public static net.minecraft.class_2487 superLoad(net.minecraft.class_29 storage, net.minecraft.class_1657 player) {
        boolean entered = Guard.cbEnter(storage);
        try {
            return storage.method_55789(player).orElse(null);
        } finally {
            if (entered) {
                Guard.cbExit(storage);
            }
        }
    }

    /** the answer of a 1.20.4 load(player) override as what 1.20.5's load answers */
    public static java.util.Optional<net.minecraft.class_2487> optional(net.minecraft.class_2487 tag) {
        return java.util.Optional.ofNullable(tag);
    }

    /** playerDataStorage.getSeenPlayers(): the class's own override, else 1.20.4's body */
    public static String[] getSeenPlayers(net.minecraft.class_29 storage) {
        return storage instanceof SeenPlayers s ? s.method_263() : superSeenPlayers(storage);
    }

    /**
     * 1.20.4's PlayerDataStorage.getSeenPlayers(), also what a subclass's super.getSeenPlayers() ran: the
     * names in the player data directory, each ".dat" name without its extension (the others as they are).
     */
    public static String[] superSeenPlayers(net.minecraft.class_29 storage) {
        String[] names = ((top.dext.centurybridge.step.v1_20_5.mixin.CbLevelStoragePlayerDataAccessor) storage).cbS4$playerDir().list();
        if (names == null) {
            names = new String[0];
        }
        for (int i = 0; i < names.length; i++) {
            if (names[i].endsWith(".dat")) {
                names[i] = names[i].substring(0, names[i].length() - 4);
            }
        }
        return names;
    }

    private CbLevelStorage() {
    }
}
