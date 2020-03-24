package com.kyle.rhythmboxremote

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ServerAdapter(private val context: Context, private val dataSource: ArrayList<String>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        rowView.findViewById<TextView>(android.R.id.text1).text = dataSource[position]
        rowView.setOnClickListener {
            val i = Intent(context, MainActivity::class.java)
            i.putExtra(EXTRA_IP, dataSource[position])
            context.startActivity(i)
        }

        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    fun getData(): ArrayList<String>{
        return dataSource
    }

}