package top.dext.centurybridge.compat.fapi.attachment.v1;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;

/**
 * An attachment type of Fabric API's data attachment API v1 (fabric-data-attachment-api-v1, which
 * 0.92 backported to 1.20.1 and 1.20.2's Fabric API does not have; Fabric API ships it again from
 * 1.20.4). This layer provides the API under its own name: references to the Fabric API name are
 * renamed here (classRenames).
 */
public interface AttachmentType<A> {

    net.minecraft.class_2960 identifier();

    Codec<A> persistenceCodec();

    default boolean isPersistent() {
        return persistenceCodec() != null;
    }

    Supplier<A> initializer();

    boolean copyOnDeath();
}
