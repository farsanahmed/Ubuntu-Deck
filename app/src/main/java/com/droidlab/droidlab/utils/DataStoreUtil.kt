package com.droidlab.droidlab.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.readWriteAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "droidlab_prefs")

object DataStoreUtil {
    private val NOTES_JSON_KEY = stringPreferencesKey("notes_json")
    private val TASKS_JSON_KEY = stringPreferencesKey("tasks_json")

    fun Context.saveNotes(notesJson: String) = dataStore.edit { it[NOTES_JSON_KEY] = notesJson }
    fun Context.loadNotes(): Flow<String> = dataStore.data.map { it[NOTES_JSON_KEY] ?: "" }

    fun Context.saveTasks(tasksJson: String) = dataStore.edit { it[TASKS_JSON_KEY] = tasksJson }
    fun Context.loadTasks(): Flow<String> = dataStore.data.map { it[TASKS_JSON_KEY] ?: "" }
}
