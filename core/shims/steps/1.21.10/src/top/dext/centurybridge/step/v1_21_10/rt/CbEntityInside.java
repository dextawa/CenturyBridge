package top.dext.centurybridge.step.v1_21_10.rt;

/**
 * 1.21.10's BlockBehaviour.entityInside and BlockStateBase.entityInside take one argument more than
 * 1.21.9's: a flag Entity's block collision sets when the entity's bounding box really intersects the block
 * (or the entity moved more than about a block that tick, when every block on its path counts). Among
 * 1.21.10's blocks only BubbleColumnBlock reads it -- it skips its whole effect when the flag is false --
 * and every other passes it on to its super call (read off 1.21.10's bytecode). 1.21.9 had no such flag:
 * every entityInside call applied the block's effect. So a 1.21.9 caller's call (callReshapes), and a 1.21.9
 * override's super call when the override was not entered through 1.21.10's collision (frameDefaults:
 * no frame to take vanilla's flag from), pass true -- 1.21.9's behaviour.
 */
public final class CbEntityInside {

    private CbEntityInside() {
    }

    /** the flag a 1.21.9 entityInside call stands for: the block applies its effect, as every 1.21.9 call did */
    public static boolean inside() {
        return true;
    }
}
