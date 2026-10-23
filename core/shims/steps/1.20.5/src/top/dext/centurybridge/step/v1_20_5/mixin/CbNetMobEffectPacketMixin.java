package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_5.facade.FactorData;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetGame;

/**
 * ClientboundUpdateMobEffectPacket: 1.20.4's getters whose type 1.20.5 changed, back beside 1.20.5's under
 * their 1.20.4 shapes (a 1.20.4 call or super call links to them; a 1.20.4 subclass's override of one is
 * reached from 1.20.5's shape through the step's override hook), and getFactorData(), which 1.20.5 removed.
 * Each reads the packet's own field, never the overridable 1.20.5 getter: a subclass's super call must not
 * come back into its own override.
 */
@Mixin(net.minecraft.class_2783.class)
public abstract class CbNetMobEffectPacketMixin implements CbNetGame.MobEffectPacketSlot {

    @Shadow
    @Final
    private net.minecraft.class_6880<net.minecraft.class_1291> field_12725;

    @Shadow
    @Final
    private int field_12729;

    @Unique
    private FactorData cbS4net$factorData;

    @Unique
    private boolean cbS4net$built;

    @Override
    public FactorData cbS4net$factorData() {
        return this.cbS4net$factorData;
    }

    @Override
    public void cbS4net$factorData(FactorData data) {
        this.cbS4net$factorData = data;
    }

    @Override
    public boolean cbS4net$built() {
        return this.cbS4net$built;
    }

    @Override
    public void cbS4net$built(boolean built) {
        this.cbS4net$built = built;
    }

    /** was getEffect(): the effect itself (1.20.5 answers its holder) */
    public net.minecraft.class_1291 method_11946() {
        return this.field_12725.comp_349();
    }

    /** was getEffectAmplifier(): 1.20.4 kept the amplifier's low byte */
    public byte method_11945() {
        return (byte) (this.field_12729 & 255);
    }

    /** was getFactorData() (rt/CbNetGame.mobEffectFactorData) */
    public FactorData method_42625() {
        return CbNetGame.mobEffectFactorData((net.minecraft.class_2783) (Object) this);
    }
}
