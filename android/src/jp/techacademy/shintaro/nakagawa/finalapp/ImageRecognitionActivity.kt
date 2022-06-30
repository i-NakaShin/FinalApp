package jp.techacademy.shintaro.nakagawa.finalapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.badlogic.gdx.backends.android.AndroidApplication
import kotlinx.coroutines.delay
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.core.*
import org.opencv.imgproc.Imgproc.*

class ImageRecognitionActivity : AndroidApplication() {

    companion object {
        const val CAMERA_REQUEST_CODE = 1
        const val CAMERA_PERMISSION_REQUEST_CODE = 2
        const val M_SQUARE = 100
    }

    private val size: Int = 3
    private var cubelets = Array<Array<Array<Cubelet?>>>(size) { Array<Array<Cubelet?>>(size) { arrayOfNulls<Cubelet>(size) } }
    private var side: Int = 0
    private var isReflection: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_recognition)
        setupPermissions()

        OpenCVLoader.initDebug()  // ← OpenCVライブラリ読込

        for (i in cubelets!!.indices) {
            for (j in cubelets[0].indices) {
                for (k in cubelets[0][0].indices) {
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
                    cubelets[i][j][k]!!.setMask(true, true, true, true, true, true)
                }
            }
        }
        val r: RelativeLayout = findViewById(R.id.gdx_view)
        val drawListener = CubeSolve(true, colorArray = cubelets)
        val view: View = initializeForView(drawListener)
        r.addView(view)

        val okButton: Button = findViewById(R.id.ok_button)
        okButton.setOnClickListener {
            if (side == 6) {
                val intent = Intent(this, SolvingActivity::class.java)
                intent.putExtra("cubelets", cubelets)
                startActivity(intent)
            }
            isReflection = true
            okButton.isClickable = false

            val drawListener = CubeSolve(true, colorArray = cubelets, sidePosition = side)
            val view: View = initializeForView(drawListener)
            r.removeAllViews()
            r.addView(view)

            val thread = Thread(Runnable {
                try {
                    Thread.sleep(800)
                    if (side < 6) {
                        side++
                    }
                    isReflection = false
                    okButton.isClickable = true
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            })
            thread.start()
        }

        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            if (side == 0) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
            if (side > 0) {
                side--
                val drawListener = CubeSolve(true, colorArray = cubelets, sidePosition = side)
                val view: View = initializeForView(drawListener)
                r.removeAllViews()
                r.addView(view)
            }
        }
    }

    //パーミッションのチェックを設定するためのメソッド
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        } else {
            initCamera()
        }
    }

    //パーミッションをリクエストするためのメソッド
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE)
    }

    //パーミッションの許可の結果による実行されるメソッド
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                    initCamera()
                }
                return
            }
        }
    }

    private fun initCamera() {
        // リスナ設定
        val cameraView: JavaCameraView = findViewById(R.id.camera_view)
        cameraView.setCvCameraViewListener(object : CameraBridgeViewBase.CvCameraViewListener2 {
            override fun onCameraViewStarted(width: Int, height: Int) {}

            override fun onCameraViewStopped() {}

            override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
                val mat = requireNotNull(inputFrame).rgba()

                if (!isReflection) {
                    scanFrame(mat)
                }

                return mat
            }
        })

        // プレビューを有効にする
        cameraView.setCameraPermissionGranted()
        cameraView.enableView()
    }

    fun scanFrame(mat: Mat) {
        val width = mat.width().toDouble()
        val height = mat.height().toDouble()

        val x1 = (width - M_SQUARE * 3) / 2
        val y1 = (height - M_SQUARE * 3) / 2
        val x2 = x1 + M_SQUARE
        val y2 = y1 + M_SQUARE
        val x3 = x2 + M_SQUARE
        val y3 = y2 + M_SQUARE
        val x4 = x3 + M_SQUARE
        val y4 = y3 + M_SQUARE

        val pt1 = Point(x1, y1)
        val pt2 = Point(x2, y2)
        val pt3 = Point(x2, y1)
        val pt4 = Point(x3, y2)
        val pt5 = Point(x3, y1)
        val pt6 = Point(x4, y2)
        val pt7 = Point(x1, y2)
        val pt8 = Point(x2, y3)
        val pt9 = Point(x3, y3)
        val pt10 = Point(x4, y3)
        val pt11 = Point(x1, y3)
        val pt12 = Point(x2, y4)
        val pt13 = Point(x3, y4)
        val pt14 = Point(x4, y4)
        val point_list = listOf(pt1, pt2,
                pt3, pt4,
                pt5, pt6,
                pt7, pt8,
                pt2, pt9,
                pt4, pt10,
                pt11, pt12,
                pt8, pt13,
                pt9, pt14)

        for (i in 0 until 18 step 2) {
            when (side) {
                0 -> {
                    val x = i / 2 % 3
                    val z = i / 6
                    cubelets[x][2][z]!!.setColor(PlainCubelet.CubeletSide.TOP, detectColor(mat, point_list[i], point_list[i + 1]))
                }
                1 -> {
                    val x = i / 2 % 3
                    val y = 2 - i / 6
                    cubelets[x][y][2]!!.setColor(PlainCubelet.CubeletSide.SOUTH, detectColor(mat, point_list[i], point_list[i + 1]))
                }
                2 -> {
                    val y = 2 - i / 6
                    val z = i / 2 % 3
                    cubelets[0][y][z]!!.setColor(PlainCubelet.CubeletSide.WEST, detectColor(mat, point_list[i], point_list[i + 1]))
                }
                3 -> {
                    val x = 2 - i / 2 % 3
                    val y = 2 - i / 6
                    cubelets[x][y][0]!!.setColor(PlainCubelet.CubeletSide.NORTH, detectColor(mat, point_list[i], point_list[i + 1]))
                }
                4 -> {
                    val y = 2 - i / 6
                    val z = 2 - i / 2 % 3
                    cubelets[2][y][z]!!.setColor(PlainCubelet.CubeletSide.EAST, detectColor(mat, point_list[i], point_list[i + 1]))
                }
                5 -> {
                    val x = 2 - i / 6
                    val z = 2 - i / 2 % 3
                    cubelets[x][0][z]!!.setColor(PlainCubelet.CubeletSide.BOTTOM, detectColor(mat, point_list[i], point_list[i + 1]))
                }
            }
        }
    }

    private fun detectColor(mat: Mat, pt1: Point, pt2: Point): PlainCubelet.CubeletColor {
        val squareMat = Mat(mat, Rect(pt1, pt2))
        var result = Mat()
        squareMat.copyTo(result)
        val frameColor = Scalar(255.0, 255.0, 255.0)
        val white = listOf(Scalar(0.0, 0.0, 100.0), Scalar(179.0, 45.0, 255.0))
        val red = listOf(Scalar(150.0, 64.0, 0.0), Scalar(179.0, 255.0, 255.0))
        val blue = listOf(Scalar(90.0, 70.0, 70.0), Scalar(110.0, 255.0, 255.0))
        val green = listOf(Scalar(45.0, 64.0, 0.0), Scalar(90.0, 255.0, 255.0))
        val yellow = listOf(Scalar(25.0, 80.0, 10.0), Scalar(45.0, 255.0, 255.0))
        val orange = listOf(Scalar(0.0, 100.0, 100.0), Scalar(25.0, 255.0, 255.0))
        val color_list = listOf(white, red, blue, green, yellow, orange)
        val fillColor = listOf(Scalar(255.0, 255.0, 255.0), Scalar(255.0, 0.0, 0.0),
                Scalar(0.0, 0.0, 255.0), Scalar(0.0, 255.0, 0.0),
                Scalar(255.0, 255.0, 0.0), Scalar(255.0, 140.0, 0.0))
        val colorName_list = listOf(PlainCubelet.CubeletColor.WHITE,
                PlainCubelet.CubeletColor.RED,
                PlainCubelet.CubeletColor.BLUE,
                PlainCubelet.CubeletColor.GREEN,
                PlainCubelet.CubeletColor.YELLOW,
                PlainCubelet.CubeletColor.ORANGE)

        rectangle(mat, pt1, pt2, frameColor, 2)
        cvtColor(squareMat, squareMat, COLOR_RGBA2BGR)
        cvtColor(squareMat, squareMat, COLOR_BGR2HSV)

        var max_color = 0.0
        var color_num = -1

        for (i in 0 until 6) {
            Core.inRange(squareMat, color_list[i][0], color_list[i][1], result)

            val image_size = (result.total() * result.channels()).toDouble()
            val whitePixels = Core.countNonZero(result).toDouble()
            val whiteAreaRatio = (whitePixels / image_size) * 100

            if (whiteAreaRatio > max_color) {
                max_color = whiteAreaRatio
                color_num = i
            }
        }
        if (color_num != -1 && max_color >= 30) {
            rectangle(mat, pt1, Point(pt1.x + 30.0, pt1.y + 30.0), fillColor[color_num], -1)
            return colorName_list[color_num]
        }

        return PlainCubelet.CubeletColor.GRAY
    }
}