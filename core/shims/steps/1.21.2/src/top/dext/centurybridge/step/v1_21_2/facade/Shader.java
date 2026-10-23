package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's Shader (net/minecraft/class_3679, classRenames), gone in 1.21.2: a linked GL program with its vertex and
 * fragment Program, as ProgramManager links and releases it. 1.21.2's compiled program (class_5944, 1.21.1's
 * ShaderInstance) implements it again (CbRenderShaderMixin) for getId / markDirty; it keeps no Program (see the ledger).
 */
@Environment(EnvType.CLIENT)
public interface Shader {

    /** getId() */
    int method_1270();

    /** markDirty() */
    void method_1279();

    /** getVertexProgram() */
    Program method_1274();

    /** getFragmentProgram() */
    Program method_1278();

    /** attachToProgram() */
    void method_34418();
}
