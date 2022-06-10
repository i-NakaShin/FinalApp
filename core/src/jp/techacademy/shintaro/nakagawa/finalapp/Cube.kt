package jp.techacademy.shintaro.nakagawa.finalapp

import jp.techacademy.shintaro.nakagawa.finalapp.PlainCubelet.CubeletSide
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.utils.Disposable
import java.util.*
import kotlin.collections.ArrayList


/**
 * Rubik's cube containing multiple cubelets
 */
class Cube(
        /**
         * @return Number of rows in the cube
         */
        val size: Int) : Disposable {
    private val cubelets: Array<Array<Array<Cubelet?>>>
    private var mesh: Mesh? = null
    private var model: Model? = null
    private var modelInstance: ModelInstance? = null
    private val modelBuilder: ModelBuilder
    private var disableAutoRerender = false
    private val cubeletTexture: Texture
    private val cubeMaterial: Material

    /**
     * Get the cubelet at the specified position
     *
     * @param x X position of the cubelet
     * @param y Y position of the cubelet
     * @param z Z position of the cubelet
     *
     * @return Cubelet at the position
     */
    fun getCubelet(x: Int, y: Int, z: Int): Cubelet? {
        return cubelets[x][y][z]
    }

    /**
     * Render the cube to the ModelBatch
     *
     * @param batch ModelBatch to render to
     * @param environment Environment to render with
     */
    fun render(batch: ModelBatch, environment: Environment?) {
        batch.render(modelInstance, environment)
    }// Check top
    // Check bottom

    // Check south
    // Check north

    // Check west
    // Check east

    // None of the side checks failed
    /**
     * Calculates whether to cube is solved
     *
     * @return Whether all of the sides are of only one color
     */
    val isSolved: Boolean
        get() {
            // Check top
            val topColor: PlainCubelet.CubeletColor? = cubelets[0][size - 1][0]?.getColor(CubeletSide.TOP)
            for (x in 0 until size) {
                for (z in 0 until size) {
                    if (!cubelets[x][size - 1][z]!!.getColor(CubeletSide.TOP)!!.equals(topColor)) return false
                }
            }
            // Check bottom
            val bottomColor: PlainCubelet.CubeletColor? = cubelets[0][0][0]?.getColor(CubeletSide.BOTTOM)
            for (x in 0 until size) {
                for (z in 0 until size) {
                    if (!cubelets[x][0][z]!!.getColor(CubeletSide.BOTTOM)!!.equals(bottomColor)) return false
                }
            }

            // Check south
            val southColor: PlainCubelet.CubeletColor? = cubelets[0][0][0]?.getColor(CubeletSide.SOUTH)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    if (!cubelets[x][y][0]!!.getColor(CubeletSide.SOUTH)!!.equals(southColor)) return false
                }
            }
            // Check north
            val northColor: PlainCubelet.CubeletColor? = cubelets[0][0][size - 1]?.getColor(CubeletSide.NORTH)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    if (!cubelets[x][y][size - 1]!!.getColor(CubeletSide.NORTH)!!.equals(northColor)) return false
                }
            }

            // Check west
            val westColor: PlainCubelet.CubeletColor? = cubelets[0][0][0]?.getColor(CubeletSide.WEST)
            for (z in 0 until size) {
                for (y in 0 until size) {
                    if (!cubelets[0][y][z]!!.getColor(CubeletSide.WEST)!!.equals(westColor)) return false
                }
            }
            // Check east
            val eastColor: PlainCubelet.CubeletColor? = cubelets[size - 1][0][0]?.getColor(CubeletSide.EAST)
            for (z in 0 until size) {
                for (y in 0 until size) {
                    if (!cubelets[size - 1][y][z]!!.getColor(CubeletSide.EAST)!!.equals(eastColor)) return false
                }
            }

            // None of the side checks failed
            return true
        }

    /**
     * Rotate a column tall-wise counter-clockwise
     * @param row Row (x) to rotate
     */
    fun rotateColumn(row: Int) {
        for (y in 0 until size) {
            for (z in 0 until size) {
                val cblt: Cubelet = cubelets[row][y][z] ?: continue
                cblt.rotateTallCCW()
            }
        }
        if (!disableAutoRerender) rerenderCube()
    }

    /**
     * Rotate a row wide-wise counter-clockwise
     * @param row Row (y) to rotate
     */
    fun rotateRow(row: Int) {
        for (x in 0 until size) {
            for (z in 0 until size) {
                val cblt: Cubelet = cubelets[x][row][z] ?: continue
                cblt.rotateWideCCW()
            }
        }
        if (!disableAutoRerender) rerenderCube()
    }

    /**
     * Rotate a face depth-wise counter-clockwise
     * @param row Row (z) to rotate
     */
    fun rotateFace(row: Int) {
        for (x in 0 until size) {
            for (y in 0 until size) {
                val cblt: Cubelet = cubelets[x][y][row] ?: continue
                cblt.rotateDepthCCW()
            }
        }
        if (!disableAutoRerender) rerenderCube()
    }

    /**
     * Reset the cube to its default state
     */
    fun reset() {
        fillWithDefault()
        rerenderCube()
    }

    /**
     * Shuffle the cube
     */
    fun shuffle(rng: Random) {
        disableAutoRerender = true
        for (iter in 0..99) {
            for (row in 0 until size) {
                for (times in 0 until rng.nextInt(3)) {
                    rotateRow(row)
                }
            }
            for (col in 0 until size) {
                for (times in 0 until rng.nextInt(3)) {
                    rotateColumn(col)
                }
            }
            for (face in 0 until size) {
                for (times in 0 until rng.nextInt(3)) {
                    rotateFace(face)
                }
            }
        }
        disableAutoRerender = false
        rerenderCube()
    }

    override fun dispose() {
        mesh!!.dispose()
        cubeletTexture.dispose()
    }

    /**
     * Rerender the cube with its current status
     */
    private fun rerenderCube() {
//        if (mesh != null) mesh!!.dispose()
//        if (model != null) model!!.dispose()
        modelBuilder.begin()
        val builder = MeshBuilder()
        val startX: Float
        val startY: Float
        val startZ: Float
        startZ = -cubelets.size * 3f / 2f
        startY = startZ
        startX = startY
        builder.begin((VertexAttributes.Usage.Position or VertexAttributes.Usage.TextureCoordinates or VertexAttributes.Usage.ColorPacked or VertexAttributes.Usage.Normal).toLong(), GL20.GL_TRIANGLES)
        for (xT in cubelets.indices) {
            for (yT in cubelets[0].indices) {
                for (zT in cubelets[0].indices) {
                    val cblt: Cubelet = cubelets[xT][yT][zT] ?: continue
                    cblt.drawMeshes(builder, startX + xT * 3f, startY + yT * 3f, startZ + zT * 3f, 3f)
                }
            }
        }
        mesh = builder.end()
        modelBuilder.part("cube", mesh, GL20.GL_TRIANGLES, cubeMaterial)
        model = modelBuilder.end()
        modelInstance = ModelInstance(model)
    }

    private fun fillWithDefault() {
        for (i in cubelets.indices) {
            for (j in 0 until cubelets[0].size) {
                for (k in 0 until cubelets[0][0].size) {
                    // Don't add cubelets that are internal
                    if (i > 0 && i < cubelets.size - 1
                            && j > 0 && j < cubelets.size - 1
                            && k > 0 && k < cubelets.size - 1) continue
                    cubelets[i][j][k] = PlainCubelet(
                            PlainCubelet.CubeletColor.WHITE,
                            PlainCubelet.CubeletColor.YELLOW,
                            PlainCubelet.CubeletColor.GREEN,
                            PlainCubelet.CubeletColor.BLUE,
                            PlainCubelet.CubeletColor.RED,
                            PlainCubelet.CubeletColor.ORANGE)
                    cubelets[i][j][k]!!.setMask(j == cubelets.size - 1, j == 0, i == 0, i == cubelets.size - 1, k == cubelets.size - 1, k == 0)
                }
            }
        }
    }

    fun setGray() {
        for (i in cubelets.indices) {
            for (j in 0 until cubelets[0].size) {
                for (k in 0 until cubelets[0][0].size) {
                    // Don't add cubelets that are internal
                    if (i > 0 && i < cubelets.size - 1
                            && j > 0 && j < cubelets.size - 1
                            && k > 0 && k < cubelets.size - 1) continue
                    cubelets[i][j][k] = PlainCubelet(
                            PlainCubelet.CubeletColor.GRAY,
                            PlainCubelet.CubeletColor.BLACK,
                            PlainCubelet.CubeletColor.BLACK,
                            PlainCubelet.CubeletColor.BLACK,
                            PlainCubelet.CubeletColor.BLACK,
                            PlainCubelet.CubeletColor.BLACK)
                    cubelets[i][j][k]!!.setMask(j == cubelets.size - 1, j == 0, i == 0, i == cubelets.size - 1, k == cubelets.size - 1, k == 0)
                }
            }
        }
    }

    /**
     * Creates a Rubik's cube with the default configuration
     *
     * @param size Number of rows for the cube to have
     */
    init {
        cubelets = Array<Array<Array<Cubelet?>>>(size) { Array<Array<Cubelet?>>(size) { arrayOfNulls<Cubelet>(size) } }
        modelBuilder = ModelBuilder()
        cubeletTexture = Texture(Gdx.files.internal("cubelet.png"))
        cubeMaterial = Material(ColorAttribute.createSpecular(Color.WHITE),
                TextureAttribute.createDiffuse(cubeletTexture))
        fillWithDefault()
        rerenderCube()
    }
}