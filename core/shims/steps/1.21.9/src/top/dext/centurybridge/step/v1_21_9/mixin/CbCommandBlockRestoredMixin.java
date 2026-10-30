package top.dext.centurybridge.step.v1_21_9.mixin;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.class_1918;
import net.minecraft.class_1928;
import net.minecraft.class_2165;
import net.minecraft.class_2168;
import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8's BaseCommandBlock was itself the CommandSource of its commands: sendSystemMessage recorded the output (when
 * tracking) as "[HH:mm:ss] " + message and reported the change, acceptsSuccess / acceptsFailure / shouldInformAdmins
 * answered from tracking and the gamerules; createCommandSourceStack() built the stack with the block as source
 * (javap -c 1.21.8). 1.21.9 moved the four into a per-run source (BaseCommandBlock.CloseableCommandBlockSource, made
 * only while tracking, closed after the run) and takes that source as createCommandSourceStack's argument.
 *
 * <p>The block is a CommandSource again with 1.21.8's bodies and fields (the SimpleDateFormat under its 1.21.8 name);
 * createCommandSourceStack() is 1.21.9's with the block as source, what 1.21.8's subclasses built. While a converted
 * mod mixes one of the four, 1.21.9's per-run source answers it in its place (CbCommandBlockOutputHooksMixin).
 */
@Mixin(value = class_1918.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbCommandBlockRestoredMixin implements class_2165 {

    private static final SimpleDateFormat field_9169 = new SimpleDateFormat("HH:mm:ss");

    @Shadow
    private boolean field_9164;

    @Shadow
    class_2561 field_9165;

    /**
     * getLevel() / onUpdated() / createCommandSourceStack(source) are called through the vanilla class, not @Shadow-ed:
     * 1.21.11 reshapes all three (getLevel() gone, the other two take the ServerLevel), and a call site climbs through
     * the later steps' wiring as any converted call does, where a shadow of a reshaped member strips the mixin.
     */
    @Unique
    private class_1918 cb$block() {
        return (class_1918) (Object) this;
    }

    /** BaseCommandBlock.sendSystemMessage(message) */
    @Override
    @CbVanillaReads("CbCommandBlockOutputHooksMixin: 1.21.9's per-run command block source runs it in its place while it is mixed")
    public void method_43496(class_2561 message) {
        if (this.field_9164) {
            this.field_9165 = class_2561.method_43470("[" + field_9169.format(new Date()) + "] ").method_10852(message);
            this.cb$block().method_8295();
        }
    }

    /** BaseCommandBlock.acceptsSuccess() */
    @Override
    @CbVanillaReads("CbCommandBlockOutputHooksMixin: 1.21.9's per-run command block source runs it in its place while it is mixed")
    public boolean method_9200() {
        return this.cb$block().method_8293().method_64395().method_8355(class_1928.field_19400) && this.field_9164;
    }

    /** BaseCommandBlock.acceptsFailure() */
    @Override
    @CbVanillaReads("CbCommandBlockOutputHooksMixin: 1.21.9's per-run command block source runs it in its place while it is mixed")
    public boolean method_9202() {
        return this.field_9164;
    }

    /** BaseCommandBlock.shouldInformAdmins() */
    @Override
    @CbVanillaReads("CbCommandBlockOutputHooksMixin: 1.21.9's per-run command block source runs it in its place while it is mixed")
    public boolean method_9201() {
        return this.cb$block().method_8293().method_64395().method_8355(class_1928.field_19394);
    }

    /** BaseCommandBlock.createCommandSourceStack(): the block as source, as 1.21.8's subclasses built it */
    public class_2168 method_8303() {
        return this.cb$block().method_8303(this);
    }
}
