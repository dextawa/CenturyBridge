package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_1656;
import net.minecraft.class_2487;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_6.rt.CbSaveData;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's Abilities.addSaveData(CompoundTag) / loadSaveData(CompoundTag), restored on Abilities with 1.21.5's bodies
 * (javap): an "abilities" compound of invulnerable, flying, mayfly, instabuild, mayBuild, flySpeed, walkSpeed, read
 * back with defaults false, false, false, false, 0.05, 0.1, true. 1.21.6's pack / apply(Abilities.Packed) stand.
 * Priority below every mod's.
 */
@Mixin(value = class_1656.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAbilitiesSaveRestoredMixin implements CbSaveData.LegacyAbilities {

    @Shadow
    public boolean field_7480;

    @Shadow
    public boolean field_7479;

    @Shadow
    public boolean field_7478;

    @Shadow
    public boolean field_7477;

    @Shadow
    public boolean field_7476;

    @Shadow
    private float field_7481;

    @Shadow
    private float field_7482;

    @CbVanillaReads("CbPlayerSaveRouteMixin: Player.addAdditionalSaveData, after 1.21.6's save, when a converted mod mixed it "
        + "(CbMixedMembers) or the abilities are a subclass: it writes \"abilities\", as 1.21.5's save did")
    @Override
    public void method_7251(class_2487 tag) {
        class_2487 t = new class_2487();
        t.method_10556("invulnerable", this.field_7480);
        t.method_10556("flying", this.field_7479);
        t.method_10556("mayfly", this.field_7478);
        t.method_10556("instabuild", this.field_7477);
        t.method_10556("mayBuild", this.field_7476);
        t.method_10548("flySpeed", this.field_7481);
        t.method_10548("walkSpeed", this.field_7482);
        tag.method_10566("abilities", t);
    }

    @CbVanillaReads("CbPlayerSaveRouteMixin: Player.readAdditionalSaveData, after 1.21.6's load, when a converted mod mixed it "
        + "(CbMixedMembers) or the abilities are a subclass: it reads \"abilities\" again, as 1.21.5's load did")
    @Override
    public void method_7249(class_2487 tag) {
        class_2487 t = tag.method_68568("abilities");
        this.field_7480 = t.method_68566("invulnerable", false);
        this.field_7479 = t.method_68566("flying", false);
        this.field_7478 = t.method_68566("mayfly", false);
        this.field_7477 = t.method_68566("instabuild", false);
        this.field_7481 = t.method_66563("flySpeed", 0.05F);
        this.field_7482 = t.method_66563("walkSpeed", 0.1F);
        this.field_7476 = t.method_68566("mayBuild", true);
    }
}
