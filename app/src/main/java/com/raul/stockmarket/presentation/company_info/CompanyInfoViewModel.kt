package com.raul.stockmarket.presentation.company_info

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raul.stockmarket.domain.model.CompanyInfo
import com.raul.stockmarket.domain.repository.StockRepository
import com.raul.stockmarket.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


fun createFakeCompany(name: String, symbol: String, description: String): CompanyInfo {

    return CompanyInfo(
        name = name,
        symbol = symbol,
        description = description,
        country = "U.S",
        industry = "Software"
    )

}

val descriptionEvilCorp =
    "Evil Corp is a trailblazing tech company at the forefront of innovation, leveraging cutting-edge technologies to redefine industries. Specializing in artificial intelligence, cybersecurity, robotics, and data analytics, Evil Corp is committed to pushing the boundaries of what's possible. Led by visionary executives, the company boasts a stellar track record of strategic acquisitions and groundbreaking advancements. With a global market presence, Evil Corp's products and services reflect a commitment to excellence, driving impressive financial performance and investor confidence. Rooted in corporate responsibility, the company not only pioneers technological progress but also actively engages in initiatives promoting ethical business practices, environmental sustainability, and social responsibility. Evil Corp is poised to continue shaping the digital landscape with its dynamic and forward-thinking approach"


@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, private val repository: StockRepository
) : ViewModel() {


    var state by mutableStateOf(CompanyInfoState())


    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            Log.i("SavedStateHandle", "$symbol")
            state = state.copy(isLoading = true)
            val companyInfoResult = async { repository.getCompanyInfo(symbol) }
            val intradayInfoResult = async {
                repository.getIntradayInfo(symbol)
            }
            // if we use async we can await

            when (val result = companyInfoResult.await()) {
                is Resource.Success -> {
                    state = state.copy(
                        company = result.data, //createFakeCompany("Evil Corp", "EVCP", descriptionEvilCorp),
                        isLoading = false,
                        error = null
                    )
                }

                is Resource.Error -> {
                    state = state.copy(
                        error = result.message, isLoading = false, company = null
                    )

                }

                else -> Unit
            }
            when (val result = intradayInfoResult.await()) {
                is Resource.Success -> {
                    state = state.copy(
                        stockInfos = result.data ?: emptyList(), isLoading = false, error = null
                    )
                }

                is Resource.Error -> {
                    state = state.copy(
                        error = result.message, isLoading = false, company = null
                    )
                }

                else -> Unit
            }

        }
    }

}