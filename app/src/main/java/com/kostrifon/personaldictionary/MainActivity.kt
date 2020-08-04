package com.kostrifon.personaldictionary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_main)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, EnterWordFragment.newInstance())
            commit()
        }
    }
}





