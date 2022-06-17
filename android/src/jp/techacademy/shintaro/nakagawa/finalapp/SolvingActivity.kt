package jp.techacademy.shintaro.nakagawa.finalapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import com.badlogic.gdx.backends.android.AndroidApplication

class SolvingActivity : AndroidApplication() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solving)

//        val matrix = Matrix()
//        val prevButton: Button = findViewById(R.id.prev_button)
//        val nextImg: Drawable = findViewById(R.drawable.ic_baseline_arrow_forward_ios_24)
//        val nextBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_baseline_arrow_forward_ios_24)
        val r: RelativeLayout = findViewById(R.id.solving_gdx_view)
        val drawListener = CubeSolve()
        val view: View = initializeForView(drawListener)
        r.addView(view)

        Log.d("kotlintest", e_combination_to_index(arrayOf(true,true,true,true,false,false,false,false,false,false,false,false)).toString())
        Log.d("kotlintest", e_combination_to_index(arrayOf(true,false,false,true,false,true,false,true,false,false,false,false)).toString())
        Log.d("kotlintest", e_combination_to_index(arrayOf(false,false,false,false,false,false,false,false,true,true,true,true)).toString())
        Log.d("kotlintest", index_to_e_combination(0).contentToString())
        Log.d("kotlintest", index_to_e_combination(48).contentToString())
        Log.d("kotlintest", index_to_e_combination(494).contentToString())

//        matrix.preScale(-1f, 1f)
//        val prevImg = Bitmap.createBitmap(nextBitmap, 0, 0, nextBitmap.getWidth(), nextBitmap.height, matrix, false);
//        prevButton.background = BitmapDrawable(resources, prevImg)
    }

    fun co_to_index(co: Array<Int>): Int {
        var index = 0
        for (co_i in 0 until co.size-1) {
            index *= 3
            index += co[co_i]
        }
        return index
    }

    fun index_to_co(index: Int): Array<Int> {
        var index_tmp = index
        var co = Array<Int>(8){0}
        var sum_co = 0
        for(i in 6 downTo 0) {
            co[i] = index_tmp % 3
            index_tmp /= 3
            sum_co += co[i]
        }
        co[7] = index_tmp % 3
        index_tmp /= 3
        sum_co += co[7]
        co[7] = (3 - sum_co % 3) % 3

        return co
    }

    fun eo_to_index(eo: Array<Int>): Int {
        var index = 0
        for (eo_i in 0 until eo.size-1) {
            index *= 2
            index += eo[eo_i]
        }
        return index
    }

    fun index_to_eo(index: Int): Array<Int> {
        var index_tmp = index
        var eo = Array<Int>(12){0}
        var sum_eo = 0
        for(i in 10 downTo 0) {
            eo[i] = index_tmp % 2
            index_tmp /= 2
            sum_eo += eo[i]
        }
        eo[11] = index_tmp % 2
        index_tmp /= 2
        sum_eo += eo[11]
        eo[11] = (2 - sum_eo % 2) % 2

        return eo
    }

    fun calc_combination(n: Int, r:Int): Int {
        var ret = 0
        for (i in 0..r) {
            ret *= n - i
        }
        for (i in 0..r) {
            if (r != 0) {
                ret /= r - i
            }
        }
        return ret
    }

    fun e_combination_to_index(comb: Array<Boolean>): Int {
        var index = 0
        var r = 4
        for (i in 12 - 1 downTo 0) {
            if (comb[i]) {
                index += calc_combination(i, r)
                r -= 1
            }
        }
        if (comb[11]) {
            index += calc_combination(11, r)
        }
        return index
    }

    fun index_to_e_combination(index: Int): Array<Boolean> {
        var index_tmp = index
        var combination = Array<Boolean>(12){false}
        var r = 4
        for (i in 12 - 1 downTo 0) {
            if (index_tmp >= calc_combination(i, r)) {
                combination[i] = true
                index_tmp -= calc_combination(i, r)
                r -= 1
            }
        }
        if (index_tmp >= calc_combination(11, r)) {
            combination[11] = true
        }
        return combination
    }

    companion object {
        val NUM_CORNERS = 8
        val NUM_EDGES = 12

        val NUM_CO = 2187  // 3 ** 7
        val NUM_EO = 2048  // 2 ** 11
        val NUM_E_COMBINATIONS = 495  // 12C4

        val NUM_CP = 40320  // 8!
        //        val NUM_EP = 479001600  // 12! # これは使わない
        val NUM_UD_EP = 40320  // 8!
        val NUM_E_EP = 24  // 4!
    }
}