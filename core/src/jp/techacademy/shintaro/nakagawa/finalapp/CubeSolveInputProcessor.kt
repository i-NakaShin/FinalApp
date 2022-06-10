package jp.techacademy.shintaro.nakagawa.finalapp

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import java.util.*


class CubeSolveInputProcessor(private val game: CubeSolve, camera: Camera?) : CameraInputController(camera) {
    override fun keyDown(keycode: Int): Boolean {
        var consumed = false
        when (keycode) {
            Input.Keys.Q -> {
                game.cube!!.rotateColumn(0)
                game.updateSolved()
                consumed = true
            }
            Input.Keys.W -> {
                game.cube!!.rotateColumn(1)
                game.updateSolved()
                consumed = true
            }
            Input.Keys.E -> {
                game.cube!!.rotateColumn(2)
                game.updateSolved()
                consumed = true
            }
            Input.Keys.A -> {
                game.cube!!.rotateRow(2)
                game.updateSolved()
                consumed = true
            }
            Input.Keys.S -> {
                game.cube!!.rotateRow(1)
                game.updateSolved()
                consumed = true
            }
            Input.Keys.D -> {
                game.cube!!.rotateRow(0)
                game.updateSolved()
                consumed = true
            }
            Input.Keys.Z -> {
                game.cube!!.rotateFace(2)
                game.updateSolved()
                consumed = true
            }
            Input.Keys.X -> {
                game.cube!!.rotateFace(1)
                game.updateSolved()
                consumed = true
            }
            Input.Keys.C -> {
                game.cube!!.rotateFace(0)
                game.updateSolved()
                consumed = true
            }
            Input.Keys.R -> {
                game.cube!!.shuffle(Random())
                game.updateSolved()
                consumed = true
            }
            Input.Keys.T -> {
                game.cube!!.reset()
                game.updateSolved()
                consumed = true
            }
        }
        return consumed || super.keyDown(keycode)
    }
}
