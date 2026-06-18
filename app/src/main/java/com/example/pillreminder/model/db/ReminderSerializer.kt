package com.example.pillreminder.model.db

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.pillreminder.model.reminder.ReminderList
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object ReminderSerializer : Serializer<ReminderList> {
    override val defaultValue: ReminderList = ReminderList.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ReminderList {
        return try {
            ReminderList.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }
    }

    override suspend fun writeTo(t: ReminderList, output: OutputStream) {
        t.writeTo(output)
    }
}