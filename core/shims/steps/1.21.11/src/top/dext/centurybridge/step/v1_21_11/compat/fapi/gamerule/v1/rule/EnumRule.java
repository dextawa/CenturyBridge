package top.dext.centurybridge.step.v1_21_11.compat.fapi.gamerule.v1.rule;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.context.CommandContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;
import top.dext.centurybridge.step.v1_21_11.facade.GameRules;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;

/**
 * 1.21.10's Fabric API EnumRule, which 1.21.11's Fabric API removed (its enum rules are GameRule&lt;E&gt;).
 * 1.21.10's members and bodies; a value getRule handed out reads and writes the 1.21.11 rule (facade/GameRules).
 */
public final class EnumRule<E extends Enum<E>> extends GameRules.Value<EnumRule<E>> {
    private final Class<E> classType;
    private final List<E> supportedValues;
    private E value;

    public EnumRule(GameRules.Type<EnumRule<E>> type, E value, E[] supportedValues) {
        this(type, value, Arrays.asList(supportedValues));
    }

    public EnumRule(GameRules.Type<EnumRule<E>> type, E value, Collection<E> supportedValues) {
        super(type);
        this.classType = value.getDeclaringClass();
        this.value = value;
        this.supportedValues = new ArrayList<>(supportedValues);
        if (!this.supports(value)) {
            throw new IllegalArgumentException("Cannot set default value");
        }
    }

    @Override
    public void method_20776(CommandContext<class_2168> context, String name) {
        // 1.21.10's EnumRule took no command argument (EnumRuleType registers one literal per value)
    }

    @Override
    public void method_20777(String value) {
        try {
            E e = Enum.valueOf(this.classType, value);
            if (!this.supports(e)) {
                CbGameRules.LOGGER.warn("Failed to parse rule of value {} for rule of type {}. Since the value {}, is unsupported.", value, this.classType, value);
                return;
            }
            this.set(e, null);
        } catch (IllegalArgumentException e) {
            CbGameRules.LOGGER.warn("Failed to parse rule of value {} for rule of type {}", value, this.classType);
        }
    }

    @Override
    public String method_20779() {
        return this.get().name();
    }

    @Override
    public int method_20781() {
        return this.get().ordinal();
    }

    protected EnumRule<E> getThis() {
        return this;
    }

    public Class<E> getEnumClass() {
        return this.classType;
    }

    @Override
    public String toString() {
        return this.get().toString();
    }

    protected EnumRule<E> copy() {
        return new EnumRule<>(this.field_19417, this.get(), this.supportedValues);
    }

    public void setValue(EnumRule<E> rule, MinecraftServer server) {
        if (!this.supports(rule.get())) {
            throw new IllegalArgumentException(String.format("Rule does not support value: %s", rule.get()));
        }
        this.set(rule.get(), server);
    }

    @SuppressWarnings("unchecked")
    public E get() {
        return this.cb$rules() != null ? (E) this.cb$read() : this.value;
    }

    public void cycle() {
        int index = this.supportedValues.indexOf(this.get());
        if (index < 0) {
            throw new IllegalArgumentException(String.format("Invalid value: %s", this.get()));
        }
        this.set(this.supportedValues.get((index + 1) % this.supportedValues.size()), null);
    }

    public boolean supports(E value) {
        return this.supportedValues.contains(value);
    }

    public void set(E value, MinecraftServer server) throws IllegalArgumentException {
        Preconditions.checkNotNull(value);
        if (!this.supports(value)) {
            throw new IllegalArgumentException("Tried to set an unsupported value: " + value.toString());
        }
        if (this.cb$rules() != null) {
            this.cb$write(value, server);
            return;
        }
        this.value = value;
        this.method_20778(server);
    }

    public void set(String value, MinecraftServer server) throws IllegalArgumentException {
        for (E e : this.getEnumClass().getEnumConstants()) {
            if (e.name().equalsIgnoreCase(value)) {
                this.set(e, server);
                return;
            }
        }
        throw new IllegalArgumentException("Tried to set an unsupported value: " + value);
    }

    /** the supported values (for the 1.21.11 rule this registers) */
    @SuppressWarnings("unchecked")
    public E[] cb$supported() {
        return this.supportedValues.toArray((E[]) java.lang.reflect.Array.newInstance(this.classType, 0));
    }

    @Override
    public void method_27337(EnumRule<E> other, MinecraftServer server) {
        this.setValue(other, server);
    }

    @Override
    public EnumRule<E> method_27338() {
        return this.copy();
    }

    @Override
    public EnumRule<E> method_20782() {
        return this.getThis();
    }
}
