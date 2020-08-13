package com.kostrifon.personaldictionary

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class WordItemRecyclerViewAdapter(
    private val values: List<DictionaryWord>
) : RecyclerView.Adapter<WordItemRecyclerViewAdapter.ViewHolder>() {
    lateinit var activity: AppCompatActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        activity = parent.context as AppCompatActivity
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = values[position]
        holder.itemNumber.text = "${(position + 1)}"
        holder.primaryText.text = item.word
        holder.secondaryText.text =
            listOf(item.noun.translates, item.verb.translates, item.adjective.translates).mapNotNull {
                it.firstOrNull()
            }.joinToString(separator = ", ")
        holder.itemRow.setOnClickListener {
            activity.supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, DictionaryEntryFragment.newInstance(item))
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemRow: LinearLayout = view.findViewById(R.id.item_row)
        val itemNumber: TextView = view.findViewById(R.id.text_number)
        val primaryText: TextView = view.findViewById(R.id.text_primary)
        val secondaryText: TextView = view.findViewById(R.id.text_secondary)

        override fun toString(): String {
            return super.toString() + " '" + secondaryText.text + "'"
        }
    }
}