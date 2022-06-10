package jp.techacademy.shintaro.nakagawa.finalapp

import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder


/**
 * Single piece of the rubik's cube
 */
interface Cubelet {
    /**
     * Draw the cubelet into the MeshBuilder
     *
     * @param builder Meshbuilder to draw onto
     * @param x       X position to start the cubelet
     * @param y       Y position to start the cubelet
     * @param z       Z position to start the cubelet
     * @param depth   Width/Height/Depth of the cubelet in gl units
     */
    fun drawMeshes(builder: MeshBuilder?, x: Float, y: Float, z: Float, depth: Float)

    /**
     * Rotate the cubelet tall-wise counter-clockwise
     */
    fun rotateTallCCW()

    /**
     * Rotate the cubelet tall-wise clockwise
     */
    fun rotateTallCW()

    /**
     * Rotate the cubelet wide-wise counter-clockwise
     */
    fun rotateWideCCW()

    /**
     * Rotate the cubelet wide-wise clockwise
     */
    fun rotateWideCW()

    /**
     * Rotate the cubelet depth-wise counter-clockwise
     */
    fun rotateDepthCCW()

    /**
     * Rotate the cubelet depth-wise clockwise
     */
    fun rotateDepthCW()

    /**
     * Set the render mask for the sides of the cubelet
     * @param top Whether to draw the top
     * @param bottom Whether to draw the bottom
     * @param west Whether to draw the west
     * @param east Whether to draw the east
     * @param north Whether to draw the north
     * @param south Whether to draw the south
     */
    fun setMask(top: Boolean, bottom: Boolean, west: Boolean, east: Boolean, north: Boolean, south: Boolean)

    /**
     * Get the color on a side of the cubelet
     * @param side Side to get the color of
     * @return Color of the specified side
     */
    fun getColor(side: PlainCubelet.CubeletSide?): PlainCubelet.CubeletColor?
}