package top.dext.centurybridge.step.v1_21.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.rt.CbClientGui;

/**
 * 1.20.6's SimpleOptionsSubScreen (class_5500), gone in 1.21 (its subclasses became OptionsSubScreen subclasses
 * with addOptions). A 1.20.6 subclass gets 1.20.6's body: init builds an OptionsList of the screen's options (the
 * narrator button made active only when a narrator is), then OptionsSubScreen's 1.20.6 init (title, footer,
 * positions; CbClientOptionsSubScreenMixin); repositionElements also sizes the list to the layout. Members 1.20.6
 * kept private or protected are public: a mod's access widener may name them, and wideners never reach a
 * CenturyBridge class.
 */
@Environment(EnvType.CLIENT)
public abstract class SimpleOptionsSubScreen extends net.minecraft.class_4667 implements CbClientGui.CbLegacyScreen {

    /** smallOptions */
    public final net.minecraft.class_7172<?>[] field_26679;

    /** narratorButton */
    public net.minecraft.class_339 field_26680;

    /** list */
    public net.minecraft.class_353 field_26681;

    public SimpleOptionsSubScreen(net.minecraft.class_437 parent, net.minecraft.class_315 options, net.minecraft.class_2561 title,
                                  net.minecraft.class_7172<?>[] smallOptions) {
        super(parent, options, title);
        this.field_26679 = smallOptions;
    }

    /** init() */
    @Override
    public void method_25426() {
        this.field_26681 = this.method_37063(new net.minecraft.class_353(this.field_22787, this.field_22789, this));
        this.field_26681.method_20408(this.field_26679);
        this.field_26680 = this.field_26681.method_31046(this.field_21336.method_42476());
        if (this.field_26680 != null) {
            this.field_26680.field_22763 = this.field_22787.method_44713().method_1791();
        }
        super.method_25426();
    }

    /** repositionElements() */
    @Override
    public void method_48640() {
        super.method_48640();
        this.field_26681.method_57712(this.field_22789, this.field_49503);
    }

    /** updateNarratorButton() */
    @SuppressWarnings("unchecked")
    public void method_31050() {
        if (this.field_26680 instanceof net.minecraft.class_5676<?> cycle) {
            ((net.minecraft.class_5676<Object>) cycle).method_32605(this.field_21336.method_42476().method_41753());
        }
    }

    /**
     * 1.21's abstract addOptions: reached only from 1.21's OptionsSubScreen.init, which a 1.20.6 screen does not run
     * (its init is 1.20.6's, above); the screen's options are in its own list.
     */
    @Override
    protected void method_60325() {
    }
}
