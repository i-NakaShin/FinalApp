package jp.techacademy.shintaro.nakagawa.finalapp

import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder
import java.io.Serializable

interface Cubelet: Serializable {

    fun drawMeshes(builder: MeshBuilder?, x: Float, y: Float, z: Float, depth: Float)

    fun rotateTallCCW()

    fun rotateTallCW()

    fun rotateWideCCW()

    fun rotateWideCW()

    fun rotateDepthCCW()

    fun rotateDepthCW()

    fun setMask(top: Boolean, bottom: Boolean, west: Boolean, east: Boolean, north: Boolean, south: Boolean)

    fun getColor(side: PlainCubelet.CubeletSide?): PlainCubelet.CubeletColor?

    fun setColor(side: PlainCubelet.CubeletSide, color: PlainCubelet.CubeletColor)
}