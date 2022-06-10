package jp.techacademy.shintaro.nakagawa.finalapp

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder


/**
 * Cubelet surrounded on 4 sides with the same color
 */
class PlainCubelet(private var topColor: CubeletColor, private var bottomColor: CubeletColor,
                   private var westColor: CubeletColor, private var eastColor: CubeletColor,
                   private var northColor: CubeletColor, private var southColor: CubeletColor) : Cubelet {
    private var topMask: Boolean
    private var bottomMask: Boolean
    private var westMask: Boolean
    private var eastMask: Boolean
    private var northMask: Boolean
    private var southMask = true
    override fun drawMeshes(builder: MeshBuilder?, x: Float, y: Float, z: Float, depth: Float) {
        if (topMask) {
            builder!!.setColor(topColor.gdxColor)
            builder.setUVRange(0f, 0f, 1f, 1f)
            builder.rect(x, y + depth, z + depth,
                    x + depth, y + depth, z + depth,
                    x + depth, y + depth, z,
                    x, y + depth, z, 0f, 1f, 0f)
        }
        if (bottomMask) {
            builder!!.setColor(bottomColor.gdxColor)
            builder.setUVRange(0f, 0f, 1f, 1f)
            builder.rect(x, y, z,
                    x + depth, y, z,
                    x + depth, y, z + depth,
                    x, y, z + depth, 0f, -1f, 0f)
        }
        if (southMask) {
            builder!!.setColor(southColor.gdxColor)
            builder.setUVRange(0f, 0f, 1f, 1f)
            builder.rect(x, y + depth, z,
                    x + depth, y + depth, z,
                    x + depth, y, z,
                    x, y, z, 0f, 0f, -1f)
        }
        if (northMask) {
            builder!!.setColor(northColor.gdxColor)
            builder.setUVRange(0f, 0f, 1f, 1f)
            builder.rect(x, y, z + depth,
                    x + depth, y, z + depth,
                    x + depth, y + depth, z + depth,
                    x, y + depth, z + depth, 0f, 0f, 1f)
        }
        if (westMask) {
            builder!!.setUVRange(0f, 0f, 1f, 1f)
            builder.setColor(westColor.gdxColor)
            builder.rect(x, y, z + depth,
                    x, y + depth, z + depth,
                    x, y + depth, z,
                    x, y, z, -1f, 0f, 0f)
        }
        if (eastMask) {
            builder!!.setUVRange(0f, 0f, 1f, 1f)
            builder.setColor(eastColor.gdxColor)
            builder.rect(x + depth, y, z,
                    x + depth, y + depth, z,
                    x + depth, y + depth, z + depth,
                    x + depth, y, z + depth, 1f, 0f, 0f)
        }
    }

    override fun rotateTallCCW() {
        // Temp store the old values so we can overwrite without losing what it was
        val nc = northColor
        val sc = southColor
        val tc = topColor
        val bc = bottomColor
        val nm = northMask
        val sm = southMask
        val tm = topMask
        val bm = bottomMask

        // Rotate colors
        northColor = tc
        bottomColor = nc
        southColor = bc
        topColor = sc
    }

    override fun rotateTallCW() {
        // Temp store the old values so we can overwrite without losing what it was
        val nc = northColor
        val sc = southColor
        val tc = topColor
        val bc = bottomColor

        // Rotate colors
        topColor = nc
        northColor = bc
        bottomColor = sc
        southColor = tc
    }

    override fun rotateWideCCW() {
        // Temp store the old values so we can overwrite without losing what it was
        val nc = northColor
        val sc = southColor
        val wc = westColor
        val ec = eastColor

        // Rotate colors
        northColor = ec
        westColor = nc
        southColor = wc
        eastColor = sc
    }

    override fun rotateWideCW() {
        // Temp store the old values so we can overwrite without losing what it was
        val nc = northColor
        val sc = southColor
        val wc = westColor
        val ec = eastColor

        // Rotate colors
        eastColor = nc
        northColor = wc
        westColor = sc
        southColor = ec
    }

    override fun rotateDepthCCW() {
        // Temp store the old values so we can overwrite without losing what it was
        val tc = topColor
        val bc = bottomColor
        val wc = westColor
        val ec = eastColor

        // Rotate colors
        topColor = ec
        westColor = tc
        bottomColor = wc
        eastColor = bc
    }

    override fun rotateDepthCW() {
        // Temp store the old values so we can overwrite without losing what it was
        val tc = topColor
        val bc = bottomColor
        val wc = westColor
        val ec = eastColor

        // Rotate colors
        eastColor = tc
        topColor = wc
        westColor = bc
        bottomColor = ec
    }

    override fun setMask(top: Boolean, bottom: Boolean, west: Boolean, east: Boolean, north: Boolean, south: Boolean) {
        topMask = top
        bottomMask = bottom
        westMask = west
        eastMask = east
        northMask = north
        southMask = south
    }

    override fun getColor(side: CubeletSide?): CubeletColor? {
        return when (side) {
            CubeletSide.TOP -> topColor
            CubeletSide.BOTTOM -> bottomColor
            CubeletSide.WEST -> westColor
            CubeletSide.EAST -> eastColor
            CubeletSide.NORTH -> northColor
            CubeletSide.SOUTH -> southColor
            else -> null
        }
    }

    enum class CubeletColor(red: Float, green: Float, blue: Float) {
        GREEN(0f, 1f, 0f),
        YELLOW(0.8f, 0.8f, 0f),
        RED(1f, 0f, 0f),
        BLUE(0f, 0f, 1f),
        WHITE(1f, 1f, 1f),
        ORANGE(0.8f, 0.3f, 0f),
        BLACK(0f, 0f, 0f),
        GRAY(0.67f, 0.67f, 0.67f);

        /**
         * @return The GDX color representing the cubelet color
         */
        val gdxColor: Color

        /**
         * Create a cubelet with the specified RGB color
         *
         * @param red   Intensity, between 0.0 and 1.0, inclusive, of the red channel
         * @param green Intensity, between 0.0 and 1.0, inclusive, of the green channel
         * @param blue  Intensity, between 0.0 and 1.0, inclusive, of the blue channel
         */
        init {
            gdxColor = Color(red, green, blue, 1f /*Opaque*/)
        }
    }

    /**
     * Side of a cubelet
     */
    enum class CubeletSide {
        TOP, BOTTOM, WEST, EAST, NORTH, SOUTH
    }

    /**
     * Create a plain cubelet with the specified colors
     * If there is no color on a side, use CubeletColor.NONE
     */
    init {
        northMask = southMask
        eastMask = northMask
        westMask = eastMask
        bottomMask = westMask
        topMask = bottomMask
    }
}