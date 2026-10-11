package top.dext.centurybridge.rt.v26_2;

/**
 * 1.20.5 holderization (core/tools/gen_holder_bridges.py -- generated, do not edit): a 1.20.1
 * mod reads MobEffect/Potion/GameEvent/SoundEvent/Attribute/MapDecorationType constants as
 * values and passes values; the API now holds and takes Holder<T>. Constants answer their
 * holder's value(); arguments are wrapped by their registry, returned holders unwrapped.
 */
public final class CbHolders {

    /** net/minecraft/class_1291 -> its built-in registry's holder (Registry.wrapAsHolder) */
    public static net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> wrapMobEffect(net.minecraft.world.effect.MobEffect value) {
        return (value == null ? null : net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(value));
    }

    /** net/minecraft/class_1320 -> its built-in registry's holder (Registry.wrapAsHolder) */
    public static net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> wrapAttribute(net.minecraft.world.entity.ai.attributes.Attribute value) {
        return (value == null ? null : net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(value));
    }

    /** net/minecraft/class_1842 -> its built-in registry's holder (Registry.wrapAsHolder) */
    public static net.minecraft.core.Holder<net.minecraft.world.item.alchemy.Potion> wrapPotion(net.minecraft.world.item.alchemy.Potion value) {
        return (value == null ? null : net.minecraft.core.registries.BuiltInRegistries.POTION.wrapAsHolder(value));
    }

    /** net/minecraft/class_5712 -> its built-in registry's holder (Registry.wrapAsHolder) */
    public static net.minecraft.core.Holder<net.minecraft.world.level.gameevent.GameEvent> wrapGameEvent(net.minecraft.world.level.gameevent.GameEvent value) {
        return (value == null ? null : net.minecraft.core.registries.BuiltInRegistries.GAME_EVENT.wrapAsHolder(value));
    }

    /** net/minecraft/class_1294.field_5904:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5904() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.SPEED.value();
    }

    /** net/minecraft/class_1294.field_5909:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5909() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.SLOWNESS.value();
    }

    /** net/minecraft/class_1294.field_5917:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5917() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.HASTE.value();
    }

    /** net/minecraft/class_1294.field_5901:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5901() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.MINING_FATIGUE.value();
    }

    /** net/minecraft/class_1294.field_5910:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5910() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.STRENGTH.value();
    }

    /** net/minecraft/class_1294.field_5915:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5915() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.INSTANT_HEALTH.value();
    }

    /** net/minecraft/class_1294.field_5921:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5921() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.INSTANT_DAMAGE.value();
    }

    /** net/minecraft/class_1294.field_5913:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5913() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.JUMP_BOOST.value();
    }

    /** net/minecraft/class_1294.field_5916:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5916() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.NAUSEA.value();
    }

    /** net/minecraft/class_1294.field_5924:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5924() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.REGENERATION.value();
    }

    /** net/minecraft/class_1294.field_5907:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5907() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.RESISTANCE.value();
    }

    /** net/minecraft/class_1294.field_5918:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5918() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE.value();
    }

    /** net/minecraft/class_1294.field_5923:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5923() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.WATER_BREATHING.value();
    }

    /** net/minecraft/class_1294.field_5905:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5905() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.INVISIBILITY.value();
    }

    /** net/minecraft/class_1294.field_5919:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5919() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.BLINDNESS.value();
    }

    /** net/minecraft/class_1294.field_5925:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5925() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.NIGHT_VISION.value();
    }

    /** net/minecraft/class_1294.field_5903:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5903() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.HUNGER.value();
    }

    /** net/minecraft/class_1294.field_5911:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5911() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.WEAKNESS.value();
    }

    /** net/minecraft/class_1294.field_5899:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5899() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.POISON.value();
    }

    /** net/minecraft/class_1294.field_5920:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5920() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.WITHER.value();
    }

    /** net/minecraft/class_1294.field_5914:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5914() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.HEALTH_BOOST.value();
    }

    /** net/minecraft/class_1294.field_5898:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5898() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.ABSORPTION.value();
    }

    /** net/minecraft/class_1294.field_5922:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5922() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.SATURATION.value();
    }

    /** net/minecraft/class_1294.field_5912:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5912() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.GLOWING.value();
    }

    /** net/minecraft/class_1294.field_5902:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5902() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.LEVITATION.value();
    }

    /** net/minecraft/class_1294.field_5926:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5926() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.LUCK.value();
    }

    /** net/minecraft/class_1294.field_5908:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5908() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.UNLUCK.value();
    }

    /** net/minecraft/class_1294.field_5906:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5906() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.SLOW_FALLING.value();
    }

    /** net/minecraft/class_1294.field_5927:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5927() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.CONDUIT_POWER.value();
    }

    /** net/minecraft/class_1294.field_5900:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_5900() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.DOLPHINS_GRACE.value();
    }

    /** net/minecraft/class_1294.field_16595:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_16595() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.BAD_OMEN.value();
    }

    /** net/minecraft/class_1294.field_18980:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_18980() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.HERO_OF_THE_VILLAGE.value();
    }

    /** net/minecraft/class_1294.field_38092:Lnet/minecraft/class_1291;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.effect.MobEffect field_38092() {
        return (net.minecraft.world.effect.MobEffect) net.minecraft.world.effect.MobEffects.DARKNESS.value();
    }

    /** net/minecraft/class_5134.field_23722:Lnet/minecraft/class_1320;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23722() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_KNOCKBACK.value();
    }

    /** net/minecraft/class_5134.field_23717:Lnet/minecraft/class_1320;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23717() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE.value();
    }

    /** net/minecraft/class_5134.field_23726:Lnet/minecraft/class_1320;: the holder's value, as 1.20.1 read the constant */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23726() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.LUCK.value();
    }

    /** net/minecraft/class_2102$class_8748.method_53201(Lnet/minecraft/class_1291;)Lnet/minecraft/class_2102$class_8748; */
    public static net.minecraft.advancements.predicates.MobEffectsPredicate.Builder method_53201(net.minecraft.advancements.predicates.MobEffectsPredicate.Builder self, net.minecraft.world.effect.MobEffect a0) {
        return self.and((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(a0)));
    }

    /** net/minecraft/class_2102$class_8748.method_53202(Lnet/minecraft/class_1291;Lnet/minecraft/class_2102$class_2103;)Lnet/minecraft/class_2102$class_8748; */
    public static net.minecraft.advancements.predicates.MobEffectsPredicate.Builder method_53202(net.minecraft.advancements.predicates.MobEffectsPredicate.Builder self, net.minecraft.world.effect.MobEffect a0, net.minecraft.advancements.predicates.MobEffectsPredicate.MobEffectInstancePredicate a1) {
        return self.and((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(a0)), a1);
    }

    /** net/minecraft/class_4516.method_52889(Lnet/minecraft/class_1309;Lnet/minecraft/class_1291;I)V */
    public static void method_52889(net.minecraft.gametest.framework.GameTestHelper self, net.minecraft.world.entity.LivingEntity a0, net.minecraft.world.effect.MobEffect a1, int a2) {
        self.assertLivingEntityHasMobEffect(a0, (a1 == null ? null : net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(a1)), a2);
    }

    /** net/minecraft/class_2783.method_11946()Lnet/minecraft/class_1291; */
    public static net.minecraft.world.effect.MobEffect method_11946(net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket self) {
        net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> h = self.getEffect();
        return h == null ? null : (net.minecraft.world.effect.MobEffect) h.value();
    }

    /** net/minecraft/class_1293.method_5579()Lnet/minecraft/class_1291; */
    public static net.minecraft.world.effect.MobEffect method_5579(net.minecraft.world.effect.MobEffectInstance self) {
        net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> h = self.getEffect();
        return h == null ? null : (net.minecraft.world.effect.MobEffect) h.value();
    }

    /** net/minecraft/class_1309.method_6059(Lnet/minecraft/class_1291;)Z */
    public static boolean method_6059(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.effect.MobEffect a0) {
        return self.hasEffect((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(a0)));
    }

    /** net/minecraft/class_1309.method_6112(Lnet/minecraft/class_1291;)Lnet/minecraft/class_1293; */
    public static net.minecraft.world.effect.MobEffectInstance method_6112(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.effect.MobEffect a0) {
        return self.getEffect((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(a0)));
    }

    /** net/minecraft/class_1309.method_6111(Lnet/minecraft/class_1291;)Lnet/minecraft/class_1293; */
    public static net.minecraft.world.effect.MobEffectInstance method_6111(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.effect.MobEffect a0) {
        return self.removeEffectNoUpdate((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(a0)));
    }

    /** net/minecraft/class_1309.method_6016(Lnet/minecraft/class_1291;)Z */
    public static boolean method_6016(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.effect.MobEffect a0) {
        return self.removeEffect((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(a0)));
    }

    /** net/minecraft/class_1324.method_6198()Lnet/minecraft/class_1320; */
    public static net.minecraft.world.entity.ai.attributes.Attribute method_6198(net.minecraft.world.entity.ai.attributes.AttributeInstance self) {
        net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> h = self.getAttribute();
        return h == null ? null : (net.minecraft.world.entity.ai.attributes.Attribute) h.value();
    }

    /** net/minecraft/class_5131.method_26856(Lnet/minecraft/class_1320;)D */
    public static double method_26856(net.minecraft.world.entity.ai.attributes.AttributeMap self, net.minecraft.world.entity.ai.attributes.Attribute a0) {
        return self.getBaseValue((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(a0)));
    }

    /** net/minecraft/class_1704.method_53173(Lnet/minecraft/class_1291;)I */
    public static int method_53173(net.minecraft.world.effect.MobEffect a0) {
        return net.minecraft.world.inventory.BeaconMenu.encodeEffect((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(a0)));
    }

    /** net/minecraft/class_1936.method_32888(Lnet/minecraft/class_5712;Lnet/minecraft/class_243;Lnet/minecraft/class_5712$class_7397;)V */
    public static void method_32888(net.minecraft.world.level.LevelAccessor self, net.minecraft.world.level.gameevent.GameEvent a0, net.minecraft.world.phys.Vec3 a1, net.minecraft.world.level.gameevent.GameEvent.Context a2) {
        self.gameEvent((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.GAME_EVENT.wrapAsHolder(a0)), a1, a2);
    }

    /** net/minecraft/class_1936.method_43275(Lnet/minecraft/class_1297;Lnet/minecraft/class_5712;Lnet/minecraft/class_243;)V */
    public static void method_43275(net.minecraft.world.level.LevelAccessor self, net.minecraft.world.entity.Entity a0, net.minecraft.world.level.gameevent.GameEvent a1, net.minecraft.world.phys.Vec3 a2) {
        self.gameEvent(a0, (a1 == null ? null : net.minecraft.core.registries.BuiltInRegistries.GAME_EVENT.wrapAsHolder(a1)), a2);
    }

    /** net/minecraft/class_1936.method_33596(Lnet/minecraft/class_1297;Lnet/minecraft/class_5712;Lnet/minecraft/class_2338;)V */
    public static void method_33596(net.minecraft.world.level.LevelAccessor self, net.minecraft.world.entity.Entity a0, net.minecraft.world.level.gameevent.GameEvent a1, net.minecraft.core.BlockPos a2) {
        self.gameEvent(a0, (a1 == null ? null : net.minecraft.core.registries.BuiltInRegistries.GAME_EVENT.wrapAsHolder(a1)), a2);
    }

    /** net/minecraft/class_1936.method_43276(Lnet/minecraft/class_5712;Lnet/minecraft/class_2338;Lnet/minecraft/class_5712$class_7397;)V */
    public static void method_43276(net.minecraft.world.level.LevelAccessor self, net.minecraft.world.level.gameevent.GameEvent a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.gameevent.GameEvent.Context a2) {
        self.gameEvent((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.GAME_EVENT.wrapAsHolder(a0)), a1, a2);
    }

    /** net/minecraft/class_5712$class_7447.method_43724()Lnet/minecraft/class_5712; */
    public static net.minecraft.world.level.gameevent.GameEvent method_43724(net.minecraft.world.level.gameevent.GameEvent.ListenerInfo self) {
        net.minecraft.core.Holder<net.minecraft.world.level.gameevent.GameEvent> h = self.gameEvent();
        return h == null ? null : (net.minecraft.world.level.gameevent.GameEvent) h.value();
    }

    /** net/minecraft/class_5714.method_32947(Lnet/minecraft/class_3218;Lnet/minecraft/class_5712;Lnet/minecraft/class_5712$class_7397;Lnet/minecraft/class_243;)Z */
    public static boolean method_32947(net.minecraft.world.level.gameevent.GameEventListener self, net.minecraft.server.level.ServerLevel a0, net.minecraft.world.level.gameevent.GameEvent a1, net.minecraft.world.level.gameevent.GameEvent.Context a2, net.minecraft.world.phys.Vec3 a3) {
        return self.handleGameEvent(a0, (a1 == null ? null : net.minecraft.core.registries.BuiltInRegistries.GAME_EVENT.wrapAsHolder(a1)), a2, a3);
    }

    /** net/minecraft/class_5713.method_32943(Lnet/minecraft/class_5712;Lnet/minecraft/class_243;Lnet/minecraft/class_5712$class_7397;Lnet/minecraft/class_5713$class_7721;)Z */
    public static boolean method_32943(net.minecraft.world.level.gameevent.GameEventListenerRegistry self, net.minecraft.world.level.gameevent.GameEvent a0, net.minecraft.world.phys.Vec3 a1, net.minecraft.world.level.gameevent.GameEvent.Context a2, net.minecraft.world.level.gameevent.GameEventListenerRegistry.ListenerVisitor a3) {
        return self.visitInRangeListeners((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.GAME_EVENT.wrapAsHolder(a0)), a1, a2, a3);
    }

    /** net/minecraft/class_7269.comp_657()Lnet/minecraft/class_5712; */
    public static net.minecraft.world.level.gameevent.GameEvent comp_657(net.minecraft.world.level.gameevent.vibrations.VibrationInfo self) {
        net.minecraft.core.Holder<net.minecraft.world.level.gameevent.GameEvent> h = self.gameEvent();
        return h == null ? null : (net.minecraft.world.level.gameevent.GameEvent) h.value();
    }

    /** net/minecraft/class_8514$class_5719.method_32970(Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;Lnet/minecraft/class_5712;Lnet/minecraft/class_5712$class_7397;)Z */
    public static boolean method_32970(net.minecraft.world.level.gameevent.vibrations.VibrationSystem.User self, net.minecraft.server.level.ServerLevel a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.gameevent.GameEvent a2, net.minecraft.world.level.gameevent.GameEvent.Context a3) {
        return self.canReceiveVibration(a0, a1, (a2 == null ? null : net.minecraft.core.registries.BuiltInRegistries.GAME_EVENT.wrapAsHolder(a2)), a3);
    }

    /** net/minecraft/class_8514$class_5719.method_32969(Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;Lnet/minecraft/class_5712;Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;F)V */
    public static void method_32969(net.minecraft.world.level.gameevent.vibrations.VibrationSystem.User self, net.minecraft.server.level.ServerLevel a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.gameevent.GameEvent a2, net.minecraft.world.entity.Entity a3, net.minecraft.world.entity.Entity a4, float a5) {
        self.onReceiveVibration(a0, a1, (a2 == null ? null : net.minecraft.core.registries.BuiltInRegistries.GAME_EVENT.wrapAsHolder(a2)), a3, a4, a5);
    }

    /** net/minecraft/class_8514$class_5719.method_42324(Lnet/minecraft/class_5712;Lnet/minecraft/class_5712$class_7397;)Z */
    public static boolean method_42324(net.minecraft.world.level.gameevent.vibrations.VibrationSystem.User self, net.minecraft.world.level.gameevent.GameEvent a0, net.minecraft.world.level.gameevent.GameEvent.Context a1) {
        return self.isValidVibration((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.GAME_EVENT.wrapAsHolder(a0)), a1);
    }

    /** net/minecraft/class_6662.method_38927(Lnet/minecraft/class_1842;)Lnet/minecraft/class_120$class_121; */
    public static net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction.Builder method_38927(net.minecraft.world.item.alchemy.Potion a0) {
        return net.minecraft.world.level.storage.loot.functions.SetPotionFunction.setPotion((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.POTION.wrapAsHolder(a0)));
    }

    /** net/minecraft/class_152$class_153.method_640(Lnet/minecraft/class_1291;Lnet/minecraft/class_5658;)Lnet/minecraft/class_152$class_153; */
    public static net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction.Builder method_640(net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction.Builder self, net.minecraft.world.effect.MobEffect a0, net.minecraft.world.level.storage.loot.providers.number.NumberProvider a1) {
        return self.withEffect((a0 == null ? null : net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(a0)), a1);
    }

    private CbHolders() {
    }
}
