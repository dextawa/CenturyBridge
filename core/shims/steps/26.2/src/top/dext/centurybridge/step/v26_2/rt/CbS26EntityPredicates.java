package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Lifecycle;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.DistancePredicate;
import net.minecraft.advancements.predicates.MobEffectsPredicate;
import net.minecraft.advancements.predicates.NbtPredicate;
import net.minecraft.advancements.predicates.SlotsPredicate;
import net.minecraft.advancements.predicates.entity.CubeMobPredicate;
import net.minecraft.advancements.predicates.entity.DistanceToPlayerPredicate;
import net.minecraft.advancements.predicates.entity.EntityEffectsPredicate;
import net.minecraft.advancements.predicates.entity.EntityEquipmentPredicate;
import net.minecraft.advancements.predicates.entity.EntityExactDataComponentsPredicate;
import net.minecraft.advancements.predicates.entity.EntityFlagsPredicate;
import net.minecraft.advancements.predicates.entity.EntityLocationPredicate;
import net.minecraft.advancements.predicates.entity.EntityNbtPredicate;
import net.minecraft.advancements.predicates.entity.EntityPartialComponentsPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.predicates.entity.EntitySlotsPredicate;
import net.minecraft.advancements.predicates.entity.EntitySubPredicate;
import net.minecraft.advancements.predicates.entity.EntityTagPredicate;
import net.minecraft.advancements.predicates.entity.EntityTypePredicate;
import net.minecraft.advancements.predicates.entity.FishingHookPredicate;
import net.minecraft.advancements.predicates.entity.LightningBoltPredicate;
import net.minecraft.advancements.predicates.entity.MovementAffectedByPredicate;
import net.minecraft.advancements.predicates.entity.MovementPredicate;
import net.minecraft.advancements.predicates.entity.PassengerPredicate;
import net.minecraft.advancements.predicates.entity.PeriodicEntityTickPredicate;
import net.minecraft.advancements.predicates.entity.PlayerPredicate;
import net.minecraft.advancements.predicates.entity.RaiderPredicate;
import net.minecraft.advancements.predicates.entity.SheepPredicate;
import net.minecraft.advancements.predicates.entity.SteppingOnPredicate;
import net.minecraft.advancements.predicates.entity.TargetedEntityPredicate;
import net.minecraft.advancements.predicates.entity.TeamPredicate;
import net.minecraft.advancements.predicates.entity.VehiclePredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import top.dext.centurybridge.step.v26_2.facade.EntityPredicateLocationWrapper;

/**
 * 26.1.2's EntityPredicate on 26.2. 26.1.2's was a record of sixteen optional components plus one type-specific
 * EntitySubPredicate (dispatched by a registry of map codecs: lightning, fishing_hook, player, slime, raider,
 * sheep); 26.2's is a map from a registry of codecs to sub-predicates, one per component (EntityPredicate.Builder
 * puts each: entityType -> EntityTypePredicate.CODEC, distance -> DistanceToPlayerPredicate, ...), the type-specific
 * ones included. A 26.1.2 predicate is built as 26.2's Builder builds the same components; each 26.1.2 component is
 * read back from its part.
 */
public final class CbS26EntityPredicates {
    private CbS26EntityPredicates() {}

    // ---------------------------------------------------------------- construction and components

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static EntityPredicate cbNewEntityPredicate(Optional entityType, Optional distanceToPlayer, Optional movement,
            EntityPredicateLocationWrapper location, Optional effects, Optional nbt, Optional flags, Optional equipment,
            Optional subPredicate, Optional periodicTick, Optional vehicle, Optional passenger, Optional targetedEntity,
            Optional team, Optional slots, DataComponentMatchers components) {
        EntityPredicate.Builder b = new EntityPredicate.Builder();
        entityType.ifPresent(v -> b.put(EntityTypePredicate.CODEC, (EntityTypePredicate) v));
        distanceToPlayer.ifPresent(v -> b.put(DistanceToPlayerPredicate.CODEC, new DistanceToPlayerPredicate((DistancePredicate) v)));
        movement.ifPresent(v -> b.put(MovementPredicate.CODEC, (MovementPredicate) v));
        if (location != null) {
            location.located().ifPresent(v -> b.put(EntityLocationPredicate.CODEC, new EntityLocationPredicate(v)));
            location.steppingOn().ifPresent(v -> b.put(SteppingOnPredicate.CODEC, new SteppingOnPredicate(v)));
            location.affectsMovement().ifPresent(v -> b.put(MovementAffectedByPredicate.CODEC, new MovementAffectedByPredicate(v)));
        }
        effects.ifPresent(v -> b.put(EntityEffectsPredicate.CODEC, new EntityEffectsPredicate((MobEffectsPredicate) v)));
        nbt.ifPresent(v -> b.put(EntityNbtPredicate.CODEC, new EntityNbtPredicate((NbtPredicate) v)));
        flags.ifPresent(v -> b.put(EntityFlagsPredicate.CODEC, (EntityFlagsPredicate) v));
        equipment.ifPresent(v -> b.put(EntityEquipmentPredicate.CODEC, (EntityEquipmentPredicate) v));
        subPredicate.ifPresent(v -> b.put(codecOf((EntitySubPredicate) v), (EntitySubPredicate) v));
        periodicTick.ifPresent(v -> b.put(PeriodicEntityTickPredicate.CODEC, new PeriodicEntityTickPredicate((Integer) v)));
        vehicle.ifPresent(v -> b.put(VehiclePredicate.CODEC, new VehiclePredicate((EntityPredicate) v)));
        passenger.ifPresent(v -> b.put(PassengerPredicate.CODEC, new PassengerPredicate((EntityPredicate) v)));
        targetedEntity.ifPresent(v -> b.put(TargetedEntityPredicate.CODEC, new TargetedEntityPredicate((EntityPredicate) v)));
        team.ifPresent(v -> b.put(TeamPredicate.CODEC, new TeamPredicate((String) v)));
        slots.ifPresent(v -> b.put(EntitySlotsPredicate.CODEC, new EntitySlotsPredicate((SlotsPredicate) v)));
        if (components != null) {
            b.components(components);
        }
        return b.build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> Optional<T> part(EntityPredicate p, Codec codec) {
        return Optional.ofNullable((T) ((Map) p.parts).get(codec));
    }

    public static Optional<EntityTypePredicate> entityType(EntityPredicate p) {
        return part(p, EntityTypePredicate.CODEC);
    }

    public static Optional<DistancePredicate> distanceToPlayer(EntityPredicate p) {
        return CbS26EntityPredicates.<DistanceToPlayerPredicate>part(p, DistanceToPlayerPredicate.CODEC).map(DistanceToPlayerPredicate::distance);
    }

    public static Optional<MovementPredicate> movement(EntityPredicate p) {
        return part(p, MovementPredicate.CODEC);
    }

    public static EntityPredicateLocationWrapper location(EntityPredicate p) {
        return new EntityPredicateLocationWrapper(
            CbS26EntityPredicates.<EntityLocationPredicate>part(p, EntityLocationPredicate.CODEC).map(EntityLocationPredicate::predicate),
            CbS26EntityPredicates.<SteppingOnPredicate>part(p, SteppingOnPredicate.CODEC).map(SteppingOnPredicate::predicate),
            CbS26EntityPredicates.<MovementAffectedByPredicate>part(p, MovementAffectedByPredicate.CODEC).map(MovementAffectedByPredicate::predicate));
    }

    public static Optional<MobEffectsPredicate> effects(EntityPredicate p) {
        return CbS26EntityPredicates.<EntityEffectsPredicate>part(p, EntityEffectsPredicate.CODEC).map(EntityEffectsPredicate::effects);
    }

    public static Optional<NbtPredicate> nbt(EntityPredicate p) {
        return CbS26EntityPredicates.<EntityNbtPredicate>part(p, EntityNbtPredicate.CODEC).map(EntityNbtPredicate::nbt);
    }

    public static Optional<EntityFlagsPredicate> flags(EntityPredicate p) {
        return part(p, EntityFlagsPredicate.CODEC);
    }

    public static Optional<EntityEquipmentPredicate> equipment(EntityPredicate p) {
        return part(p, EntityEquipmentPredicate.CODEC);
    }

    /** the type-specific part: the one whose codec is none of the parts 26.1.2 kept as record components */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional<EntitySubPredicate> subPredicate(EntityPredicate p) {
        for (Map.Entry e : ((Map<?, ?>) p.parts).entrySet()) {
            if (!componentCodecs().contains(e.getKey())) {
                return Optional.of((EntitySubPredicate) e.getValue());
            }
        }
        return Optional.empty();
    }

    public static Optional<Integer> periodicTick(EntityPredicate p) {
        return CbS26EntityPredicates.<PeriodicEntityTickPredicate>part(p, PeriodicEntityTickPredicate.CODEC).map(PeriodicEntityTickPredicate::periodicTick);
    }

    public static Optional<EntityPredicate> vehicle(EntityPredicate p) {
        return CbS26EntityPredicates.<VehiclePredicate>part(p, VehiclePredicate.CODEC).map(VehiclePredicate::vehicle);
    }

    public static Optional<EntityPredicate> passenger(EntityPredicate p) {
        return CbS26EntityPredicates.<PassengerPredicate>part(p, PassengerPredicate.CODEC).map(PassengerPredicate::passenger);
    }

    public static Optional<EntityPredicate> targetedEntity(EntityPredicate p) {
        return CbS26EntityPredicates.<TargetedEntityPredicate>part(p, TargetedEntityPredicate.CODEC).map(TargetedEntityPredicate::targetedEntity);
    }

    public static Optional<String> team(EntityPredicate p) {
        return CbS26EntityPredicates.<TeamPredicate>part(p, TeamPredicate.CODEC).map(TeamPredicate::team);
    }

    public static Optional<SlotsPredicate> slots(EntityPredicate p) {
        return CbS26EntityPredicates.<EntitySlotsPredicate>part(p, EntitySlotsPredicate.CODEC).map(EntitySlotsPredicate::slots);
    }

    /** 26.1.2's components: the exact and partial component predicates (DataComponentMatchers.ANY's when absent) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static DataComponentMatchers components(EntityPredicate p) {
        Optional<EntityExactDataComponentsPredicate> exact = part(p, EntityExactDataComponentsPredicate.CODEC);
        Optional<EntityPartialComponentsPredicate> partial = part(p, EntityPartialComponentsPredicate.CODEC);
        if (exact.isEmpty() && partial.isEmpty()) {
            return DataComponentMatchers.ANY;
        }
        return new DataComponentMatchers(exact.map(EntityExactDataComponentsPredicate::predicate).orElse(DataComponentMatchers.ANY.exact()),
            (Map) partial.map(EntityPartialComponentsPredicate::predicates).orElse(Map.of()));
    }

    private static Set<Codec<?>> componentCodecs;

    private static synchronized Set<Codec<?>> componentCodecs() {
        if (componentCodecs == null) {
            Set<Codec<?>> s = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
            s.addAll(java.util.List.of(EntityTypePredicate.CODEC, DistanceToPlayerPredicate.CODEC, MovementPredicate.CODEC,
                EntityLocationPredicate.CODEC, SteppingOnPredicate.CODEC, MovementAffectedByPredicate.CODEC, EntityEffectsPredicate.CODEC,
                EntityNbtPredicate.CODEC, EntityFlagsPredicate.CODEC, EntityEquipmentPredicate.CODEC, PeriodicEntityTickPredicate.CODEC,
                VehiclePredicate.CODEC, PassengerPredicate.CODEC, TargetedEntityPredicate.CODEC, TeamPredicate.CODEC, EntitySlotsPredicate.CODEC,
                EntityExactDataComponentsPredicate.CODEC, EntityPartialComponentsPredicate.CODEC, EntityTagPredicate.CODEC));
            componentCodecs = s;
        }
        return componentCodecs;
    }

    // ---------------------------------------------------------------- EntityPredicate.Builder.subPredicate

    /** a 26.1.2 Builder subclass that overrides subPredicate (adapterHooks adds this to it) */
    public interface LegacyBuilder {
        EntityPredicate.Builder subPredicate(EntitySubPredicate predicate);
    }

    public static EntityPredicate.Builder subPredicate(EntityPredicate.Builder b, EntitySubPredicate predicate) {
        if (b instanceof LegacyBuilder l) {
            return l.subPredicate(predicate);
        }
        return subPredicateSuper(b, predicate);
    }

    /** 26.1.2's Builder.subPredicate: the type-specific part */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static EntityPredicate.Builder subPredicateSuper(EntityPredicate.Builder b, EntitySubPredicate predicate) {
        return b.put((Codec) codecOf(predicate), predicate);
    }

    // ---------------------------------------------------------------- MobEffectsPredicate.Builder.build

    public static Optional<MobEffectsPredicate> build(MobEffectsPredicate.Builder b) {
        return Optional.of(b.build());
    }

    /** a mod's super.build(): 26.1.2's body (26.2's build would dispatch back into the mod's own override) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional<MobEffectsPredicate> buildSuper(MobEffectsPredicate.Builder b) {
        return Optional.of(new MobEffectsPredicate((Map) b.effectMap.build()));
    }

    /** a 26.1.2 override's answer as 26.2's build(): an empty Optional is no effect requirement */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static MobEffectsPredicate unwrapEffects(Optional o) {
        return o != null && o.isPresent() ? (MobEffectsPredicate) o.get() : new MobEffectsPredicate(Map.of());
    }

    // ---------------------------------------------------------------- type-specific sub-predicates: map codecs

    /** a 26.1.2 sub-predicate class of a mod: it declares codec() itself (adapterHooks adds this to it) */
    public interface LegacySub {
        MapCodec<? extends EntitySubPredicate> codec();
    }

    @SuppressWarnings("rawtypes")
    private static MapCodec mapOf(Codec codec) {
        return ((MapCodec.MapCodecCodec) codec).codec();
    }

    public static MapCodec<LightningBoltPredicate> LIGHTNING() {
        return mapOf(LightningBoltPredicate.CODEC);
    }

    public static MapCodec<FishingHookPredicate> FISHING_HOOK() {
        return mapOf(FishingHookPredicate.CODEC);
    }

    public static MapCodec<PlayerPredicate> PLAYER() {
        return mapOf(PlayerPredicate.CODEC);
    }

    public static MapCodec<CubeMobPredicate> SLIME() {
        return mapOf(CubeMobPredicate.CODEC);
    }

    public static MapCodec<RaiderPredicate> RAIDER() {
        return mapOf(RaiderPredicate.CODEC);
    }

    public static MapCodec<SheepPredicate> SHEEP() {
        return mapOf(SheepPredicate.CODEC);
    }

    /** 26.1.2's EntitySubPredicates.bootstrap: its class initialisation registered the six; it answered LIGHTNING */
    @SuppressWarnings("rawtypes")
    public static MapCodec bootstrap(Registry registry) {
        mirror();
        return LIGHTNING();
    }

    /** 26.1.2's sub-predicate codec(): a mod's own, else the vanilla class's map codec */
    @SuppressWarnings("rawtypes")
    public static MapCodec codec(EntitySubPredicate p) {
        if (p instanceof LegacySub l) {
            return l.codec();
        }
        Codec c = vanillaCodec(p);
        if (c == null) {
            throw new IllegalArgumentException("not a 26.1.2 type-specific entity sub-predicate: " + p);
        }
        return mapOf(c);
    }

    @SuppressWarnings("rawtypes")
    public static MapCodec codec(LightningBoltPredicate p) {
        return codec((EntitySubPredicate) p);
    }

    @SuppressWarnings("rawtypes")
    public static MapCodec codec(FishingHookPredicate p) {
        return codec((EntitySubPredicate) p);
    }

    @SuppressWarnings("rawtypes")
    public static MapCodec codec(PlayerPredicate p) {
        return codec((EntitySubPredicate) p);
    }

    @SuppressWarnings("rawtypes")
    public static MapCodec codec(CubeMobPredicate p) {
        return codec((EntitySubPredicate) p);
    }

    @SuppressWarnings("rawtypes")
    public static MapCodec codec(RaiderPredicate p) {
        return codec((EntitySubPredicate) p);
    }

    @SuppressWarnings("rawtypes")
    public static MapCodec codec(SheepPredicate p) {
        return codec((EntitySubPredicate) p);
    }

    @SuppressWarnings("rawtypes")
    private static Codec vanillaCodec(EntitySubPredicate p) {
        if (p instanceof LightningBoltPredicate) {
            return LightningBoltPredicate.CODEC;
        }
        if (p instanceof FishingHookPredicate) {
            return FishingHookPredicate.CODEC;
        }
        if (p instanceof PlayerPredicate) {
            return PlayerPredicate.CODEC;
        }
        if (p instanceof CubeMobPredicate) {
            return CubeMobPredicate.CODEC;
        }
        if (p instanceof RaiderPredicate) {
            return RaiderPredicate.CODEC;
        }
        if (p instanceof SheepPredicate) {
            return SheepPredicate.CODEC;
        }
        return null;
    }

    /** the key of a sub-predicate in 26.2's EntityPredicate: the codec 26.2's registry holds for it */
    @SuppressWarnings("rawtypes")
    static Codec codecOf(EntitySubPredicate p) {
        if (p instanceof LegacySub l) {
            return codecFor(l.codec());
        }
        Codec c = vanillaCodec(p);
        if (c == null) {
            throw new IllegalArgumentException("not a 26.1.2 type-specific entity sub-predicate: " + p);
        }
        return c;
    }

    /** a mod's map codec as the codec 26.2's registry holds for it (one codec per map codec) */
    private static final Map<MapCodec<?>, Codec<?>> CODECS = new IdentityHashMap<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static synchronized Codec codecFor(MapCodec m) {
        return CODECS.computeIfAbsent(m, k -> ((MapCodec) k).codec());
    }

    private static Codec<EntitySubPredicate> subCodec;

    /** 26.1.2's EntitySubPredicate.CODEC: the type-specific sub-predicate, dispatched by its map codec's id */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static synchronized Codec<EntitySubPredicate> subPredicateCodec() {
        if (subCodec == null) {
            subCodec = ((Registry) mirror()).byNameCodec().dispatch(p -> codec((EntitySubPredicate) p), Function.identity());
        }
        return subCodec;
    }

    // ---------------------------------------------------------------- the registry

    /**
     * 26.1.2's entity_sub_predicate_type registry as 26.1.2 code holds it: map codecs, its six under 26.1.2's ids. A
     * 26.1.2 registration goes into 26.2's registry too (the map codec's codec, under the same id), so 26.2's
     * EntityPredicate codec writes and reads the mod's sub-predicate.
     */
    public static final class Mirror extends MappedRegistry<MapCodec<? extends EntitySubPredicate>> {
        final Registry<Codec<? extends EntitySubPredicate>> real;

        @SuppressWarnings({"unchecked", "rawtypes"})
        Mirror(Registry<Codec<? extends EntitySubPredicate>> real) {
            super((ResourceKey) real.key(), Lifecycle.stable());
            this.real = real;
            vanilla("lightning", LIGHTNING());
            vanilla("fishing_hook", FISHING_HOOK());
            vanilla("player", PLAYER());
            vanilla("slime", SLIME());
            vanilla("raider", RAIDER());
            vanilla("sheep", SHEEP());
            if (real instanceof CbS26Processors.Frozen f && f.cbS26$frozen()) {
                freeze();
            }
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void vanilla(String id, MapCodec codec) {
            super.register(ResourceKey.create((ResourceKey) key(), Identifier.withDefaultNamespace(id)), codec, RegistrationInfo.BUILT_IN);
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Holder.Reference<MapCodec<? extends EntitySubPredicate>> register(ResourceKey<MapCodec<? extends EntitySubPredicate>> key,
                MapCodec<? extends EntitySubPredicate> value, RegistrationInfo info) {
            ((WritableRegistry) real).register((ResourceKey) key, codecFor(value), info);
            return super.register(key, value, info);
        }
    }

    private static Mirror mirror;

    /** BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, as 26.1.2 code holds it */
    public static synchronized Registry<?> mirror() {
        if (mirror == null) {
            mirror = new Mirror(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE);
        }
        return mirror;
    }

    /** mixin/CbS26ProcessorRegistryMixin: a registry froze; the mirror of 26.2's entity_sub_predicate_type freezes with it */
    public static synchronized void frozen(Registry<?> registry) {
        if (mirror != null && mirror.real == registry) {
            mirror.freeze();
        }
    }
}
