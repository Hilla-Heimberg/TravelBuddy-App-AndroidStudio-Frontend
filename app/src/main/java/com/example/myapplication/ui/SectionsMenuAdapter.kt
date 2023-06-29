package com.example.myapplication.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R

class SectionsMenuAdapter(
    private val getSectionImageCallback : ((Int) -> Uri),
    private val getSectionTitleCallback : ((Int) -> String),
    private val clickListenerCallback : ((Int) -> Unit),
    private val itemCountCallback : (() -> Int)
) : RecyclerView.Adapter<SectionsMenuAdapter.SectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.sections_menu_item, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        bindViewHolderImage(position, holder)
        bindViewHolderTitle(position, holder)
        bindViewHolderOnClickListener(holder, position)
    }

    private fun bindViewHolderImage(position: Int, holder: SectionViewHolder) {
        val uri = getSectionImageCallback.invoke(position)
        Glide.with(holder.itemView.context)
            .load(uri)
            .error(R.drawable.section_placeholder_image)
            .into(holder.sectionImageView)
    }

    private fun bindViewHolderTitle(position: Int, holder: SectionViewHolder) {
        val title = getSectionTitleCallback.invoke(position)
        holder.sectionTitleTextView.text = title.toString()
    }

    private fun bindViewHolderOnClickListener(holder: SectionViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            clickListenerCallback.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return itemCountCallback.invoke()
    }

    class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val sectionImageView : ImageView =
            itemView.findViewById<ImageView>(R.id.sectionsMenuItemImageView)
        val sectionTitleTextView : TextView =
            itemView.findViewById<TextView>(R.id.sectionsMenuItemTextView)
    }
}
