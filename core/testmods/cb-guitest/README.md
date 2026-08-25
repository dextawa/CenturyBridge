# cb-guitest

A 1.20.1-era client mod (intermediary names, no fabric-api) that opens a screen
12 s after boot and draws through the 1.20.1 DrawContext API: fill, gradient,
texture blit, matrix push/translate/scale, shadowed and centered text, plus a
vanilla ButtonWidget. Converting it through the chain and watching it render on
a modern client is the regression fixture for the GUI bridges (26.2: the
DrawContext facade over GuiGraphicsExtractor + the override hooks).

Build (any fabric-loader jar works for the API interface):

    javac --release 17 -cp "data/jars/client-1.20.1-intermediary.jar;<fabric-loader.jar>;<client libs>" \
          -d /tmp/gt core/testmods/cb-guitest/src/cb/guitest/*.java
    cp core/testmods/cb-guitest/res/fabric.mod.json /tmp/gt/ && (cd /tmp/gt && jar cf cb-guitest.jar .)

Then `convert core/segments 1.20.1 <target> <out> cb-guitest.jar`, drop the
result next to the bridge jar in a client's mods/, and look for the panel.
