package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.List;
import net.minecraft.class_6862;
import net.minecraft.class_6880;

/**
 * 1.21.1's TagNetworkSerialization.TagOutput (net/minecraft/class_6864$class_6865, classRenames), gone in 1.21.2
 * (network tags resolve into a TagLoader.LoadResult): the receiver of each tag a payload held. Only 1.21.1's
 * package-private deserializer called it; a converted class implementing it keeps its type.
 */
public interface TagOutput<T> {

    void accept(class_6862<T> tag, List<class_6880<T>> elements);
}
