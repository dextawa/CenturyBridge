package top.dext.centurybridge.step.v26_1.facade;

/**
 * 1.21.11's ServerboundInteractPacket.Handler (net/minecraft/class_2824$class_5908): what a packet's dispatch(..)
 * called for its action -- an interaction with a hand, one at a location on the entity, or an attack. 26.1 split
 * the packet into ServerboundInteractPacket and ServerboundAttackPacket and dropped the handler; a 1.21.11
 * handler is called as 1.21.11's dispatch called it (rt/CbWallInteractPacket). Members keep 1.21.11's
 * intermediary names.
 */
public interface ServerboundInteractPacketHandler {

    /** onInteraction(hand) */
    void method_34219(net.minecraft.world.InteractionHand hand);

    /** onInteraction(hand, location) */
    void method_34220(net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.Vec3 location);

    /** onAttack() */
    void method_34218();
}
