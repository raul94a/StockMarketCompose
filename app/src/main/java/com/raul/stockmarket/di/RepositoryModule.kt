package com.raul.stockmarket.di

import com.raul.stockmarket.data.csv.CSVParser
import com.raul.stockmarket.data.csv.CompanyListingsParser
import com.raul.stockmarket.data.csv.IntradayInfoParser
import com.raul.stockmarket.data.repository.StockRepositoryImpl
import com.raul.stockmarket.domain.model.CompanyListing
import com.raul.stockmarket.domain.model.IntradayInfo
import com.raul.stockmarket.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ): CSVParser<IntradayInfo>
}