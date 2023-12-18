package com.raul.stockmarket.data.repository

import android.util.Log
import com.raul.stockmarket.data.csv.CSVParser
import com.raul.stockmarket.data.csv.CompanyListingsParser
import com.raul.stockmarket.data.local.StockDatabase
import com.raul.stockmarket.data.mappers.toCompanyInfo
import com.raul.stockmarket.data.mappers.toCompanyListing
import com.raul.stockmarket.data.mappers.toCompanyListingEntity
import com.raul.stockmarket.data.remote.StockApi
import com.raul.stockmarket.domain.model.CompanyInfo
import com.raul.stockmarket.domain.model.CompanyListing
import com.raul.stockmarket.domain.model.IntradayInfo
import com.raul.stockmarket.domain.repository.StockRepository
import com.raul.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class StockRepositoryImpl @Inject constructor(
    private val stockApi: StockApi,
    private val db: StockDatabase,
    private val csvParser: CSVParser<CompanyListing>,
    private val intradayCsvParser: CSVParser<IntradayInfo>

) : StockRepository {


    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {

        Log.i("CompanyListings", "FetchingCompanyListings")
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            Log.i("CompanyListingsLocal", "${localListings}")
            emit(Resource.Success(data = localListings.map { it.toCompanyListing() }))
            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListings = try {
                val response = stockApi.getListings(StockApi.API_KEY)
                Log.i("CompanyListingsResponse", "$response")

                csvParser.parse(response.byteStream())

            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Could not load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Could not load data HttpException"))
                null
            }

            remoteListings?.let { listings ->
                dao.clear()
                dao.insertMany(listings.map { it.toCompanyListingEntity() })
                Log.i("CompanyListingsRemoteParsed", "$listings")
                emit(
                    Resource.Success(
                        data = dao.searchCompanyListing("").map { it.toCompanyListing() })
                )
                emit(Resource.Loading(false))


            }
        }


    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = stockApi.getIntradyInfo(symbol)
            val results = intradayCsvParser.parse(response.byteStream())
            Resource.Success(data = results)
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(message = "Could not load intraday info")
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(message = "Could not load intraday info")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val response = stockApi.getCompanyInfo(symbol)

            Resource.Success(data = response.toCompanyInfo())
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(message = "Could not load intraday info")
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(message = "Could not load intraday info")
        }
    }


}