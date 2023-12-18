package com.raul.stockmarket.data.mappers

import com.raul.stockmarket.data.remote.dto.IntradayInfoDto
import com.raul.stockmarket.domain.model.IntradayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun IntradayInfoDto.toIntradayInfo(): IntradayInfo {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val localDateTime = LocalDateTime.parse(timestamp, formatter)
    return IntradayInfo(
        date = localDateTime,
        close = close
    )
}

//
//fun IntradayInfo.toIntradayInfoDto(): IntradayInfoDto {
//    val pattern = "yyyy-MM-dd HH:mm:ss"
//    val formatter = DateTimeFormatter.ofPattern(pattern)
//    val timestamp =
//    return IntradayInfoDto(
//        timestamp = localDateTime,
//        close = close
//    )
//}