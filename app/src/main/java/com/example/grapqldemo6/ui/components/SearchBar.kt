package com.example.grapqldemo6.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.res.stringResource
import com.example.grapqldemo6.R
import com.example.grapqldemo6.data.ApiConstants
import com.example.grapqldemo6.ui.theme.Dimens

@Composable
fun SearchBar(
    searchText: String,
    isInputValid: Boolean,
    isDesc: String,
    isLoading: Boolean,
    onSearchTextChange: (String) -> Unit,
    onCleanButtonClick: () -> Unit,
    onOrderButtonClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            placeholder = { Text(stringResource(R.string.search_hint)) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            isError = searchText.isNotEmpty() && !isInputValid,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    onSearchClick()
                }
            )
        )

        Spacer(modifier = Modifier.width(Dimens.spacingExtraSmall))
        Button(
            onClick = onSearchClick,
            enabled = searchText.isNotBlank() && isInputValid && !isLoading,
            modifier = Modifier.height(Dimens.buttonHeight)
        ) {
            Text(stringResource(R.string.search_button))
        }
        Spacer(modifier = Modifier.width(Dimens.spacingExtraSmall))

        Button(
            onClick = onOrderButtonClick,
            enabled = searchText.isNotBlank() && isInputValid && !isLoading,
            modifier = Modifier.height(Dimens.buttonHeight)
        ) {
            Text(stringResource(if (isDesc == ApiConstants.PAGE_DESC) R.string.order_des else R.string.order_asc))
        }

        Button(
            onClick = onCleanButtonClick,
            enabled = searchText.isNotBlank() && isInputValid && !isLoading,
            modifier = Modifier.height(Dimens.buttonHeight)
        ) {
            Text(stringResource(R.string.clean_button))
        }
    }
}

@Composable
fun SearchErrorText(
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    errorMessage?.let {
        Text(
            text = it,
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier
        )
    }
}
