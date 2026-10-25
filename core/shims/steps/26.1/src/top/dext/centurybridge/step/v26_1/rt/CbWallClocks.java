package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.Optional;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyframeTrack;
import net.minecraft.world.attribute.AttributeType;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.LerpFunction;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.predicates.TimeCheck;
import net.minecraft.world.timeline.AttributeTrack;
import net.minecraft.world.timeline.AttributeTrackSampler;
import net.minecraft.world.timeline.Timeline;

/**
 * 1.21.11's timeline / environment-attribute / TimeCheck API ran on one time: the level's day time
 * (Level.getDayTime(), the overworld's), or a LongSupplier of it the caller handed in. 26.1 runs every timeline on
 * a world clock (Holder&lt;WorldClock&gt;) read through a ClockManager. The 1.21.11 shapes map onto 26.1's:
 * <ul>
 * <li>a clock 1.21.11 never named is the overworld clock (WorldClocks.OVERWORLD) -- the clock 1.21.11's day time is;</li>
 * <li>a LongSupplier of the time becomes a ClockManager answering it for the clock asked
 *     ({@link #clockManager});</li>
 * <li>a Level becomes the level's own ClockManager (Level.clockManager()), which 26.1's timelines read.</li>
 * </ul>
 */
public final class CbWallClocks {

    private CbWallClocks() {
    }

    /** the overworld clock: the registry's holder once registries are up, else one that looks it up on first use */
    public static Holder<WorldClock> overworldClock() {
        RegistryAccess access = CbWallRegistries.accessOrNull();
        if (access != null) {
            return access.lookupOrThrow(Registries.WORLD_CLOCK).getOrThrow(WorldClocks.OVERWORLD);
        }
        return new Lazy<>(WorldClocks.OVERWORLD);
    }

    /** a 1.21.11 time supplier as the ClockManager 26.1 asks: the supplier's time, for whichever clock */
    public static ClockManager clockManager(LongSupplier time) {
        return clock -> time.getAsLong();
    }

    /** 1.21.11 Timeline.builder() (class_12329.method_76478): a timeline on the overworld clock */
    public static Timeline.Builder timelineBuilder() {
        return Timeline.builder(overworldClock());
    }

    /** 1.21.11 Timeline.getCurrentTicks(level) (class_12329.method_76481): the ticks into the period, on the level's clocks */
    public static long getCurrentTicks(Timeline timeline, Level level) {
        return timeline.getCurrentTicks(level.clockManager());
    }

    /** 1.21.11 Timeline.getTotalTicks(level) (class_12329.method_76485): the timeline's clock time on the level */
    public static long getTotalTicks(Timeline timeline, Level level) {
        return timeline.getTotalTicks(level.clockManager());
    }

    /** 1.21.11 Timeline.createTrackSampler(attribute, dayTime) (class_12329.method_76479) */
    public static <V> AttributeTrackSampler<V, ?> createTrackSampler(Timeline timeline, EnvironmentAttribute<V> attribute,
                                                                     LongSupplier dayTime) {
        return timeline.createTrackSampler(attribute, clockManager(dayTime));
    }

    /** 1.21.11 AttributeTrack.bakeSampler(attribute, period, dayTime) (class_12327.method_76473) */
    public static <V, A> AttributeTrackSampler<V, A> bakeSampler(AttributeTrack<V, A> track, EnvironmentAttribute<V> attribute,
                                                                 Optional<Integer> period, LongSupplier dayTime) {
        return track.bakeSampler(attribute, overworldClock(), period, clockManager(dayTime));
    }

    /** 1.21.11 EnvironmentAttributeSystem.Builder.addTimelineLayer(timeline, dayTime) (class_12205$class_12314.method_76420) */
    public static EnvironmentAttributeSystem.Builder addTimelineLayer(EnvironmentAttributeSystem.Builder builder,
                                                                      Holder<Timeline> timeline, LongSupplier dayTime) {
        return builder.addTimelineLayer(timeline, clockManager(dayTime));
    }

    /** 1.21.11 new AttributeTrackSampler(period, modifier, track, lerp, dayTime) (class_12328) */
    public static <V, A> AttributeTrackSampler<V, A> newAttributeTrackSampler(Optional<Integer> period, AttributeModifier<V, A> modifier,
                                                                             KeyframeTrack<A> track, LerpFunction<A> lerp,
                                                                             LongSupplier dayTime) {
        return new AttributeTrackSampler<>(overworldClock(), period, modifier, track, lerp, clockManager(dayTime));
    }

    /** 1.21.11 new TimeCheck(period, value) (class_4571): tested the level's day time -- the overworld clock */
    public static TimeCheck newTimeCheck(Optional<Long> period, IntRange value) {
        return new TimeCheck(overworldClock(), period, value);
    }

    /** 1.21.11 TimeCheck.time(value) (class_4571.method_35559) */
    public static TimeCheck.Builder time(IntRange value) {
        return TimeCheck.time(overworldClock(), value);
    }

    /** 1.21.11 new TimeCheck.Builder(value) (class_4571$class_6164) */
    public static TimeCheck.Builder newTimeCheckBuilder(IntRange value) {
        return new TimeCheck.Builder(overworldClock(), value);
    }

    /**
     * 1.21.11 new AttributeType(valueCodec, modifierLibrary, modifierCodec, keyframeLerp, stateChangeLerp, spatialLerp,
     * partialTickLerp) (class_12192): 26.1 adds the float view (toFloat), which 26.1's own types without one leave null
     * (AttributeType.ofNotInterpolated).
     */
    public static <V> AttributeType<V> newAttributeType(Codec<V> valueCodec, Map<AttributeModifier.OperationId, AttributeModifier<V, ?>> library,
                                                        Codec<AttributeModifier<V, ?>> modifierCodec, LerpFunction<V> keyframeLerp,
                                                        LerpFunction<V> stateChangeLerp, LerpFunction<V> spatialLerp,
                                                        LerpFunction<V> partialTickLerp) {
        return new AttributeType<>(valueCodec, library, modifierCodec, keyframeLerp, stateChangeLerp, spatialLerp, partialTickLerp, null);
    }

    /**
     * A holder of a registry entry looked up on first use, for a 1.21.11 call made before any registries are up (a
     * timeline or loot condition built at mod init). Every question goes to the registry's own holder; equals and
     * hashCode are that holder's, so 26.1's clock maps (HashMap keyed by holder) find it.
     */
    static final class Lazy<T> implements Holder<T> {
        private final ResourceKey<T> key;
        private volatile Holder<T> resolved;

        Lazy(ResourceKey<T> key) {
            this.key = key;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private Holder<T> get() {
            Holder<T> h = this.resolved;
            if (h == null) {
                RegistryAccess access = CbWallRegistries.access();
                h = (Holder<T>) access.lookupOrThrow((ResourceKey) this.key.registryKey()).getOrThrow((ResourceKey) this.key);
                this.resolved = h;
            }
            return h;
        }

        @Override
        public T value() {
            return get().value();
        }

        @Override
        public boolean isBound() {
            return get().isBound();
        }

        @Override
        public boolean areComponentsBound() {
            return get().areComponentsBound();
        }

        @Override
        public boolean is(Identifier id) {
            return this.key.identifier().equals(id);
        }

        @Override
        public boolean is(ResourceKey<T> other) {
            return this.key == other;
        }

        @Override
        public boolean is(Predicate<ResourceKey<T>> predicate) {
            return predicate.test(this.key);
        }

        @Override
        public boolean is(TagKey<T> tag) {
            return get().is(tag);
        }

        @Override
        public boolean is(Holder<T> other) {
            return get().is(other instanceof Lazy<T> lazy ? lazy.get() : other);
        }

        @Override
        public Stream<TagKey<T>> tags() {
            return get().tags();
        }

        @Override
        public DataComponentMap components() {
            return get().components();
        }

        @Override
        public Either<ResourceKey<T>, T> unwrap() {
            return Either.left(this.key);
        }

        @Override
        public Optional<ResourceKey<T>> unwrapKey() {
            return Optional.of(this.key);
        }

        @Override
        public Holder.Kind kind() {
            return Holder.Kind.REFERENCE;
        }

        @Override
        public boolean canSerializeIn(HolderOwner<T> owner) {
            return get().canSerializeIn(owner);
        }

        @Override
        public boolean equals(Object o) {
            return o == this || get().equals(o instanceof Lazy<?> lazy ? lazy.get() : o);
        }

        @Override
        public int hashCode() {
            return get().hashCode();
        }

        @Override
        public String toString() {
            return "CbWallClocks.Lazy[" + this.key + "]";
        }
    }
}
