package com.cuinsolutions.liftingdice.android.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.io.InputStream
import java.io.OutputStream

class DataStoreModule(val context: Context) {

    fun provideProtoDataStore(): DataStore<UserPreferencesOuterClass.UserPreferences> {
        return DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            produceFile = { context.dataStoreFile("user_preferences.pb") },
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()))
    }
}

object UserPreferencesSerializer: Serializer<UserPreferencesOuterClass.UserPreferences> {
    override val defaultValue = UserPreferencesOuterClass.UserPreferences.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): UserPreferencesOuterClass.UserPreferences {
        try {
            return UserPreferencesOuterClass.UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("cannot read proto", exception)
        }
    }

    override suspend fun writeTo(
        t: UserPreferencesOuterClass.UserPreferences,
        output: OutputStream
    ) = t.writeTo(output)
}