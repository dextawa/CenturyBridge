package top.dext.centurybridge.compat.fapi.impl.attachment;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import top.dext.centurybridge.compat.fapi.attachment.v1.AttachmentType;

/** 1.20.1's AttachmentTypeImpl */
public record AttachmentTypeImpl<A>(net.minecraft.class_2960 identifier, Supplier<A> initializer, Codec<A> persistenceCodec,
                                    boolean copyOnDeath) implements AttachmentType<A> {
}
