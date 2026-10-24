#version 330

// 1.21.11's rendertype_entity_alpha vertex stage (CbWallRenderTypes: the dragon death explosion's
// vertex-alpha dissolve). It reads no lightmap or fog, so it is unchanged.

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;

out vec4 vertexColor;
out vec2 texCoord0;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    vertexColor = Color;
    texCoord0 = UV0;
}
