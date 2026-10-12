package top.dext.centurybridge.rt.v1_20_2;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client-only bridges of the 1.20.1 -> 1.20.2 networking area (s1net): the client packet handler,
 * chunk cache and world, the chat report builder, quick play, advancement entries on the client.
 * Server-side code never reaches this class (server-check holds the line).
 */
@Environment(EnvType.CLIENT)
public final class CbNetClient {

    // ---------------------------------------------------------------- client packet handler

    /**
     * ClientPlayNetworkHandler(client, screen, connection, serverInfo, profile, telemetry): 1.20.2
     * bundles the per-connection state into a CommonListenerCookie. A handler made the 1.20.1 way
     * starts where 1.20.1's did -- the built-in registries only (ClientRegistryLayer, as 1.20.1's
     * constructor made them), the default feature flags, no server brand yet -- with the old
     * arguments for the rest (the screen is the one shown after a disconnect).
     */
    public static net.minecraft.class_8675 cbClientCookie(com.mojang.authlib.GameProfile profile,
            net.minecraft.class_7975 telemetry, net.minecraft.class_642 serverInfo, net.minecraft.class_437 screen) {
        return new net.minecraft.class_8675(profile, telemetry,
            net.minecraft.class_7756.method_45738().method_45926(), net.minecraft.class_7701.field_40183,
            null, serverInfo, screen);
    }

    // ---------------------------------------------------------------- chat report builder

    /** 1.20.1 had chat reports only: AbuseReportSender.send(id, report) sends a CHAT report */
    public static net.minecraft.class_8776 cbChatReportType() {
        return net.minecraft.class_8776.field_46064;
    }

    // 1.20.1 ChatReportBuilder is 1.20.2's ChatReport.Builder (class_7566 -> class_7566$class_8772,
    // classRenames), every member under a new name. Called through the new names, so a mod subclass
    // overriding one (hooked onto the new name) still answers.

    /** ChatReportBuilder.report(): the report being built */
    public static net.minecraft.class_7566 method_46545(net.minecraft.class_7566.class_8772 b) {
        return b.method_53611();
    }

    /** .reportedProfileId() */
    public static UUID method_44568(net.minecraft.class_7566.class_8772 b) {
        return b.method_53612();
    }

    /** .reportedMessages() */
    public static it.unimi.dsi.fastutil.ints.IntSet method_44579(net.minecraft.class_7566.class_8772 b) {
        return b.method_53597();
    }

    /** .comments() */
    public static String method_44582(net.minecraft.class_7566.class_8772 b) {
        return b.method_53613();
    }

    /** .setComments(comments) */
    public static void method_44576(net.minecraft.class_7566.class_8772 b, String comments) {
        b.method_53610(comments);
    }

    /** .reason() */
    public static net.minecraft.class_7573 method_44583(net.minecraft.class_7566.class_8772 b) {
        return b.method_53614();
    }

    /** .setReason(reason) */
    public static void method_44574(net.minecraft.class_7566.class_8772 b, net.minecraft.class_7573 reason) {
        b.method_53609(reason);
    }

    /** .toggleReported(index) */
    public static void method_44569(net.minecraft.class_7566.class_8772 b, int index) {
        b.method_53598(index);
    }

    /** .isReported(index) */
    public static boolean method_44580(net.minecraft.class_7566.class_8772 b, int index) {
        return b.method_53603(index);
    }

    /** .hasContent() */
    public static boolean method_46546(net.minecraft.class_7566.class_8772 b) {
        return b.method_53602();
    }

    /** .checkBuildable() */
    public static net.minecraft.class_7855.class_7567 method_44584(net.minecraft.class_7566.class_8772 b) {
        return b.method_53605();
    }

    /** .build(context) */
    public static com.mojang.datafixers.util.Either<net.minecraft.class_7855.class_7568, net.minecraft.class_7855.class_7567>
            method_44575(net.minecraft.class_7566.class_8772 b, net.minecraft.class_7574 context) {
        return b.method_53600(context);
    }

    /** .copy() */
    public static net.minecraft.class_7566.class_8772 method_44585(net.minecraft.class_7566.class_8772 b) {
        return b.method_53606();
    }

    /** ChatReportBuilder.ChatReport.toggleReported(index, limits) -- 1.20.2's ChatReport.toggleReported */
    public static void method_46548(net.minecraft.class_7566 report, int index,
            com.mojang.authlib.minecraft.report.AbuseReportLimits limits) {
        report.method_44569(index, limits);
    }

    /** ChatReportBuilder.ChatReport.copy() -- 1.20.2's ChatReport.copy() */
    public static net.minecraft.class_7566 method_46547(net.minecraft.class_7566 report) {
        return report.method_44585();
    }

    // ---------------------------------------------------------------- quick play

    /**
     * QuickPlay.connect(client, data, reload, realms): 1.20.1 waited for the resource reload to
     * finish and then connected on the client thread; 1.20.2 is called once the reload is done.
     */
    public static void method_51260(net.minecraft.class_310 client, net.minecraft.class_542.class_8495 data,
            net.minecraft.class_4011 reload, net.minecraft.class_4341 realms) {
        reload.method_18364().thenRunAsync(() -> net.minecraft.class_8496.method_51260(client, data, realms), client);
    }

    // ---------------------------------------------------------------- advancement entries

    /**
     * The AdvancementEntry 1.20.2 keys an advancement by, for a 1.20.1 Advancement value: one the
     * server loaded (Trackers), else one the client was sent (its advancement tree). 1.20.1's
     * Advancement carried its id; a value neither side registered has none to give.
     */
    public static net.minecraft.class_8779 cbAdvancementEntry(net.minecraft.class_161 advancement) {
        net.minecraft.class_8779 entry = Trackers.ADVANCEMENT_ENTRIES.get(advancement);
        if (entry != null) {
            return entry;
        }
        net.minecraft.class_634 handler = net.minecraft.class_310.method_1551().method_1562();
        if (handler != null) {
            for (net.minecraft.class_8781 node : handler.method_2869().method_53814().method_53693()) {
                if (node.method_53647() == advancement) {
                    return node.method_53649();
                }
            }
        }
        throw new IllegalArgumentException("CenturyBridge: this advancement was loaded by neither the server nor"
            + " the client, and 1.20.2 identifies an advancement only by the entry it was registered under");
    }

    /** ServerboundSeenAdvancementsPacket.openedTab(advancement): the tab is named by its entry's id */
    public static net.minecraft.class_2859 method_12418(net.minecraft.class_161 advancement) {
        return net.minecraft.class_2859.method_12418(cbAdvancementEntry(advancement));
    }

    // ---------------------------------------------------------------- 1.20.1 onFeatures

    private static final Map<Class<?>, MethodHandle> FEATURES = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * ClientPlayPacketListener.onFeatures(packet), and the features packet's apply(listener): 1.20.2
     * delivers the features to the configuration listener, so no play listener declares the method
     * any more. A 1.20.1 call reached the listener's own onFeatures -- ClientPlayNetworkHandler's
     * (put back by CbNetClientPacketListenerBridge) or a mod listener's -- and so does this.
     */
    public static void method_45728(net.minecraft.class_2602 listener, net.minecraft.class_7832 packet) {
        MethodHandle h = FEATURES.computeIfAbsent(listener.getClass(), c -> {
            try {
                return MethodHandles.publicLookup().findVirtual(c, "method_45728",
                    MethodType.methodType(void.class, net.minecraft.class_7832.class));
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("CenturyBridge: " + c.getName()
                    + " has no 1.20.1 onFeatures(UpdateEnabledFeaturesS2CPacket) to deliver the features to", e);
            }
        });
        try {
            h.invoke(listener, packet);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /** FeaturesS2CPacket.apply(ClientPlayPacketListener): 1.20.1's body, listener.onFeatures(this) */
    public static void method_46348(net.minecraft.class_7832 packet, net.minecraft.class_2602 listener) {
        method_45728(listener, packet);
    }

    // ---------------------------------------------------------------- chunk cache / world adapters

    /**
     * ClientChunkManager.unload(x, z) became unload(ChunkPos): the 1.20.2 shape vanilla calls on a
     * mod subclass runs the subclass's 1.20.1 override with the position's coordinates.
     */
    public static void cbDropChunk(CbNetLegacyChunkDrop self, net.minecraft.class_1923 pos) {
        self.method_2859(pos.field_9181, pos.field_9180);
    }

    /**
     * ClientWorld.addEntity(Entity) replaced 1.20.1's addPlayer(id, player) and
     * addEntity(id, entity): a mod subclass overriding either has the one vanilla now calls routed to
     * the 1.20.1 method of that kind -- players to addPlayer, everything else to addEntity -- with the
     * entity's own id, as vanilla 1.20.1 passed it. The one the subclass does not override is the
     * 1.20.1 body CbNetClientLevelBridge puts back, which hands the entity on to vanilla.
     */
    public static void cbAddEntity(CbNetLegacyEntityAdder self, net.minecraft.class_1297 entity) {
        if (entity instanceof net.minecraft.class_742 player) {
            self.method_18107(player.method_5628(), player);
        } else {
            self.method_2942(entity.method_5628(), entity);
        }
    }

    /**
     * getSkinTextures() on a 1.20.1 PlayerListEntry subclass overriding a skin getter: 1.20.1 vanilla
     * read the skin, cape, elytra and model through those getters; 1.20.2 reads the record. The
     * record is built from the getters (the subclass's, or the 1.20.1 bodies that answer from
     * vanilla's record -- reached through the hook's guard), the texture URL and the secure flag
     * from vanilla's, which 1.20.1 had no getter for.
     */
    public static net.minecraft.class_8685 cbSkin(CbNetLegacySkin self) {
        net.minecraft.class_8685 vanilla = ((net.minecraft.class_640) self).method_52810();
        return new net.minecraft.class_8685(self.method_2968(), vanilla.comp_1911(), self.method_2979(), self.method_2957(),
            net.minecraft.class_8685.class_7920.method_52857(self.method_2977()), vanilla.comp_1630());
    }

    private CbNetClient() {
    }
}
