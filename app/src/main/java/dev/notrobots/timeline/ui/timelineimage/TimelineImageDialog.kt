package dev.notrobots.timeline.ui.timelineimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import dev.notrobots.timeline.R
import dev.notrobots.timeline.databinding.DialogTimelineImageBinding
import dev.notrobots.timeline.models.CachedImage

class TimelineImageDialog(
    private var image: CachedImage? = null
) : DialogFragment() {
    private lateinit var binding: DialogTimelineImageBinding

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(SAVED_STATE_IMAGE, image)
    }

    override fun onStart() {
        super.onStart()

        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT

            it.window?.setLayout(width, height)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.Timeline_Dialog_ImageDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogTimelineImageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            image = savedInstanceState.getSerializable(SAVED_STATE_IMAGE) as CachedImage
        }

        requireNotNull(image) {
            "Image is null"
        }

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

            }

            true
        }

        Glide.with(binding.root)
            .load(image?.url)
            .placeholder(R.drawable.placeholder_512x512)
            .into(binding.photoView)    //TODO: anime this https://developer.android.com/guide/fragments/animate#shared
    }

    companion object {
        private val SAVED_STATE_IMAGE = "${TimelineImageDialog::class.simpleName}.image"
    }
}