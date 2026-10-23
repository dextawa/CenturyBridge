package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.class_10209;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1541;
import net.minecraft.class_1542;
import net.minecraft.class_156;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1927;
import net.minecraft.class_1928;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3532;
import net.minecraft.class_3610;
import net.minecraft.class_3695;
import net.minecraft.class_4770;
import net.minecraft.class_5134;
import net.minecraft.class_5361;
import net.minecraft.class_5362;
import net.minecraft.class_5712;
import net.minecraft.class_5819;
import net.minecraft.class_6880;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelGameRules;

/**
 * 1.21.1's Explosion (net/minecraft/class_1927), a class in 1.21.1 and an interface in 1.21.2 (whose one vanilla
 * implementation is ServerExplosion). A 1.21.1 mod's {@code new Explosion(..)} builds this (ctorRedirects through
 * CbLevelExplosions), a 1.21.1 subclass extends it (superRenames), and it is a 1.21.2 Explosion, so it goes wherever
 * vanilla takes one (damage calculators, onExplosionHit, ignoreExplosion, damage sources).
 *
 * <p>It keeps 1.21.1's two phases: {@link #method_8348 explode()} works out the blocks to blow (toBlow) and hurts and
 * pushes the entities; {@link #method_8350 finalizeExplosion(spawnParticles)} plays the sound (client), adds the
 * particle, makes every block of toBlow take the hit and lights fires. Each body is 1.21.1's, read off its bytecode,
 * on 1.21.2's API:
 * <ul>
 * <li>1.21.2's ExplosionDamageCalculator.getEntityDamageAmount takes the seen percent 1.21.1's computed itself from
 *     the explosion's center (getSeenPercent, the same body as 1.21.2's ServerExplosion.getSeenPercent);</li>
 * <li>entity.hurt is 1.21.2's Entity.hurt (hurtServer on a server level, hurtClient on a client one -- 1.21.1's
 *     LivingEntity.hurt refused on the client the same way);</li>
 * <li>onExplosionHit takes a ServerLevel in 1.21.2. On a client level the block takes 1.21.1's BlockBehaviour
 *     .onExplosionHit there: unless it is air or the explosion only triggers blocks, it becomes air (flags 3); the drops
 *     were server-only in 1.21.1 too, and 1.21.2 has no client wasExploded to tell the block (1.21.1's vanilla blocks
 *     did nothing in it on the client);</li>
 * <li>the profiler is 1.21.2's Profiler.get(); game rules are the level's as 1.21.1 answered them (CbLevelGameRules).</li>
 * </ul>
 * 1.21.2's interface asks two things 1.21.1 never did: {@link #method_64504 level()} is the ServerLevel, null for an
 * explosion on a client level; {@link #method_61722 shouldAffectBlocklikeEntities()} answers as 1.21.2's ServerExplosion
 * does for the same source, interaction and game rules.
 */
public class Explosion implements class_1927 {
    private static final class_5362 EXPLOSION_DAMAGE_CALCULATOR = new class_5362();
    private static final int MAX_DROPS_PER_COMBINED_STACK = 16;
    private final boolean fire;
    private final class_1927.class_4179 blockInteraction;
    private final class_5819 random = class_5819.method_43047();
    private final class_1937 level;
    private final double x;
    private final double y;
    private final double z;
    private final class_1297 source;
    private final float radius;
    private final class_1282 damageSource;
    private final class_5362 damageCalculator;
    private final class_2394 smallExplosionParticles;
    private final class_2394 largeExplosionParticles;
    private final class_6880<class_3414> explosionSound;
    private final ObjectArrayList<class_2338> toBlow = new ObjectArrayList<>();
    private final Map<class_1657, class_243> hitPlayers = Maps.newHashMap();

    public Explosion(class_1937 level, class_1297 source, double x, double y, double z, float radius, List<class_2338> toBlow,
                     class_1927.class_4179 blockInteraction, class_2394 small, class_2394 large, class_6880<class_3414> sound) {
        this(level, source, class_1927.method_55108(level, source), null, x, y, z, radius, false, blockInteraction, small, large,
            sound);
        this.toBlow.addAll(toBlow);
    }

    public Explosion(class_1937 level, class_1297 source, double x, double y, double z, float radius, boolean fire,
                     class_1927.class_4179 blockInteraction, List<class_2338> toBlow) {
        this(level, source, x, y, z, radius, fire, blockInteraction);
        this.toBlow.addAll(toBlow);
    }

    public Explosion(class_1937 level, class_1297 source, double x, double y, double z, float radius, boolean fire,
                     class_1927.class_4179 blockInteraction) {
        this(level, source, class_1927.method_55108(level, source), null, x, y, z, radius, fire, blockInteraction,
            class_2398.field_11236, class_2398.field_11221, class_3417.field_15152);
    }

    public Explosion(class_1937 level, class_1297 source, class_1282 damageSource, class_5362 damageCalculator, double x,
                     double y, double z, float radius, boolean fire, class_1927.class_4179 blockInteraction, class_2394 small,
                     class_2394 large, class_6880<class_3414> sound) {
        this.level = level;
        this.source = source;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.z = z;
        this.fire = fire;
        this.blockInteraction = blockInteraction;
        this.damageSource = damageSource == null ? level.method_48963().method_48807(this) : damageSource;
        this.damageCalculator = damageCalculator == null ? this.makeDamageCalculator(source) : damageCalculator;
        this.smallExplosionParticles = small;
        this.largeExplosionParticles = large;
        this.explosionSound = sound;
    }

    private class_5362 makeDamageCalculator(class_1297 entity) {
        return entity == null ? EXPLOSION_DAMAGE_CALCULATOR : new class_5361(entity);
    }

    /** getSeenPercent(center, entity): 1.21.2's ServerExplosion.getSeenPercent is 1.21.1's body */
    public static float method_17752(class_243 center, class_1297 entity) {
        return net.minecraft.class_9892.method_61731(center, entity);
    }

    @Override
    public float method_55107() {
        return this.radius;
    }

    @Override
    public class_243 method_55109() {
        return new class_243(this.x, this.y, this.z);
    }

    /** explode(): the blocks to blow, and the entities hurt and pushed */
    public void method_8348() {
        this.level.method_43275(this.source, class_5712.field_28178, new class_243(this.x, this.y, this.z));
        Set<class_2338> set = Sets.newHashSet();
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d = (float) j / 15.0F * 2.0F - 1.0F;
                        double e = (float) k / 15.0F * 2.0F - 1.0F;
                        double f = (float) l / 15.0F * 2.0F - 1.0F;
                        double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;
                        float h = this.radius * (0.7F + this.level.field_9229.method_43057() * 0.6F);
                        double m = this.x;
                        double n = this.y;
                        double o = this.z;
                        for (; h > 0.0F; h -= 0.22500001F) {
                            class_2338 pos = class_2338.method_49637(m, n, o);
                            class_2680 state = this.level.method_8320(pos);
                            class_3610 fluid = this.level.method_8316(pos);
                            if (!this.level.method_24794(pos)) {
                                break;
                            }
                            Optional<Float> resistance = this.damageCalculator.method_29555(this, this.level, pos, state, fluid);
                            if (resistance.isPresent()) {
                                h -= (resistance.get() + 0.3F) * 0.3F;
                            }
                            if (h > 0.0F && this.damageCalculator.method_29554(this, this.level, pos, state, h)) {
                                set.add(pos);
                            }
                            m += d * 0.30000001192092896;
                            n += e * 0.30000001192092896;
                            o += f * 0.30000001192092896;
                        }
                    }
                }
            }
        }
        this.toBlow.addAll(set);
        float q = this.radius * 2.0F;
        int k = class_3532.method_15357(this.x - (double) q - 1.0);
        int l = class_3532.method_15357(this.x + (double) q + 1.0);
        int r = class_3532.method_15357(this.y - (double) q - 1.0);
        int s = class_3532.method_15357(this.y + (double) q + 1.0);
        int t = class_3532.method_15357(this.z - (double) q - 1.0);
        int u = class_3532.method_15357(this.z + (double) q + 1.0);
        List<class_1297> list = this.level.method_8335(this.source, new class_238(k, r, t, l, s, u));
        class_243 center = new class_243(this.x, this.y, this.z);
        for (class_1297 entity : list) {
            if (entity.method_5659(this)) {
                continue;
            }
            double v = Math.sqrt(entity.method_5707(center)) / (double) q;
            if (v > 1.0) {
                continue;
            }
            double w = entity.method_23317() - this.x;
            double ex = (entity instanceof class_1541 ? entity.method_23318() : entity.method_23320()) - this.y;
            double ey = entity.method_23321() - this.z;
            double ez = Math.sqrt(w * w + ex * ex + ey * ey);
            if (ez == 0.0) {
                continue;
            }
            w /= ez;
            ex /= ez;
            ey /= ez;
            if (this.damageCalculator.method_55504(this, entity)) {
                entity.method_64419(this.damageSource,
                    this.damageCalculator.method_55115(this, entity, method_17752(center, entity)));
            }
            double aa = (1.0 - v) * (double) method_17752(center, entity)
                * (double) this.damageCalculator.method_57007(entity);
            double ab;
            if (entity instanceof class_1309 living) {
                ab = aa * (1.0 - living.method_45325(class_5134.field_51580));
            } else {
                ab = aa;
            }
            w *= ab;
            ex *= ab;
            ey *= ab;
            class_243 push = new class_243(w, ex, ey);
            entity.method_18799(entity.method_18798().method_1019(push));
            if (entity instanceof class_1657 player && !player.method_7325()
                && (!player.method_7337() || !player.method_31549().field_7479)) {
                this.hitPlayers.put(player, push);
            }
            entity.method_56918(this.source);
        }
    }

    /** finalizeExplosion(spawnParticles): sound (client), particle, blocks, fire */
    public void method_8350(boolean spawnParticles) {
        if (this.level.method_8608()) {
            this.level.method_8486(this.x, this.y, this.z, this.explosionSound.comp_349(), class_3419.field_15245, 4.0F,
                (1.0F + (this.level.field_9229.method_43057() - this.level.field_9229.method_43057()) * 0.2F) * 0.7F, false);
        }
        boolean interacts = this.method_46667();
        if (spawnParticles) {
            class_2394 particle = !(this.radius < 2.0F) && interacts ? this.largeExplosionParticles : this.smallExplosionParticles;
            this.level.method_8406(particle, this.x, this.y, this.z, 1.0, 0.0, 0.0);
        }
        if (interacts) {
            class_3695 profiler = class_10209.method_64146();
            profiler.method_15396("explosion_blocks");
            List<Pair<class_1799, class_2338>> drops = new ArrayList<>();
            class_156.method_43028(this.toBlow, this.level.field_9229);
            for (class_2338 pos : this.toBlow) {
                this.hit(this.level.method_8320(pos), pos, drops);
            }
            for (Pair<class_1799, class_2338> drop : drops) {
                class_2248.method_9577(this.level, drop.getSecond(), drop.getFirst());
            }
            profiler.method_15407();
        }
        if (this.fire) {
            for (class_2338 pos : this.toBlow) {
                if (this.random.method_43048(3) == 0 && this.level.method_8320(pos).method_26215()
                    && this.level.method_8320(pos.method_10074()).method_26216()) {
                    this.level.method_8501(pos, class_4770.method_24416(this.level, pos));
                }
            }
        }
    }

    /** state.onExplosionHit(level, pos, this, drop collector) -- on a client level, 1.21.1's base behaviour there */
    private void hit(class_2680 state, class_2338 pos, List<Pair<class_1799, class_2338>> drops) {
        if (this.level instanceof class_3218 server) {
            state.method_55225(server, pos, this, (stack, at) -> addOrAppendStack(drops, stack, at));
        } else if (!state.method_26215() && this.blockInteraction != class_1927.class_4179.field_47331) {
            this.level.method_8652(pos, class_2246.field_10124.method_9564(), 3);
        }
    }

    private static void addOrAppendStack(List<Pair<class_1799, class_2338>> drops, class_1799 stack, class_2338 pos) {
        for (int i = 0; i < drops.size(); ++i) {
            Pair<class_1799, class_2338> pair = drops.get(i);
            class_1799 held = pair.getFirst();
            if (class_1542.method_24017(held, stack)) {
                drops.set(i, Pair.of(class_1542.method_24018(held, stack, MAX_DROPS_PER_COMBINED_STACK), pair.getSecond()));
                if (stack.method_7960()) {
                    return;
                }
            }
        }
        drops.add(Pair.of(stack, pos));
    }

    /** interactsWithBlocks() */
    public boolean method_46667() {
        return this.blockInteraction != class_1927.class_4179.field_40878;
    }

    /** getHitPlayers() */
    public Map<class_1657, class_243> method_8351() {
        return this.hitPlayers;
    }

    @Override
    public class_1309 method_8347() {
        return class_1927.method_55110(this.source);
    }

    @Override
    public class_1297 method_46406() {
        return this.source;
    }

    /** clearToBlow() */
    public void method_8352() {
        this.toBlow.clear();
    }

    /** getToBlow() */
    public List<class_2338> method_8346() {
        return this.toBlow;
    }

    @Override
    public class_1927.class_4179 method_55111() {
        return this.blockInteraction;
    }

    /** getSmallExplosionParticles() */
    public class_2394 method_55112() {
        return this.smallExplosionParticles;
    }

    /** getLargeExplosionParticles() */
    public class_2394 method_55113() {
        return this.largeExplosionParticles;
    }

    /** getExplosionSound() */
    public class_6880<class_3414> method_55114() {
        return this.explosionSound;
    }

    @Override
    public boolean method_60274() {
        if (this.blockInteraction != class_1927.class_4179.field_47331 || this.level.method_8608()) {
            return false;
        }
        return this.source == null || this.source.method_5864() != class_1299.field_49075
            || CbLevelGameRules.of(this.level).method_8355(class_1928.field_19388);
    }

    /** level(): 1.21.2's explosion is a server one; an explosion on a client level has none */
    @Override
    public class_3218 method_64504() {
        return this.level instanceof class_3218 server ? server : null;
    }

    /** shouldAffectBlocklikeEntities(), as 1.21.2's ServerExplosion answers it */
    @Override
    public boolean method_61722() {
        boolean griefing = CbLevelGameRules.of(this.level).method_8355(class_1928.field_19388);
        boolean dry = this.source == null || !this.source.method_5799();
        boolean notWind = this.source == null
            || this.source.method_5864() != class_1299.field_49075 && this.source.method_5864() != class_1299.field_47243;
        return griefing ? dry && notWind : this.blockInteraction.method_61723() && dry && notWind;
    }
}
