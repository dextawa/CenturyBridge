package top.dext.centurybridge.rt.v1_20_2;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2561;
import net.minecraft.class_3222;

/**
 * The 1.20.1 shape of MessageDecorator.decorate, answering a future (1.20.2's answers the message).
 * A mod decorator implementing the old shape is given this interface, which its own method
 * implements; the 1.20.2 shape vanilla calls reaches it through CbNet.cbDecorate.
 */
public interface CbNetLegacyChatDecorator {

    CompletableFuture<class_2561> decorate(class_3222 sender, class_2561 message);
}
