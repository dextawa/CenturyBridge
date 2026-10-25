package cb.s26v;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * The s26 control (native 26.2, no conversion): what vanilla 26.2 does with no 26.1.2 mod present, printed so that a
 * run with the step layer installed and one without can be compared line by line ("CB-S26V ..."):
 * <ul>
 * <li>surface: per chunk of a fixed-seed world (25 chunks 1024 blocks apart) generated to the SURFACE status (the
 *     surface rules' output, before carvers and features, whose cross-chunk placement order varies run to run), a
 *     hash of each column's top six blocks (from the WORLD_SURFACE_WG height) and the biome there;</li>
 * <li>bounce: an armor stand and an item landing through Entity.move on slime, bed, honey, stone and hay from 0.5
 *     above, moving straight down and sideways, the movement after.</li>
 * </ul>
 */
public class S26VTestMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerLevel level = server.overworld();
            try {
                bounce(level);
                surface(level);
            } catch (RuntimeException e) {
                System.out.println("CB-S26V FAIL " + e);
                e.printStackTrace(System.out);
            }
            System.out.println("CB-S26V END");
        });
    }

    static Vec3 land(BlockPos pos, Entity e, Vec3 motion) {
        e.setPos(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
        e.setDeltaMovement(motion);
        e.move(MoverType.SELF, motion);
        return e.getDeltaMovement();
    }

    static void bounce(ServerLevel level) {
        level.getChunk(0, 0);
        BlockPos pos = new BlockPos(8, 250, 8);
        level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(pos.above(2), Blocks.AIR.defaultBlockState(), 3);
        for (Block block : new Block[] {Blocks.SLIME_BLOCK, Blocks.BED.red(), Blocks.HONEY_BLOCK, Blocks.STONE, Blocks.HAY_BLOCK}) {
            level.setBlock(pos, block.defaultBlockState(), 3);
            for (Vec3 motion : new Vec3[] {new Vec3(0.0, -1.0, 0.0), new Vec3(0.2, -0.6, 0.1)}) {
                Entity[] entities = {new ArmorStand(level, 8.5, 250, 8.5), new ItemEntity(level, 8.5, 250, 8.5, new ItemStack(Items.STONE))};
                for (Entity e : entities) {
                    Vec3 v = land(pos, e, motion);
                    System.out.println("CB-S26V bounce " + BuiltInRegistries.BLOCK.getKey(block) + " "
                        + BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()) + " " + motion + " -> " + v + " y=" + e.getY());
                }
            }
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    static void surface(ServerLevel level) {
        System.out.println("CB-S26V seed " + level.getSeed());
        try {
            MessageDigest all = MessageDigest.getInstance("SHA-256");
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    int cx = i * 64, cz = j * 64;
                    ChunkAccess chunk = level.getChunkSource().getChunk(cx, cz, ChunkStatus.SURFACE, true);
                    StringBuilder sb = new StringBuilder();
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            int top = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                            BlockPos col = new BlockPos(cx * 16 + x, top, cz * 16 + z);
                            sb.append(chunk.getNoiseBiome(col.getX() >> 2, top >> 2, col.getZ() >> 2).unwrapKey().map(k -> k.identifier().toString()).orElse("?")).append(':');
                            for (int d = 0; d < 6; d++) {
                                sb.append(BuiltInRegistries.BLOCK.getKey(chunk.getBlockState(col.below(d)).getBlock())).append(',');
                            }
                            sb.append(top).append(';');
                        }
                    }
                    byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
                    all.update(bytes);
                    String h = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)).substring(0, 16);
                    System.out.println("CB-S26V surface " + cx + "," + cz + " " + h);
                }
            }
            System.out.println("CB-S26V surface all " + HexFormat.of().formatHex(all.digest()));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
