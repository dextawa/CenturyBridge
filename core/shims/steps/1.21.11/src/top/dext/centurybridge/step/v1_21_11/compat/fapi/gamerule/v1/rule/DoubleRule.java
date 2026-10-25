package top.dext.centurybridge.step.v1_21_11.compat.fapi.gamerule.v1.rule;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;
import top.dext.centurybridge.step.v1_21_11.facade.GameRules;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;

/**
 * 1.21.10's Fabric API DoubleRule, which 1.21.11's Fabric API removed (its double rules are GameRule&lt;Double&gt;).
 * 1.21.10's members and bodies; a value getRule handed out reads and writes the 1.21.11 rule (facade/GameRules).
 */
public final class DoubleRule extends GameRules.Value<DoubleRule> implements ValidateableRule {
    private final double minimumValue;
    private final double maximumValue;
    private double value;

    public DoubleRule(GameRules.Type<DoubleRule> type, double value, double minimumValue, double maximumValue) {
        super(type);
        this.value = value;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            throw new IllegalArgumentException("Value cannot be infinite or NaN");
        }
        if (Double.isInfinite(minimumValue) || Double.isNaN(minimumValue)) {
            throw new IllegalArgumentException("Minimum value cannot be infinite or NaN");
        }
        if (Double.isInfinite(maximumValue) || Double.isNaN(maximumValue)) {
            throw new IllegalArgumentException("Maximum value cannot be infinite or NaN");
        }
    }

    private void store(double v) {
        if (this.cb$rules() != null) {
            this.cb$write(v, null);
        } else {
            this.value = v;
        }
    }

    @Override
    public void method_20776(CommandContext<class_2168> context, String name) {
        this.store(context.getArgument(name, Double.class));
    }

    @Override
    public void method_20777(String value) {
        if (!value.isEmpty()) {
            try {
                double d = Double.parseDouble(value);
                if (this.inBounds(d)) {
                    this.store(d);
                } else {
                    CbGameRules.LOGGER.warn("Failed to parse double {}. Was out of bounds {} - {}", value, this.minimumValue, this.maximumValue);
                }
            } catch (NumberFormatException e) {
                CbGameRules.LOGGER.warn("Failed to parse double {}", value);
            }
        }
    }

    @Override
    public String method_20779() {
        return Double.toString(this.get());
    }

    @Override
    public int method_20781() {
        return Double.compare(this.get(), 0.0D);
    }

    protected DoubleRule getThis() {
        return this;
    }

    protected DoubleRule copy() {
        return new DoubleRule(this.field_19417, this.get(), this.minimumValue, this.maximumValue);
    }

    public void setValue(DoubleRule rule, MinecraftServer server) {
        double v = rule.get();
        if (!this.inBounds(v)) {
            throw new IllegalArgumentException(String.format("Could not set value to %s. Was out of bounds %s - %s", v, this.minimumValue, this.maximumValue));
        }
        this.set(v, server);
    }

    @Override
    public boolean validate(String value) {
        try {
            double d = Double.parseDouble(value);
            if (!this.inBounds(d)) {
                return false;
            }
            this.store(d);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public double get() {
        return this.cb$rules() != null ? (Double) this.cb$read() : this.value;
    }

    public void set(double value, MinecraftServer server) {
        if (!this.inBounds(value)) {
            throw new IllegalArgumentException(String.format("Could not set value to %s. Was out of bounds %s - %s", value, this.minimumValue, this.maximumValue));
        }
        if (this.cb$rules() != null) {
            this.cb$write(value, server);
            return;
        }
        this.value = value;
        this.method_20778(server);
    }

    private boolean inBounds(double value) {
        return value >= this.minimumValue && value <= this.maximumValue;
    }

    @Override
    public void method_27337(DoubleRule other, MinecraftServer server) {
        this.setValue(other, server);
    }

    @Override
    public DoubleRule method_27338() {
        return this.copy();
    }

    @Override
    public DoubleRule method_20782() {
        return this.getThis();
    }
}
