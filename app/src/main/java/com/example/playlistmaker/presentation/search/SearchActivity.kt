package com.example.playlistmaker.presentation.search

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.di.Creator
import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.PlayerActivity

class SearchActivity : AppCompatActivity(), SearchInteractor.SearchConsumer {

    private lateinit var binding: ActivitySearchBinding
    private val searchInteractor: SearchInteractor by lazy {
        Creator.provideSearchInteractor(this)
    }

    private var searchQuery = ""
    private var isClickAllowed = true

    private val adapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        if (binding.etSearch.text.toString().trim().isNotEmpty()) {
            searchInteractor.searchTracks(binding.etSearch.text.toString().trim(), this)
        }
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query_key"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        binding.toolbar.navigationIcon?.setTint(typedValue.data)

        setupRecyclerView()
        setupHistoryRecyclerView()
        setupSearchEditText()
        setupClearButton()
        setupClearHistoryButton()

        adapter.onItemClickListener = { track ->
            if (clickDebounce()) {
                searchInteractor.addToHistory(track)
                openPlayer(track)
            }
        }

        historyAdapter.onItemClickListener = { track ->
            if (clickDebounce()) {
                searchInteractor.addToHistory(track)
                updateHistoryVisibility()
                openPlayer(track)
            }
        }

        binding.etSearch.requestFocus()
        updateHistoryVisibility()
    }

    override fun onResume() {
        super.onResume()
        updatePlaceholderImages()
        updateHistoryVisibility()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    override fun consume(tracks: List<Track>) {
        runOnUiThread {
            binding.progressBar.visibility = View.GONE

            if (tracks.isEmpty()) {
                showNoResults()
            } else {
                adapter.updateData(tracks)
                showList()
            }
        }
    }

    private fun updatePlaceholderImages() {
        val isDark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        binding.ivNoResults.setImageResource(if (isDark) R.drawable.ic_no_results_dm else R.drawable.ic_no_results)
        binding.ivServerError.setImageResource(if (isDark) R.drawable.ic_server_error_dm else R.drawable.ic_server_error)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewTracks.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapter
        }
    }

    private fun setupHistoryRecyclerView() {
        binding.recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = historyAdapter
        }
    }

    private fun setupSearchEditText() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                binding.ivClearSearch.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                handler.removeCallbacks(searchRunnable)
                if (!s.isNullOrEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewTracks.visibility = View.GONE
                    binding.placeholderNoResults.visibility = View.GONE
                    binding.placeholderError.visibility = View.GONE
                    binding.historyContainer.visibility = View.GONE

                    handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
                } else {
                    adapter.updateData(emptyList())
                    binding.recyclerViewTracks.visibility = View.GONE
                    binding.placeholderNoResults.visibility = View.GONE
                    binding.placeholderError.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                }
                updateHistoryVisibility()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun updateHistoryVisibility() {
        val history = searchInteractor.getHistory()
        val show = binding.etSearch.text.isEmpty() && binding.etSearch.hasFocus() && history.isNotEmpty()
        if (show) {
            historyAdapter.updateData(history)
            binding.historyContainer.visibility = View.VISIBLE
            binding.recyclerViewTracks.visibility = View.GONE
            binding.placeholderNoResults.visibility = View.GONE
            binding.placeholderError.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        } else {
            binding.historyContainer.visibility = View.GONE
        }
    }

    private fun setupClearButton() {
        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
            hideKeyboard(binding.etSearch)
            handler.removeCallbacks(searchRunnable)
            adapter.updateData(emptyList())
            binding.recyclerViewTracks.visibility = View.GONE
            binding.placeholderNoResults.visibility = View.GONE
            binding.placeholderError.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            updateHistoryVisibility()
        }
    }

    private fun setupClearHistoryButton() {
        binding.btnClearHistory.setOnClickListener {
            searchInteractor.clearHistory()
            historyAdapter.updateData(emptyList())
            binding.historyContainer.visibility = View.GONE
        }
    }

    private fun showList() {
        binding.recyclerViewTracks.visibility = View.VISIBLE
    }

    private fun showNoResults() {
        binding.placeholderNoResults.visibility = View.VISIBLE
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_TRACK, track)
        }
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "") ?: ""
        binding.etSearch.setText(searchQuery)
        binding.etSearch.setSelection(searchQuery.length)
    }
}