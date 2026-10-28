package top.dext.centurybridge.step.v1_21_5.facade;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.class_18;
import net.minecraft.class_2487;
import net.minecraft.class_2512;
import net.minecraft.class_4284;
import net.minecraft.class_7225;

/**
 * 1.21.4's SavedData contract (net/minecraft/class_18) for converted mods' own saved data. 1.21.5's SavedData keeps
 * only the dirty flag: a SavedDataType's codec reads and writes the data (javap 1.21.5 class_18, class_10741,
 * class_26.method_120 / method_67442). A converted mod's SavedData subclass extends this class instead
 * (superRenames), so it is a 1.21.5 SavedData that still implements 1.21.4's save(CompoundTag, Provider); the storage
 * calls go through rt/CbSavedData, whose SavedDataType encodes such data with save() and decodes it with the mod's
 * factory, as 1.21.4's DimensionDataStorage did.
 */
public abstract class SavedData extends class_18 {

    /** save(tag, registries): the mod's data into tag */
    public abstract class_2487 method_75(class_2487 tag, class_7225.class_7874 registries);

    /**
     * save(registries): 1.21.4's file tag, {"data": save(new CompoundTag(), registries), "DataVersion": current}
     * (javap 1.21.4 class_18.method_17919)
     */
    public class_2487 method_17919(class_7225.class_7874 registries) {
        class_2487 tag = new class_2487();
        tag.method_10566("data", this.method_75(new class_2487(), registries));
        class_2512.method_48310(tag);
        return tag;
    }

    /**
     * 1.21.4 SavedData.Factory (class_18$class_8645), a record (constructor, deserializer (data tag, registries), the
     * DataFixTypes of the file). classRenames: converted references name this class. A class with public final
     * component fields rather than a record: a facade's members stand in for the class's (a record's component fields
     * would be private); equals / hashCode / toString as the record's.
     */
    public static final class Factory<T extends class_18> {
        public final Supplier<T> comp_1590;
        public final BiFunction<class_2487, class_7225.class_7874, T> comp_1591;
        public final class_4284 comp_1592;

        public Factory(Supplier<T> constructor, BiFunction<class_2487, class_7225.class_7874, T> deserializer, class_4284 type) {
            this.comp_1590 = constructor;
            this.comp_1591 = deserializer;
            this.comp_1592 = type;
        }

        public Supplier<T> comp_1590() {
            return this.comp_1590;
        }

        public BiFunction<class_2487, class_7225.class_7874, T> comp_1591() {
            return this.comp_1591;
        }

        public class_4284 comp_1592() {
            return this.comp_1592;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof Factory<?> f && java.util.Objects.equals(this.comp_1590, f.comp_1590)
                && java.util.Objects.equals(this.comp_1591, f.comp_1591) && java.util.Objects.equals(this.comp_1592, f.comp_1592);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(this.comp_1590, this.comp_1591, this.comp_1592);
        }

        @Override
        public String toString() {
            return "Factory[constructor=" + this.comp_1590 + ", deserializer=" + this.comp_1591 + ", type=" + this.comp_1592 + "]";
        }
    }
}
