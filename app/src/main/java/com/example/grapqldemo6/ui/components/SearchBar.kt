package com.example.grapqldemo6.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.grapqldemo6.ui.theme.Dimens

@Composable
fun SearchBar(
    searchText: String,
    isInputValid: Boolean,
    isLoading: Boolean,
    onSearchTextChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier.fillMaxWidth(),
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

        Spacer(modifier = Modifier.width(Dimens.spacingMedium))

        Button(
            onClick = onSearchClick,
            enabled = searchText.isNotBlank() && isInputValid && !isLoading,
            modifier = Modifier.height(Dimens.buttonHeight)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.iconSmall),
                    color = Color.White,
                    strokeWidth = Dimens.progressStrokeSmall
                )
            } else {
                Text(stringResource(R.string.search_button))
            }
        }
    }
}

@Composable
fun SearchErrorText(
    showError: Boolean,
    modifier: Modifier = Modifier
) {
    if (showError) {
        Text(
            text = stringResource(R.string.search_input_error),
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier
        )
    }
}
