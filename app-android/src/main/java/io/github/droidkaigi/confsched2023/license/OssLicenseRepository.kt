package io.github.droidkaigi.confsched2023.license

import android.content.Context
import io.github.droidkaigi.confsched2023.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import okio.BufferedSource
import okio.buffer
import okio.source
import javax.inject.Inject

public interface OssLicenseRepository {
    public fun sponsors(): Flow<PersistentList<OssLicense>>

    public fun refresh(): Unit
}

internal class OssLicenseRepositoryImpl @Inject constructor(
    private val context: Context,
) : OssLicenseRepository {

    private val ossLicenseStateFlow =
        MutableStateFlow<PersistentList<OssLicense>>(persistentListOf())

    override fun sponsors(): Flow<PersistentList<OssLicense>> {
        refresh()
        return ossLicenseStateFlow
    }

    override fun refresh() {
        ossLicenseStateFlow.value = readLicensesMetaFile()
            .toRowList()
            .parseToLibraryItem()
            .toPersistentList()
    }

    private fun readLicensesMetaFile(): BufferedSource {
        return context.resources.openRawResource(R.raw.third_party_license_metadata)
            .source()
            .buffer()
    }

    private fun List<String>.parseToLibraryItem(): List<OssLicense> {
        return map {
            val (position, name) = it.split(' ', limit = 2)
            val (offset, length) = position.split(':').map { it.toInt() }
            OssLicense(name, offset, length)
        }
    }

    private fun BufferedSource.toRowList(): List<String> {
        val list: MutableList<String> = mutableListOf()
        while (true) {
            val line = readUtf8Line() ?: break
            list.add(line)
        }
        return list
    }
}
