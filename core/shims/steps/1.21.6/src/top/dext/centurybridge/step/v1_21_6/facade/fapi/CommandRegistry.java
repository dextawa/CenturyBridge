package top.dext.centurybridge.step.v1_21_6.facade.fapi;

/**
 * Fabric API 1.21.5's fabric-commands-v0 CommandRegistry (net.fabricmc.fabric.api.registry), a module 1.21.6's Fabric
 * API removed: 1.21.5's body -- a fabric-command-api-v2 listener that skips a dedicated-only registration when the
 * selection does not include dedicated servers, and otherwise hands the dispatcher to the consumer (javap).
 */
public class CommandRegistry {

    public static final CommandRegistry INSTANCE = new CommandRegistry();

    public CommandRegistry() {
    }

    public void register(boolean dedicated, java.util.function.Consumer<com.mojang.brigadier.CommandDispatcher<net.minecraft.class_2168>> consumer) {
        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            if (dedicated && !selection.field_25423) {
                return;
            }
            consumer.accept(dispatcher);
        });
    }
}
