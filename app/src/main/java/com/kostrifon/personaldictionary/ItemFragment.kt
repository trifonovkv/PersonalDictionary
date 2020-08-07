package com.kostrifon.personaldictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                val db = DictionaryWordDbHelper(context).readableDatabase
                adapter = WordItemRecyclerViewAdapter(getAllDictionaryWordsFromDb(db))
                db.close()
            }
        }
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ItemFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}