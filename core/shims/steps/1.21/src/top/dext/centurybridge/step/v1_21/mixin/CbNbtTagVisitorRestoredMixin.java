package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * TextComponentTagVisitor (class_5628) nested visits. 1.20.6 visited a list element or a compound value through the private
 * method_59850(Tag, boolean)Component: a nested visitor new TextComponentTagVisitor(indentation, indentDepth (+1 when the
 * boolean says so), depth + 1) whose visit(tag) answer the caller appended. 1.21 visits the same tag in place, through
 * method_60585(Tag, boolean)V, which raises the same two counters, has the tag accept this visitor (appending to its one
 * builder, field_27842) and lowers them again (javap -c -p of both; both called from visitList's two sites and
 * visitCompound's one). This mixin restores method_59850 with 1.20.6's body on 1.21's visitor: 1.21 has no three-argument
 * constructor, so the nested visitor is a 1.21 visitor of the same indentation whose two counters are set to the values
 * 1.20.6 passed (cb$v1_21$nest, outside the restored body so its field accesses are 1.20.6's). Its text is the text 1.21
 * appends in place (the counters are all the nested visit reads of the caller). CbNbtTagVisitorRestoredMixinHooks runs it in
 * place of method_60585 when a converted mod mixes it (CbMixedMembers). At CbVanillaReads.PRIORITY so a mod's 1.20.6
 * injectors anchor inside it.
 */
@Mixin(value = net.minecraft.class_5628.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbNbtTagVisitorRestoredMixin implements CbPrioritySplit.TagVisitorNested {

    @Shadow
    @org.spongepowered.asm.mixin.Final
    private String field_27840;

    @Shadow
    private int field_51499;

    @Shadow
    private int field_27841;

    @Override
    public void cb$v1_21$nest(int indentDepth, int depth) {
        this.field_51499 = indentDepth;
        this.field_27841 = depth;
    }

    @Override
    public net.minecraft.class_2561 cb$v1_21$visitNested(net.minecraft.class_2520 tag, boolean indent) {
        return this.method_59850(tag, indent);
    }

    /** 1.20.6 TextComponentTagVisitor: a nested visitor one level deeper, its answer */
    @CbVanillaReads("CbNbtTagVisitorRestoredMixinHooks: visitList (method_32298) and visitCompound (method_32292) append this "
        + "method's answer in place of 1.21's in-place nested visit (method_60585) when a converted mod mixes it; body 1.20.6's")
    private net.minecraft.class_2561 method_59850(net.minecraft.class_2520 tag, boolean indent) {
        net.minecraft.class_5628 nested = new net.minecraft.class_5628(this.field_27840);
        ((CbPrioritySplit.TagVisitorNested) nested).cb$v1_21$nest(indent ? this.field_51499 + 1 : this.field_51499, this.field_27841 + 1);
        return nested.method_32305(tag);
    }
}
