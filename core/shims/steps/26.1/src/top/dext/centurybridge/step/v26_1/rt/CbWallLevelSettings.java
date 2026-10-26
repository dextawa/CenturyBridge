package top.dext.centurybridge.step.v26_1.rt;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.gamerules.GameRules;

/**
 * 1.21.11's LevelSettings (class_1940) held hardcore, difficulty and the game rules itself; 26.1's record keeps
 * hardcore and difficulty in DifficultySettings(difficulty, hardcore, locked) and the game rules nowhere (the server
 * holds the world's rules). The rules a 1.21.11 caller built the settings with are kept beside the record
 * (mixin/CbWallLevelSettingsMixin, carried by its withers and copy()), answered by gameRules() and handed to the world a
 * client creates from those settings (WorldOpenFlows.createFreshLevel's initial game rules).
 */
public final class CbWallLevelSettings {

    private CbWallLevelSettings() {
    }

    /** the rules kept beside a LevelSettings (mixin/CbWallLevelSettingsMixin); null when none were */
    public interface Rules {
        GameRules cb$gameRules();

        void cb$setGameRules(GameRules rules);
    }

    /** 1.21.11 new LevelSettings(name, gameType, hardcore, difficulty, allowCommands, gameRules, dataConfiguration) */
    public static LevelSettings newSettings(String name, GameType gameType, boolean hardcore, Difficulty difficulty,
                                            boolean allowCommands, GameRules gameRules, WorldDataConfiguration dataConfiguration) {
        LevelSettings s = new LevelSettings(name, gameType, new LevelSettings.DifficultySettings(difficulty, hardcore, false),
            allowCommands, dataConfiguration);
        ((Rules) (Object) s).cb$setGameRules(gameRules);
        return s;
    }

    /** 1.21.11 LevelSettings.hardcore() (method_8583) */
    public static boolean hardcore(LevelSettings s) {
        return s.difficultySettings().hardcore();
    }

    /** 1.21.11 LevelSettings.difficulty() (method_27340) */
    public static Difficulty difficulty(LevelSettings s) {
        return s.difficultySettings().difficulty();
    }

    /**
     * 1.21.11 LevelSettings.gameRules() (method_27341): the rules it was built with; for the running server's own
     * settings the server's rules (where 26.1 keeps the world's); otherwise the default rules of its enabled features
     * (1.21.11's for settings read without a GameRules entry), kept so every call answers the same object.
     */
    public static GameRules gameRules(LevelSettings s) {
        Rules r = (Rules) (Object) s;
        GameRules kept = r.cb$gameRules();
        if (kept != null) {
            return kept;
        }
        MinecraftServer server = CbWallLevelData.SERVER;
        if (server != null && server.getWorldData().getLevelSettings() == s) {
            return server.getGameRules();
        }
        GameRules fresh = new GameRules(s.dataConfiguration().enabledFeatures());
        STAND_INS.put(fresh, Boolean.TRUE);
        r.cb$setGameRules(fresh);
        return fresh;
    }

    /**
     * rules {@link #gameRules} made up for settings no 1.21.11 caller built (26.1-made settings, a world read from
     * disk): stand-ins for rules 26.1 keeps elsewhere, never a world's initial rules (GameRules has identity equality)
     */
    private static final Map<GameRules, Boolean> STAND_INS = Collections.synchronizedMap(new WeakHashMap<>());

    /** the settings' rules were carried onto a wither's / copy's settings (CbWallLevelSettingsMixin): a stand-in stays one */
    public static void carried(GameRules from, GameRules to) {
        if (from != to && STAND_INS.containsKey(from)) {
            STAND_INS.put(to, Boolean.TRUE);
        }
    }

    /**
     * The rules a 1.21.11 caller built these settings with (new LevelSettings(.., gameRules, ..), carried by its
     * withers and copy()), which 1.21.11 created a world from; empty when there are none.
     */
    public static Optional<GameRules> builtRules(LevelSettings s) {
        GameRules kept = ((Rules) (Object) s).cb$gameRules();
        return kept == null || STAND_INS.containsKey(kept) ? Optional.empty() : Optional.of(kept);
    }

    /** 1.21.11 LevelSummary had no requiresFileFixing: none */
    public static boolean noFileFixing() {
        return false;
    }
}
