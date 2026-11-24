package com.example.playlistmaker.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.models.TracksResponse
import com.example.playlistmaker.network.ITunesClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchHistory: SearchHistory

    private var searchQuery = ""
    private var lastFailedQuery = ""

    private val adapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query_key"
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

        searchHistory = SearchHistory(getSharedPreferences(App.PREFS_NAME, MODE_PRIVATE))

        setupRecyclerView()
        setupHistoryRecyclerView()
        setupSearchEditText()
        setupClearButton()
        setupRefreshButton()
        setupClearHistoryButton()

        // Клик по треку в результатах -> добавляем в историю и переходим в плеер
        adapter.onItemClickListener = { track ->
            searchHistory.add(track)
            openPlayer(track)
        }

        // Клик по треку в истории -> поднимаем наверх и переходим в плеер
        historyAdapter.onItemClickListener = { track ->
            searchHistory.add(track)
            updateHistoryVisibility()
            openPlayer(track)
        }

        binding.etSearch.requestFocus()
        updateHistoryVisibility()
    }

    override fun onResume() {
        super.onResume()
        updatePlaceholderImages()
        updateHistoryVisibility()
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
                updateHistoryVisibility()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) { search(); true } else false
        }

        binding.etSearch.setOnFocusChangeListener { _, _ -> updateHistoryVisibility() }
    }

    private fun updateHistoryVisibility() {
        val history = searchHistory.read()
        val show = binding.etSearch.text.isEmpty() && binding.etSearch.hasFocus() && history.isNotEmpty()
        if (show) {
            historyAdapter.updateData(history)
            binding.historyContainer.visibility = View.VISIBLE
            binding.recyclerViewTracks.visibility = View.GONE
            binding.placeholderNoResults.visibility = View.GONE
            binding.placeholderError.visibility = View.GONE
        } else {
            binding.historyContainer.visibility = View.GONE
        }
    }

    private fun setupClearButton() {
        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
            hideKeyboard(binding.etSearch)
            adapter.updateData(emptyList())
            binding.recyclerViewTracks.visibility = View.GONE
            binding.placeholderNoResults.visibility = View.GONE
            binding.placeholderError.visibility = View.GONE
            updateHistoryVisibility()
        }
    }

    private fun setupClearHistoryButton() {
        binding.btnClearHistory.setOnClickListener {
            searchHistory.clear()
            historyAdapter.updateData(emptyList())
            binding.historyContainer.visibility = View.GONE
        }
    }

    private fun setupRefreshButton() {
        binding.btnRefresh.setOnClickListener {
            if (lastFailedQuery.isNotEmpty()) performSearch(lastFailedQuery)
        }
    }

    private fun search() {
        val query = binding.etSearch.text.toString().trim()
        if (query.isNotEmpty()) performSearch(query)
    }

    private fun performSearch(query: String) {
        ITunesClient.api.searchTracks(query).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                if (response.code() == 200) {
                    val tracks = response.body()?.results.orEmpty()
                    if (tracks.isEmpty()) showNoResults() else {
                        adapter.updateData(tracks)
                        showList()
                    }
                } else {
                    lastFailedQuery = query
                    showError()
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                lastFailedQuery = query
                showError()
            }
        })
    }

    private fun showList() {
        binding.historyContainer.visibility = View.GONE
        binding.recyclerViewTracks.visibility = View.VISIBLE
        binding.placeholderNoResults.visibility = View.GONE
        binding.placeholderError.visibility = View.GONE
    }

    private fun showNoResults() {
        binding.historyContainer.visibility = View.GONE
        binding.recyclerViewTracks.visibility = View.GONE
        binding.placeholderNoResults.visibility = View.VISIBLE
        binding.placeholderError.visibility = View.GONE
    }

    private fun showError() {
        binding.historyContainer.visibility = View.GONE
        binding.recyclerViewTracks.visibility = View.GONE
        binding.placeholderNoResults.visibility = View.GONE
        binding.placeholderError.visibility = View.VISIBLE
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Переход на экран плеера
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