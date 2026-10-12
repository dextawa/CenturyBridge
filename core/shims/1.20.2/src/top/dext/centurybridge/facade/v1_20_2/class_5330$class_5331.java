package top.dext.centurybridge.facade.v1_20_2;

import com.mojang.datafixers.util.Pair;
import java.util.function.Function;

/**
 * Facade for 1.20.1 net/minecraft/class_5330$class_5331 (GsonAdapterFactory.Builder), deleted in
 * 1.20.2. Ported as it was (the constructor is public: a facade is reached from other packages).
 */
public class class_5330$class_5331<E, T extends class_5336<E>> {

    // 1.20.1's private fields, under their names (a mod's access widener may reach them)
    public final net.minecraft.class_2378<T> field_25192;
    public final String field_25193;
    public final String field_25194;
    public final Function<E, T> field_25195;
    public Pair<T, class_5330$class_5332<? extends E>> field_25196;
    public T field_28444;

    public class_5330$class_5331(net.minecraft.class_2378<T> registry, String rootFieldName, String idFieldName,
            Function<E, T> typeGetter) {
        this.field_25192 = registry;
        this.field_25193 = rootFieldName;
        this.field_25194 = idFieldName;
        this.field_25195 = typeGetter;
    }

    /** withInlineSerializer(type, serializer) */
    public class_5330$class_5331<E, T> method_32385(T type, class_5330$class_5332<? extends E> serializer) {
        this.field_25196 = Pair.of(type, serializer);
        return this;
    }

    /** withDefaultType(type) */
    public class_5330$class_5331<E, T> method_33409(T type) {
        this.field_28444 = type;
        return this;
    }

    /** build() */
    public Object method_29307() {
        return new class_5330$class_5333<>(this.field_25192, this.field_25193, this.field_25194, this.field_25195,
            this.field_28444, this.field_25196);
    }
}
