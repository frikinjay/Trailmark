package com.frikinjay.trailmark.api.quest;

import com.frikinjay.trailmark.api.util.TrailmarkCodecs;
import com.mojang.serialization.Codec;

/**
 * The outline a quest node is drawn with on the chapter map. Resource packs can restyle every shape
 * through the quest screen stylesheet, so these are really just names.
 */
public enum NodeShape {
    SQUARE,
    ROUNDED,
    CIRCLE,
    HEXAGON,
    DIAMOND,
    OCTAGON,
    SHIELD,
    STAR;

    public static final Codec<NodeShape> CODEC = TrailmarkCodecs.enumCodec(NodeShape.class);
}
