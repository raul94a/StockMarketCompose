package com.raul.stockmarket.presentation.company_listings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.raul.stockmarket.presentation.destinations.CompanyInfoScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(start = true)
fun companyListingsScreen(
    navigator: DestinationsNavigator,

    ) {
    val viewModel = hiltViewModel<CompanyListingsViewModel>()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "Search...")
            },
            maxLines = 1,
            singleLine = true,
            value = state.searchQuery,
            onValueChange = {
                viewModel.onEvent(CompanyListingsEvent.OnSearchQueryChange(it))
            },

            )
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                viewModel.onEvent(CompanyListingsEvent.Refresh)
            }) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.companies.size) { i ->
                    val company = state.companies[i]
                    CompanyItem(company = company, modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navigator.navigate(
                                CompanyInfoScreenDestination(company.symbol)
                            )
                        }
                        .padding(16.dp)
                    )
                    if (i < state.companies.size) {
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}