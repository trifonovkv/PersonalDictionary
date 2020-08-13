package com.kostrifon.personaldictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_item_list.view.*


class ItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Set the adapter
        if (view.list is RecyclerView) {
            with(view.list) {
                layoutManager = LinearLayoutManager(context)
                val db = DbHelper(context).readableDatabase
                adapter = WordItemRecyclerViewAdapter(getAllDictionaryWordsFromDb(db))
                db.close()
            }
        }

        view.image_back.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
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