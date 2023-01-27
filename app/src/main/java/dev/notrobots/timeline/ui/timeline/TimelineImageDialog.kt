package dev.notrobots.timeline.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import dev.notrobots.timeline.R
import dev.notrobots.timeline.databinding.FragmentTimelineImageBinding
import dev.notrobots.timeline.models.CachedImage
import dev.notrobots.timeline.util.savedStateName

class TimelineImageDialog(
    private var image: CachedImage? = null
) : DialogFragment() {
    lateinit var binding: FragmentTimelineImageBinding

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(SAVED_STATE_IMAGE, image)
    }

//    override fun getTheme(): Int {
//        return R.style.Timeline_Dialog_ImageDialog
//    }

    override fun onStart() {
        super.onStart()

//        dialog?.let {
//            it.window?.setLayout(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
//        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        setStyle(STYLE_NO_TITLE, R.style.Timeline_Dialog_ImageDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTimelineImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            image = it.getSerializable(SAVED_STATE_IMAGE) as CachedImage
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
        private val SAVED_STATE_IMAGE = savedStateName<TimelineImageDialog>("image")
    }
}