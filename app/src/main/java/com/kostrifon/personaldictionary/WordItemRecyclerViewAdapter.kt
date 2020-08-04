package com.kostrifon.personaldictionary

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class WordItemRecyclerViewAdapter(
    private val values: List<DictionaryWord>
) : RecyclerView.Adapter<WordItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.itemNumber.text = "${(position + 1)}"
        holder.primaryText.text = item.word
        holder.secondaryText.text = StringBuilder("${item.noun.translates[0]}, ${item.verb.translates[0]}, " +
                item.adjective.translates[0]
        )
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemNumber: TextView = view.findViewById(R.id.item_number)
        val primaryText: TextView = view.findViewById(R.id.item_primary_text)
        val secondaryText: TextView = view.findViewById(R.id.item_secondary_text)

        override fun toString(): String {
            return super.toString() + " '" + secondaryText.text + "'"
        }
    }
}