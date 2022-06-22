package jp.techacademy.shintaro.nakagawa.finalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView

class TimeAttackActivity : AppCompatActivity() {
    val handler = Handler()
    var timeValue = 0
    var isStart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_attack)
        val timeText: TextView = findViewById(R.id.timeText)
        val start: Button = findViewById(R.id.start)
        val reset: Button = findViewById(R.id.reset)

        val runnable = object : Runnable {
            override fun run() {
                timeValue++
                timeToText(timeValue)?.let {
                    timeText.text = it
                }
                handler.postDelayed(this, 1000)
            }
        }

        start.setOnClickListener {
            if (!isStart) {
                isStart = true
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
}