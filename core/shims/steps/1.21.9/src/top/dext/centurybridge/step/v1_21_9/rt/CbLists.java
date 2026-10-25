package top.dext.centurybridge.step.v1_21_9.rt;

import com.mojang.authlib.GameProfile;
import net.minecraft.class_11560;
import net.minecraft.class_11869;
import net.minecraft.class_11871;
import net.minecraft.class_3330;
import net.minecraft.class_3331;

/**
 * 1.21.9's StoredUserList / StoredUserEntry (ops, bans, the whitelist) for 1.21.8 code.
 *
 * Keys: the player lists' key type K went from GameProfile to NameAndId (UserBanList, UserWhiteList, ServerOpList;
 * IpBanList keeps String). K is erased, so get / remove / contains / getKeyForUser and StoredUserEntry.getUser keep
 * their descriptors and a 1.21.8 GameProfile would reach 1.21.9's casts to NameAndId. The converters are adaptive
 * (a String key, or a key already of the wanted kind, passes) and undo each other.
 *
 * Constructors: 1.21.9's lists take a NotificationService (the management protocol's change notifications). A
 * 1.21.8 list notified nobody, so a list a 1.21.8 mod constructs gets the empty service.
 *
 * Overrides: add / remove / remove(entry) answer whether the list changed in 1.21.9 (void in 1.21.8). The bridges run
 * the old override and answer what 1.21.9's body would have: add -- the entry was not already the stored one;
 * remove -- an entry was stored under the key.
 */
public final class CbLists {

    private CbLists() {
    }

    /** a key handed in by 1.21.8 code: a GameProfile becomes its id and name */
    public static Object cbKeyIn(Object key) {
        return key instanceof GameProfile p ? CbProfiles.toNameAndId(p) : key;
    }

    /** a key handed to 1.21.8 code: a NameAndId becomes a GameProfile (CbProfiles.toProfile) */
    public static Object cbKeyOut(Object key) {
        return key instanceof class_11560 n ? CbProfiles.toProfile(n) : key;
    }

    /** the NotificationService of a list a 1.21.8 mod constructs: none (1.21.8 lists notified nobody) */
    public static class_11871 cbNoNotifications() {
        return new class_11869();
    }

    // ------------------------------------------------------------------ override bridges (void -> boolean)

    /** StoredUserList.add(entry) (1.21.8: void) */
    public interface LegacyAdd {
        void method_14633(class_3330<?> entry);
    }

    /** StoredUserList.remove(key) (1.21.8: void) */
    public interface LegacyRemove {
        void method_14635(Object key);
    }

    /** StoredUserList.remove(entry) (1.21.8: void) */
    public interface LegacyRemoveEntry {
        void method_14638(class_3330<?> entry);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static class_3330<?> stored(Object self, Object key) {
        return ((class_3331) self).method_14640(key);
    }

    /** 1.21.9's add answers false when the entry equals the one stored under its key, else true */
    public static boolean cbAdd(LegacyAdd self, class_3330<?> entry) {
        boolean same = entry.equals(stored(self, entry.method_14626()));
        self.method_14633(entry);
        return !same;
    }

    /** 1.21.9's remove answers whether an entry was stored under the key */
    public static boolean cbRemove(LegacyRemove self, Object key) {
        boolean had = stored(self, key) != null;
        self.method_14635(cbKeyOut(key));
        return had;
    }

    /** 1.21.9's remove(entry) is remove(entry.getUser()) */
    public static boolean cbRemoveEntry(LegacyRemoveEntry self, class_3330<?> entry) {
        boolean had = stored(self, entry.method_14626()) != null;
        self.method_14638(entry);
        return had;
    }
}
