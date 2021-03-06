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

class Cube(
        val size: Int,
        private val isDetect: Boolean = false,
        private val isBack: Boolean = false,
        private val isSolving: Boolean = false,
        private val colorArray: Array<Array<Array<Cubelet?>>>? = null,
        private val solveArray: MutableList<Int> = mutableListOf(),
        val sidePosition: Int = -1) : Disposable {
    private var cubelets: Array<Array<Array<Cubelet?>>>
    private var mesh: Mesh? = null
    private var model: Model? = null
    var modelInstance: ModelInstance? = null
    private val modelBuilder: ModelBuilder
    private var disableAutoRerender = false
    private val cubeletTexture: Texture
    private val cubeMaterial: Material

    fun getCubelet(x: Int, y: Int, z: Int): Cubelet? {
        return cubelets[x][y][z]
    }

    fun render(batch: ModelBatch, environment: Environment?) {
        batch.render(modelInstance, environment)
    }

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

    fun rotateColumn(row: Int, reverse: Boolean = false) {
        var cubelets_tmp = Array<Array<Cubelet?>>(size) { arrayOfNulls<Cubelet>(size) }
        for (y in 0 until size) {
            for (z in 0 until size) {
                val cblt: Cubelet = cubelets[row][y][z] ?: continue
                if ((!reverse && row < size - 1) || (reverse && row == size - 1)) {
                    cblt.rotateTallCW()
                    cubelets_tmp[2 - z][y] = cblt
                } else {
                    cblt.rotateTallCCW()
                    cubelets_tmp[z][2 - y] = cblt
                }
            }
        }
        cubelets[row] = cubelets_tmp
        if (!disableAutoRerender) rerenderCube()
    }

    fun rotateRow(row: Int, reverse: Boolean = false) {
        var cubelets_tmp = Array<Array<Cubelet?>>(size) { arrayOfNulls<Cubelet>(size) }
        for (x in 0 until size) {
            for (z in 0 until size) {
                val cblt: Cubelet = cubelets[x][row][z] ?: continue
                if ((!reverse && row < size - 1) || (reverse && row == size - 1)) {
                    cblt.rotateWideCW()
                    cubelets_tmp[2 - z][x] = cblt
                } else {
                    cblt.rotateWideCCW()
                    cubelets_tmp[z][2 - x] = cblt
                }
            }
        }
        for (x in 0 until size) {
            for (z in 0 until size) {
                cubelets[x][row][z] = cubelets_tmp[x][z]
            }
        }
        if (!disableAutoRerender) rerenderCube()
    }

    fun rotateFace(row: Int, reverse: Boolean = false) {
        var cubelets_tmp = Array<Array<Cubelet?>>(size) { arrayOfNulls<Cubelet>(size) }
        for (x in 0 until size) {
            for (y in 0 until size) {
                val cblt: Cubelet = cubelets[x][y][row] ?: continue
                if ((!reverse && row < size - 1) || (reverse && row == size - 1)) {
                    cblt.rotateDepthCW()
                    cubelets_tmp[2 - y][x] = cblt
                } else {
                    cblt.rotateDepthCCW()
                    cubelets_tmp[y][2 - x] = cblt
                }
            }
        }
        for (x in 0 until size) {
            for (y in 0 until size) {
                cubelets[x][y][row] = cubelets_tmp[x][y]
            }
        }
        if (!disableAutoRerender) rerenderCube()
    }

    fun reset() {
        if (!isDetect) {
            fillWithDefault()
        } else {
            setGray()
        }
        rerenderCube()
    }

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

    private fun rerenderCube() {
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
                    if (i > 0 && i < cubelets.size - 1
                            && j > 0 && j < cubelets.size - 1
                            && k > 0 && k < cubelets.size - 1) continue
                    cubelets[i][j][k] = PlainCubelet(
                            PlainCubelet.CubeletColor.WHITE,
                            PlainCubelet.CubeletColor.YELLOW,
                            PlainCubelet.CubeletColor.GREEN,
                            PlainCubelet.CubeletColor.BLUE,
                            PlainCubelet.CubeletColor.ORANGE,
                            PlainCubelet.CubeletColor.RED)
                    cubelets[i][j][k]!!.setMask(true, true, true, true, true, true)
                }
            }
        }
    }

    fun setGray() {
        for (i in cubelets.indices) {
            for (j in 0 until cubelets[0].size) {
                for (k in 0 until cubelets[0][0].size) {
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

    fun solving() {
        for (i in solveArray.indices) {
            val dec = solveArray[i] / 10
            val milli = solveArray[i] % 10
            when (dec) {
                0 -> {
                    when (milli) {
                        0 -> {
                            rotateRow(2)
                            rotateRow(2)
                            rotateRow(2)
                        }
                        1 -> {
                            rotateRow(0)
                            rotateRow(0)
                            rotateRow(0)
                        }
                        2 -> rotateColumn(0)
                        3 -> rotateColumn(2)
                        4 -> {
                            rotateFace(2)
                            rotateFace(2)
                            rotateFace(2)
                        }
                        5 -> {
                            rotateFace(0)
                            rotateFace(0)
                            rotateFace(0)
                        }
                    }
                }
                1 -> {
                    when (milli) {
                        0 -> rotateRow(2)
                        1 -> rotateRow(0)
                        2 -> {
                            rotateColumn(0)
                            rotateColumn(0)
                            rotateColumn(0)
                        }
                        3 -> {
                            rotateColumn(2)
                            rotateColumn(2)
                            rotateColumn(2)
                        }
                        4 -> rotateFace(2)
                        5 -> rotateFace(0)
                    }
                }
                2 -> {
                    when (milli) {
                        0 -> {
                            rotateRow(2)
                            rotateRow(2)
                        }
                        1 -> {
                            rotateRow(0)
                            rotateRow(0)
                        }
                        2 -> {
                            rotateColumn(0)
                            rotateColumn(0)
                        }
                        3 -> {
                            rotateColumn(2)
                            rotateColumn(2)
                        }
                        4 -> {
                            rotateFace(2)
                            rotateFace(2)
                        }
                        5 -> {
                            rotateFace(0)
                            rotateFace(0)
                        }
                    }
                }
            }
        }
    }

    init {
        cubelets = Array<Array<Array<Cubelet?>>>(size) { Array<Array<Cubelet?>>(size) { arrayOfNulls<Cubelet>(size) } }
        modelBuilder = ModelBuilder()
        cubeletTexture = Texture(Gdx.files.internal("cubelet.png"))
        cubeMaterial = Material(ColorAttribute.createSpecular(Color.WHITE),
                TextureAttribute.createDiffuse(cubeletTexture))
        if (!isDetect && !isSolving) {
            fillWithDefault()
        } else {
            cubelets = colorArray!!
            if (sidePosition >= 0) {
                when (sidePosition) {
                    0 -> {
                        for (x in 0..2) {
                            for (y in 0..2) {
                                if (!isBack) {
                                    cubelets[x][y][2]!!.setColor(CubeletSide.SOUTH, PlainCubelet.CubeletColor.GRAY)
                                } else {

                                }
                            }
                        }
                    }
                    1 -> {
                        for (y in 0..2) {
                            for (z in 0..2) {
                                if (!isBack) {
                                    cubelets[0][y][z]!!.setColor(CubeletSide.WEST, PlainCubelet.CubeletColor.GRAY)
                                } else {

                                }
                            }
                        }
                    }
                    2 -> {
                        for (x in 0..2) {
                            for (y in 0..2) {
                                if (!isBack) {
                                    cubelets[x][y][0]!!.setColor(CubeletSide.NORTH, PlainCubelet.CubeletColor.GRAY)
                                } else {

                                }
                            }
                        }
                    }
                    3 -> {
                        for (y in 0..2) {
                            for (z in 0..2) {
                                if (!isBack) {
                                    cubelets[2][y][z]!!.setColor(CubeletSide.EAST, PlainCubelet.CubeletColor.GRAY)
                                } else {

                                }
                            }
                        }
                    }
                    4 -> {
                        for (x in 0..2) {
                            for (z in 0..2) {
                                if (!isBack) {
                                    cubelets[x][0][z]!!.setColor(CubeletSide.BOTTOM, PlainCubelet.CubeletColor.GRAY)
                                } else {

                                }
                            }
                        }
                    }
                }
            }
        }
        rerenderCube()
    }
}