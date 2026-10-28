package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_1923;
import net.minecraft.class_3204;
import net.minecraft.class_3215;
import net.minecraft.class_3228;
import net.minecraft.class_3230;

/**
 * 1.21.4's DistanceManager ticket members on 1.21.5, which keeps tickets in TicketStorage (class_10592, the
 * DistanceManager's field_55591) and has no ticket key: a 1.21.5 Ticket is (type, level), and removal takes away one
 * ticket of the same type and level (TicketStorage.isTicketSameTypeAndLevel). javap -c -p 1.21.4 class_3204 /
 * 1.21.5 class_3204, class_10592. A 1.21.4 key told tickets of one type and level apart; 1.21.5 counts them, so adding
 * twice and removing once still leaves one, as before, whatever the keys were.
 */
public final class CbTickets {
    private CbTickets() {
    }

    /** addTicket(type, pos, level, key): a ticket of that level */
    public static void addTicket(class_3204 dm, class_3230 type, class_1923 pos, int level, Object key) {
        dm.field_55591.method_66357(new class_3228(type, level), pos);
    }

    /** removeTicket(type, pos, level, key) */
    public static void removeTicket(class_3204 dm, class_3230 type, class_1923 pos, int level, Object key) {
        dm.field_55591.method_66372(new class_3228(type, level), pos);
    }

    /** addRegionTicket(type, pos, radius, key): level FULL - radius (also simulated), 1.21.5's addTicketWithRadius */
    public static void addRegionTicket(class_3204 dm, class_3230 type, class_1923 pos, int radius, Object key) {
        dm.field_55591.method_66358(type, pos, radius);
    }

    /** removeRegionTicket(type, pos, radius, key) */
    public static void removeRegionTicket(class_3204 dm, class_3230 type, class_1923 pos, int radius, Object key) {
        dm.field_55591.method_66373(type, pos, radius);
    }

    /** ServerChunkCache.addRegionTicket(type, pos, radius, key): 1.21.5's addTicketWithRadius (same level FULL - radius) */
    public static void addRegionTicketOnCache(class_3215 cache, class_3230 type, class_1923 pos, int radius, Object key) {
        cache.method_66009(type, pos, radius);
    }

    /** ServerChunkCache.removeRegionTicket(type, pos, radius, key): 1.21.5's removeTicketWithRadius */
    public static void removeRegionTicketOnCache(class_3215 cache, class_3230 type, class_1923 pos, int radius, Object key) {
        cache.method_66010(type, pos, radius);
    }
}
