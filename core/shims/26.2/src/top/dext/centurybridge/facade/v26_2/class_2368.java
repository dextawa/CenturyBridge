package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Facade for 1.20.1 net/minecraft/class_2368, a trivial HalfTransparentBlock
 * subclass (its only member is the (Properties) constructor; all behaviour is
 * inherited). The class was removed before the 1.21.11 namespace wall, so it is
 * absent from the wall table and a mod that does {@code new class_2368(props)}
 * (Ad Astra's ModBlocks) hits NoClassDefFound. Its surviving ancestor
 * HalfTransparentBlock (class_2373) is still a non-final {@code public class} on
 * 26.2, so this facade extends it and forwards the Properties constructor --
 * the block registers and renders as the half-transparent block it always was.
 * Reached via a ctorRedirect on the old constructor (new class_2368 ->
 * Statics.cbNewclass_2368) so both direct new and class_2368::new resolve.
 */
public class class_2368 extends HalfTransparentBlock {

    public class_2368(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
