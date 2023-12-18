package com.raul.stockmarket.data.csv

import com.opencsv.CSVReader
import com.raul.stockmarket.data.mappers.toIntradayInfo
import com.raul.stockmarket.data.remote.dto.IntradayInfoDto
import com.raul.stockmarket.domain.model.CompanyListing
import com.raul.stockmarket.domain.model.IntradayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyListingsParser @Inject constructor() : CSVParser<CompanyListing> {
    override suspend fun parse(stream: InputStream): List<CompanyListing> {

        val csvReader = CSVReader(InputStreamReader(stream))

        return withContext(Dispatchers.IO) {
            csvReader.readAll().drop(1).mapNotNull {

                val symbol = it.getOrNull(0)
                val name = it.getOrNull(1)
                val exchange = it.getOrNull(2)
                CompanyListing(
                    name = name ?: return@mapNotNull null,
                    symbol = symbol ?: return@mapNotNull null,
                    exchange = exchange ?: return@mapNotNull null,
                )


            }
        }
    }

}

@Singleton
class IntradayInfoParser @Inject constructor() : CSVParser<IntradayInfo> {
    override suspend fun parse(stream: InputStream): List<IntradayInfo> {

        val csvReader = CSVReader(InputStreamReader(stream))

        return withContext(Dispatchers.IO) {
            csvReader.readAll().drop(1).mapNotNull {

                val timestamp = it.getOrNull(0) ?: return@mapNotNull null
                val close = it.getOrNull(4) ?: return@mapNotNull null

                val dto = IntradayInfoDto(
                    timestamp, close.toDouble()
                )
                dto.toIntradayInfo()


            }.filter {
                it.date.dayOfMonth == LocalDateTime.now().minusDays(3).dayOfMonth
            }.sortedBy {
                it.date.hour
            }
            .also {
                csvReader.close()
            }
        }
    }

}