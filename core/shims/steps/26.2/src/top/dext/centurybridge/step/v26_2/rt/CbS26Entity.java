package top.dext.centurybridge.step.v26_2.rt;

import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PostSpawnProcessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;

/**
 * 26.1.2 entity API on 26.2 (javap of both releases' bodies).
 *
 * <ul>
 * <li>Spawn configuration: 26.1.2 passed a Consumer of the new entity, 26.2 a PostSpawnProcessor (the same single
 *     method, apply(T)). Values are adapted both ways; an adapter handed back is unwrapped to what it wraps, so a
 *     26.1.2 configuration round-tripping through 26.2 (createDefaultStackConfig, then spawn) stays the object
 *     26.2 built.</li>
 * <li>A spawn reason: 26.2 passes an EntitySpawnRequest(reason, ignoreChecks); 26.2's own reason-only overloads
 *     (create(Level, EntitySpawnReason), loadEntityRecursive(ValueInput, ..., EntitySpawnReason, ...)) build
 *     new EntitySpawnRequest(reason, false), which is what 26.1.2's reason-only shapes meant.</li>
 * <li>knockback(strength, x, z): 26.2's knockback takes the DamageSource and amount that caused it; the base body
 *     is 26.1.2's and reads neither, the overrides 26.1.2 had (EnderDragon, Creaking) are on the five-argument
 *     shape, and the only reader (26.2's SulfurCube) uses them only when the source has an entity. 26.1.2's call
 *     carries no cause: it passes the entity's own generic() source (no entity) and amount 0.</li>
 * <li>causeExtraKnockback(target, strength, look): 26.1.2's body, knockback through 26.1.2's shape (above).</li>
 * <li>currentExplosionCause: 26.2 keeps the field on ServerPlayer only (26.1.2's only reader and writer of it);
 *     on any other living entity 26.1.2's field is kept here per entity, cleared where 26.1.2's
 *     resetCurrentImpulseContext cleared it (mixin CbS26ImpulseResetMixin).</li>
 * </ul>
 */
public final class CbS26Entity {
    private CbS26Entity() {}

    // ---- Consumer <-> PostSpawnProcessor

    private record AsProcessor<T extends Entity>(Consumer<T> consumer) implements PostSpawnProcessor<T> {
        @Override
        public void apply(T entity) {
            this.consumer.accept(entity);
        }
    }

    private record AsConsumer<T extends Entity>(PostSpawnProcessor<T> processor) implements Consumer<T> {
        @Override
        public void accept(T entity) {
            this.processor.apply(entity);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static PostSpawnProcessor toProcessor(Consumer c) {
        if (c == null) {
            return null;
        }
        return c instanceof AsConsumer a ? a.processor() : new AsProcessor(c);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Consumer toConsumer(PostSpawnProcessor p) {
        if (p == null) {
            return null;
        }
        return p instanceof AsProcessor a ? a.consumer() : new AsConsumer(p);
    }

    public static Optional<EntityType<?>> byString(String id) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.tryParse(id));
    }

    public static EntitySpawnRequest request(EntitySpawnReason reason) {
        return new EntitySpawnRequest(reason, false);
    }

    // ---- knockback

    /** the cause 26.1.2's knockback calls carry: none -- the entity's generic() source (no entity) */
    public static DamageSource noCause(LivingEntity self) {
        return self.damageSources().generic();
    }

    /** frameDefaults: blockUsingItem's attacker-side source when the mod's old override is entered directly */
    public static DamageSource noCauseBlocking(LivingEntity self) {
        return noCause(self);
    }

    public static void knockback(LivingEntity self, double strength, double x, double z) {
        self.knockback(strength, x, z, noCause(self), 0.0F);
    }

    /** 26.1.2's body */
    public static void causeExtraKnockback(LivingEntity self, Entity target, float strength, net.minecraft.world.phys.Vec3 oldMovement) {
        if (strength > 0.0F && target instanceof LivingEntity living) {
            float rad = self.getYRot() * 0.017453292F;
            knockback(living, strength, Mth.sin(rad), -Mth.cos(rad));
            self.setDeltaMovement(self.getDeltaMovement().multiply(0.6, 1.0, 0.6));
        }
    }

    public static void blockUsingItem(LivingEntity self, ServerLevel level, LivingEntity attacker) {
        self.blockUsingItem(level, attacker, noCause(self), 0.0F);
    }

    public static void blockedByItem(LivingEntity self, LivingEntity attacker) {
        self.blockedByItem(attacker, noCause(attacker), 0.0F);
    }

    // ---- fields

    public static String TAG_HURT_BY_TIMESTAMP() {
        return "HurtByTimestamp";
    }

    private static final WeakHashMap<LivingEntity, Entity> EXPLOSION_CAUSE = new WeakHashMap<>();

    public static Entity getCurrentExplosionCause(LivingEntity self) {
        if (self instanceof ServerPlayer player) {
            return player.currentExplosionCause;
        }
        synchronized (EXPLOSION_CAUSE) {
            return EXPLOSION_CAUSE.get(self);
        }
    }

    public static void setCurrentExplosionCause(LivingEntity self, Entity cause) {
        if (self instanceof ServerPlayer player) {
            player.currentExplosionCause = cause;
            return;
        }
        synchronized (EXPLOSION_CAUSE) {
            if (cause == null) {
                EXPLOSION_CAUSE.remove(self);
            } else {
                EXPLOSION_CAUSE.put(self, cause);
            }
        }
    }

    /** 26.1.2's resetCurrentImpulseContext also cleared the field (called from CbS26ImpulseResetMixin) */
    public static void clearExplosionCause(LivingEntity self) {
        if (!(self instanceof ServerPlayer)) {
            synchronized (EXPLOSION_CAUSE) {
                EXPLOSION_CAUSE.remove(self);
            }
        }
    }

    // ---- spawn shapes with a Consumer (26.1.2) on 26.2's PostSpawnProcessor shapes

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Entity spawn(EntityType type, ServerLevel level, Consumer config, BlockPos pos, EntitySpawnReason reason,
                               boolean alignPosition, boolean invertY) {
        return type.spawn(level, toProcessor(config), pos, reason, alignPosition, invertY);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Entity create(EntityType type, ServerLevel level, Consumer config, BlockPos pos, EntitySpawnReason reason,
                                boolean alignPosition, boolean invertY) {
        return type.create(level, toProcessor(config), pos, reason, alignPosition, invertY);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Consumer createDefaultStackConfig(Level level, ItemStack stack, LivingEntity user) {
        return toConsumer(EntityType.createDefaultStackConfig(level, stack, user));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Consumer appendDefaultStackConfig(Consumer config, Level level, ItemStack stack, LivingEntity user) {
        return toConsumer(EntityType.appendDefaultStackConfig(toProcessor(config), level, stack, user));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Consumer appendComponentsConfig(Consumer config, ItemStack stack) {
        return toConsumer(EntityType.appendComponentsConfig(toProcessor(config), stack));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Consumer appendCustomEntityStackConfig(Consumer config, Level level, ItemStack stack, LivingEntity user) {
        return toConsumer(EntityType.appendCustomEntityStackConfig(toProcessor(config), level, stack, user));
    }

    public static Optional<Entity> create(ValueInput input, Level level, EntitySpawnReason reason) {
        return EntityType.create(input, level, request(reason));
    }

    public static Entity loadEntityRecursive(CompoundTag tag, Level level, EntitySpawnReason reason, EntityProcessor processor) {
        return EntityType.loadEntityRecursive(tag, level, request(reason), processor);
    }
}
