package top.dext.centurybridge.step.v1_21_5.facade;

import net.minecraft.class_2487;
import net.minecraft.class_2940;
import net.minecraft.class_2945;
import net.minecraft.class_4980;

/**
 * 1.21.4's ItemBasedSteering (net/minecraft/class_4980) for converted mods' own steerable entities. 1.21.4's steering
 * also held the entity's saddle: the mod passed its own synched Boolean (hasSaddleAccessor, field_23220) and the
 * steering read, wrote, saved and loaded it (setSaddle / hasSaddle / addAdditionalSaveData / readAdditionalSaveData).
 * 1.21.5 moved the saddle to the SADDLE equipment slot and dropped that accessor, the constructor argument and those
 * four methods; every member it kept has the same body (javap -c -p 1.21.4 / 1.21.5 class_4980: onSynced, boost,
 * tickBoost, boostFactor, boostTimeTotal identical).
 *
 * A converted mod's ItemBasedSteering is this 1.21.5 ItemBasedSteering, built with 1.21.4's constructor (ctorRedirects
 * through rt/CbSteering; a converted subclass extends it through superRenames) and still keeping the saddle in the
 * mod's own accessor. The mod defines that accessor on its entity and reads the saddle through this steering, as in
 * 1.21.4: the state, its sync and its save format ("Saddle") are the mod's own. Vanilla's ItemBasedSteering objects
 * (Pig, Strider: a private field) are never this class.
 */
public class ItemBasedSteering extends class_4980 {
    private final class_2945 entityData;
    /** hasSaddleAccessor: the mod's synched saddle flag (1.21.4 field_23220) */
    public final class_2940<Boolean> field_23220;

    public ItemBasedSteering(class_2945 entityData, class_2940<Integer> boostTimeAccessor, class_2940<Boolean> hasSaddleAccessor) {
        super(entityData, boostTimeAccessor);
        this.entityData = entityData;
        this.field_23220 = hasSaddleAccessor;
    }

    /** addAdditionalSaveData(tag): putBoolean("Saddle", hasSaddle()) (1.21.4's body) */
    public void method_26309(class_2487 tag) {
        tag.method_10556("Saddle", this.method_26311());
    }

    /** readAdditionalSaveData(tag): setSaddle(getBoolean("Saddle")), false when absent (1.21.4's getBoolean) */
    public void method_26312(class_2487 tag) {
        this.method_26310(tag.method_68566("Saddle", false));
    }

    /** setSaddle(saddled): the mod's accessor */
    public void method_26310(boolean saddled) {
        this.entityData.method_12778(this.field_23220, saddled);
    }

    /** hasSaddle(): the mod's accessor */
    public boolean method_26311() {
        return this.entityData.method_12789(this.field_23220);
    }
}
