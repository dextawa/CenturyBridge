package top.dext.centurybridge.step.v1_21_2.rt;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1924;
import net.minecraft.class_238;
import net.minecraft.class_4051;

/**
 * 1.21.1's EntityGetter targeting defaults on 1.21.2 (getNearestPlayer, getNearestEntity, getNearbyPlayers,
 * getNearbyEntities and their overloads), which 1.21.2 moved to ServerEntityGetter with a ServerLevel-taking
 * TargetingConditions.test. Each body is 1.21.1's, read off its bytecode, on any EntityGetter (1.21.1 asked them of
 * client levels too); the condition is tested with 1.21.1's semantics on either side (CbEntityTargeting.test, s7entity).
 * 1.21.1's defaults called one another through the getter -- a 1.21.1 implementation overriding one (found through
 * its adapter) is reached the same way.
 */
public final class CbLevelEntities {

    public interface LegacyNearestPlayer {
        class_1657 method_18462(class_4051 conditions, class_1309 source);
    }

    public interface LegacyNearestPlayerAt {
        class_1657 method_18463(class_4051 conditions, class_1309 source, double x, double y, double z);
    }

    public interface LegacyNearestPlayerPos {
        class_1657 method_18461(class_4051 conditions, double x, double y, double z);
    }

    public interface LegacyNearestEntityOfClass {
        <T extends class_1309> T method_21726(Class<? extends T> type, class_4051 conditions, class_1309 source, double x,
                                              double y, double z, class_238 area);
    }

    public interface LegacyNearestEntity {
        <T extends class_1309> T method_18468(List<? extends T> entities, class_4051 conditions, class_1309 source,
                                              double x, double y, double z);
    }

    public interface LegacyNearbyPlayers {
        List<class_1657> method_18464(class_4051 conditions, class_1309 source, class_238 area);
    }

    public interface LegacyNearbyEntities {
        <T extends class_1309> List<T> method_18466(Class<T> type, class_4051 conditions, class_1309 source, class_238 area);
    }

    // ---- calls (virtual in 1.21.1: an override first) ----

    public static class_1657 method_18462(class_1924 self, class_4051 conditions, class_1309 source) {
        return self instanceof LegacyNearestPlayer l ? l.method_18462(conditions, source) : nearestPlayer(self, conditions, source);
    }

    public static class_1657 method_18463(class_1924 self, class_4051 conditions, class_1309 source, double x, double y, double z) {
        return self instanceof LegacyNearestPlayerAt l ? l.method_18463(conditions, source, x, y, z)
            : nearestPlayerAt(self, conditions, source, x, y, z);
    }

    public static class_1657 method_18461(class_1924 self, class_4051 conditions, double x, double y, double z) {
        return self instanceof LegacyNearestPlayerPos l ? l.method_18461(conditions, x, y, z)
            : nearestPlayerPos(self, conditions, x, y, z);
    }

    public static <T extends class_1309> T method_21726(class_1924 self, Class<? extends T> type, class_4051 conditions,
                                                        class_1309 source, double x, double y, double z, class_238 area) {
        return self instanceof LegacyNearestEntityOfClass l ? l.method_21726(type, conditions, source, x, y, z, area)
            : nearestEntityOfClass(self, type, conditions, source, x, y, z, area);
    }

    public static <T extends class_1309> T method_18468(class_1924 self, List<? extends T> entities, class_4051 conditions,
                                                        class_1309 source, double x, double y, double z) {
        return self instanceof LegacyNearestEntity l ? l.method_18468(entities, conditions, source, x, y, z)
            : nearestEntity(self, entities, conditions, source, x, y, z);
    }

    public static List<class_1657> method_18464(class_1924 self, class_4051 conditions, class_1309 source, class_238 area) {
        return self instanceof LegacyNearbyPlayers l ? l.method_18464(conditions, source, area)
            : nearbyPlayers(self, conditions, source, area);
    }

    public static <T extends class_1309> List<T> method_18466(class_1924 self, Class<T> type, class_4051 conditions,
                                                              class_1309 source, class_238 area) {
        return self instanceof LegacyNearbyEntities l ? l.method_18466(type, conditions, source, area)
            : nearbyEntities(self, type, conditions, source, area);
    }

    // ---- 1.21.1's default bodies (also EntityGetter.super.x(..), superRedirects) ----

    public static class_1657 nearestPlayer(class_1924 self, class_4051 conditions, class_1309 source) {
        return (class_1657) method_18468(self, self.method_18456(), conditions, source, source.method_23317(),
            source.method_23318(), source.method_23321());
    }

    public static class_1657 nearestPlayerAt(class_1924 self, class_4051 conditions, class_1309 source, double x, double y,
                                             double z) {
        return (class_1657) method_18468(self, self.method_18456(), conditions, source, x, y, z);
    }

    public static class_1657 nearestPlayerPos(class_1924 self, class_4051 conditions, double x, double y, double z) {
        return (class_1657) method_18468(self, self.method_18456(), conditions, null, x, y, z);
    }

    public static <T extends class_1309> T nearestEntityOfClass(class_1924 self, Class<? extends T> type,
                                                                class_4051 conditions, class_1309 source, double x,
                                                                double y, double z, class_238 area) {
        return method_18468(self, self.method_8390(type, area, e -> true), conditions, source, x, y, z);
    }

    public static <T extends class_1309> T nearestEntity(class_1924 self, List<? extends T> entities, class_4051 conditions,
                                                         class_1309 source, double x, double y, double z) {
        double best = -1.0;
        T nearest = null;
        for (T entity : entities) {
            if (CbEntityTargeting.test(conditions, source, entity)) {
                double distance = entity.method_5649(x, y, z);
                if (best == -1.0 || distance < best) {
                    best = distance;
                    nearest = entity;
                }
            }
        }
        return nearest;
    }

    public static List<class_1657> nearbyPlayers(class_1924 self, class_4051 conditions, class_1309 source, class_238 area) {
        List<class_1657> list = Lists.newArrayList();
        for (class_1657 player : self.method_18456()) {
            if (area.method_1008(player.method_23317(), player.method_23318(), player.method_23321())
                && CbEntityTargeting.test(conditions, source, player)) {
                list.add(player);
            }
        }
        return list;
    }

    public static <T extends class_1309> List<T> nearbyEntities(class_1924 self, Class<T> type, class_4051 conditions,
                                                                class_1309 source, class_238 area) {
        List<T> candidates = self.method_8390(type, area, e -> true);
        List<T> list = Lists.newArrayList();
        for (T entity : candidates) {
            if (CbEntityTargeting.test(conditions, source, entity)) {
                list.add(entity);
            }
        }
        return list;
    }

    private CbLevelEntities() {
    }
}
