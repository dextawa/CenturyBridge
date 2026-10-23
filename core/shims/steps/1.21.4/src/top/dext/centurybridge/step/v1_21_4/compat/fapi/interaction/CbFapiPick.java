package top.dext.centurybridge.step.v1_21_4.compat.fapi.interaction;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import top.dext.centurybridge.step.v1_21_4.rt.CbClientRest;

/**
 * Fabric API 1.21.3's client pick-block events on 1.21.4, where the server picks (pick-from-block / entity requests) and no client
 * event is asked.
 * <p>
 * 1.21.3 picked on the client: the gather callback (then the old callback's container) could supply the stack, else vanilla
 * computed it from the block or entity; the apply callback changed or cancelled it; then the client put it in the hand
 * (creative: the picked item and a creative slot update; survival: select the hotbar slot holding it or ask the server to pick
 * its inventory slot). While a 1.21.3 mod listens to any of these events (or the game's MultiPlayerGameMode is a 1.21.3 subclass
 * overriding handlePickItem), the client picks as 1.21.3's did, those requests
 * included ({@link CbClientRest#method_2916}); without a listener the pick is 1.21.4's (CbFapiPickMixin).
 */
@Environment(EnvType.CLIENT)
public final class CbFapiPick {
    private CbFapiPick() {
    }

    private static volatile boolean listened;

    /** an array-backed event that notes a listener was registered */
    private static final class ListenedEvent<T> extends Event<T> {
        private final Event<T> events;

        ListenedEvent(Event<T> events) {
            this.events = events;
            this.invoker = events.invoker();
        }

        @Override
        public void register(T listener) {
            this.register(Event.DEFAULT_PHASE, listener);
        }

        @Override
        public void register(net.minecraft.class_2960 phase, T listener) {
            this.events.register(phase, listener);
            this.invoker = this.events.invoker();
            listened = true;
        }

        @Override
        public void addPhaseOrdering(net.minecraft.class_2960 first, net.minecraft.class_2960 second) {
            this.events.addPhaseOrdering(first, second);
        }
    }

    static <T> Event<T> event(Class<? super T> type, Function<T[], T> invoker) {
        return new ListenedEvent<>(EventFactory.createArrayBacked(type, invoker));
    }

    public static boolean listened() {
        return listened;
    }

    /** 1.21.3's Minecraft.pickBlock with Fabric API's 1.21.3 client events */
    public static void pick(net.minecraft.class_310 mc) {
        net.minecraft.class_746 player = mc.field_1724;
        net.minecraft.class_239 hit = mc.field_1765;
        // Fabric API: the gather callback, then the old callback
        net.minecraft.class_1799 stack = ClientPickBlockGatherCallback.EVENT.invoker().pick(player, hit);
        if (stack.method_7960()) {
            ClientPickBlockCallback.Container container = new ClientPickBlockCallback.Container(net.minecraft.class_1799.field_8037);
            ClientPickBlockCallback.EVENT.invoker().pick(player, hit, container);
            stack = container.getStack();
        }
        net.minecraft.class_2586 blockEntity = null;
        if (stack.method_7960()) {
            // vanilla 1.21.3
            if (hit == null || hit.method_17783() == net.minecraft.class_239.class_240.field_1333) {
                return;
            }
            boolean creative = player.method_31549().field_7477;
            if (hit.method_17783() == net.minecraft.class_239.class_240.field_1332) {
                net.minecraft.class_2338 pos = ((net.minecraft.class_3965) hit).method_17777();
                net.minecraft.class_2680 state = mc.field_1687.method_8320(pos);
                if (state.method_26215()) {
                    return;
                }
                stack = state.method_65171(mc.field_1687, pos, false);
                if (stack.method_7960()) {
                    return;
                }
                if (creative && net.minecraft.class_437.method_25441() && state.method_31709()) {
                    blockEntity = mc.field_1687.method_8321(pos);
                }
            } else {
                if (hit.method_17783() != net.minecraft.class_239.class_240.field_1331 || !creative) {
                    return;
                }
                stack = ((net.minecraft.class_3966) hit).method_17782().method_31480();
                if (stack == null || stack.method_7960()) {
                    return;
                }
            }
        } else if (player.method_56992() && net.minecraft.class_437.method_25441()
                && hit.method_17783() == net.minecraft.class_239.class_240.field_1332) {
            blockEntity = mc.field_1687.method_8321(((net.minecraft.class_3965) hit).method_17777());
        }
        net.minecraft.class_1661 inventory = player.method_31548();
        if (blockEntity != null) {
            addCustomNbtData(stack, blockEntity, mc.field_1687.method_30349());
        }
        stack = ClientPickBlockApplyCallback.EVENT.invoker().pick(player, hit, stack);
        if (stack.method_7960()) {
            return;
        }
        if (player.method_56992()) {
            setPickedItem(inventory, stack);
            mc.field_1761.method_2909(player.method_5998(net.minecraft.class_1268.field_5808), 36 + inventory.field_7545);
        } else {
            int slot = inventory.method_7395(stack);
            if (slot != -1) {
                if (net.minecraft.class_1661.method_7380(slot)) {
                    inventory.field_7545 = slot;
                } else {
                    CbClientRest.method_2916(mc.field_1761, slot);
                }
            }
        }
    }

    /** 1.21.3 Minecraft.addCustomNbtData: the block entity's data and components on the stack */
    private static void addCustomNbtData(net.minecraft.class_1799 stack, net.minecraft.class_2586 blockEntity, net.minecraft.class_5455 registries) {
        net.minecraft.class_2487 tag = blockEntity.method_59535(registries);
        blockEntity.method_57569(tag);
        net.minecraft.class_1747.method_57338(stack, blockEntity.method_11017(), tag);
        stack.method_57365(blockEntity.method_57590());
    }

    /** 1.21.3 Inventory.setPickedItem(stack) */
    private static void setPickedItem(net.minecraft.class_1661 inventory, net.minecraft.class_1799 stack) {
        int slot = inventory.method_7395(stack);
        if (net.minecraft.class_1661.method_7380(slot)) {
            inventory.field_7545 = slot;
        } else if (slot == -1) {
            inventory.field_7545 = inventory.method_7386();
            if (!inventory.field_7547.get(inventory.field_7545).method_7960()) {
                int free = inventory.method_7376();
                if (free != -1) {
                    inventory.field_7547.set(free, inventory.field_7547.get(inventory.field_7545));
                }
            }
            inventory.field_7547.set(inventory.field_7545, stack);
        } else {
            inventory.method_7365(slot);
        }
    }
}
