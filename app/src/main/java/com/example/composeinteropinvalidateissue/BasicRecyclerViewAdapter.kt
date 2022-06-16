package com.example.composeinteropinvalidateissue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BasicRecyclerViewAdapter(private val dataSet: Array<BasicRecyclerAdapterModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class StandardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.android_text_view)
        }
    }

    class DrawAndInvalidateHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
        }
    }

    enum class ViewType {
        VIEW,
        DRAW_AND_INVALIDATE
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewType.VIEW.ordinal) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.android_text_view_row, viewGroup, false)

            StandardViewHolder(view)
        } else {
            DrawAndInvalidateHolder(DrawAndInvalidateViewImpl(viewGroup.context))
        }
    }

    override fun getItemCount() = dataSet.size

    override fun getItemViewType(position: Int): Int {
        return when (dataSet[position]) {
            is StandardView -> ViewType.VIEW.ordinal
            is DrawAndInvalidateView -> ViewType.DRAW_AND_INVALIDATE.ordinal
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (dataSet[position]) {
            is StandardView -> (holder as StandardViewHolder).textView.text =
                (dataSet[position] as StandardView).text
            is DrawAndInvalidateView -> (holder as DrawAndInvalidateHolder).bind()
        }
    }
}

sealed interface BasicRecyclerAdapterModel
data class StandardView(val text: String) : BasicRecyclerAdapterModel
data class DrawAndInvalidateView(val text: String) : BasicRecyclerAdapterModel