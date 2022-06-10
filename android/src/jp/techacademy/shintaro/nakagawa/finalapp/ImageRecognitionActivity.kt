package jp.techacademy.shintaro.nakagawa.finalapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.badlogic.gdx.backends.android.AndroidApplication
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_recognition)
        setupPermissions()

        OpenCVLoader.initDebug()  // ← OpenCVライブラリ読込
        initCamera()

        val r: RelativeLayout = findViewById(R.id.gdx_view)
        val drawListener = CubeSolve()
        val view: View = initializeForView(drawListener)
        r.addView(view)

        drawListener.create()
    }

    private fun initCamera() {
        // リスナ設定
        val cameraView: JavaCameraView = findViewById(R.id.camera_view)
        cameraView.setCvCameraViewListener(object : CameraBridgeViewBase.CvCameraViewListener2 {
            override fun onCameraViewStarted(width: Int, height: Int) {}

            override fun onCameraViewStopped() {}

            override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
                // このメソッド内で画像処理. 今回はポジネガ反転.
                val mat = requireNotNull(inputFrame).rgba()
//                Core.bitwise_not(mat, mat)
                scanFrame(mat)

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
//        val color_list = listOf(Scalar(), red, blue, green, yellow, orange)
        var faces = mutableListOf<Int>()

        for (i in 0 until 18 step 2) {
            faces.add(detectColor(mat, point_list[i], point_list[i + 1]))
        }

//        detectColor(mat, point_list[0], point_list[1])

        val squareMat = Mat(mat, Rect(pt1, pt2))


//        cvtColor(mat, mat, COLOR_RGBA2BGR)
//        cvtColor(mat, mat, COLOR_BGR2HSV)
//        Core.inRange(mat, Scalar(90.0, 70.0, 70.0), Scalar(110.0, 255.0, 255.0), mat)



//        cvtColor(mat, mat, COLOR_GRAY2BGR)
    }

    private fun detectColor(mat: Mat, pt1: Point, pt2: Point): Int {
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
        val colorName_list = listOf("White", "Red", "Blue", "Green", "Yellow", "Orange")


//        Log.d("native", mat.nativeObj.toString())
        rectangle(mat, pt1, pt2, frameColor, 2)
        rectangle(mat, pt1, Point(pt1.x + 30.0, pt1.y + 30.0), frameColor, 2)
        cvtColor(squareMat, squareMat, COLOR_RGBA2BGR)
        cvtColor(squareMat, squareMat, COLOR_BGR2HSV)

//        val return_buff = ByteArray((squareMat.total() * squareMat.channels()).toInt())
//        squareMat[0, 0, return_buff]

        var max_color = 0.0
        var color_num = -1

        for (i in 0 until 6) {
            Core.inRange(squareMat, color_list[i][0], color_list[i][1], result)

            val image_size = (result.total() * result.channels()).toDouble()
            val whitePixels = Core.countNonZero(result).toDouble()
            val whiteAreaRatio = (whitePixels/image_size) * 100

            if (whiteAreaRatio > max_color) {
                max_color = whiteAreaRatio
                color_num = i
            }
        }
        if (color_num != -1 && max_color >= 30) {
            Log.d("kotlintest", "${colorName_list[color_num]} Area [%] : $max_color")
        }

        return color_num
    }

    //パーミッションのチェックを設定するためのメソッド
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
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
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "カメラ機能が許可されませんでした。", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "カメラ機能が許可されました。", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}