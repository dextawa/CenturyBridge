package top.dext.centurybridge.step.v1_21_6.rt;

/** 1.21.5 player members 1.21.6 reshaped. */
public final class CbPlayers {

    private CbPlayers() {
    }

    /**
     * After a mod player's super(level, pos, yRot, profile), now super(level, profile): the end of 1.21.5's Player
     * constructor, which placed the player at the block's top centre facing yRot.
     */
    public static void placed(net.minecraft.class_1657 self, net.minecraft.class_2338 pos, float yRot) {
        self.method_5808(pos.method_10263() + 0.5, pos.method_10264() + 1, pos.method_10260() + 0.5, yRot, 0.0F);
    }

    /**
     * PlayerDataStorage.load(Player): 1.21.6's load(Player, ProblemReporter) loads the player from its file the same
     * way and answers the data it read; 1.21.5 answered it as the CompoundTag, which is the TagValueInput's own tag.
     * Problems are logged when the load returns, as 1.21.6's PlayerList logs them.
     */
    public static java.util.Optional<net.minecraft.class_2487> load(net.minecraft.class_29 self, net.minecraft.class_1657 player) {
        net.minecraft.class_8942.class_11340 rep = CbValueIo.reporter();
        try {
            return self.method_55789(player, rep).map(CbValueIo::tag);
        } finally {
            CbValueIo.close(rep);
        }
    }

    /**
     * PlayerList.getPlayerForLogin(profile, clientInformation) (1.21.5 method_14613), gone from 1.21.6, which builds
     * the player inline in ServerConfigurationPacketListenerImpl: 1.21.5's body, new ServerPlayer(server,
     * server.overworld(), profile, clientInformation)
     */
    public static net.minecraft.class_3222 method_14613(net.minecraft.class_3324 self, com.mojang.authlib.GameProfile profile,
                                                        net.minecraft.class_8791 clientInformation) {
        net.minecraft.server.MinecraftServer server = self.method_14561();
        return new net.minecraft.class_3222(server, server.method_30002(), profile, clientInformation);
    }

    /** ServerPlayer.server, private in 1.21.6: the player's server (Entity.getServer answers the same object) */
    public static net.minecraft.server.MinecraftServer field_13995(net.minecraft.class_3222 self) {
        return self.method_5682();
    }
}
