package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * Registry values as 1.20.5 holders, and back: 1.20.5 hands a Holder where 1.20.4 handed the
 * value itself (GameEvent in LevelAccessor.gameEvent and GameEventListener.handleGameEvent,
 * SoundEvent in Explosion and Level.explode, MobEffect in FlowerBlock and SuspiciousStewEffects,
 * Potion, Enchantment, Instrument...). Shared by every area of this step.
 *
 * <p>value -> holder is the registry's wrapAsHolder (MappedRegistry.wrapAsHolder, read off 1.20.5's
 * bytecode): the registered Holder.Reference when the value is registered at the time of the call,
 * else Holder.direct(value). That is also how 1.20.4 itself turned a value into a holder where it
 * needed one (Level.playSound wrapped its SoundEvent with BuiltInRegistries.SOUND_EVENT.wrapAsHolder
 * before passing it on), so a value 1.20.4 code made and never registered -- a SoundEvent from
 * SoundEvent.createVariableRangeEvent -- reaches 1.20.5 as the same direct holder 1.20.4 sent.
 * A direct holder made before the value got registered stays direct; ask again after registration
 * for the reference.
 *
 * <p>holder -> value is Holder.value(). Both directions answer null for null: 1.20.4 code passing
 * no value passes no holder.
 */
public final class CbHolders {

    /** GameEvent -> Holder&lt;GameEvent&gt; (BuiltInRegistries.GAME_EVENT) */
    public static net.minecraft.class_6880<net.minecraft.class_5712> gameEvent(net.minecraft.class_5712 event) {
        return event == null ? null : net.minecraft.class_7923.field_41171.method_47983(event);
    }

    /** Holder&lt;GameEvent&gt; -> GameEvent */
    public static net.minecraft.class_5712 gameEventValue(net.minecraft.class_6880<net.minecraft.class_5712> holder) {
        return holder == null ? null : holder.comp_349();
    }

    /** SoundEvent -> Holder&lt;SoundEvent&gt; (BuiltInRegistries.SOUND_EVENT) */
    public static net.minecraft.class_6880<net.minecraft.class_3414> soundEvent(net.minecraft.class_3414 sound) {
        return sound == null ? null : net.minecraft.class_7923.field_41172.method_47983(sound);
    }

    /** Holder&lt;SoundEvent&gt; -> SoundEvent */
    public static net.minecraft.class_3414 soundEventValue(net.minecraft.class_6880<net.minecraft.class_3414> holder) {
        return holder == null ? null : holder.comp_349();
    }

    /** MobEffect -> Holder&lt;MobEffect&gt; (BuiltInRegistries.MOB_EFFECT) */
    public static net.minecraft.class_6880<net.minecraft.class_1291> mobEffect(net.minecraft.class_1291 effect) {
        return effect == null ? null : net.minecraft.class_7923.field_41174.method_47983(effect);
    }

    /** Holder&lt;MobEffect&gt; -> MobEffect */
    public static net.minecraft.class_1291 mobEffectValue(net.minecraft.class_6880<net.minecraft.class_1291> holder) {
        return holder == null ? null : holder.comp_349();
    }

    /** Potion -> Holder&lt;Potion&gt; (BuiltInRegistries.POTION) */
    public static net.minecraft.class_6880<net.minecraft.class_1842> potion(net.minecraft.class_1842 potion) {
        return potion == null ? null : net.minecraft.class_7923.field_41179.method_47983(potion);
    }

    /** Holder&lt;Potion&gt; -> Potion */
    public static net.minecraft.class_1842 potionValue(net.minecraft.class_6880<net.minecraft.class_1842> holder) {
        return holder == null ? null : holder.comp_349();
    }

    /** Enchantment -> Holder&lt;Enchantment&gt; (BuiltInRegistries.ENCHANTMENT) */
    public static net.minecraft.class_6880<net.minecraft.class_1887> enchantment(net.minecraft.class_1887 enchantment) {
        return enchantment == null ? null : net.minecraft.class_7923.field_41176.method_47983(enchantment);
    }

    /** Holder&lt;Enchantment&gt; -> Enchantment */
    public static net.minecraft.class_1887 enchantmentValue(net.minecraft.class_6880<net.minecraft.class_1887> holder) {
        return holder == null ? null : holder.comp_349();
    }

    /** Instrument -> Holder&lt;Instrument&gt; (BuiltInRegistries.INSTRUMENT) */
    public static net.minecraft.class_6880<net.minecraft.class_7444> instrument(net.minecraft.class_7444 instrument) {
        return instrument == null ? null : net.minecraft.class_7923.field_41166.method_47983(instrument);
    }

    /** Holder&lt;Instrument&gt; -> Instrument */
    public static net.minecraft.class_7444 instrumentValue(net.minecraft.class_6880<net.minecraft.class_7444> holder) {
        return holder == null ? null : holder.comp_349();
    }

    /** any holder's value, for a converter whose old type is Object (an erased generic) */
    public static Object value(net.minecraft.class_6880<?> holder) {
        return holder == null ? null : holder.comp_349();
    }

    private CbHolders() {
    }
}
