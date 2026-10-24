#version 330

// 1.21.11's rendertype_entity_alpha fragment stage (CbWallRenderTypes): a texel survives while its alpha
// reaches the vertex alpha.

uniform sampler2D Sampler0;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < vertexColor.a) {
        discard;
    }
    fragColor = color;
}
