package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1685;
import net.minecraft.class_9304;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21.rt.CbEntityEnchanting;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * ThrownTrident and channeling (CbEntityEnchanting). 1.21 made Channeling a data-driven enchantment effect
 * (channeling.json: post_attack and hit_block summon_entity lightning_bolt + play_sound, run by EnchantmentHelper
 * for the trident's weapon) and dropped 1.20.6's isChanneling(), whose answer decided 1.20.6's strike. This mixin
 * restores isChanneling() on every trident and routes 1.21's strike through it: around 1.21's weapon effects of an
 * entity hit and of a block hit its answer is asked, as 1.20.6 asked it (once per hit, in a thunderstorm; for a
 * block, only a lightning rod's), and 1.21's Channeling effect runs only when it says yes -- with 1.20.6's own
 * strike afterwards where it says yes and 1.21 has no Channeling to run. A mod's 1.20.6 mixin on isChanneling(),
 * or a 1.20.6 subclass's override, decides the strike as on 1.20.6; with neither, the answer is the enchantment's
 * presence and 1.21 runs as it does. The routes into 1.21's own onHitEntity / hitBlockEnchantmentEffects are
 * CbEntityTridentMixinHooks (default priority).
 */
@Mixin(value = class_1685.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityTridentMixin implements CbEntityEnchanting.TridentHit, CbEntityEnchanting.LegacyChanneling {

    @Unique
    private class_9304 cb$heldEnchantments;

    @Unique
    private Boolean cb$channelingAnswer;

    @Override
    public class_9304 cb$heldEnchantments() {
        return this.cb$heldEnchantments;
    }

    @Override
    public void cb$holdEnchantments(class_9304 enchantments) {
        this.cb$heldEnchantments = enchantments;
    }

    @Override
    public Boolean cb$channelingAnswer() {
        return this.cb$channelingAnswer;
    }

    @Override
    public void cb$channelingAnswer(Boolean answer) {
        this.cb$channelingAnswer = answer;
    }

    /** 1.20.6 ThrownTrident.isChanneling() (class_1685.method_31551): EnchantmentHelper.hasChanneling(the thrown stack) */
    @CbVanillaReads(value = "CbEntityTridentMixinHooks: 1.21's channeling strike of an entity hit (EnchantmentHelper.doPostAttackEffects, "
        + "channeling.json post_attack) and of a lightning rod hit (ThrownTrident.method_59956, hit_block) runs only when "
        + "this answers true (CbEntityEnchanting.beforeWeaponEffects / beforeBlockEffects take Channeling off the weapon "
        + "for the call otherwise), and 1.20.6's own strike follows where it answers true without the enchantment; asked "
        + "once per hit under 1.20.6's conditions (server level, thunderstorm; a lightning rod for a block hit). Vanilla "
        + "takes the answer, so what decides it is carried; an INVOKE inside 1.20.6's body (hasChanneling) has no "
        + "counterpart in this body",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN"})
    @Override
    public boolean method_31551() {
        return CbEntityEnchanting.superIsChanneling((class_1685) (Object) this);
    }
}
