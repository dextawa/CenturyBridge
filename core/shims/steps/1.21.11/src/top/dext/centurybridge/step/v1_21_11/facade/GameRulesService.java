package top.dext.centurybridge.step.v1_21_11.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.class_11845;
import net.minecraft.class_3542;

/**
 * 1.21.10's server management protocol game rule records (jsonrpc GameRulesService.TypedRule / UntypedRule), which
 * 1.21.11 replaced by GameRuleUpdate&lt;T&gt;(rule, value): a rule named by its 1.21.10 id with its value as a string
 * (and its type: 1.21.10's RuleType is 1.21.11's GameRuleType, same constants). Plain classes with the records'
 * components, codecs, equality, hash and text (a facade's members must be public).
 */
public final class GameRulesService {
    private GameRulesService() {
    }

    /** 1.21.10 GameRulesService.TypedRule(key, value, type) */
    public static final class TypedRule {
        public static final MapCodec<TypedRule> field_62403 = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("key").forGetter(TypedRule::comp_4719),
            Codec.STRING.fieldOf("value").forGetter(TypedRule::comp_4720),
            class_3542.method_28140(class_11845::values).fieldOf("type").forGetter(TypedRule::comp_4721)
        ).apply(i, TypedRule::new));

        public final String comp_4719;
        public final String comp_4720;
        public final class_11845 comp_4721;

        public TypedRule(String key, String value, class_11845 type) {
            this.comp_4719 = key;
            this.comp_4720 = value;
            this.comp_4721 = type;
        }

        public String comp_4719() {
            return this.comp_4719;
        }

        public String comp_4720() {
            return this.comp_4720;
        }

        public class_11845 comp_4721() {
            return this.comp_4721;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof TypedRule t && Objects.equals(this.comp_4719, t.comp_4719)
                && Objects.equals(this.comp_4720, t.comp_4720) && Objects.equals(this.comp_4721, t.comp_4721);
        }

        @Override
        public int hashCode() {
            return 31 * (31 * Objects.hashCode(this.comp_4719) + Objects.hashCode(this.comp_4720)) + Objects.hashCode(this.comp_4721);
        }

        /** the record's text as 1.21.10 printed it: runtime simple name, then the component names it kept */
        @Override
        public String toString() {
            return "class_11846[key=" + this.comp_4719 + ", value=" + this.comp_4720 + ", type=" + this.comp_4721 + "]";
        }
    }

    /** 1.21.10 GameRulesService.UntypedRule(key, value) */
    public static final class UntypedRule {
        public static final MapCodec<UntypedRule> field_62404 = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("key").forGetter(UntypedRule::comp_4722),
            Codec.STRING.fieldOf("value").forGetter(UntypedRule::comp_4723)
        ).apply(i, UntypedRule::new));

        public final String comp_4722;
        public final String comp_4723;

        public UntypedRule(String key, String value) {
            this.comp_4722 = key;
            this.comp_4723 = value;
        }

        public String comp_4722() {
            return this.comp_4722;
        }

        public String comp_4723() {
            return this.comp_4723;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof UntypedRule t && Objects.equals(this.comp_4722, t.comp_4722)
                && Objects.equals(this.comp_4723, t.comp_4723);
        }

        @Override
        public int hashCode() {
            return 31 * Objects.hashCode(this.comp_4722) + Objects.hashCode(this.comp_4723);
        }

        @Override
        public String toString() {
            return "class_11847[key=" + this.comp_4722 + ", value=" + this.comp_4723 + "]";
        }
    }

    /** 1.21.10 jsonrpc internalapi GameRules (an interface 1.21.10 itself never implemented; 1.21.11 dropped it) */
    public interface RpcGameRules {
        <T extends GameRules.Value<T>> T method_74479(GameRules.Key<T> key);
    }
}
