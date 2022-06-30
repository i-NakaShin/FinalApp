package jp.techacademy.shintaro.nakagawa.finalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible

class TimeAttackActivity : AppCompatActivity() {
    val handler = Handler()
    var timeValue = 0
    var isStart = false
    val faceRange = (0..5)
    val agreeRange = (0..2)
    var moveList = ""
    val face = hashMapOf<Int, String>(0 to "U", 1 to "D", 2 to "L",
            3 to "R", 4 to "F", 5 to "B")
    val agree = hashMapOf<Int, String>(0 to "", 1 to "'", 2 to "2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_attack)
        val timeText: TextView = findViewById(R.id.timeText)
        val scrambleText: TextView = findViewById(R.id.moveList)
        val start: Button = findViewById(R.id.start)
        val reset: Button = findViewById(R.id.reset)

        supportActionBar!!.hide()

        val runnable = object : Runnable {
            override fun run() {
                timeValue++
                timeToText(timeValue)?.let {
                    timeText.text = it
                }
                handler.postDelayed(this, 1000)
            }
        }

        scramble()
        scrambleText.text = moveList
        scrambleText.visibility = View.GONE

        start.setOnClickListener {
            if (!isStart) {
                isStart = true
                scrambleText.visibility = View.GONE
                start.text = "STOP"
                handler.post(runnable)
            } else {
                isStart = false
                start.text = "START"
                handler.removeCallbacks(runnable)
            }
        }
        reset.setOnClickListener {
            handler.removeCallbacks(runnable)
            isStart = false
            scramble()
            scrambleText.text = moveList
            start.text = "START"
            timeValue = 0
            timeToText()?.let {
                timeText.text = it
            }
        }
    }

    private fun timeToText(time: Int = 0): String? {
        return if (time < 0) {
            null
        } else if (time == 0) {
            "00:00:00"
        } else {
            val h = time / 3600
            val m = time % 3600 / 60
            val s = time % 60
            "%1$02d:%2$02d:%3$02d".format(h, m, s)
        }
    }

    private fun scramble() {
        moveList = ""
        for (i in 0 until 10) {
            moveList += "${face[faceRange.random()]}${agree[agreeRange.random()]}"
            if (i != 10 - 1) moveList += " "
        }
    }
}