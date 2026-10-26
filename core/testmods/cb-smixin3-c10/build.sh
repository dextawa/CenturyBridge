#!/usr/bin/env bash
# smixin3 C10 test: a 26.1.2 mod with a Gui mixin using (Gui)(Object)this and a Gui accessor cast from mc.gui, converted 26.1.2 -> 26.2
set -e
cd ~/cbwork; A=agents/smixin3; T=$A/c10
J=$(ls -d ~/cbtest/jdk-25*/bin | head -1)
MX=$(find data/runtime/client-26.2/libraries -name "sponge-mixin*.jar" | head -1)
rm -rf $T/cls $T/out; mkdir -p $T/cls $T/out
$J/javac -nowarn --release 21 -proc:none -cp "data/jars/client-26.1.2.jar:$MX" -d $T/cls $(find $T/src -name "*.java")
cp $T/res/* $T/cls/
(cd $T/cls && $J/jar cf ../cb-c10.jar .)
LIBS=$(ls data/runtime/client-26.2/libraries/org/ow2/asm/*/*/*.jar data/runtime/client-26.2/libraries/com/google/code/gson/gson/*/*.jar | tr '\n' ':')
CP="$A/core/build/classes:$LIBS"
$J/java -Xmx8g -Dcb.wiring=$A/core/segments/steps/26.1.2__26.2.json -Dcb.layerJar=$A/build/centurybridge-0.3.0+26.2.jar -cp "$CP" top.dext.centurybridge.Main convert $A/core/segments 26.1.2 26.2 $T/out $T/cb-c10.jar > $T/convert.log 2>&1
OUT=$(ls $T/out/*.jar | head -1); echo "converted: $OUT"
rm -rf $T/x; mkdir -p $T/x; (cd $T/x && $J/jar xf ../../../../$OUT 2>/dev/null || unzip -q -o ../../../../$OUT)
$J/javap -c -p -v -cp $T/x cb.c10.mixin.GuiMixin | grep -E "RuntimeInvisibleAnnotations|Hud|Gui|checkcast|getfield" | head -20
$J/javap -c -p -cp $T/x cb.c10.Seen | grep -E "invokestatic|checkcast|invokeinterface"
ls $T/x/centurybridge/gen/ 2>/dev/null
grep -a "mixin split\|follows" $T/out/*.md 2>/dev/null | head; grep -a "mixin split\|follows its target" $T/convert.log | head
mkdir -p $T/vcls; $J/javac -nowarn -cp "$CP" -d $T/vcls $A/core/tools/BytecodeVerify.java; $J/java -cp "$T/vcls:$CP" BytecodeVerify $T/out 2>&1 | tail -4
