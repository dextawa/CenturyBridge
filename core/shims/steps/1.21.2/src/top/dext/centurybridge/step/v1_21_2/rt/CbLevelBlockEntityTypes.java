package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.datafixers.types.Type;
import java.util.Set;
import net.minecraft.class_2248;
import net.minecraft.class_2586;
import net.minecraft.class_2591;

/**
 * 1.21.1's BlockEntityType constructor on 1.21.2. 1.21.1's took (factory, valid blocks, datafixer type) and stored the
 * type in a field nothing read (BlockEntityType.dataType, private, no getter, unused by 1.21.1's own code); 1.21.2's
 * private constructor (made public again by the layer's access widener, as 1.21.1 had it) takes the factory and the
 * blocks and keeps the same two fields. A mod's {@code new BlockEntityType(f, blocks, type)} (ctorRedirects) builds
 * 1.21.2's type from the first two; its subclass's super call drops the third (superCtorReshapes).
 */
public final class CbLevelBlockEntityTypes {
    private CbLevelBlockEntityTypes() {
    }

    public static <T extends class_2586> class_2591<T> cbNew(class_2591.class_5559<? extends T> factory, Set<class_2248> validBlocks,
                                                             Type<?> dataType) {
        return new class_2591<>(factory, validBlocks);
    }
}
