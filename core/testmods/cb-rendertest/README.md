# cb-rendertest

A 1.20.1-era client mod (intermediary names, no fabric-api) exercising the entity and
block entity render bridges: a MobEntityRenderer subclass with its own EntityModel and a
FeatureRenderer for pigs, a plain EntityRenderer writing raw vertices for arrows, and a
BlockEntityRenderer for chests. Once the player is in a world it adds one pig and one arrow
(client side) and places a chest in front of a fixed camera, and logs each hook the first
time it runs (`CB-RENDERTEST ...`). Camera and model pose are fixed, so a native 1.20.1 run
of the unconverted jar and a converted run on a modern client frame the objects identically:
the native run is the reference the converted one is compared against.

Build (the mod's access widener opens the two private register methods, so it compiles
against a widened 1.20.1 stub):

    java -cp "core/build/classes;$LIBS" top.dext.centurybridge.Main apply-aw \
         data/jars/client-1.20.1-intermediary.jar /tmp/rt/stub-aw.jar core/testmods/cb-rendertest/res/cb_rendertest.accesswidener
    javac --release 17 -cp "/tmp/rt/stub-aw.jar;<data/runtime/client-1.20.1/libraries jars>" \
          -d /tmp/rt/classes core/testmods/cb-rendertest/src/cb/rendertest/*.java
    cp core/testmods/cb-rendertest/res/* /tmp/rt/classes/ && (cd /tmp/rt/classes && jar cf ../cb-rendertest.jar .)

Run both sides with `core/tools/cbrender_fixture.sh` (beta2: headless client, Xvfb + Mesa
llvmpipe): `cbrender_fixture.sh 1.20.1 <tag> cb-rendertest.jar` for the reference,
`cbrender_fixture.sh 26.2 <tag> cb-rendertest+26.2.jar` for the converted jar.
