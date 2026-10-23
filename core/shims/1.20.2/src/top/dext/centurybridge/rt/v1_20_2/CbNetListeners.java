package top.dext.centurybridge.rt.v1_20_2;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.class_2535;
import net.minecraft.class_2539;
import net.minecraft.class_2547;
import net.minecraft.class_2561;
import net.minecraft.class_2598;
import net.minecraft.class_2602;
import net.minecraft.class_2792;
import net.minecraft.class_2923;
import net.minecraft.class_2935;
import net.minecraft.class_7832;
import net.minecraft.class_8588;
import net.minecraft.class_8590;
import net.minecraft.class_8591;
import net.minecraft.class_8608;
import net.minecraft.class_8738;
import net.minecraft.class_8739;

/**
 * What 1.20.2 newly asks of a packet listener a 1.20.1 mod implemented itself (abstractFills).
 *
 * 1.20.1's Connection took any listener (setPacketListener) and handled whatever protocol it was
 * set to; 1.20.2's asks the listener its flow and protocol when it is attached and refuses one that
 * does not match the connection. A 1.20.1 listener declares neither: it is the listener of the
 * connection it is attached to, so it answers that connection's receiving side and current inbound
 * protocol (Connection.setListener records the pair before it asks, CbNetListenerConnectionsBridge).
 *
 * The play protocol packets 1.20.2 added are answered as 1.20.2's protocol requires of the side the
 * listener stands for, on the listener's connection, so that the 1.20.1 contract -- chunks just
 * arrive, ping requests are answered by the play phase -- holds for it.
 */
public final class CbNetListeners {

    /** connection side of Connection, implemented by CbNetListenerConnectionsBridge */
    public interface Attached {

        /** the protocol of the connection's inbound codec */
        class_2539 cb$inboundProtocol();
    }

    private static final Map<class_2547, class_2535> CONNECTIONS = Collections.synchronizedMap(new WeakHashMap<>());

    /** Connection.setListener / setListenerForServerboundHandshake, before it validates the listener */
    public static void attaching(class_2547 listener, class_2535 connection) {
        CONNECTIONS.put(listener, connection);
    }

    private static final Map<class_2535, class_7832> CONFIGURED_FEATURES = Collections.synchronizedMap(new WeakHashMap<>());

    /** ClientConfigurationPacketListenerImpl.handleEnabledFeatures: the features this connection was sent */
    public static void configured(class_2535 connection, class_7832 features) {
        CONFIGURED_FEATURES.put(connection, features);
    }

    /**
     * The features packet the configuration phase of the listener's connection received, or null when
     * the listener is not attached to a connection that went through one (a vanilla 1.20.2 server
     * always sends it there: ServerConfigurationPacketListenerImpl.startConfiguration).
     */
    public static class_7832 configuredFeatures(class_2547 listener) {
        class_2535 connection = CONNECTIONS.get(listener);
        return connection == null ? null : CONFIGURED_FEATURES.get(connection);
    }

    private static class_2535 connection(class_2547 listener) {
        class_2535 connection = CONNECTIONS.get(listener);
        if (connection == null) {
            throw new IllegalStateException("1.20.1 packet listener " + listener.getClass().getName()
                + " was asked about its connection before being attached to one: 1.20.1 listeners take the flow and protocol of the connection they are attached to");
        }
        return connection;
    }

    /** flow(): the side of the connection that receives what the listener handles */
    public static class_2598 method_52895(class_2547 listener) {
        return connection(listener).method_36121();
    }

    /** protocol(): the connection's current inbound protocol */
    public static class_2539 method_52280(class_2547 listener) {
        return ((Attached) connection(listener)).cb$inboundProtocol();
    }

    /**
     * ClientGamePacketListener.handleChunkBatchFinished: 1.20.2's server sends chunks in batches and
     * waits for each to be acknowledged with the chunks per tick the client wants. 1.20.1's server sent
     * chunks without waiting; the acknowledgement asks for the most 1.20.2's chunk sender grants
     * (PlayerChunkSender.MAX_CHUNKS_PER_TICK), the nearest it has to that.
     */
    public static void method_52799(class_2602 listener, class_8738 packet) {
        connection(listener).method_10743(new class_8590(class_8608.field_45001));
    }

    /**
     * ClientGamePacketListener.handleChunkBatchStart: vanilla 1.20.2 only starts timing the batch for
     * its own estimate of the chunks per tick it wants; the acknowledgement above asks for the most
     * the server grants and needs no estimate. Nothing in 1.20.1's contract follows from it.
     */
    public static void method_52800(class_2602 listener, class_8739 packet) {
    }

    /**
     * ClientGamePacketListener.handlePongResponse: the server's answer to a play-phase ping request,
     * read by the code that sent the request (1.20.2's ClientPacketListener keeps its own ping monitor).
     * 1.20.1's play protocol had no ping request, so the listener cannot have asked for this answer
     * and has nothing that reads it.
     */
    public static void method_12666(class_2602 listener, class_2923 packet) {
    }

    /**
     * ClientGamePacketListener.handleConfigurationStart: the server asks the client to go back to the
     * configuration phase, which 1.20.2's client answers by acknowledging and attaching a
     * configuration listener. A 1.20.1 play listener has no configuration phase to go to; as 1.20.1
     * did with a packet outside the connection's protocol, the connection is closed, with the reason.
     */
    public static void method_52798(class_2602 listener, class_8588 packet) {
        connection(listener).method_10747(class_2561.method_43470(
            "The server asked to reconfigure the connection; the 1.20.1 play listener "
                + listener.getClass().getName() + " has no configuration phase"));
    }

    /**
     * ServerGamePacketListener.handlePingRequest: 1.20.2's play phase answers a ping request with a
     * pong carrying the request's time, as ServerGamePacketListenerImpl does.
     */
    public static void method_12697(class_2792 listener, class_2935 packet) {
        connection(listener).method_10743(new class_2923(packet.method_12700()));
    }

    /**
     * ServerGamePacketListener.handleChunkBatchReceived: the acknowledgement 1.20.2's chunk sender
     * (owned by ServerGamePacketListenerImpl) waits for before sending the next batch. A 1.20.1
     * listener sends its chunks itself, without batches, so no sender waits for it.
     */
    public static void method_52278(class_2792 listener, class_8590 packet) {
    }

    /**
     * ServerGamePacketListener.handleConfigurationAcknowledged: the client's answer to a
     * StartConfiguration the server sent. A 1.20.1 listener never sends one; vanilla 1.20.2 refuses an
     * acknowledgement it did not ask for with this same exception.
     */
    public static void method_52279(class_2792 listener, class_8591 packet) {
        throw new IllegalStateException("Client acknowledged config, but none was requested");
    }

    private CbNetListeners() {
    }
}
