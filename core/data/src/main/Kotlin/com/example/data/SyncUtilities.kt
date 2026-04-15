package com.example.data

interface Synchronizer {
    suspend fun Syncable.sync() = this@sync.syncWith(this@Synchronizer)
}


interface Syncable {
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}