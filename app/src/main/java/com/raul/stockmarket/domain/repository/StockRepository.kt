package com.raul.stockmarket.domain.repository

import com.raul.stockmarket.domain.model.CompanyInfo
import com.raul.stockmarket.domain.model.CompanyListing
import com.raul.stockmarket.domain.model.IntradayInfo
import com.raul.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean, query: String
    ): Flow<Resource<List<CompanyListing>>>


    suspend fun getIntradayInfo(
        symbol: String
    ) : Resource<List<IntradayInfo>>


    suspend fun getCompanyInfo(
        symbol: String
    ): Resource<CompanyInfo>
}