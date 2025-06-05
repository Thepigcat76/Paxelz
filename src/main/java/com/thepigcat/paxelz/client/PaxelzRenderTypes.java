package com.thepigcat.paxelz.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class PaxelzRenderTypes extends RenderStateShard {
    public static final RenderType TEST_RENDER_TYPE;

    public PaxelzRenderTypes(String name, Runnable setupState, Runnable clearState) {
        super(name, setupState, clearState);
    }

    private static RenderType createDefault(String name, VertexFormat format, VertexFormat.Mode mode, RenderType.CompositeState state) {
        return RenderType.create(name, format, mode, 256, false, true, state);
    }

    static {
        TEST_RENDER_TYPE = createDefault(
                "paxelz:highlight_cube",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.QUADS,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE) // Don't write to depth buffer
                        .setCullState(RenderStateShard.NO_CULL)          // Disable face culling
                        .createCompositeState(false)
        );
    }
}