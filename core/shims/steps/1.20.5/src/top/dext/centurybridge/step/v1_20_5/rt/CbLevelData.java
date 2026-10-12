package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's world spawn, one coordinate at a time -- LevelData.getXSpawn / getYSpawn / getZSpawn and
 * WritableLevelData.setXSpawn / setYSpawn / setZSpawn / setSpawnAngle -- on 1.20.5's spawn position
 * (LevelData.getSpawnPos, WritableLevelData.setSpawn(pos, angle)).
 * <p>
 * Calls (instance redirects on LevelData / WritableLevelData): a call reaches the object's own 1.20.4
 * method when its class declares one -- the converter adds the adapter interfaces below to every mod
 * class declaring the method -- and otherwise does what the vanilla class's 1.20.4 method did.
 * <p>
 * Implementations (adapter hooks): 1.20.5 asks getSpawnPos() and setSpawn(pos, angle). A mod class
 * declaring any of 1.20.4's coordinate getters gets getSpawnPos() synthesized ({@link #spawnPos}): the
 * position 1.20.4's game put together from the three getters (ServerLevel.getSharedSpawnPos, Level,
 * MinecraftServer read them coordinate by coordinate). One declaring any of the setters, and not
 * setSpawn itself, gets setSpawn synthesized ({@link #setSpawn}): what 1.20.4 ran for setSpawn on it.
 * <p>
 * Super calls (super redirects on PrimaryLevelData, DerivedLevelData, ClientLevel.ClientLevelData --
 * CbLevelDataClient -- and WritableLevelData's default setSpawn): what that class's 1.20.4 method did,
 * on its 1.20.5 state ({@link Vanilla}).
 */
public final class CbLevelData {

    /**
     * What the three vanilla implementations carry again (mixin/CbLevelDataPrimaryMixin,
     * CbLevelDataDerivedMixin, the client's CbLevelDataClientMixin): their own spawn state, read and
     * written the way their 1.20.4 per-coordinate methods did. PrimaryLevelData and ClientLevelData
     * kept the coordinates and the angle in fields, which 1.20.5 folded into one position; 1.20.4's
     * DerivedLevelData answered its wrapped data's spawn and ignored every write.
     */
    public interface Vanilla {
        /** the class's own spawn position (DerivedLevelData: the wrapped data's) */
        net.minecraft.class_2338 cbS4$spawnPos();

        /** replaces the class's own spawn position (DerivedLevelData: ignored) */
        void cbS4$spawnPos(net.minecraft.class_2338 pos);

        /** replaces the class's own spawn angle (DerivedLevelData: ignored) */
        void cbS4$spawnAngle(float angle);
    }

    /** a mod class declaring 1.20.4's getXSpawn() */
    public interface GetX {
        int method_215();
    }

    /** a mod class declaring 1.20.4's getYSpawn() */
    public interface GetY {
        int method_144();
    }

    /** a mod class declaring 1.20.4's getZSpawn() */
    public interface GetZ {
        int method_166();
    }

    /** a mod class declaring 1.20.4's setXSpawn(int) */
    public interface SetX {
        void method_27416(int x);
    }

    /** a mod class declaring 1.20.4's setYSpawn(int) */
    public interface SetY {
        void method_27417(int y);
    }

    /** a mod class declaring 1.20.4's setZSpawn(int) */
    public interface SetZ {
        void method_27419(int z);
    }

    /** a mod class declaring 1.20.4's setSpawnAngle(float) */
    public interface SetAngle {
        void method_30657(float angle);
    }

    // ---------------------------------------------------------------- calls

    /** levelData.getXSpawn() */
    public static int getXSpawn(net.minecraft.class_5217 d) {
        return d instanceof GetX g ? g.method_215() : d.method_56126().method_10263();
    }

    /** levelData.getYSpawn() */
    public static int getYSpawn(net.minecraft.class_5217 d) {
        return d instanceof GetY g ? g.method_144() : d.method_56126().method_10264();
    }

    /** levelData.getZSpawn() */
    public static int getZSpawn(net.minecraft.class_5217 d) {
        return d instanceof GetZ g ? g.method_166() : d.method_56126().method_10260();
    }

    /** levelData.setXSpawn(x): the class's own setter, else the vanilla setter's effect */
    public static void setXSpawn(net.minecraft.class_5269 d, int x) {
        if (d instanceof SetX s) {
            s.method_27416(x);
        } else if (d instanceof Vanilla v) {
            stateX(v, x);
        } else {
            net.minecraft.class_2338 p = d.method_56126();
            d.method_187(new net.minecraft.class_2338(x, p.method_10264(), p.method_10260()), d.method_30656());
        }
    }

    /** levelData.setYSpawn(y) */
    public static void setYSpawn(net.minecraft.class_5269 d, int y) {
        if (d instanceof SetY s) {
            s.method_27417(y);
        } else if (d instanceof Vanilla v) {
            stateY(v, y);
        } else {
            net.minecraft.class_2338 p = d.method_56126();
            d.method_187(new net.minecraft.class_2338(p.method_10263(), y, p.method_10260()), d.method_30656());
        }
    }

    /** levelData.setZSpawn(z) */
    public static void setZSpawn(net.minecraft.class_5269 d, int z) {
        if (d instanceof SetZ s) {
            s.method_27419(z);
        } else if (d instanceof Vanilla v) {
            stateZ(v, z);
        } else {
            net.minecraft.class_2338 p = d.method_56126();
            d.method_187(new net.minecraft.class_2338(p.method_10263(), p.method_10264(), z), d.method_30656());
        }
    }

    /** levelData.setSpawnAngle(angle): the class's own setter, else the vanilla setter's effect */
    public static void setSpawnAngle(net.minecraft.class_5269 d, float angle) {
        if (d instanceof SetAngle s) {
            s.method_30657(angle);
        } else if (d instanceof Vanilla v) {
            v.cbS4$spawnAngle(angle);
        } else {
            d.method_187(d.method_56126(), angle);
        }
    }
    // (the last branch of each: an implementation neither vanilla nor converted from 1.20.4 -- written for
    // 1.20.5, so it never had the per-coordinate setters -- moves its spawn through 1.20.5's API)

    /** a vanilla class's 1.20.4 setXSpawn: that one coordinate of its own state replaced */
    static void stateX(Vanilla v, int x) {
        net.minecraft.class_2338 p = v.cbS4$spawnPos();
        v.cbS4$spawnPos(new net.minecraft.class_2338(x, p.method_10264(), p.method_10260()));
    }

    /** a vanilla class's 1.20.4 setYSpawn */
    static void stateY(Vanilla v, int y) {
        net.minecraft.class_2338 p = v.cbS4$spawnPos();
        v.cbS4$spawnPos(new net.minecraft.class_2338(p.method_10263(), y, p.method_10260()));
    }

    /** a vanilla class's 1.20.4 setZSpawn */
    static void stateZ(Vanilla v, int z) {
        net.minecraft.class_2338 p = v.cbS4$spawnPos();
        v.cbS4$spawnPos(new net.minecraft.class_2338(p.method_10263(), p.method_10264(), z));
    }

    // ---------------------------------------------------------------- implementations

    /**
     * getSpawnPos() of a mod class declaring any of 1.20.4's coordinate getters: each coordinate from
     * the class's own getter where it declares one, else what its vanilla superclass's 1.20.4 getter
     * answered (a class implementing LevelData itself declares all three -- they were abstract), in
     * 1.20.4's order x, y, z.
     */
    public static net.minecraft.class_2338 spawnPos(net.minecraft.class_5217 d) {
        net.minecraft.class_2338 v = d instanceof GetX && d instanceof GetY && d instanceof GetZ ? null : vanillaPos(d);
        int x = d instanceof GetX g ? g.method_215() : v.method_10263();
        int y = d instanceof GetY g ? g.method_144() : v.method_10264();
        int z = d instanceof GetZ g ? g.method_166() : v.method_10260();
        return new net.minecraft.class_2338(x, y, z);
    }

    private static net.minecraft.class_2338 vanillaPos(net.minecraft.class_5217 d) {
        if (d instanceof Vanilla v) {
            return v.cbS4$spawnPos();
        }
        // concrete only by inheriting a getter no class declares: 1.20.4 failed the same way at the call
        throw new AbstractMethodError(d.getClass().getName() + " declares only some of getXSpawn/getYSpawn/getZSpawn"
            + " and extends no vanilla LevelData");
    }

    /**
     * setSpawn(pos, angle) of a mod class declaring any of 1.20.4's setters but not setSpawn (a guarded
     * synthesized method): what 1.20.4 ran for setSpawn on that class. Under a vanilla implementation,
     * that class's own setSpawn -- 1.20.4's PrimaryLevelData and ClientLevelData wrote their fields,
     * DerivedLevelData ignored it, none called the per-coordinate setters -- reached by calling setSpawn
     * again, which the guard sends on to super's. A class implementing WritableLevelData itself ran
     * 1.20.4's default: each coordinate, then the angle, through its own setters (all four were abstract,
     * so it declares them).
     */
    public static void setSpawn(net.minecraft.class_5269 d, net.minecraft.class_2338 pos, float angle) {
        if (d instanceof Vanilla) {
            d.method_187(pos, angle);
        } else {
            superSetSpawn(d, pos, angle);
        }
    }

    // ---------------------------------------------------------------- super calls

    /** WritableLevelData.super.setSpawn(pos, angle) (1.20.5 made it abstract): 1.20.4's default body */
    public static void superSetSpawn(net.minecraft.class_5269 d, net.minecraft.class_2338 pos, float angle) {
        setXSpawn(d, pos.method_10263());
        setYSpawn(d, pos.method_10264());
        setZSpawn(d, pos.method_10260());
        setSpawnAngle(d, angle);
    }

    /** super.getXSpawn() in a PrimaryLevelData subclass: 1.20.4 read its xSpawn field */
    public static int superXSpawn(net.minecraft.class_31 d) {
        return ((Vanilla) d).cbS4$spawnPos().method_10263();
    }

    /** super.getYSpawn() in a PrimaryLevelData subclass */
    public static int superYSpawn(net.minecraft.class_31 d) {
        return ((Vanilla) d).cbS4$spawnPos().method_10264();
    }

    /** super.getZSpawn() in a PrimaryLevelData subclass */
    public static int superZSpawn(net.minecraft.class_31 d) {
        return ((Vanilla) d).cbS4$spawnPos().method_10260();
    }

    /** super.setXSpawn(x) in a PrimaryLevelData subclass: 1.20.4 wrote its xSpawn field */
    public static void superSetXSpawn(net.minecraft.class_31 d, int x) {
        stateX((Vanilla) d, x);
    }

    /** super.setYSpawn(y) in a PrimaryLevelData subclass */
    public static void superSetYSpawn(net.minecraft.class_31 d, int y) {
        stateY((Vanilla) d, y);
    }

    /** super.setZSpawn(z) in a PrimaryLevelData subclass */
    public static void superSetZSpawn(net.minecraft.class_31 d, int z) {
        stateZ((Vanilla) d, z);
    }

    /** super.setSpawnAngle(angle) in a PrimaryLevelData subclass: 1.20.4 wrote its spawnAngle field */
    public static void superSetSpawnAngle(net.minecraft.class_31 d, float angle) {
        ((Vanilla) d).cbS4$spawnAngle(angle);
    }

    /** super.getXSpawn() in a DerivedLevelData subclass: 1.20.4 asked the wrapped data */
    public static int superXSpawn(net.minecraft.class_27 d) {
        return ((Vanilla) d).cbS4$spawnPos().method_10263();
    }

    /** super.getYSpawn() in a DerivedLevelData subclass */
    public static int superYSpawn(net.minecraft.class_27 d) {
        return ((Vanilla) d).cbS4$spawnPos().method_10264();
    }

    /** super.getZSpawn() in a DerivedLevelData subclass */
    public static int superZSpawn(net.minecraft.class_27 d) {
        return ((Vanilla) d).cbS4$spawnPos().method_10260();
    }

    /** super.setXSpawn(x) in a DerivedLevelData subclass: 1.20.4's body was empty */
    public static void superSetXSpawn(net.minecraft.class_27 d, int x) {
    }

    /** super.setYSpawn(y) in a DerivedLevelData subclass: 1.20.4's body was empty */
    public static void superSetYSpawn(net.minecraft.class_27 d, int y) {
    }

    /** super.setZSpawn(z) in a DerivedLevelData subclass: 1.20.4's body was empty */
    public static void superSetZSpawn(net.minecraft.class_27 d, int z) {
    }

    /** super.setSpawnAngle(angle) in a DerivedLevelData subclass: 1.20.4's body was empty */
    public static void superSetSpawnAngle(net.minecraft.class_27 d, float angle) {
    }

    private CbLevelData() {
    }
}
