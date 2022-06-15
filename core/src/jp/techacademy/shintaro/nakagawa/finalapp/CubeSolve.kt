package jp.techacademy.shintaro.nakagawa.finalapp

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.BitmapFontCache
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController


/**
 * Rubik's cube in libgdx
 */
class CubeSolve(private val isDetect: Boolean = false,
                private val isBack: Boolean = false,
                private val colorArray: Array<Array<Array<Cubelet?>>>? = null,
                val sidePosition: Int = -1) : ApplicationListener {
    private var environment: Environment? = null
    private var hudCam: OrthographicCamera? = null
    private var cam: PerspectiveCamera? = null
    private var camController: CameraInputController? = null
    private var modelBatch: ModelBatch? = null
    private var fpsFont: BitmapFont? = null
    private var controlsCache: BitmapFontCache? = null
    private var fpsCache: BitmapFontCache? = null
    private var hudBatch: SpriteBatch? = null

    /**
     * @return The cube being rendered
     */
    var cube: Cube? = null
    private var solved = true
    private var lastFpsUpdate: Long = 0

    override fun create() {
        environment = Environment()
        environment!!.set(ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 0.8f))
        environment!!.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, 1f, 1f))
        environment!!.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -1f, -1f))
        modelBatch = ModelBatch()
        cube = Cube(3, isDetect, colorArray = colorArray, sidePosition = sidePosition)
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam!!.position[13f, 13f] = 13f
//        cam!!.rotate(270f, 1f, 0f, 0f)
        cam!!.lookAt(0f, 0f, 0f)
        if (isDetect) {
            when (sidePosition) {
                -1 -> {
                    cam!!.position[0f, 20f] = 0f
                    cam!!.lookAt(0f, 0f, 0f)
                    cam!!.rotate(-45f, 0f, 1f, 0f)
//                    cam!!.transform()
                }
                0 -> {
                    cam!!.position[0f, 20f] = 0f
                    cam!!.lookAt(0f, 0f, 0f)
                    cam!!.rotate(-45f, 0f, 1f, 0f)
                    cube!!.modelInstance!!.transform.rotate(1f, 0f, 0f, -45f)

                }
                1 -> {
                    cam!!.position[0f, 20f] = 0f
                    cam!!.lookAt(0f, 0f, 0f)
                    cam!!.rotate(-45f, 0f, 1f, 0f)
                    cube!!.modelInstance!!.transform.rotate(1f, 0f, 0f, -60f)
                    cube!!.modelInstance!!.transform.rotate(0f, 1f, 0f, 60f)
                }
                2 -> {
                    cam!!.position[0f, 20f] = 0f
                    cam!!.lookAt(0f, 0f, 0f)
                    cube!!.modelInstance!!.transform.rotate(1f, 0f, 0f, -60f)
                    cube!!.modelInstance!!.transform.rotate(0f, 1f, 0f, 150f)
                    cube!!.modelInstance!!.transform.rotate(0f, 0f, 1f, -60f)
                }
                3 -> {
                    cam!!.position[0f, 20f] = 0f
                    cam!!.lookAt(0f, 0f, 0f)
                    cube!!.modelInstance!!.transform.rotate(1f, 0f, 0f, -60f)
                    cube!!.modelInstance!!.transform.rotate(0f, 1f, 0f, 240f)
                }
                4 -> {
                    cam!!.position[0f, 20f] = 0f
                    cam!!.lookAt(0f, 0f, 0f)
                    cube!!.modelInstance!!.transform.rotate(1f, 0f, 0f, -60f)
                    cube!!.modelInstance!!.transform.rotate(0f, 1f, 0f, 330f)
                }
                5 -> {
                    cam!!.position[0f, 20f] = 0f
                    cam!!.lookAt(0f, 0f, 0f)
                }
            }
        }
        cam!!.near = 1f
        cam!!.far = 300f
        cam!!.update()
        hudCam = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        hudBatch = SpriteBatch()
//        hudBatch!!.projectionMatrix = hudCam!!.combined
        fpsFont = BitmapFont()
        if (Gdx.app.type == Application.ApplicationType.Desktop || Gdx.app.type == Application.ApplicationType.WebGL) {
            controlsCache = BitmapFontCache(fpsFont, true)
            createControlsCache()
        }
        fpsCache = BitmapFontCache(fpsFont, true)
        updateFpsCache()
        camController = CubeSolveInputProcessor(this, cam)
        if (sidePosition == 5) {
            Gdx.input.inputProcessor = camController
        }
        Gdx.input.inputProcessor = camController
        Gdx.graphics.isContinuousRendering = true
    }

    override fun render() {
        hudBatch!!.projectionMatrix = hudCam!!.combined
        camController!!.update()
//        updateFpsCache()
        Gdx.gl.glClearColor(0.2f,
                if (solved) 0.2f + (1 + Math.sin((System.currentTimeMillis() % 6282 / 200.0f).toDouble()).toFloat()) * 0.05f else 0.2f,
                0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        modelBatch!!.begin(cam)
        cube!!.render(modelBatch!!, environment)
        modelBatch!!.end()
        hudBatch!!.begin()
//        fpsCache!!.draw(hudBatch)
        if (Gdx.app.type == Application.ApplicationType.Desktop || Gdx.app.type == Application.ApplicationType.WebGL) {
//            controlsCache?.draw(hudBatch)
        }
        hudBatch!!.end()
    }

    override fun dispose() {
        modelBatch!!.dispose()
        cube!!.dispose()
    }

    override fun resize(width: Int, height: Int) {
        cam!!.viewportWidth = width.toFloat()
        cam!!.viewportHeight = height.toFloat()
        cam!!.update()
        hudCam!!.setToOrtho(false, width.toFloat(), height.toFloat())
        hudCam!!.update()
        hudBatch!!.projectionMatrix = hudCam!!.combined
        if (Gdx.app.type == Application.ApplicationType.Desktop || Gdx.app.type == Application.ApplicationType.WebGL) {
            createControlsCache()
        }
    }

    override fun pause() {}
    override fun resume() {}

    /**
     * Check whether the cube is solved.
     * Should be called every time the cube changes.
     */
    fun updateSolved() {
        solved = cube!!.isSolved
        if (solved) {
            Gdx.graphics.setTitle("CubeSolve - You Win")
        } else {
            Gdx.graphics.setTitle("CubeSolve")
        }
    }

    private fun createControlsCache() {
        // The controls only matter for desktop since it's the only platform with a keyboard
        if (Gdx.app.type == Application.ApplicationType.Desktop || Gdx.app.type == Application.ApplicationType.WebGL) {
            controlsCache!!.clear()
            var y = fpsFont!!.lineHeight
            for (i in CONTROLS_INFO.indices.reversed()) { // Backwards because Y is upside down
                val s = CONTROLS_INFO[i]
                controlsCache!!.addText(s, 4f, y)
                y += fpsFont!!.lineHeight
            }
        }
    }

    private fun updateFpsCache() {
        if (System.currentTimeMillis() - lastFpsUpdate > 1000L) {
            fpsCache!!.setText("FPS: " + Gdx.graphics.framesPerSecond, 4f, (Gdx.graphics.height - 4).toFloat())
            lastFpsUpdate = System.currentTimeMillis()
        }
    }

    companion object {
        private val CONTROLS_INFO = arrayOf(
                "---Column---",
                "Q - Rotate Left",
                "W - Rotate Middle",
                "E - Rotate Right",
                "",
                "--Row--",
                "A - Rotate Top",
                "S - Rotate Middle",
                "D - Rotate Bottom",
                "",
                "--Face--",
                "Z - Rotate Front",
                "X - Rotate Middle",
                "C - Rotate Back",
                "",
                "--Other--",
                "R - Randomize",
                "T - Reset"
        )
    }
}