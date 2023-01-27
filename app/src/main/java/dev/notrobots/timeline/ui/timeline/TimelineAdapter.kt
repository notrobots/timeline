package dev.notrobots.timeline.ui.timeline

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.notrobots.androidstuff.extensions.makeToast
import dev.notrobots.androidstuff.widget.BindableViewHolder
import dev.notrobots.timeline.databinding.ItemTimelineRedditBinding
import dev.notrobots.timeline.models.*
import dev.notrobots.timeline.models.social.PostWithMedia
import dev.notrobots.timeline.models.reddit.RedditPostWithMedia
import dev.notrobots.timeline.widgets.CachedImageAdapter
import java.text.SimpleDateFormat
import java.util.*

class TimelineAdapter(
    private var context: Context
) : RecyclerView.Adapter<BindableViewHolder<*>>() {
    private val items: MutableList<PostWithMedia<*>> = mutableListOf()
    private var timelineListener: TimelineListener? = null
    private var imageClickListener: OnImageClickListener? = null

    init {
//        setHasStableIds(true)
    }

    //region Rendering

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder<*> {
//        return when (viewType) {
//            Socials.Reddit.hashCode() -> RedditPostViewHolder(parent)

        return RedditPostViewHolder(parent)

//            else -> throw Exception("Unknown view type: $viewType")
//        }
    }

    override fun onBindViewHolder(holder: BindableViewHolder<*>, position: Int) {
        val item = getItem(position)

        holder as TimelineViewHolder
        holder.bind(item)
        holder.binding.root.setOnLongClickListener {
            timelineListener?.onLongClick(item, item.post.social)
            true
        }
    }

    //endregion

    //region Items management

    override fun getItemCount(): Int {
        return items.size
    }

//    override fun getItemViewType(position: Int): Int {
//        val item = getItem(position)
//
//        return VIEW_TYPE_REDDIT
//
//        return when (val item = getItem(position)) {
//            is FakeTwitterPostWithMedia -> VIEW_TYPE_FAKE_TWITTER
//            is FakeRedditPostWithMedia -> VIEW_TYPE_FAKE_REDDIT
//
//            else -> throw Exception("Unknown Post type: ${item::class.simpleName}")
//        }
//    }

    override fun getItemId(position: Int): Long {
        return getItem(position).post.postId
    }

    fun getItem(position: Int): PostWithMedia<*> {
        return items[position]
    }

    //region Listeners

    interface TimelineListener {
        fun onLongClick(item: PostWithMedia<*>, social: String)
    }

    fun setTimelineListener(timelineListener: TimelineListener?) {
        this.timelineListener = timelineListener
    }

    fun interface OnImageClickListener {
        fun onImageClick(image: CachedImage, adapter: TimelineAdapter)
    }

    fun setOnImageClickListener(imageClickListener: OnImageClickListener?) {
        this.imageClickListener = imageClickListener
    }

    //endregion

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<PostWithMedia<*>>) {
        if (this.items.isEmpty()) {
            this.items.addAll(items)
            notifyDataSetChanged()
        } else {
            val oldList = this.items
            val diffUtil = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldList.size
                }

                override fun getNewListSize(): Int {
                    return items.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldList[oldItemPosition].post.postId == items[newItemPosition].post.postId
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldList[oldItemPosition].post == items[newItemPosition].post
                }
            })

            oldList.clear()
            oldList.addAll(items)
            diffUtil.dispatchUpdatesTo(this)
        }
    }

    //endregion

    companion object {
        const val VIEW_TYPE_FAKE_TWITTER = 0
        const val VIEW_TYPE_FAKE_REDDIT = 1
        const val VIEW_TYPE_REDDIT = 2

        private fun formatDate(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())

            return dateFormat.format(Date(timestamp))
        }
    }

    interface TimelineViewHolder {
        fun bind(item: PostWithMedia<*>)
    }

    inner class RedditPostViewHolder(parent: ViewGroup) : BindableViewHolder<ItemTimelineRedditBinding>(
        ItemTimelineRedditBinding::class,
        parent
    ), TimelineViewHolder {
        override fun bind(item: PostWithMedia<*>) {
            item as RedditPostWithMedia

            binding.displayName.text = item.post.author
            binding.username.text = "u${item.post.author}"
            binding.date.text = formatDate(item.post.timestamp)
            binding.text.text = item.post.selfText

            binding.social.setOnClickListener {
                context.makeToast("${item.profile?.username} (${item.profile?.social})")
            }

            if (item.images.isNotEmpty()) {
                val adapter = CachedImageAdapter()

                adapter.setItems(item.images)
                adapter.setOnImageClickListener { image, _ ->
                    imageClickListener?.onImageClick(image, this@TimelineAdapter)
                }
                binding.images.isGone = false
                binding.images.layoutManager = GridLayoutManager(binding.root.context, if (item.images.size > 1) 2 else 1)
                binding.images.adapter = adapter
            } else {
                binding.images.isGone = true
            }
        }
    }
}