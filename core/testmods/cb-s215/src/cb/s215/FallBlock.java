package cb.s215;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

/** a 1.21.4 block overriding fallOn(.., float distance); records what it got and calls super */
public class FallBlock extends class_2248 {
    public static float got = -1;
    public static int calls;

    public FallBlock(class_2251 props) {
        super(props);
    }

    @Override
    public void method_9554(class_1937 level, class_2680 state, class_2338 pos, class_1297 entity, float distance) {
        got = distance;
        calls++;
        super.method_9554(level, state, pos, entity, distance);
    }
}
