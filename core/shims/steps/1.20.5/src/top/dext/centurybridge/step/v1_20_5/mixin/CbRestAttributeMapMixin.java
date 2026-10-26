package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Map;
import java.util.UUID;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestAttributes;
import top.dext.centurybridge.step.v1_20_5.rt.Guard;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * 1.20.4 AttributeMap's Attribute-keyed members, which 1.20.5 dropped for their Holder-keyed siblings.
 * In 1.20.4 the Holder ones were the delegates (getInstance(holder) was getInstance(holder.value())), so
 * each cover asks 1.20.5's Holder member for the attribute's registered holder -- a mod's override of
 * the 1.20.4 member answers it through its override hook (guarded: a super call from that override
 * comes back here and goes on to 1.20.5's implementation). removeAttributeModifiers /
 * addTransientAttributeModifiers keep 1.20.4's bodies over 1.20.5's map.
 */
@Mixin(value = net.minecraft.class_5131.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestAttributeMapMixin {

    @Shadow
    @Final
    private Map<net.minecraft.class_6880<net.minecraft.class_1320>, net.minecraft.class_1324> field_23709;

    @Shadow
    public abstract net.minecraft.class_1324 method_45329(net.minecraft.class_6880<net.minecraft.class_1320> attribute);

    @Shadow
    public abstract boolean method_45331(net.minecraft.class_6880<net.minecraft.class_1320> attribute);

    @Shadow
    public abstract boolean method_45330(net.minecraft.class_6880<net.minecraft.class_1320> attribute, UUID id);

    @Shadow
    public abstract double method_45332(net.minecraft.class_6880<net.minecraft.class_1320> attribute, UUID id);

    /** 1.20.4 getInstance(Attribute) */
    public net.minecraft.class_1324 method_26842(net.minecraft.class_1320 attribute) {
        boolean entered = Guard.cbEnter(this);
        try {
            return this.method_45329(CbRestAttributes.holder(attribute));
        } finally {
            if (entered) {
                Guard.cbExit(this);
            }
        }
    }

    /** 1.20.4 hasAttribute(Attribute) */
    public boolean method_27306(net.minecraft.class_1320 attribute) {
        boolean entered = Guard.cbEnter(this);
        try {
            return this.method_45331(CbRestAttributes.holder(attribute));
        } finally {
            if (entered) {
                Guard.cbExit(this);
            }
        }
    }

    /** 1.20.4 hasModifier(Attribute, UUID) */
    public boolean method_27305(net.minecraft.class_1320 attribute, UUID id) {
        boolean entered = Guard.cbEnter(this);
        try {
            return this.method_45330(CbRestAttributes.holder(attribute), id);
        } finally {
            if (entered) {
                Guard.cbExit(this);
            }
        }
    }

    /** 1.20.4 getModifierValue(Attribute, UUID) */
    public double method_27307(net.minecraft.class_1320 attribute, UUID id) {
        boolean entered = Guard.cbEnter(this);
        try {
            return this.method_45332(CbRestAttributes.holder(attribute), id);
        } finally {
            if (entered) {
                Guard.cbExit(this);
            }
        }
    }

    /**
     * 1.20.4 removeAttributeModifiers(modifiers): for each attribute the map already has an instance of
     * (none is created), its modifiers of those ids removed
     */
    public void method_26847(com.google.common.collect.Multimap<net.minecraft.class_1320, net.minecraft.class_1322> modifiers) {
        modifiers.asMap().forEach((attribute, mods) -> {
            net.minecraft.class_1324 instance = this.field_23709.get(CbRestAttributes.holder(attribute));
            if (instance != null) {
                mods.forEach(m -> instance.method_6200(m.comp_2447()));
            }
        });
    }

    /**
     * 1.20.4 addTransientAttributeModifiers(modifiers): each modifier replaces the one of its id on the
     * attribute's instance (getInstance, which creates one the supplier has), as a transient modifier
     */
    public void method_26854(com.google.common.collect.Multimap<net.minecraft.class_1320, net.minecraft.class_1322> modifiers) {
        modifiers.forEach((attribute, modifier) -> {
            net.minecraft.class_1324 instance = this.method_26842(attribute);
            if (instance != null) {
                instance.method_6200(modifier.comp_2447());
                instance.method_26835(modifier);
            }
        });
    }
}
