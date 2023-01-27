package dev.notrobots.timeline.ui.timeline

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.notrobots.androidstuff.extensions.makeSnackBar
import dev.notrobots.androidstuff.extensions.startActivity
import dev.notrobots.androidstuff.extensions.viewBindings
import dev.notrobots.preferences2.getFilterFakeRedditEnabled
import dev.notrobots.preferences2.getFilterFakeTwitterEnabled
import dev.notrobots.timeline.R
import dev.notrobots.timeline.databinding.ActivityTimelineBinding
import dev.notrobots.timeline.models.*
import dev.notrobots.timeline.models.social.PostWithMedia
import dev.notrobots.timeline.ui.settings.SettingsActivity
import dev.notrobots.timeline.ui.timelineimage.TimelineImageDialog
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimelineActivity : AppCompatActivity() {
    private val viewModel by viewModels<TimelineViewModel>()
    private val binding by viewBindings<ActivityTimelineBinding>()
    private val preferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    private val adapter by lazy { TimelineAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupUI()

        viewModel.posts.observe(this) {
            val size = it.size - adapter.itemCount

            adapter.setItems(it)

            if (size > 0) {
                makeSnackBar("New posts", binding.root, action = "Scroll up") { _, bar ->
                    binding.list.smoothScrollToPosition(0)
                    bar.dismiss()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        updateFilter()
        binding.list.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                startActivity(SettingsActivity::class)
            }

            ///
            /// Debug options
            ///
            R.id.menu_debug_clear_db -> {
                lifecycleScope.launch {
                    viewModel.redditPostDao.delete(viewModel.redditPostDao.getPosts())
                }
            }

            else -> return false
        }

        return true
    }

    private fun setupRecyclerView() {
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = adapter

        adapter.setTimelineListener(object : TimelineAdapter.TimelineListener {
            override fun onLongClick(item: PostWithMedia<*>, social: String) {
                lifecycleScope.launch {
                    when (social) {
//                        Social.FakeTwitter -> viewModel.fakeTwitterPostDao.delete((item as FakeTwitterPostWithMedia).post)    //TODO: Delete images too
//                        Social.FakeReddit -> viewModel.fakeRedditPostDao.delete((item as FakeRedditPostWithMedia).post)

                        else -> {}
                    }
                }
            }
        })
        adapter.setOnImageClickListener { image, _ ->
            TimelineImageDialog(image)
                .show(supportFragmentManager, null)
        }
    }

    private fun setupUI() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshPosts()
            binding.swipeRefreshLayout.isRefreshing = false
        }

//        binding.add.setOnClickListener {
//            viewModel.debugAddRandomPost(this)
//        }
//        binding.delete.setOnClickListener {
//            viewModel.debugDeleteRandomPost()
//        }
//        binding.update.setOnClickListener {
//            viewModel.debugUpdateRandomPost()
//        }
    }

    private fun updateFilter() {
        lifecycleScope.launch {

            // TODO Save a list of enabled users in the preferences, it can be saved as a json array
            val enabledProfiles = viewModel.profileDao.getProfiles()
                .filter(Profile::enabled)
                .groupBy({ it.social }, { it })
                .toMap()

            viewModel.setSocialFilter(
                SocialFilter(
                    preferences.getFilterFakeTwitterEnabled(),
                    preferences.getFilterFakeRedditEnabled(),
                    emptyMap()
                )
            )
        }
    }
}