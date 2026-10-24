package cb.stags;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_3481;
import net.minecraft.class_6862;

/**
 * Real-server check of the generated tag-drift bridge for step 1.21.3 -> 1.21.4 (written against 1.21.3,
 * converted one step): BlockTags constants whose vanilla tag 1.21.4 changed must keep 1.21.3's members.
 * SWORD_EFFICIENT (1.21.4 dropped the flowers: poppy was in, is out in vanilla 1.21.4) and
 * REPLACEABLE_BY_TREES (1.21.4 added the flowers: dandelion was out, is in in vanilla 1.21.4); a member
 * in both versions shows the tag is not simply empty. Prints "CBTEST PASS|FAIL name" lines and a summary.
 */
public class StagsTestMod implements ModInitializer {

    private static final List<String> FAILS = new ArrayList<>();
    private static int passed;

    private static void check(String name, boolean ok, Object detail) {
        if (ok) {
            passed++;
            System.out.println("CBTEST PASS " + name + " : " + detail);
        } else {
            FAILS.add(name);
            System.out.println("CBTEST FAIL " + name + " : " + detail);
        }
    }

    private static boolean in(class_2248 block, class_6862<class_2248> tag) {
        return block.method_9564().method_26164(tag);
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            class_6862<class_2248> sword = class_3481.field_44469;
            class_6862<class_2248> trees = class_3481.field_44470;
            check("sword_efficient key", true, sword.comp_327());
            check("replaceable_by_trees key", true, trees.comp_327());
            // 1.21.3: poppy and oak_leaves sword-efficient; 1.21.4 vanilla keeps only oak_leaves (via #leaves)
            check("sword_efficient holds poppy (1.21.3 member, gone in 1.21.4)", in(class_2246.field_10449, sword), "poppy");
            check("sword_efficient holds oak_leaves (member in both)", in(class_2246.field_10503, sword), "oak_leaves");
            // 1.21.3: short_grass replaceable by trees, dandelion not; 1.21.4 vanilla adds dandelion
            check("replaceable_by_trees lacks dandelion (added in 1.21.4)", !in(class_2246.field_10182, trees), "dandelion");
            check("replaceable_by_trees holds short_grass (member in both)", in(class_2246.field_10479, trees), "short_grass");
            System.out.println("CBTEST SUMMARY passed=" + passed + " failed=" + FAILS.size() + " " + FAILS);
        });
    }
}
