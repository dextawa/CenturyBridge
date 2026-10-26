package top.dext.centurybridge.step.v1_21_11.facade;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_1928;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_5198;
import net.minecraft.class_7699;
import net.minecraft.server.MinecraftServer;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;

/**
 * 1.21.10's nested game rule classes (GameRules.Key / Type / Value / BooleanValue / IntegerValue / Category /
 * GameRuleTypeVisitor / VisitorCaller), which 1.21.11 replaced by registered GameRule&lt;T&gt; objects whose values a
 * GameRuleMap holds (GameRules.get / set(rule, value, server)). Same members and bodies as 1.21.10, with one
 * addition: a value handed out by GameRules.getRule(key) is bound to that GameRules and its 1.21.11 rule
 * (rt/CbGameRules), so reading it reads the live 1.21.11 value and writing it is 1.21.11's set (which runs the
 * server's game rule hook, where 1.21.11 keeps the effects 1.21.10's per-type callbacks had). A value a mod made
 * itself (Type.createRule(), new BooleanValue(..)) keeps its own state, as in 1.21.10.
 */
public final class GameRules {
    private GameRules() {
    }

    /** 1.21.10 GameRules.Category: the enum 1.21.11 replaced by the GameRuleCategory record (same seven; constants under their intermediary names) */
    public enum Category {
        field_24094("gamerule.category.player"),
        field_24095("gamerule.category.mobs"),
        field_24096("gamerule.category.spawning"),
        field_24097("gamerule.category.drops"),
        field_24098("gamerule.category.updates"),
        field_24099("gamerule.category.chat"),
        field_24100("gamerule.category.misc");

        public final String field_24101;

        Category(String descriptionId) {
            this.field_24101 = descriptionId;
        }

        public String method_27328() {
            return this.field_24101;
        }

        /** the 1.21.11 category standing for this one */
        public class_5198 cb$toW() {
            return switch (this) {
                case field_24094 -> class_5198.field_24094;
                case field_24095 -> class_5198.field_24095;
                case field_24096 -> class_5198.field_24096;
                case field_24097 -> class_5198.field_24097;
                case field_24098 -> class_5198.field_24098;
                case field_24099 -> class_5198.field_24099;
                case field_24100 -> class_5198.field_24100;
            };
        }

        /** the 1.21.10 category of a 1.21.11 one (any category 1.21.10 did not have is MISC) */
        public static Category cb$ofW(class_5198 c) {
            for (Category v : values()) {
                if (v.cb$toW().equals(c)) {
                    return v;
                }
            }
            return field_24100;
        }
    }

    /** 1.21.10 GameRules.Key: a rule's name and category; equal by name */
    public static final class Key<T extends Value<T>> {
        public final String field_19413;
        public final Category field_24103;

        public Key(String id, Category category) {
            this.field_19413 = id;
            this.field_24103 = category;
        }

        @Override
        public String toString() {
            return this.field_19413;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            return o instanceof Key<?> k && this.field_19413.equals(k.field_19413);
        }

        @Override
        public int hashCode() {
            return this.field_19413.hashCode();
        }

        public String method_20771() {
            return this.field_19413;
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRuleMixin: 1.21.11's GameRule.getDescriptionId() of the rule this key stands for takes this answer where it differs from this body's (CbGameRules.routeDescriptionId)")
        public String method_27334() {
            return "gamerule." + this.field_19413;
        }

        public Category method_27335() {
            return this.field_24103;
        }
    }

    /** 1.21.10 GameRules.VisitorCaller (Fabric API's access widener made it public) */
    public interface VisitorCaller<T extends Value<T>> {
        void call(GameRuleTypeVisitor visitor, Key<T> key, Type<T> type);
    }

    /** 1.21.10 GameRules.GameRuleTypeVisitor */
    public interface GameRuleTypeVisitor {
        default <T extends Value<T>> void method_20762(Key<T> key, Type<T> type) {
        }

        default void method_27329(Key<BooleanValue> key, Type<BooleanValue> type) {
        }

        default void method_27330(Key<IntegerValue> key, Type<IntegerValue> type) {
        }
    }

    /** 1.21.10 GameRules.Type (constructor made public and the class extendable by Fabric API's access widener) */
    public static class Type<T extends Value<T>> {
        public final Supplier<ArgumentType<?>> field_19414;
        public final Function<Type<T>, T> field_19415;
        public final BiConsumer<MinecraftServer, T> field_19416;
        public final VisitorCaller<T> field_24104;
        public final Class<T> field_55983;
        public final class_7699 field_52616;

        public Type(Supplier<ArgumentType<?>> argument, Function<Type<T>, T> factory, BiConsumer<MinecraftServer, T> callback,
                    VisitorCaller<T> visitorCaller, Class<T> valueClass, class_7699 requiredFeatures) {
            this.field_19414 = argument;
            this.field_19415 = factory;
            this.field_19416 = callback;
            this.field_24104 = visitorCaller;
            this.field_55983 = valueClass;
            this.field_52616 = requiredFeatures;
        }

        public RequiredArgumentBuilder<class_2168, ?> method_20775(String name) {
            return class_2170.method_9244(name, this.field_19414.get());
        }

        public T method_20773() {
            return this.field_19415.apply(this);
        }

        public void method_27336(GameRuleTypeVisitor visitor, Key<T> key) {
            this.field_24104.call(visitor, key, this);
        }

        public class_7699 method_61728() {
            return this.field_52616;
        }

        /** the change callback (1.21.10 package access) */
        public BiConsumer<MinecraftServer, T> cb$callback() {
            return this.field_19416;
        }

        /** the argument type the command parses (1.21.10 package access) */
        public ArgumentType<?> cb$argument() {
            return this.field_19414.get();
        }

        /** the value class (1.21.10 package access) */
        public Class<T> cb$valueClass() {
            return this.field_55983;
        }
    }

    /** 1.21.10 GameRules.Value */
    public abstract static class Value<T extends Value<T>> {
        public final Type<T> field_19417;
        /** where getRule(key) took this value from: its GameRules and key (null: a value of its own) */
        class_1928 cb$rules;
        Key<T> cb$key;

        public Value(Type<T> type) {
            this.field_19417 = type;
        }

        public abstract void method_20776(CommandContext<class_2168> context, String name);

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRuleCommandMixin: 1.21.11's game rule command (GameRuleCommand.setRule) of a rule one 1.21.10 boolean / integer key stands for runs this value's setFromArgument(context, \"value\") first and writes the value it hands over (CbGameRules.routeWrite)")
        public void method_20780(CommandContext<class_2168> context, String name) {
            this.method_20776(context, name);
            this.method_20778(context.getSource().method_9211());
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRuleCommandMixin / CbGameRulesMixinHooks: 1.21.11's game rule command and set run this value's setFromArgument / set first, which call onChanged(server); 1.21.11's set then notifies the server (CbGameRules.changed)")
        public void method_20778(MinecraftServer server) {
            if (this.cb$rules != null) {
                // a bound value: 1.21.11 runs a rule's effects (vanilla's and, through CbGameRulesServerMixin, a mod
                // rule's callback) from the server's game rule hook, which 1.21.11's set calls itself
                CbGameRules.changed(this.cb$rules, this.cb$key, server);
                return;
            }
            if (server != null) {
                this.field_19417.field_19416.accept(server, this.method_20782());
            }
        }

        public abstract void method_20777(String value);

        public abstract String method_20779();

        @Override
        public String toString() {
            return this.method_20779();
        }

        public abstract int method_20781();

        public abstract T method_20782();

        public abstract T method_27338();

        public abstract void method_27337(T other, MinecraftServer server);

        /** bind a value to the GameRules and key it was taken from */
        public final void cb$bind(class_1928 rules, Key<T> key) {
            this.cb$rules = rules;
            this.cb$key = key;
        }

        public final class_1928 cb$rules() {
            return this.cb$rules;
        }

        public final Key<T> cb$key() {
            return this.cb$key;
        }

        /** the bound value as 1.21.10 saw it (a Boolean, Integer, Double or enum constant) */
        protected final Object cb$read() {
            return CbGameRules.read(this.cb$rules, this.cb$key);
        }

        /** write a bound value; 1.21.11's set runs the game rule hook when server is not null */
        protected final void cb$write(Object v, MinecraftServer server) {
            CbGameRules.write(this.cb$rules, this.cb$key, v, server);
        }

        /** the protected 1.21.10 calls, for the layer's own helpers */
        public final void cb$deserialize(String s) {
            this.method_20777(s);
        }

        public final T cb$copy() {
            return this.method_27338();
        }
    }

    /** 1.21.10 GameRules.BooleanValue */
    public static class BooleanValue extends Value<BooleanValue> {
        public boolean field_19411;

        public static Type<BooleanValue> method_70743(boolean defaultValue, BiConsumer<MinecraftServer, BooleanValue> callback, class_7699 features) {
            return new Type<>(BoolArgumentType::bool, t -> new BooleanValue(t, defaultValue), callback,
                GameRuleTypeVisitor::method_27329, BooleanValue.class, features);
        }

        public static Type<BooleanValue> method_20760(boolean defaultValue, BiConsumer<MinecraftServer, BooleanValue> callback) {
            return method_70743(defaultValue, callback, class_7699.method_45397());
        }

        public static Type<BooleanValue> method_20759(boolean defaultValue) {
            return method_20760(defaultValue, (server, value) -> {
            });
        }

        public BooleanValue(Type<BooleanValue> type, boolean value) {
            super(type);
            this.field_19411 = value;
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRuleCommandMixin: 1.21.11's game rule command runs setFromArgument, which runs this (CbGameRules.routeWrite)")
        @Override
        public void method_20776(CommandContext<class_2168> context, String name) {
            this.cb$set(BoolArgumentType.getBool(context, name));
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRulesMixinHooks: 1.21.11's GameRules.get(rule) of a rule a 1.21.10 boolean key stands for reads the key's value through getBoolean, which answers this value's get()")
        public boolean method_20753() {
            return this.cb$rules != null ? (Boolean) this.cb$read() : this.field_19411;
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRulesMixinHooks: 1.21.11's GameRules.set(rule, value, server) of a rule one 1.21.10 boolean key stands for runs this value's set first and writes the value it hands over (CbGameRules.routeWrite; negated for the three disable rules)")
        public void method_20758(boolean value, MinecraftServer server) {
            if (this.cb$rules != null) {
                this.cb$write(value, server);
                this.method_20778(server);
                return;
            }
            this.field_19411 = value;
            this.method_20778(server);
        }

        private void cb$set(boolean value) {
            if (this.cb$rules != null) {
                this.cb$write(value, null);
            } else {
                this.field_19411 = value;
            }
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRuleMixin: 1.21.11's GameRule.serialize(value) of a rule one 1.21.10 boolean / integer key stands for consults a value of its own holding the value, through this, and takes its answer where it differs from this body's (CbGameRules.routeSerialize)")
        @Override
        public String method_20779() {
            return Boolean.toString(this.method_20753());
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRuleMixin: 1.21.11's GameRule.deserialize(string) of a rule one 1.21.10 boolean / integer key stands for consults a value of its own holding the value, through this, and takes its answer where it differs from this body's (CbGameRules.routeDeserialize)")
        @Override
        public void method_20777(String value) {
            this.cb$set(Boolean.parseBoolean(value));
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRuleMixin: 1.21.11's GameRule.getCommandResult(value) of a rule one 1.21.10 boolean / integer key stands for consults a value of its own holding the value, through this, and takes its answer where it differs from this body's (CbGameRules.routeCommandResult)")
        @Override
        public int method_20781() {
            return this.method_20753() ? 1 : 0;
        }

        public BooleanValue method_20761() {
            return this;
        }

        public BooleanValue method_27327() {
            return new BooleanValue(this.field_19417, this.method_20753());
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRulesMixinHooks: 1.21.11's GameRules.setAll(other, server) runs, through the restored assignFrom, 1.21.11's set of each boolean rule, which runs this first and writes the value it hands over (CbGameRules.routeWrite)")
        public void method_27326(BooleanValue other, MinecraftServer server) {
            if (this.cb$rules != null) {
                this.cb$write(other.method_20753(), server);
                this.method_20778(server);
                return;
            }
            this.field_19411 = other.method_20753();
            this.method_20778(server);
        }

        @Override
        public void method_27337(BooleanValue other, MinecraftServer server) {
            this.method_27326(other, server);
        }

        @Override
        public BooleanValue method_27338() {
            return this.method_27327();
        }

        @Override
        public BooleanValue method_20782() {
            return this.method_20761();
        }
    }

    /** 1.21.10 GameRules.IntegerValue */
    public static class IntegerValue extends Value<IntegerValue> {
        public int field_19412;

        public static Type<IntegerValue> cb$create(int defaultValue, int min, int max, class_7699 features,
                                                   BiConsumer<MinecraftServer, IntegerValue> callback) {
            return new Type<>(() -> IntegerArgumentType.integer(min, max), t -> new IntegerValue(t, defaultValue), callback,
                GameRuleTypeVisitor::method_27330, IntegerValue.class, features);
        }

        public static Type<IntegerValue> method_20766(int defaultValue, BiConsumer<MinecraftServer, IntegerValue> callback) {
            return new Type<>(IntegerArgumentType::integer, t -> new IntegerValue(t, defaultValue), callback,
                GameRuleTypeVisitor::method_27330, IntegerValue.class, class_7699.method_45397());
        }

        public static Type<IntegerValue> method_56115(int defaultValue, int min, int max, class_7699 features,
                                                      BiConsumer<MinecraftServer, IntegerValue> callback) {
            return cb$create(defaultValue, min, max, features, callback);
        }

        public static Type<IntegerValue> method_20768(int defaultValue) {
            return method_20766(defaultValue, (server, value) -> {
            });
        }

        public IntegerValue(Type<IntegerValue> type, int value) {
            super(type);
            this.field_19412 = value;
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRuleCommandMixin: 1.21.11's game rule command runs setFromArgument, which runs this (CbGameRules.routeWrite)")
        @Override
        public void method_20776(CommandContext<class_2168> context, String name) {
            this.cb$set(IntegerArgumentType.getInteger(context, name));
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRulesMixinHooks: 1.21.11's GameRules.get(rule) of a rule a 1.21.10 integer key stands for reads the key's value through getInt, which answers this value's get()")
        public int method_20763() {
            return this.cb$rules != null ? (Integer) this.cb$read() : this.field_19412;
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRulesMixinHooks: 1.21.11's GameRules.set(rule, value, server) of a rule a 1.21.10 integer key stands for runs this value's set first and writes the value it hands over (CbGameRules.routeWrite)")
        public void method_35236(int value, MinecraftServer server) {
            if (this.cb$rules != null) {
                this.cb$write(value, server);
                this.method_20778(server);
                return;
            }
            this.field_19412 = value;
            this.method_20778(server);
        }

        protected final void cb$set(int value) {
            if (this.cb$rules != null) {
                this.cb$write(value, null);
            } else {
                this.field_19412 = value;
            }
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRuleMixin: 1.21.11's GameRule.serialize(value) of a rule one 1.21.10 boolean / integer key stands for consults a value of its own holding the value, through this, and takes its answer where it differs from this body's (CbGameRules.routeSerialize)")
        @Override
        public String method_20779() {
            return Integer.toString(this.method_20763());
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRuleMixin: 1.21.11's GameRule.deserialize(string) of a rule one 1.21.10 boolean / integer key stands for consults a value of its own holding the value, through this, and takes its answer where it differs from this body's (CbGameRules.routeDeserialize)")
        @Override
        public void method_20777(String value) {
            this.cb$set(method_20769(value));
        }

        public boolean method_27332(String input) {
            try {
                StringReader reader = new StringReader(input);
                this.cb$set((Integer) this.field_19417.field_19414.get().parse(reader));
                return !reader.canRead();
            } catch (CommandSyntaxException e) {
                return false;
            }
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRuleMixin: 1.21.11's GameRule.deserialize(string) of a rule one 1.21.10 integer key stands for consults a value of its own holding the value, through this, and takes its answer where it differs from this body's (CbGameRules.routeDeserialize, through IntegerValue.deserialize)")
        public static int method_20769(String value) {
            if (!value.isEmpty()) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    CbGameRules.LOGGER.warn("Failed to parse integer {}", value);
                }
            }
            return 0;
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRuleMixin: 1.21.11's GameRule.getCommandResult(value) of a rule one 1.21.10 boolean / integer key stands for consults a value of its own holding the value, through this, and takes its answer where it differs from this body's (CbGameRules.routeCommandResult)")
        @Override
        public int method_20781() {
            return this.method_20763();
        }

        public IntegerValue method_20770() {
            return this;
        }

        public IntegerValue method_27333() {
            return new IntegerValue(this.field_19417, this.method_20763());
        }

        @top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads("CbGameRulesMixinHooks: 1.21.11's GameRules.setAll(other, server) runs, through the restored assignFrom, 1.21.11's set of each integer rule, which runs this first and writes the value it hands over (CbGameRules.routeWrite)")
        public void method_27331(IntegerValue other, MinecraftServer server) {
            if (this.cb$rules != null) {
                this.cb$write(other.method_20763(), server);
                this.method_20778(server);
                return;
            }
            this.field_19412 = other.method_20763();
            this.method_20778(server);
        }

        @Override
        public void method_27337(IntegerValue other, MinecraftServer server) {
            this.method_27331(other, server);
        }

        @Override
        public IntegerValue method_27338() {
            return this.method_27333();
        }

        @Override
        public IntegerValue method_20782() {
            return this.method_20770();
        }
    }
}
