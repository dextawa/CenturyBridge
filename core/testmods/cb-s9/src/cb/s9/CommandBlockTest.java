package cb.s9;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1697;
import net.minecraft.class_1918;
import net.minecraft.class_2165;
import net.minecraft.class_2168;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_3218;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.8 BaseCommandBlock: the block is the CommandSource of its commands (sendSystemMessage records the output,
 * acceptsSuccess / acceptsFailure / shouldInformAdmins), createCommandSourceStack() builds the stack with the block as
 * source, a 1.21.8 mixin on sendSystemMessage sees what vanilla's run records (cb.s9.mixin.CommandBlockOutputMixin),
 * and a 1.21.8 subclass implementing the abstract createCommandSourceStack() is the one a run uses.
 */
public final class CommandBlockTest {
    /** what the 1.21.8 mixin on BaseCommandBlock.sendSystemMessage saw */
    public static final List<class_2561> SEEN = new CopyOnWriteArrayList<>();

    private CommandBlockTest() {
    }

    /** a 1.21.8 mod's own command block */
    static final class TestCommandBlock extends class_1918 {
        final class_3218 level;
        int created;
        int updated;

        TestCommandBlock(class_3218 level) {
            this.level = level;
        }

        @Override
        public class_3218 method_8293() {
            return this.level;
        }

        @Override
        public void method_8295() {
            this.updated++;
        }

        @Override
        public class_243 method_8300() {
            return class_243.field_1353;
        }

        @Override
        public class_2168 method_8303() {
            this.created++;
            return new class_2168(this, class_243.field_1353, class_241.field_1340, this.level, 2, "CbTest",
                class_2561.method_43470("CbTest"), this.level.method_8503(), (class_1297) null);
        }

        @Override
        public boolean method_52175() {
            return true;
        }
    }

    static void run(MinecraftServer server) {
        class_3218 level = server.method_30002();
        S9TestMod.run("command block: 1.21.8 CommandSource, its mixin, a 1.21.8 subclass", () -> {
            class_1918 block = new class_1697(class_1299.field_6136, level).method_7567();
            block.method_8286("seed");
            block.method_8287(true);
            SEEN.clear();
            block.method_8301(level);
            class_2561 out = block.method_8292();
            S9TestMod.check(out != null && out.getString().contains("Seed"),
                "a vanilla command block run records its output, got " + (out == null ? null : out.getString()));
            S9TestMod.check(SEEN.size() == 1 && SEEN.get(0).getString().contains("Seed"),
                "the 1.21.8 mixin on BaseCommandBlock.sendSystemMessage sees the run's output, got " + SEEN);
            int nativeSends = Integer.getInteger("cb.s9n.sends", -1);
            S9TestMod.check(nativeSends >= 1,
                "a native 1.21.9 mixin on the per-run source's sendSystemMessage still runs, got " + nativeSends);

            class_2165 source = block;
            S9TestMod.check(source.method_9202() && source.method_9200() && source.method_9201(),
                "the block answers acceptsFailure / acceptsSuccess / shouldInformAdmins as 1.21.8 did (tracking, default gamerules), got "
                    + source.method_9202() + " " + source.method_9200() + " " + source.method_9201());
            block.method_8290(null);
            SEEN.clear();
            class_2168 stack = block.method_8303();
            server.method_3734().method_44252(stack, "seed");
            out = block.method_8292();
            S9TestMod.check(stack.method_9228() instanceof class_1697 && out != null && out.getString().contains("Seed") && SEEN.size() == 1,
                "createCommandSourceStack() (the minecart's, its entity the minecart) runs a command whose output the block records, got " + (out == null ? null : out.getString())
                    + " seen " + SEEN.size());
            block.method_8287(false);
            SEEN.clear();
            source.method_43496(class_2561.method_43470("untracked"));
            S9TestMod.check(block.method_8292() != null && block.method_8292().getString().contains("Seed") && SEEN.size() == 1,
                "not tracking, sendSystemMessage keeps the last output, got " + block.method_8292().getString());

            TestCommandBlock own = new TestCommandBlock(level);
            own.method_8286("seed");
            own.method_8287(true);
            own.method_8301(level);
            out = own.method_8292();
            S9TestMod.check(own.created == 1 && out != null && out.getString().contains("Seed") && own.updated >= 1,
                "a 1.21.8 subclass's createCommandSourceStack() is the one the run uses, got created " + own.created
                    + " updated " + own.updated + " output " + (out == null ? null : out.getString()));
        });
    }
}
