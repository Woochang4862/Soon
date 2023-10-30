package com.lusle.android.soon

import android.content.SearchRecentSuggestionsProvider

class SuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.lusle.android.soon.SuggestionProvider"
        const val MODE = DATABASE_MODE_QUERIES
    }
}