package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 advancement shapes a converted mod class still speaks, as adapter interfaces
 * (adapterHooks add them to the mod class, whose own methods implement them): the bridges reach a
 * 1.20.1 trigger's or trigger instance's own methods through these, typed. CbLegacyCriterion
 * (CriterionTrigger.getId) and CbLegacyAbstractCriterion (SimpleCriterionTrigger.createInstance)
 * are the trigger side's.
 */
public final class CbAdvLegacy {

    /** 1.20.1 CriterionTriggerInstance.getCriterion(): the id of the trigger the instance belongs to */
    public interface CriterionId {
        net.minecraft.class_2960 method_806();
    }

    /** 1.20.1 CriterionTriggerInstance.serializeToJson(SerializationContext) */
    public interface InstanceJson {
        com.google.gson.JsonObject method_807(top.dext.centurybridge.facade.v1_20_2.class_5267 context);
    }

    /** 1.20.1 Criterion.getTrigger() (a Criterion subclass's override) */
    public interface CriterionInstance {
        net.minecraft.class_184 method_774();
    }

    /** 1.20.1 Criterion.serializeToNetwork(FriendlyByteBuf) (a Criterion subclass's override) */
    public interface CriterionNetwork {
        void method_771(net.minecraft.class_2540 buf);
    }

    /** 1.20.1 CriterionTrigger.Listener.getTriggerInstance() (a Listener subclass's override) */
    public interface ListenerInstance {
        net.minecraft.class_184 method_797();
    }

    // ---- the predicates' serializeToJson(), 1.20.1's own methods a subclass may override

    /** EnchantmentPredicate.serializeToJson() */
    public interface EnchantmentJson {
        com.google.gson.JsonElement method_8881();
    }

    /** BlockPredicate.serializeToJson() */
    public interface BlockJson {
        com.google.gson.JsonElement method_22452();
    }

    /** EntityEquipmentPredicate.serializeToJson() */
    public interface EquipmentJson {
        com.google.gson.JsonElement method_16225();
    }

    /** EntityFlagsPredicate.serializeToJson() */
    public interface FlagsJson {
        com.google.gson.JsonElement method_8894();
    }

    /** FluidPredicate.serializeToJson() */
    public interface FluidJson {
        com.google.gson.JsonElement method_22473();
    }

    /** NbtPredicate.serializeToJson() */
    public interface NbtJson {
        com.google.gson.JsonElement method_9075();
    }

    /** TagPredicate.serializeToJson() */
    public interface TagJson {
        com.google.gson.JsonElement method_48964();
    }

    /** MobEffectsPredicate.MobEffectInstancePredicate.serializeToJson() */
    public interface EffectInstanceJson {
        com.google.gson.JsonElement method_9071();
    }

    /** EntityTypePredicate.serializeToJson() -- abstract in 1.20.1, every subclass wrote its own */
    public interface EntityTypeJson {
        com.google.gson.JsonElement method_8927();
    }

    /** WrappedMinMaxBounds.getMin(), a 1.20.1 method a subclass may override */
    public interface WrappedMin {
        Float method_9175();
    }

    /** WrappedMinMaxBounds.getMax() */
    public interface WrappedMax {
        Float method_9177();
    }

    /** MobEffectsPredicate.and(effect) */
    public interface EffectsAnd {
        net.minecraft.class_2102 method_9065(net.minecraft.class_1291 effect);
    }

    /** MobEffectsPredicate.and(effect, instance) */
    public interface EffectsAndInstance {
        net.minecraft.class_2102 method_35290(net.minecraft.class_1291 effect, net.minecraft.class_2102.class_2103 instance);
    }

    /** MinMaxBounds.BoundsFactory.create(min, max), 1.20.1's nullable shape (1.20.2: Optionals) */
    public interface BoundsFactory {
        net.minecraft.class_2096<?> create(Number min, Number max);
    }

    private CbAdvLegacy() {
    }
}
