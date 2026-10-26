package top.dext.centurybridge.step.v1_21_5.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.class_1297;
import net.minecraft.class_2378;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_6895;
import net.minecraft.class_7376;

/**
 * 1.21.4's EntitySubPredicates variant predicate types (net/minecraft/class_7378$class_7380 EntityVariantPredicateType,
 * class_7378$class_9419 EntityHolderVariantPredicateType, and their predicates $class_9353 / $class_9420), gone in 1.21.5
 * (entity variants became data components matched by component predicates). Converted references name these classes
 * (classRenames). 1.21.4's bodies, javap -c -p 1.21.4 class_7378$class_7380 / $class_9419 / $class_9353 / $class_9420:
 * the type holds a MapCodec of its predicate (one "variant" field: the value codec, or RegistryCodecs.homogeneousList
 * of the registry key) and the entity -> variant getter; its predicate is an EntitySubPredicate (class_7376, unchanged
 * in 1.21.5) matching an entity whose variant equals the value (is in the holder set). 1.21.5 still registers entity
 * sub-predicate codecs in its registry (class_7923.field_49911, class_7378.method_58157), so a type's codec registered
 * there reads and writes "type_specific" predicates as 1.21.4 did.
 */
public final class EntitySubPredicates {

    private EntitySubPredicates() {
    }

    /** 1.21.4 EntitySubPredicates.EntityVariantPredicateType (class_7378$class_7380) */
    public static class EntityVariantPredicateType<V> {
        public final MapCodec<EntityVariantPredicateType<V>.Instance> field_49794;
        public final Function<class_1297, Optional<V>> field_38741;

        /** create(registry, getter): the registry's byNameCodec */
        public static <V> EntityVariantPredicateType<V> method_43116(class_2378<V> registry, Function<class_1297, Optional<V>> getter) {
            return new EntityVariantPredicateType<>(registry.method_39673(), getter);
        }

        /** create(codec, getter) */
        public static <V> EntityVariantPredicateType<V> method_47840(Codec<V> codec, Function<class_1297, Optional<V>> getter) {
            return new EntityVariantPredicateType<>(codec, getter);
        }

        public EntityVariantPredicateType(Codec<V> codec, Function<class_1297, Optional<V>> getter) {
            this.field_38741 = getter;
            this.field_49794 = RecordCodecBuilder.mapCodec(i -> i.group(
                codec.fieldOf("variant").forGetter((Instance p) -> p.field_49796)).apply(i, this::method_58159));
        }

        /** createPredicate(value) */
        public class_7376 method_43117(V value) {
            return new Instance(value);
        }

        private Instance method_58159(V value) {
            return new Instance(value);
        }

        /** the predicate (class_7378$class_7380$class_9353): the entity's variant equals the value */
        public class Instance implements class_7376 {
            public final V field_49796;

            Instance(V value) {
                this.field_49796 = value;
            }

            @Override
            public MapCodec<EntityVariantPredicateType<V>.Instance> method_58152() {
                return EntityVariantPredicateType.this.field_49794;
            }

            @Override
            public boolean method_22497(class_1297 entity, class_3218 level, class_243 position) {
                return EntityVariantPredicateType.this.field_38741.apply(entity).filter(this.field_49796::equals).isPresent();
            }
        }
    }

    /** 1.21.4 EntitySubPredicates.EntityHolderVariantPredicateType (class_7378$class_9419) */
    public static class EntityHolderVariantPredicateType<V> {
        public final MapCodec<EntityHolderVariantPredicateType<V>.Instance> field_49974;
        public final Function<class_1297, Optional<class_6880<V>>> field_49975;

        /** create(registryKey, getter) */
        public static <V> EntityHolderVariantPredicateType<V> method_58354(class_5321<? extends class_2378<V>> registryKey,
                Function<class_1297, Optional<class_6880<V>>> getter) {
            return new EntityHolderVariantPredicateType<>(registryKey, getter);
        }

        public EntityHolderVariantPredicateType(class_5321<? extends class_2378<V>> registryKey,
                Function<class_1297, Optional<class_6880<V>>> getter) {
            this.field_49975 = getter;
            this.field_49974 = RecordCodecBuilder.mapCodec(i -> i.group(
                class_6895.method_40340(registryKey).fieldOf("variant").forGetter((Instance p) -> p.field_49977)).apply(i, this::method_58357));
        }

        /** createPredicate(holders) */
        public class_7376 method_58356(class_6885<V> holders) {
            return new Instance(holders);
        }

        private Instance method_58357(class_6885<V> holders) {
            return new Instance(holders);
        }

        /** the predicate (class_7378$class_9419$class_9420): the entity's variant is in the holder set */
        public class Instance implements class_7376 {
            public final class_6885<V> field_49977;

            Instance(class_6885<V> holders) {
                this.field_49977 = holders;
            }

            @Override
            public MapCodec<EntityHolderVariantPredicateType<V>.Instance> method_58152() {
                return EntityHolderVariantPredicateType.this.field_49974;
            }

            @Override
            public boolean method_22497(class_1297 entity, class_3218 level, class_243 position) {
                return EntityHolderVariantPredicateType.this.field_49975.apply(entity).filter(this.field_49977::method_40241).isPresent();
            }
        }
    }
}
