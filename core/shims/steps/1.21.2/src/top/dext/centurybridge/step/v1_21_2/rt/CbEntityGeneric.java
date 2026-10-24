package top.dext.centurybridge.step.v1_21_2.rt;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1535;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import net.minecraft.class_9135;
import net.minecraft.class_9139;

/**
 * Entity-area members whose erased shape stayed while their type arguments changed (generic drift):
 * <ul>
 * <li>PaintingVariant.DIRECT_STREAM_CODEC: 1.21.1's was a StreamCodec over any ByteBuf (width, height as var ints,
 *     the asset id); 1.21.2's needs a RegistryFriendlyByteBuf (it also writes the title and author components).
 *     A 1.21.1 read gets a codec of 1.21.1's shape, its variants completed as the 1.21.1 constructor is
 *     (CbEntityStatics.paintingTitle / paintingAuthor).</li>
 * <li>Player.awardRecipesByKey(list): 1.21.1 took recipe ids, 1.21.2 recipe keys (the id in the recipe registry).
 *     Converted both ways, element by element: an id becomes the recipe key of that id, a key passes; a key becomes
 *     its id, an id passes.</li>
 * </ul>
 */
public final class CbEntityGeneric {

    private static final class_9139<ByteBuf, class_1535> PAINTING_DIRECT = class_9139.method_56436(
        class_9135.field_48550, class_1535::comp_2670,
        class_9135.field_48550, class_1535::comp_2671,
        class_2960.field_48267, class_1535::comp_2672,
        (width, height, asset) -> new class_1535(width, height, asset,
            CbEntityStatics.paintingTitle(asset), CbEntityStatics.paintingAuthor(asset)));

    /** 1.21.1 PaintingVariant.DIRECT_STREAM_CODEC */
    public static class_9139<ByteBuf, class_1535> paintingDirectStreamCodec() {
        return PAINTING_DIRECT;
    }

    /** 1.21.1's recipe ids as 1.21.2's recipe keys (keys pass) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List recipeKeys(List ids) {
        if (ids == null) {
            return null;
        }
        List out = new ArrayList(ids.size());
        for (Object o : ids) {
            out.add(o instanceof class_2960 id ? class_5321.method_29179(class_7924.field_52178, id) : o);
        }
        return out;
    }

    /** 1.21.2's recipe keys as 1.21.1's recipe ids (ids pass) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List recipeIds(List keys) {
        if (keys == null) {
            return null;
        }
        List out = new ArrayList(keys.size());
        for (Object o : keys) {
            out.add(o instanceof class_5321<?> key ? key.method_29177() : o);
        }
        return out;
    }

    private CbEntityGeneric() {
    }
}
