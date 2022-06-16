package com.example.composeinteropinvalidateissue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.composeinteropinvalidateissue.databinding.NestedExampleBinding
import com.example.composeinteropinvalidateissue.ui.theme.ComposeInteropInvalidateIssueTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeInteropInvalidateIssueTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    Column() {
                        Button(
                            onClick = {
                                setContentView(R.layout.nested_example)
                                val basicRecyclerView =
                                    this@MainActivity.findViewById<RecyclerView>(R.id.basicRecyclerView)
                                basicRecyclerView.setupAndPopulateRecyclerView()

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(text = "Switch to Android View Only")
                        }
                        AndroidViewBinding(factory = ::inflate)
                    }
                }
            }
        }
    }

    private fun RecyclerView.setupAndPopulateRecyclerView() {
        val listData = arrayListOf<BasicRecyclerAdapterModel>()
        listData.add(DrawAndInvalidateView(""))
        for (i in 1..50) {
            listData.add(StandardView("Hi, I'm an Android View position $i"))
        }
        adapter = BasicRecyclerViewAdapter(listData.toTypedArray())
        layoutManager = LinearLayoutManager(this.context)

    }

    private fun inflate(
        inflater: LayoutInflater,
        parent: ViewGroup?, attachToParent: Boolean
    ): NestedExampleBinding {
        val root: View = inflater.inflate(R.layout.nested_example, parent, false)
        if (attachToParent) {
            parent!!.addView(root)
        }
        return NestedExampleBinding.bind(root).apply {
            basicRecyclerView.setupAndPopulateRecyclerView()
        }
    }
}