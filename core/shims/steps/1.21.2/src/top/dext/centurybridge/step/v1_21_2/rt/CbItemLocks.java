package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.class_1273;
import net.minecraft.class_1799;
import net.minecraft.class_2073;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_9329;
import net.minecraft.class_9334;

/**
 * 1.21.1's LockCode (a key: the custom name a held item must have; empty = no lock) on 1.21.2's (an item
 * predicate). A lock made from a key is 1.21.2's lock whose predicate asks for that custom name -- the predicate
 * 1.21.2's own data fixer makes of an old "Lock" string -- and it remembers its key, so that it unlocks exactly as
 * 1.21.1's did (CbItemLockMixin: the held item's custom name, as a string, equal to the key) and key() answers it.
 * The empty key is 1.21.2's NO_LOCK. addToTag / fromTag write and read 1.21.1's format (the "Lock" string), which is
 * what a 1.21.1 mod reads back; a lock 1.21.2 made has no key: its predicate's custom name, if it names one.
 */
public final class CbItemLocks {

    private static final Map<class_1273, String> KEYS = Collections.synchronizedMap(new IdentityHashMap<>());

    /** new LockCode(key) */
    public static class_1273 newLockCode(String key) {
        if (key.isEmpty()) {
            return class_1273.field_5817;
        }
        class_2073 predicate = class_2073.class_2074.method_8973()
            .method_57299(class_9329.method_57862().method_57872(class_9334.field_49631, class_2561.method_43470(key)).method_57871())
            .method_8976();
        class_1273 lock = new class_1273(predicate);
        KEYS.put(lock, key);
        return lock;
    }

    /** the key a lock was made from, null for one 1.21.2 made */
    public static String keyOf(class_1273 lock) {
        return lock == class_1273.field_5817 ? "" : KEYS.get(lock);
    }

    /** key() */
    public static String comp_2371(class_1273 lock) {
        String key = keyOf(lock);
        if (key != null) {
            return key;
        }
        for (net.minecraft.class_9336<?> c : ((top.dext.centurybridge.step.v1_21_2.mixin.CbItemComponentPredicateAccess) (Object)
                lock.comp_3222().comp_2374()).cb$expected()) {
            if (c.comp_2443() == class_9334.field_49631 && c.comp_2444() instanceof class_2561 name) {
                return name.getString();
            }
        }
        return "";
    }

    /** 1.21.1's unlocksWith(stack) for a keyed lock */
    public static boolean unlocks(String key, class_1799 stack) {
        if (key.isEmpty()) {
            return true;
        }
        class_2561 name = stack.method_57824(class_9334.field_49631);
        return name != null && key.equals(name.getString());
    }

    /** addToTag(tag): 1.21.1's "Lock" string, nothing for no lock */
    public static void method_5474(class_1273 lock, class_2487 tag) {
        String key = comp_2371(lock);
        if (!key.isEmpty()) {
            tag.method_10582("Lock", key);
        }
    }

    /** fromTag(tag): the "Lock" string's lock, NO_LOCK without one */
    public static class_1273 method_5473(class_2487 tag) {
        return tag.method_10573("Lock", 8) ? newLockCode(tag.method_10558("Lock")) : class_1273.field_5817;
    }

    private CbItemLocks() {
    }
}
