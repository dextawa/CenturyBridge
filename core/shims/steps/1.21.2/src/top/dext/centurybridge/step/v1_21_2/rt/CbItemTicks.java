package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_1923;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2520;
import net.minecraft.class_3695;
import net.minecraft.class_4296;
import net.minecraft.class_6757;
import net.minecraft.class_6759;
import net.minecraft.class_6760;
import net.minecraft.class_6761;

/**
 * 1.21.1's tick containers on 1.21.2 (world/ticks). 1.21.1 saved a chunk's ticks as NBT straight from the container
 * (SerializableTickContainer.save(gameTime, idGetter) -> a ListTag of SavedTick compounds); 1.21.2's containers
 * pack(gameTime) into SavedTick records that the chunk serializer writes the same way. Each body is 1.21.1's:
 * <ul>
 * <li>save(gameTime, idGetter) on a 1.21.2 container: the compounds of what it packs, each SavedTick.save(idGetter)
 *     -- 1.21.1's ProtoChunkTicks body; LevelChunkTicks' converted its scheduled ticks with the game time first,
 *     as pack(gameTime) does;</li>
 * <li>a 1.21.1 container (a mod class or lambda) asked 1.21.2's pack(gameTime): its own save, with an id getter that
 *     names each tick type by a fresh string, read back into SavedTicks by the same names ({@link #pack}) -- exactly
 *     the ticks it wrote;</li>
 * <li>ProtoChunkTicks.load / SavedTick.loadTickList from a ListTag: 1.21.2's list loader, the ticks handed on in
 *     order; SavedTick.saveTick(tick, idGetter, gameTime): the compound of the tick's type, position, delay to the game
 *     time and priority;</li>
 * <li>LevelTicks(isTickable, profiler): 1.21.2 profiles its tick on the thread's profiler; a 1.21.1 constructor's
 *     profiler supplier is kept and used there instead (CbItemLevelTicksMixin).</li>
 * </ul>
 */
public final class CbItemTicks {
    private CbItemTicks() {
    }

    /** a 1.21.1 SerializableTickContainer: save(gameTime, idGetter) */
    public interface LegacySave<T> {
        class_2520 method_20463(long gameTime, Function<T, String> idGetter);
    }

    /** save(gameTime, idGetter), called */
    @SuppressWarnings("unchecked")
    public static <T> class_2520 save(class_6761<T> container, long gameTime, Function<T, String> idGetter) {
        if (container instanceof LegacySave<?> legacy) {
            return ((LegacySave<T>) legacy).method_20463(gameTime, idGetter);
        }
        return superSave(container, gameTime, idGetter);
    }

    /** the same on ProtoChunkTicks (a call keyed on it) */
    public static <T> class_2520 save(class_4296<T> container, long gameTime, Function<T, String> idGetter) {
        return save((class_6761<T>) container, gameTime, idGetter);
    }

    /** 1.21.1's ProtoChunkTicks / LevelChunkTicks body over 1.21.2's pack(gameTime) */
    public static <T> class_2520 superSave(class_6761<T> container, long gameTime, Function<T, String> idGetter) {
        class_2499 list = new class_2499();
        for (class_6759<T> tick : container.method_61898(gameTime)) {
            list.add(tick.method_39404(idGetter));
        }
        return list;
    }

    /** a mod ProtoChunkTicks subclass's super.save(..) */
    public static <T> class_2520 superSave(class_4296<T> container, long gameTime, Function<T, String> idGetter) {
        return superSave((class_6761<T>) container, gameTime, idGetter);
    }

    /** 1.21.2's pack(gameTime) of a 1.21.1 container: what its own save wrote, read back */
    public static <T> List<class_6759<T>> pack(LegacySave<T> container, long gameTime) {
        Map<T, String> names = new IdentityHashMap<>();
        Map<String, T> types = new HashMap<>();
        class_2520 saved = container.method_20463(gameTime, type -> names.computeIfAbsent(type, t -> {
            String name = "cb:" + types.size();
            types.put(name, t);
            return name;
        }));
        List<class_6759<T>> ticks = new ArrayList<>();
        if (saved instanceof class_2499 list) {
            for (int i = 0; i < list.size(); i++) {
                class_6759.method_40559(list.method_10602(i), name -> Optional.ofNullable(types.get(name)))
                    .ifPresent(ticks::add);
            }
        }
        return ticks;
    }

    /** a 1.21.1 SerializableTickContainer lambda as 1.21.2's */
    public static <T> class_6761<T> lambda(LegacySave<T> legacy) {
        return gameTime -> pack(legacy, gameTime);
    }

    /** ProtoChunkTicks.load(list, idParser, chunkPos) */
    public static <T> class_4296<T> load(class_2499 list, Function<String, Optional<T>> idParser, class_1923 chunkPos) {
        return class_4296.method_39398(class_6759.method_39406(list, idParser, chunkPos));
    }

    /** SavedTick.loadTickList(list, idParser, chunkPos, output): the chunk's ticks, in the list's order */
    public static <T> void loadTickList(class_2499 list, Function<String, Optional<T>> idParser, class_1923 chunkPos,
            Consumer<class_6759<T>> output) {
        class_6759.method_39406(list, idParser, chunkPos).forEach(output);
    }

    /** SavedTick.saveTick(tick, idGetter, gameTime) */
    public static <T> class_2487 saveTick(class_6760<T> tick, Function<T, String> idGetter, long gameTime) {
        return class_6759.method_39403(idGetter.apply(tick.comp_252()), tick.comp_253(), (int) (tick.comp_254() - gameTime),
            tick.comp_255());
    }

    /** LevelTicks keeps a 1.21.1 constructor's profiler supplier (CbItemLevelTicksMixin) */
    public interface Profiled {
        void cb$setProfiler(Supplier<class_3695> profiler);
    }

    /** LevelTicks(isTickable, profiler)'s profiler, set right after 1.21.2's LevelTicks(isTickable) */
    public static void keepProfiler(class_6757<?> ticks, Supplier<class_3695> profiler) {
        ((Profiled) ticks).cb$setProfiler(profiler);
    }
}
