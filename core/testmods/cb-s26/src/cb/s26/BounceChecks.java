package cb.s26;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.phys.Vec3;

/**
 * 26.1.2 bouncing (s26b, queue item 5; rt/CbS26Bounce): Block / SlimeBlock / BedBlock.updateEntityMovementAfterFallOn
 * called as 26.1.2 mods call it, and a 26.1.2 block overriding it, landed on through Entity.move as on 26.1.2. Runs
 * once the server has started (a loaded level), then prints "CB-S26 SERVER DONE passed/checks".
 */
final class BounceChecks {
    private BounceChecks() {}

    static int fallOnCalls;

    /** a 26.1.2 mod's bouncy block: half the impact speed back up */
    static class HalfBounce extends Block {
        HalfBounce() {
            super(S26TestMod.blockProps("half_bounce"));
        }

        @Override
        public void updateEntityMovementAfterFallOn(BlockGetter level, Entity entity) {
            fallOnCalls++;
            Vec3 v = entity.getDeltaMovement();
            entity.setDeltaMovement(v.x, -v.y * 0.5, v.z);
        }
    }

    /** a 26.1.2 mod's slime block subclass going to super */
    static class ModSlime extends SlimeBlock {
        ModSlime() {
            super(S26TestMod.blockProps("mod_slime"));
        }

        @Override
        public void updateEntityMovementAfterFallOn(BlockGetter level, Entity entity) {
            fallOnCalls++;
            super.updateEntityMovementAfterFallOn(level, entity);
        }
    }

    static HalfBounce half;
    static ModSlime modSlime;

    static void register() {
        half = Registry.register(BuiltInRegistries.BLOCK, S26TestMod.blockKey("half_bounce"), new HalfBounce());
        modSlime = Registry.register(BuiltInRegistries.BLOCK, S26TestMod.blockKey("mod_slime"), new ModSlime());
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            try {
                run(server.overworld());
            } catch (RuntimeException e) {
                S26TestMod.check("bounce.run", false, e);
                e.printStackTrace(System.out);
            }
            S26TestMod.done("SERVER");
        });
    }

    /** the vertical speed after a direct 26.1.2 call on a falling entity moving (0.3, -1, 0.2) */
    static double direct(Block block, ServerLevel level, Entity e) {
        e.setDeltaMovement(0.3, -1.0, 0.2);
        block.updateEntityMovementAfterFallOn(level, e);
        Vec3 v = e.getDeltaMovement();
        S26TestMod.check("bounce.direct keeps x/z " + block, v.x == 0.3 && v.z == 0.2, v);
        return v.y;
    }

    /** lands the entity on a block at pos through Entity.move: from 0.5 above, moving (0, -1, 0) */
    static Vec3 land(ServerLevel level, BlockPos pos, Entity e) {
        e.setPos(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
        e.setDeltaMovement(0.0, -1.0, 0.0);
        e.move(MoverType.SELF, new Vec3(0.0, -1.0, 0.0));
        return e.getDeltaMovement();
    }

    static void run(ServerLevel level) {
        level.getChunk(0, 0);
        ArmorStand stand = new ArmorStand(level, 8.5, 250, 8.5);
        ItemEntity item = new ItemEntity(level, 8.5, 250, 8.5, new ItemStack(Items.STONE));

        // 26.1.2's bodies, as a 26.1.2 mod's call reaches them (Block: movement.multiply(1, 0, 1), so -1.0 * 0.0 = -0.0)
        S26TestMod.check("bounce.Block (stone)", String.valueOf(direct(Blocks.STONE, level, stand)), "-0.0");
        S26TestMod.check("bounce.SlimeBlock living", String.valueOf(direct(Blocks.SLIME_BLOCK, level, stand)), "1.0");
        S26TestMod.check("bounce.SlimeBlock item", String.valueOf(direct(Blocks.SLIME_BLOCK, level, item)), "0.8");
        S26TestMod.check("bounce.BedBlock living", String.valueOf(direct(Blocks.RED_BED, level, stand)), String.valueOf(1.0 * 0.66F));
        S26TestMod.check("bounce.BedBlock item", String.valueOf(direct(Blocks.RED_BED, level, item)), String.valueOf(1.0 * 0.66F * 0.8));
        int before = fallOnCalls;
        S26TestMod.check("bounce.mod slime super", String.valueOf(direct(modSlime, level, stand)), "1.0");
        S26TestMod.check("bounce.mod slime override called", fallOnCalls == before + 1, fallOnCalls - before);

        // a 26.1.2 block overriding it, landed on: 26.1.2's Entity.move asked it on the vertical collision
        BlockPos pos = new BlockPos(8, 250, 8);
        level.setBlock(pos, half.defaultBlockState(), 3);
        level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(pos.above(2), Blocks.AIR.defaultBlockState(), 3);
        before = fallOnCalls;
        Vec3 v = land(level, pos, stand);
        S26TestMod.check("bounce.mod block landed", fallOnCalls == before + 1 && v.y == 0.5 && v.x == 0.0 && v.z == 0.0,
            "calls " + (fallOnCalls - before) + " " + v);
        S26TestMod.check("bounce.mod block landed on top", stand.getY() == pos.getY() + 1.0, stand.getY());
        before = fallOnCalls;
        v = land(level, pos, item);
        S26TestMod.check("bounce.mod block landed (item)", fallOnCalls == before + 1 && v.y == 0.5, "calls " + (fallOnCalls - before) + " " + v);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }
}
