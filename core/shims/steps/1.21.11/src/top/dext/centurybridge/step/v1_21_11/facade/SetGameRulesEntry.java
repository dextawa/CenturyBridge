package top.dext.centurybridge.step.v1_21_11.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;

/**
 * 1.21.10's gametest TestEnvironmentDefinition.SetGameRules.Entry(rule, value), which 1.21.11 dropped (its
 * SetGameRules holds a GameRuleMap). The record's components, codec ("rule": the 1.21.10 id of a rule of that value
 * class, "value"), equality, hash and text, as a plain class (a facade's members must be public).
 */
public final class SetGameRulesEntry<S, T extends GameRules.Value<T>> {
    public final GameRules.Key<T> comp_3554;
    public final S comp_3555;

    public SetGameRulesEntry(GameRules.Key<T> key, S value) {
        this.comp_3554 = key;
        this.comp_3555 = value;
    }

    public GameRules.Key<T> comp_3554() {
        return this.comp_3554;
    }

    public S comp_3555() {
        return this.comp_3555;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <S, T extends GameRules.Value<T>> Codec<SetGameRulesEntry<S, T>> method_67064(Class<T> valueClass, Codec<S> valueCodec) {
        Codec<GameRules.Key<T>> keyCodec = (Codec) CbGameRules.method_66703(valueClass);
        return RecordCodecBuilder.create(i -> i.group(
            keyCodec.fieldOf("rule").forGetter(SetGameRulesEntry::comp_3554),
            valueCodec.fieldOf("value").forGetter(SetGameRulesEntry::comp_3555)
        ).apply(i, SetGameRulesEntry::new));
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof SetGameRulesEntry<?, ?> e && Objects.equals(this.comp_3554, e.comp_3554)
            && Objects.equals(this.comp_3555, e.comp_3555);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.comp_3554) + Objects.hashCode(this.comp_3555);
    }

    /** the record's text as 1.21.10 printed it: runtime simple name, then the component names it kept */
    @Override
    public String toString() {
        return "class_10669[key=" + this.comp_3554 + ", value=" + this.comp_3555 + "]";
    }
}
