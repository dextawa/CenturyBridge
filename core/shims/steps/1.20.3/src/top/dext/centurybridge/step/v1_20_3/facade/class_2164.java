package top.dext.centurybridge.step.v1_20_3.facade;

/**
 * Facade for 1.20.2's net/minecraft/class_2164 (CommandRuntimeException), gone in 1.20.3: an unchecked
 * exception a command throws with the message to show. 1.20.2's Commands.performCommand sent that message
 * to the source as the failure; 1.20.3's execution handles only CommandSyntaxException so -- its command
 * step (the ExecuteCommand mixin) turns this one into such an exception carrying the same message.
 */
public class class_2164 extends RuntimeException {

    /** message */
    public final net.minecraft.class_2561 field_9813;

    public class_2164(net.minecraft.class_2561 message) {
        super(message.getString(), null, com.mojang.brigadier.exceptions.CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES,
            com.mojang.brigadier.exceptions.CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
        this.field_9813 = message;
    }

    /** getComponent() */
    public net.minecraft.class_2561 method_9199() {
        return field_9813;
    }
}
