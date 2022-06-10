package jp.techacademy.shintaro.nakagawa.finalapp

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder


class Basic3DTest : ApplicationListener {
    var environment: Environment? = null
    var cam: PerspectiveCamera? = null
    var camController: CameraInputController? = null
    var modelBatch: ModelBatch? = null
    var model: Model? = null
    var model2: Model? = null
    var model3: Model? = null
    var instances = ArrayList<ModelInstance>()
    var instance: ModelInstance? = null
    var instance2: ModelInstance? = null
    var instance3: ModelInstance? = null

    override fun create() {
        environment = Environment()
        environment!!.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment!!.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

        modelBatch = ModelBatch()
        cam = PerspectiveCamera(67F, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam!!.position[10f, 10f] = 10f
        cam!!.lookAt(0f, 0f, 0f)
        cam!!.near = 1f
        cam!!.far = 300f
        cam!!.update()

        val modelBuilder = ModelBuilder()
        model = modelBuilder.createBox(5f, 5f, 5f,
                Material(ColorAttribute.createDiffuse(Color.GREEN)),
                (Usage.Position or Usage.Normal).toLong())
        model2 = modelBuilder.createBox(5f, 5f, 5f,
                Material(ColorAttribute.createDiffuse(Color.BLUE)),
                (Usage.Position or Usage.Normal).toLong())
        model3 = modelBuilder.createBox(5f, 5f, 5f,
                Material(ColorAttribute.createDiffuse(Color.RED)),
                (Usage.Position or Usage.Normal).toLong())
        instance = ModelInstance(model)
//        while (true) {
//
//        }
        instance2 = ModelInstance(model2)
        instance2!!.transform.translate(5f, 0f, 0f)
//        instance2 = ModelInstance(model2, null, "2", true,
//                  true, false, true)
        instance3 = ModelInstance(model3)

        camController = CameraInputController(cam)
        Gdx.input.setInputProcessor(camController)
    }

    override fun render() {
        camController!!.update()
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        modelBatch!!.begin(cam)
        modelBatch!!.render(instance, environment)
        modelBatch!!.render(instance2, environment)
//        modelBatch!!.render(instance3, environment)
        modelBatch!!.end()
    }

    override fun dispose() {
        modelBatch!!.dispose();
        model!!.dispose()
        model2!!.dispose()
        model3!!.dispose()
    }

    override fun resume() {}
    override fun resize(width: Int, height: Int) {}
    override fun pause() {}
}