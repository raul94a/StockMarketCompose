package com.raul.stockmarket.data.mappers

import com.raul.stockmarket.data.remote.dto.CompanyInfoDto
import com.raul.stockmarket.domain.model.CompanyInfo


fun CompanyInfoDto.toCompanyInfo(): CompanyInfo {
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""

    )
}