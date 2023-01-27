package dev.notrobots.timeline.widgets

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.notrobots.androidstuff.widget.BindableViewHolder
import dev.notrobots.timeline.R
import dev.notrobots.timeline.databinding.ItemCachedImageBinding
import dev.notrobots.timeline.models.CachedImage

class CachedImageAdapter : RecyclerView.Adapter<CachedImageAdapter.ImageViewHolder>() {
    private val items = mutableListOf<CachedImage>()
    private var onImageClickListener: OnImageClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val binding = holder.binding
        val item = getItem(position)

        Glide.with(binding.root)
            .load(item.url)
            .placeholder(R.drawable.placeholder_512x512)
            .into(binding.image)

        binding.image.setOnClickListener {
            onImageClickListener?.onClick(item, position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(items: List<CachedImage>) {
        this.items.clear()
        this.items.addAll(items)
    }

    fun getItem(position: Int): CachedImage {
        return items[position]
    }

    fun setOnImageClickListener(onImageClickListener: OnImageClickListener?) {
        this.onImageClickListener = onImageClickListener
    }

    class ImageViewHolder(
        parent: ViewGroup
    ) : BindableViewHolder<ItemCachedImageBinding>(ItemCachedImageBinding::class, parent)

    fun interface OnImageClickListener {
        fun onClick(item: CachedImage, position: Int)
    }
}