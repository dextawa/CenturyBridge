package top.dext.centurybridge.rt.v1_20_2;

import com.mojang.util.UndashedUuid;
import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4881;

/**
 * RealmsServerPlayerList.players (class_4881.field_22627): the players online on a Realm, a
 * List&lt;String&gt; in 1.20.1 and a List&lt;UUID&gt; in 1.20.2 -- the same erased List (generic drift).
 * 1.20.1 kept the service's strings as they came and compared them with User.getUuid(), the undashed
 * form (UUIDTypeAdapter); 1.20.2 parses them into UUIDs. So a UUID reads as its undashed string
 * (UndashedUuid.toString, the form User.getUuid() answers on 1.20.2 through CbGuiUserBridge, and the one
 * CbNetRealms gives the other Realms DTOs' UUIDs), and a string -- dashed or not -- writes as the UUID
 * it spells (UndashedUuid.fromStringLenient; a string that spells none is the IllegalArgumentException
 * that call throws, at the read that needs the UUID).
 *
 * <p>The field holds the list itself in both releases, and 1.20.1 code that set it may keep changing its
 * own list afterwards; so both directions are live views (reads and writes convert per element) rather
 * than copies, adaptive (a value already of the wanted kind passes), and they undo each other: the list a
 * 1.20.1 mod stored reads back as that very list.
 */
@Environment(EnvType.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbGenRealms {

    /** instanceFieldGetters: RealmsServerPlayerList.players read by 1.20.1 code */
    public static List field_22627(class_4881 players) {
        return view(players.field_22627, true);
    }

    /** instanceFieldSetters: RealmsServerPlayerList.players written by 1.20.1 code */
    public static void field_22627(class_4881 players, List value) {
        players.field_22627 = view(value, false);
    }

    /** a UUID as 1.20.1 held it (a string passes) */
    static Object string(Object value) {
        return value instanceof UUID id ? UndashedUuid.toString(id) : value;
    }

    /** a 1.20.1 string as the UUID 1.20.2 holds (a UUID passes) */
    static Object uuid(Object value) {
        return value instanceof String s ? UndashedUuid.fromStringLenient(s) : value;
    }

    /** strings true: a 1.20.2 list of UUIDs as 1.20.1's strings; false: a 1.20.1 list of strings as UUIDs */
    private static List view(List value, boolean strings) {
        if (value == null) {
            return null;
        }
        if (value instanceof ListView v) {
            return v.strings == strings ? value : v.base;
        }
        return value instanceof RandomAccess ? new RandomAccessListView(value, strings) : new ListView(value, strings);
    }

    private static class ListView extends AbstractList<Object> {
        final List<Object> base;
        final boolean strings;

        ListView(List<Object> base, boolean strings) {
            this.base = base;
            this.strings = strings;
        }

        private Object out(Object e) {
            return this.strings ? string(e) : uuid(e);
        }

        private Object in(Object e) {
            return this.strings ? uuid(e) : string(e);
        }

        @Override
        public Object get(int index) {
            return out(this.base.get(index));
        }

        @Override
        public Object set(int index, Object element) {
            return out(this.base.set(index, in(element)));
        }

        @Override
        public void add(int index, Object element) {
            this.base.add(index, in(element));
        }

        @Override
        public Object remove(int index) {
            return out(this.base.remove(index));
        }

        @Override
        public int size() {
            return this.base.size();
        }
    }

    private static final class RandomAccessListView extends ListView implements RandomAccess {
        RandomAccessListView(List<Object> base, boolean strings) {
            super(base, strings);
        }
    }

    private CbGenRealms() {
    }
}
