package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.List;
import java.util.Map;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1927;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_5362;
import net.minecraft.class_6880;
import net.minecraft.class_9892;
import top.dext.centurybridge.step.v1_21_2.facade.Explosion;

/**
 * 1.21.1's Explosion API on 1.21.2's Explosion interface.
 *
 * <p>The members 1.21.2 dropped from Explosion (explode, finalizeExplosion, interactsWithBlocks, getHitPlayers,
 * clearToBlow, getToBlow, the particle and sound getters) are asked of whatever explosion a 1.21.1 mod holds:
 * <ul>
 * <li>its own -- the facade 1.21.1's {@code new Explosion(..)} builds now, or a subclass of it: the facade's method;</li>
 * <li>vanilla's ServerExplosion (handed to the mod by 1.21.2's callbacks, or answered by a 1.21.1 Level.explode call):
 *     1.21.1's two phases on 1.21.2's own steps (CbLevelExplosionServerMixin). explode() is the game event, the exploded
 *     positions (recorded: the explosion's live toBlow) and hurtEntities; finalizeExplosion is interactWithBlocks of that
 *     list when the explosion interacts with blocks, then createFire when it makes fire -- 1.21.2's explode() is these
 *     same steps in this order. Particles and sound: what ServerLevel.explode made it with (recorded there), else
 *     1.21.1's constructor defaults;</li>
 * <li>any other implementation (a mod written for 1.21.2): 1.21.1 never had one, and nothing in it holds a toBlow or
 *     the phases -- an explicit UnsupportedOperationException naming its class.</li>
 * </ul>
 * 1.21.1's Level.explode variants answered the explosion they made; 1.21.2's answer nothing. On a server level the
 * call is 1.21.2's same variant (the explosion, its packets, every hook on them) and answers the ServerExplosion
 * ServerLevel.explode made for it -- its toBlow cleared when it does not interact with blocks, as 1.21.1's
 * ServerLevel.explode cleared it. On a client level 1.21.1 exploded locally (Level.explode: explode, then
 * finalizeExplosion(spawnParticles)); 1.21.2's ClientLevel.explode does nothing, so the facade does 1.21.1's.
 */
public final class CbLevelExplosions {

    /** a ServerExplosion seen as 1.21.1 saw its Explosion (CbLevelExplosionServerMixin) */
    public interface ServerState {
        List<class_2338> cb$toBlow();

        void cb$explodePhase();

        void cb$finalizePhase();

        void cb$effects(class_2394 small, class_2394 large, class_6880<class_3414> sound);

        class_2394 cb$small();

        class_2394 cb$large();

        class_6880<class_3414> cb$sound();
    }

    /**
     * the slot a 1.21.1 Level.explode call on this thread waits on for the ServerExplosion ServerLevel.explode makes
     * (CbLevelExplosionServerLevelMixin); null when no such call is running, so nothing is kept
     */
    private static final ThreadLocal<class_9892[]> MADE = new ThreadLocal<>();

    public static void made(class_9892 explosion) {
        class_9892[] slot = MADE.get();
        if (slot != null && slot[0] == null) {
            slot[0] = explosion;
        }
    }

    private static ServerState server(class_1927 e, String what) {
        if (e instanceof ServerState s) {
            return s;
        }
        throw new UnsupportedOperationException("1.21.1's Explosion." + what + " asked of " + e.getClass().getName()
            + ", a 1.21.2 Explosion implementation that is neither 1.21.1's Explosion nor 1.21.2's ServerExplosion");
    }

    // ---- constructors (ctorRedirects) ----

    public static class_1927 cbNew(class_1937 level, class_1297 source, double x, double y, double z, float radius,
                                   List<class_2338> toBlow, class_1927.class_4179 interaction, class_2394 small,
                                   class_2394 large, class_6880<class_3414> sound) {
        return new Explosion(level, source, x, y, z, radius, toBlow, interaction, small, large, sound);
    }

    public static class_1927 cbNew(class_1937 level, class_1297 source, double x, double y, double z, float radius,
                                   boolean fire, class_1927.class_4179 interaction, List<class_2338> toBlow) {
        return new Explosion(level, source, x, y, z, radius, fire, interaction, toBlow);
    }

    public static class_1927 cbNew(class_1937 level, class_1297 source, double x, double y, double z, float radius,
                                   boolean fire, class_1927.class_4179 interaction) {
        return new Explosion(level, source, x, y, z, radius, fire, interaction);
    }

    public static class_1927 cbNew(class_1937 level, class_1297 source, class_1282 damageSource, class_5362 calculator,
                                   double x, double y, double z, float radius, boolean fire,
                                   class_1927.class_4179 interaction, class_2394 small, class_2394 large,
                                   class_6880<class_3414> sound) {
        return new Explosion(level, source, damageSource, calculator, x, y, z, radius, fire, interaction, small, large,
            sound);
    }

    // ---- 1.21.1 members gone from the interface (instanceRedirects / staticRedirects) ----

    /** Explosion.getSeenPercent(center, entity) */
    public static float method_17752(class_243 center, class_1297 entity) {
        return class_9892.method_61731(center, entity);
    }

    /**
     * the seen percent 1.21.2's ExplosionDamageCalculator.getEntityDamageAmount takes: 1.21.1's computed it itself,
     * Explosion.getSeenPercent(explosion.center(), entity) (a 1.21.1 call, callReshapes)
     */
    public static float seenPercent(class_1927 explosion, class_1297 entity) {
        return class_9892.method_61731(explosion.method_55109(), entity);
    }

    /** the same, for a 1.21.1 calculator's getEntityDamageAmount override entered directly (frameDefaults) */
    public static float seenPercent(class_5362 calculator, class_1927 explosion, class_1297 entity) {
        return seenPercent(explosion, entity);
    }

    /** explode() */
    public static void method_8348(class_1927 self) {
        if (self instanceof Explosion e) {
            e.method_8348();
        } else {
            server(self, "explode()").cb$explodePhase();
        }
    }

    /** finalizeExplosion(spawnParticles): a server level adds no particle (1.21.1's ServerLevel never did) */
    public static void method_8350(class_1927 self, boolean spawnParticles) {
        if (self instanceof Explosion e) {
            e.method_8350(spawnParticles);
        } else {
            server(self, "finalizeExplosion(boolean)").cb$finalizePhase();
        }
    }

    /** interactsWithBlocks(): 1.21.1's body, on any explosion's block interaction */
    public static boolean method_46667(class_1927 self) {
        if (self instanceof Explosion e) {
            return e.method_46667();
        }
        return self.method_55111() != class_1927.class_4179.field_40878;
    }

    /** getHitPlayers() */
    public static Map<class_1657, class_243> method_8351(class_1927 self) {
        if (self instanceof Explosion e) {
            return e.method_8351();
        }
        return ((class_9892) server(self, "getHitPlayers()")).method_61738();
    }

    /** clearToBlow() */
    public static void method_8352(class_1927 self) {
        if (self instanceof Explosion e) {
            e.method_8352();
        } else {
            server(self, "clearToBlow()").cb$toBlow().clear();
        }
    }

    /** getToBlow() */
    public static List<class_2338> method_8346(class_1927 self) {
        if (self instanceof Explosion e) {
            return e.method_8346();
        }
        return server(self, "getToBlow()").cb$toBlow();
    }

    /** getSmallExplosionParticles() */
    public static class_2394 method_55112(class_1927 self) {
        if (self instanceof Explosion e) {
            return e.method_55112();
        }
        class_2394 p = server(self, "getSmallExplosionParticles()").cb$small();
        return p != null ? p : class_2398.field_11236;
    }

    /** getLargeExplosionParticles() */
    public static class_2394 method_55113(class_1927 self) {
        if (self instanceof Explosion e) {
            return e.method_55113();
        }
        class_2394 p = server(self, "getLargeExplosionParticles()").cb$large();
        return p != null ? p : class_2398.field_11221;
    }

    /** getExplosionSound() */
    public static class_6880<class_3414> method_55114(class_1927 self) {
        if (self instanceof Explosion e) {
            return e.method_55114();
        }
        class_6880<class_3414> s = server(self, "getExplosionSound()").cb$sound();
        return s != null ? s : class_3417.field_15152;
    }

    // ---- Level.explode, answering the explosion ----

    private static class_1927 madeOn(class_3218 level, Runnable call) {
        class_9892[] previous = MADE.get();
        class_9892[] slot = new class_9892[1];
        MADE.set(slot);
        try {
            call.run();
            class_9892 made = slot[0];
            if (made == null) {
                // a level subclass whose explode makes no ServerExplosion: 1.21.1 always answered one
                throw new IllegalStateException("ServerLevel.explode made no ServerExplosion on " + level);
            }
            if (made.method_55111() == class_1927.class_4179.field_40878) {
                ((ServerState) made).cb$toBlow().clear();
            }
            return made;
        } finally {
            if (previous == null) {
                MADE.remove();
            } else {
                MADE.set(previous);
            }
        }
    }

    private static class_1927 local(class_1937 level, class_1297 source, class_1282 damageSource, class_5362 calculator,
                                    double x, double y, double z, float radius, boolean fire,
                                    class_1937.class_7867 interaction, boolean spawnParticles, class_2394 small,
                                    class_2394 large, class_6880<class_3414> sound) {
        Explosion e = new Explosion(level, source, damageSource, calculator, x, y, z, radius, fire,
            blockInteraction(level, interaction), small, large, sound);
        e.method_8348();
        e.method_8350(spawnParticles);
        return e;
    }

    /** 1.21.1's Level.explode: the explosion interaction as a block interaction, by the level's game rules */
    private static class_1927.class_4179 blockInteraction(class_1937 level, class_1937.class_7867 interaction) {
        net.minecraft.class_1928 rules = CbLevelGameRules.of(level);
        if (interaction == class_1937.class_7867.field_40888) {
            return class_1927.class_4179.field_40878;
        }
        if (interaction == class_1937.class_7867.field_40889) {
            return destroyType(rules, net.minecraft.class_1928.field_40880);
        }
        if (interaction == class_1937.class_7867.field_40890) {
            return rules.method_8355(net.minecraft.class_1928.field_19388)
                ? destroyType(rules, net.minecraft.class_1928.field_40881) : class_1927.class_4179.field_40878;
        }
        if (interaction == class_1937.class_7867.field_40891) {
            return destroyType(rules, net.minecraft.class_1928.field_40882);
        }
        if (interaction == class_1937.class_7867.field_51779) {
            return class_1927.class_4179.field_47331;
        }
        throw new IllegalArgumentException("explosion interaction " + interaction);
    }

    private static class_1927.class_4179 destroyType(net.minecraft.class_1928 rules,
                                                     net.minecraft.class_1928.class_4313<net.minecraft.class_1928.class_4310> key) {
        return rules.method_8355(key) ? class_1927.class_4179.field_40879 : class_1927.class_4179.field_18687;
    }

    /** level.explode(source, x, y, z, radius, interaction) */
    public static class_1927 method_8437(class_1937 self, class_1297 source, double x, double y, double z, float radius,
                                         class_1937.class_7867 interaction) {
        if (self instanceof class_3218 s) {
            return madeOn(s, () -> s.method_8437(source, x, y, z, radius, interaction));
        }
        return local(self, source, class_1927.method_55108(self, source), null, x, y, z, radius, false, interaction, true, class_2398.field_11236,
            class_2398.field_11221, class_3417.field_15152);
    }

    /** level.explode(source, x, y, z, radius, fire, interaction) */
    public static class_1927 method_8537(class_1937 self, class_1297 source, double x, double y, double z, float radius,
                                         boolean fire, class_1937.class_7867 interaction) {
        if (self instanceof class_3218 s) {
            return madeOn(s, () -> s.method_8537(source, x, y, z, radius, fire, interaction));
        }
        return local(self, source, class_1927.method_55108(self, source), null, x, y, z, radius, fire, interaction, true, class_2398.field_11236,
            class_2398.field_11221, class_3417.field_15152);
    }

    /** level.explode(source, damageSource, calculator, center, radius, fire, interaction) */
    public static class_1927 method_46407(class_1937 self, class_1297 source, class_1282 damageSource,
                                          class_5362 calculator, class_243 center, float radius, boolean fire,
                                          class_1937.class_7867 interaction) {
        if (self instanceof class_3218 s) {
            return madeOn(s, () -> s.method_46407(source, damageSource, calculator, center, radius, fire, interaction));
        }
        return local(self, source, damageSource, calculator, center.field_1352, center.field_1351, center.field_1350,
            radius, fire, interaction, true, class_2398.field_11236, class_2398.field_11221, class_3417.field_15152);
    }

    /** level.explode(source, damageSource, calculator, x, y, z, radius, fire, interaction) */
    public static class_1927 method_55117(class_1937 self, class_1297 source, class_1282 damageSource,
                                          class_5362 calculator, double x, double y, double z, float radius, boolean fire,
                                          class_1937.class_7867 interaction) {
        if (self instanceof class_3218 s) {
            return madeOn(s, () -> s.method_55117(source, damageSource, calculator, x, y, z, radius, fire, interaction));
        }
        return local(self, source, damageSource, calculator, x, y, z, radius, fire, interaction, true,
            class_2398.field_11236, class_2398.field_11221, class_3417.field_15152);
    }

    /** level.explode(source, damageSource, calculator, x, y, z, radius, fire, interaction, small, large, sound) */
    public static class_1927 method_8454(class_1937 self, class_1297 source, class_1282 damageSource,
                                         class_5362 calculator, double x, double y, double z, float radius, boolean fire,
                                         class_1937.class_7867 interaction, class_2394 small, class_2394 large,
                                         class_6880<class_3414> sound) {
        if (self instanceof class_3218 s) {
            return madeOn(s, () -> s.method_8454(source, damageSource, calculator, x, y, z, radius, fire, interaction,
                small, large, sound));
        }
        return local(self, source, damageSource, calculator, x, y, z, radius, fire, interaction, true, small, large, sound);
    }

    /**
     * level.explode(.., spawnParticles, small, large, sound), 1.21.1's base of them all: Level's own body on every
     * level -- ServerLevel did not override it (its explode(..) above called it, then sent the packets), so a direct
     * call explodes and finalizes on the server too, without packets.
     */
    public static class_1927 method_46668(class_1937 self, class_1297 source, class_1282 damageSource,
                                          class_5362 calculator, double x, double y, double z, float radius, boolean fire,
                                          class_1937.class_7867 interaction, boolean spawnParticles, class_2394 small,
                                          class_2394 large, class_6880<class_3414> sound) {
        return local(self, source, damageSource, calculator, x, y, z, radius, fire, interaction, spawnParticles, small,
            large, sound);
    }

    // ---- a 1.21.1 Level subclass's explode overrides (override hooks: bridge, superBridge) ----
    //
    // 1.21.1's variants each called this.explode(.. particles, sound) (method_8454) virtually, with the default damage
    // source (Explosion.getDefaultDamageSource), no calculator, no fire for the shortest, EXPLOSION / EXPLOSION_EMITTER /
    // GENERIC_EXPLODE; Level.method_8454 called this.method_46668(.., spawnParticles true, ..); ServerLevel.method_8454
    // called this.method_46668(.., false, ..), cleared toBlow when the explosion keeps blocks and sent each player
    // within 64 blocks the explosion packet. 1.21.2's variants return nothing: vanilla asking one of a 1.21.1 class
    // runs its override and drops the answer; the override's super call gets 1.21.1's dispatch back.

    public interface Legacy8437 {
        class_1927 method_8437(class_1297 source, double x, double y, double z, float radius, class_1937.class_7867 interaction);
    }

    public interface Legacy8537 {
        class_1927 method_8537(class_1297 source, double x, double y, double z, float radius, boolean fire,
                               class_1937.class_7867 interaction);
    }

    public interface Legacy46407 {
        class_1927 method_46407(class_1297 source, class_1282 damageSource, class_5362 calculator, class_243 center,
                                float radius, boolean fire, class_1937.class_7867 interaction);
    }

    public interface Legacy55117 {
        class_1927 method_55117(class_1297 source, class_1282 damageSource, class_5362 calculator, double x, double y,
                                double z, float radius, boolean fire, class_1937.class_7867 interaction);
    }

    public interface Legacy8454 {
        class_1927 method_8454(class_1297 source, class_1282 damageSource, class_5362 calculator, double x, double y,
                               double z, float radius, boolean fire, class_1937.class_7867 interaction, class_2394 small,
                               class_2394 large, class_6880<class_3414> sound);
    }

    public interface Legacy46668 {
        class_1927 method_46668(class_1297 source, class_1282 damageSource, class_5362 calculator, double x, double y,
                                double z, float radius, boolean fire, class_1937.class_7867 interaction,
                                boolean spawnParticles, class_2394 small, class_2394 large, class_6880<class_3414> sound);
    }

    public interface Super8437 {
        void cb$super$method_8437(class_1297 source, double x, double y, double z, float radius,
                                  class_1937.class_7867 interaction);
    }

    public interface Super8537 {
        void cb$super$method_8537(class_1297 source, double x, double y, double z, float radius, boolean fire,
                                  class_1937.class_7867 interaction);
    }

    public interface Super46407 {
        void cb$super$method_46407(class_1297 source, class_1282 damageSource, class_5362 calculator, class_243 center,
                                   float radius, boolean fire, class_1937.class_7867 interaction);
    }

    public interface Super55117 {
        void cb$super$method_55117(class_1297 source, class_1282 damageSource, class_5362 calculator, double x, double y,
                                   double z, float radius, boolean fire, class_1937.class_7867 interaction);
    }

    public interface Super8454 {
        void cb$super$method_8454(class_1297 source, class_1282 damageSource, class_5362 calculator, double x, double y,
                                  double z, float radius, boolean fire, class_1937.class_7867 interaction,
                                  class_2394 small, class_2394 large, class_6880<class_3414> sound);
    }

    // new shapes asked by vanilla: the 1.21.1 override, its answer dropped

    public static void explode8437(Legacy8437 self, class_1297 source, double x, double y, double z, float radius,
                                   class_1937.class_7867 interaction) {
        self.method_8437(source, x, y, z, radius, interaction);
    }

    public static void explode8537(Legacy8537 self, class_1297 source, double x, double y, double z, float radius,
                                   boolean fire, class_1937.class_7867 interaction) {
        self.method_8537(source, x, y, z, radius, fire, interaction);
    }

    public static void explode46407(Legacy46407 self, class_1297 source, class_1282 damageSource, class_5362 calculator,
                                    class_243 center, float radius, boolean fire, class_1937.class_7867 interaction) {
        self.method_46407(source, damageSource, calculator, center, radius, fire, interaction);
    }

    public static void explode55117(Legacy55117 self, class_1297 source, class_1282 damageSource, class_5362 calculator,
                                    double x, double y, double z, float radius, boolean fire,
                                    class_1937.class_7867 interaction) {
        self.method_55117(source, damageSource, calculator, x, y, z, radius, fire, interaction);
    }

    public static void explode8454(Legacy8454 self, class_1297 source, class_1282 damageSource, class_5362 calculator,
                                   double x, double y, double z, float radius, boolean fire,
                                   class_1937.class_7867 interaction, class_2394 small, class_2394 large,
                                   class_6880<class_3414> sound) {
        self.method_8454(source, damageSource, calculator, x, y, z, radius, fire, interaction, small, large, sound);
    }

    /** method_8454 of a class overriding only 1.21.1's method_46668: the 1.21.1 method_8454 it inherited */
    public static void explode46668(Legacy46668 self, class_1297 source, class_1282 damageSource, class_5362 calculator,
                                    double x, double y, double z, float radius, boolean fire,
                                    class_1937.class_7867 interaction, class_2394 small, class_2394 large,
                                    class_6880<class_3414> sound) {
        via46668((class_1937) self, self, source, damageSource, calculator, x, y, z, radius, fire, interaction, small,
            large, sound);
    }

    /** 1.21.1's method_8454 as a level overriding method_46668 inherited it (Level's, or ServerLevel's) */
    private static class_1927 via46668(class_1937 level, Legacy46668 self, class_1297 source, class_1282 damageSource,
                                       class_5362 calculator, double x, double y, double z, float radius, boolean fire,
                                       class_1937.class_7867 interaction, class_2394 small, class_2394 large,
                                       class_6880<class_3414> sound) {
        if (!(level instanceof class_3218 server)) {
            return self.method_46668(source, damageSource, calculator, x, y, z, radius, fire, interaction, true, small,
                large, sound);
        }
        class_1927 e = self.method_46668(source, damageSource, calculator, x, y, z, radius, fire, interaction, false,
            small, large, sound);
        if (!method_46667(e)) {
            method_8352(e);
        }
        // 1.21.2's explosion packet: what 1.21.2's ServerLevel.explode sends, for the explosion 1.21.1's made
        class_243 center = new class_243(x, y, z);
        boolean small2 = radius < 2.0F || !method_46667(e);
        class_2394 particle = small2 ? small : large;
        Map<class_1657, class_243> hit = method_8351(e);
        for (net.minecraft.class_3222 player : server.method_18456()) {
            if (player.method_5649(x, y, z) < 4096.0) {
                player.field_13987.method_14364(new net.minecraft.class_2664(center,
                    java.util.Optional.ofNullable(hit.get(player)), particle, sound));
            }
        }
        return e;
    }

    /** 1.21.1's this.method_8454(..) from a variant: the class's own 1.21.1 override when it has one */
    private static class_1927 v8454(class_1937 level, class_1297 source, class_1282 damageSource, class_5362 calculator,
                                    double x, double y, double z, float radius, boolean fire,
                                    class_1937.class_7867 interaction, class_2394 small, class_2394 large,
                                    class_6880<class_3414> sound) {
        if (level instanceof Legacy8454 l) {
            return l.method_8454(source, damageSource, calculator, x, y, z, radius, fire, interaction, small, large, sound);
        }
        if (level instanceof Legacy46668 l) {
            return via46668(level, l, source, damageSource, calculator, x, y, z, radius, fire, interaction, small, large,
                sound);
        }
        return method_8454(level, source, damageSource, calculator, x, y, z, radius, fire, interaction, small, large,
            sound);
    }

    // super calls of the 1.21.1 overrides

    public static class_1927 superExplode8437(Super8437 self, class_1297 source, double x, double y, double z,
                                              float radius, class_1937.class_7867 interaction) {
        class_1937 level = (class_1937) self;
        return v8454(level, source, class_1927.method_55108(level, source), null, x, y, z, radius, false, interaction,
            class_2398.field_11236, class_2398.field_11221, class_3417.field_15152);
    }

    public static class_1927 superExplode8537(Super8537 self, class_1297 source, double x, double y, double z,
                                              float radius, boolean fire, class_1937.class_7867 interaction) {
        class_1937 level = (class_1937) self;
        return v8454(level, source, class_1927.method_55108(level, source), null, x, y, z, radius, fire, interaction,
            class_2398.field_11236, class_2398.field_11221, class_3417.field_15152);
    }

    public static class_1927 superExplode46407(Super46407 self, class_1297 source, class_1282 damageSource,
                                               class_5362 calculator, class_243 center, float radius, boolean fire,
                                               class_1937.class_7867 interaction) {
        return v8454((class_1937) self, source, damageSource, calculator, center.method_10216(), center.method_10214(),
            center.method_10215(), radius, fire, interaction, class_2398.field_11236, class_2398.field_11221,
            class_3417.field_15152);
    }

    public static class_1927 superExplode55117(Super55117 self, class_1297 source, class_1282 damageSource,
                                               class_5362 calculator, double x, double y, double z, float radius,
                                               boolean fire, class_1937.class_7867 interaction) {
        return v8454((class_1937) self, source, damageSource, calculator, x, y, z, radius, fire, interaction,
            class_2398.field_11236, class_2398.field_11221, class_3417.field_15152);
    }

    /** super.method_8454(..): Level's (this.method_46668(.., true, ..)) or ServerLevel's */
    public static class_1927 superExplode8454(Super8454 self, class_1297 source, class_1282 damageSource,
                                              class_5362 calculator, double x, double y, double z, float radius,
                                              boolean fire, class_1937.class_7867 interaction, class_2394 small,
                                              class_2394 large, class_6880<class_3414> sound) {
        class_1937 level = (class_1937) self;
        if (self instanceof Legacy46668 l) {
            return via46668(level, l, source, damageSource, calculator, x, y, z, radius, fire, interaction, small, large,
                sound);
        }
        if (level instanceof class_3218 server) {
            return madeOn(server, () -> self.cb$super$method_8454(source, damageSource, calculator, x, y, z, radius, fire,
                interaction, small, large, sound));
        }
        return local(level, source, damageSource, calculator, x, y, z, radius, fire, interaction, true, small, large,
            sound);
    }

    /** super.method_46668(..): Level's body, on every level (ServerLevel did not override it) */
    public static class_1927 superExplode46668(Super8454 self, class_1297 source, class_1282 damageSource,
                                               class_5362 calculator, double x, double y, double z, float radius,
                                               boolean fire, class_1937.class_7867 interaction, boolean spawnParticles,
                                               class_2394 small, class_2394 large, class_6880<class_3414> sound) {
        return local((class_1937) self, source, damageSource, calculator, x, y, z, radius, fire, interaction,
            spawnParticles, small, large, sound);
    }

    private CbLevelExplosions() {
    }
}
