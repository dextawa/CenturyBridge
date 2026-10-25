package top.dext.centurybridge.step.v1_21_6.facade.fapi;

/**
 * Fabric API 1.21.5's fabric-command-api-v1 CommandRegistrationCallback (net.fabricmc.fabric.api.command.v1), a
 * module 1.21.6's Fabric API removed. 1.21.5's module was this event plus a legacy handler that, from fabric-command-
 * api-v2's event, invoked it with "dedicated" = the selection is DEDICATED (LegacyHandler, javap); v2 is unchanged in
 * 1.21.6. The handler is registered on v2 when this event is first used -- which is when a mod registers a listener,
 * before any server builds its commands -- instead of from the module's own initializer.
 */
public interface CommandRegistrationCallbackV1 {

    net.fabricmc.fabric.api.event.Event<CommandRegistrationCallbackV1> EVENT = Legacy.create();

    void register(com.mojang.brigadier.CommandDispatcher<net.minecraft.class_2168> dispatcher, boolean dedicated);

    final class Legacy {
        private Legacy() {
        }

        static net.fabricmc.fabric.api.event.Event<CommandRegistrationCallbackV1> create() {
            net.fabricmc.fabric.api.event.Event<CommandRegistrationCallbackV1> event = net.fabricmc.fabric.api.event.EventFactory
                .createArrayBacked(CommandRegistrationCallbackV1.class, callbacks -> (dispatcher, dedicated) -> {
                    for (CommandRegistrationCallbackV1 callback : callbacks) {
                        callback.register(dispatcher, dedicated);
                    }
                });
            net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) ->
                event.invoker().register(dispatcher, selection == net.minecraft.class_2170.class_5364.field_25420));
            return event;
        }
    }
}
