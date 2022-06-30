package jp.techacademy.shintaro.nakagawa.finalapp

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class SolveAdapter(val context: Context, val solutionList: MutableList<String>, var num: Int) : BaseAdapter() {
    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return solutionList.count()
    }

    override fun getItem(position: Int): Pair<String, Int> {
        return Pair(solutionList[position], position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = layoutInflater.inflate(R.layout.custom_layout, parent, false)
        val symboltext = view.findViewById<TextView>(R.id.symbolText)

        symboltext.text = solutionList[position]
        if (position == 0 || position == solutionList.size - 1) {
            symboltext.setTextColor(Color.GREEN)
        } else if (num == 0 || num == solutionList.size - 1) {
            symboltext.setTextColor(Color.WHITE)
        } else if (position == num) {
            symboltext.setTextColor(Color.WHITE)
        }

        return view
    }

    fun update(nextNum: Int) {
        num = nextNum
    }
}