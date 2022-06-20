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

//        Log.d("kotlintest", co_to_index(arrayOf(0,0,0,0,0,0,0,0)).toString())
//        Log.d("kotlintest", co_to_index(arrayOf(2,1,0,0,1,0,1,1)).toString())
//        Log.d("kotlintest", co_to_index(arrayOf(2, 2, 2, 2, 2, 2, 2, 1)).toString())
//        Log.d("kotlintest", index_to_co(0).contentToString())
//        Log.d("kotlintest", index_to_co(1711).contentToString())
//        Log.d("kotlintest", index_to_co(2186).contentToString())
//        Log.d("kotlintest", e_combination_to_index(arrayOf(true,true,true,true,false,false,false,false,false,false,false,false)).toString())
//        Log.d("kotlintest", e_combination_to_index(arrayOf(true,false,false,true,false,true,false,true,false,false,false,false)).toString())
//        Log.d("kotlintest", e_combination_to_index(arrayOf(false,false,false,false,false,false,false,false,true,true,true,true)).toString())
//        Log.d("kotlintest", index_to_e_combination(0).contentToString())
//        Log.d("kotlintest", index_to_e_combination(48).contentToString())
//        Log.d("kotlintest", index_to_e_combination(494).contentToString())
        Log.d("kotlintest", cp_to_index(arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)).toString())
        Log.d("kotlintest", cp_to_index(arrayOf(1, 7, 6, 2, 4, 5, 0, 3)).toString())
        Log.d("kotlintest", cp_to_index(arrayOf(7, 6, 5, 4, 3, 2, 1, 0)).toString())
        Log.d("kotlintest", index_to_cp(0).contentToString())
        Log.d("kotlintest", index_to_cp(10000).contentToString())
        Log.d("kotlintest", index_to_cp(40319).contentToString())

//        matrix.preScale(-1f, 1f)
//        val prevImg = Bitmap.createBitmap(nextBitmap, 0, 0, nextBitmap.getWidth(), nextBitmap.height, matrix, false);
//        prevButton.background = BitmapDrawable(resources, prevImg)
    }

    fun co_to_index(co: Array<Int>): Int {
        var index = 0
        for (co_i in 0 until co.size - 1) {
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
        co[7] = (3 - sum_co % 3) % 3

        return co
    }

    fun eo_to_index(eo: Array<Int>): Int {
        var index = 0
        for (eo_i in 0 until eo.size - 1) {
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
        eo[11] = (2 - sum_eo % 2) % 2

        return eo
    }

    fun calc_combination(n: Int, r:Int): Int {
        var ret = 1
        for (i in 0 until r) ret *= n - i
        for (i in 0 until r) ret /= r - i
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
        return combination
    }

    fun cp_to_index(cp: Array<Int>): Int {
        var index = 0
        for (i in cp.indices) {
            index *= 8 - i
            for (j in i + 1 until 8){
                if (cp[i] > cp[j]) {
                    index += 1
                }
            }
        }
        return index
    }

    fun index_to_cp(index: Int): Array<Int> {
        var index_tmp = index
        var cp = Array<Int>(8){0}
        for (i in 6 downTo 0) {
            cp[i] = index_tmp % (8 - i)
            index_tmp /= 8 - i
            for (j in i + 1 until 8) {
                if (cp[j] >= cp[i]) {
                    cp[j] += 1
                }
            }
        }
        return cp
    }

    fun ud_ep_to_index(ep: Array<Int>): Int {
        var index = 0
        for (i in ep.indices) {
            index *= 8 - i
            for (j in i + 1 until 8){
                if (ep[i] > ep[j]) {
                    index += 1
                }
            }
        }
        return index
    }

    fun index_to_ud_ep(index: Int): Array<Int> {
        var index_tmp = index
        var ep = Array<Int>(8){0}
        for (i in 6 downTo 0) {
            ep[i] = index_tmp % (8 - i)
            index_tmp /= 8 - i
            for (j in i + 1 until 8) {
                if (ep[j] >= ep[i]) {
                    ep[j] += 1
                }
            }
        }
        return ep
    }

    fun e_ep_to_index(eep: Array<Int>): Int {
        var index = 0
        for (i in eep.indices) {
            index *= 4 - i
            for (j in i + 1 until 4) {
                if (eep[i] > eep[j]) {
                    index += 1
                }
            }
        }
        return index
    }

    fun index_to_e_ep(index: Int): Array<Int> {
        var index_tmp = index
        var eep = Array<Int>(4){0}
        for (i in 4 - 2 downTo 0) {
            eep[i] = index_tmp % (4 - i)
            index_tmp /= 4 - i
            for (j in i + 1 until 4) {
                if (eep[j] >= eep[i]) {
                    eep[j] += 1
                }
            }
        }
        return eep
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

        val move_names_ph2 = arrayOf("U", "U2", "U'", "D", "D2", "D'", "L2", "R2", "F2", "B2")
    }
}