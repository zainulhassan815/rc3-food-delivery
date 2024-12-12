package org.dreamerslab.rc3.ui.auth.forgotpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.dreamerslab.rc3.R
import org.dreamerslab.rc3.design_system.components.PrimaryButton
import org.dreamerslab.rc3.design_system.components.SimpleTextField
import org.dreamerslab.rc3.design_system.theme.spacing
import org.dreamerslab.rc3.utils.form.isInProgressOrSuccess
import org.dreamerslab.rc3.utils.formfields.EmailError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordPage(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val formState by viewModel.state.collectAsStateWithLifecycle()
    val uiMessage by viewModel.message.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    uiMessage?.let {
        LaunchedEffect(uiMessage) {
            snackbarHostState.showSnackbar(
                message = it.message,
                withDismissAction = true
            )
            viewModel.clearMessage(it.id)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onNavigateUp) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            null
                        )
                    }
                },
                title = {
                    Text(stringResource(R.string.app_name))
                }
            )
        }
    ) { innerPadding ->
        ResetPasswordPageContent(
            formState = formState,
            onEmailChanged = viewModel::updateEmail,
            onSendEmailClick = viewModel::sendPasswordResetEmail,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun ResetPasswordPageContent(
    formState: ResetPasswordFormState,
    onEmailChanged: (String) -> Unit,
    onSendEmailClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.extraLarge,
                    vertical = MaterialTheme.spacing.large
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            SimpleTextField(
                label = "Email",
                text = formState.email.value,
                onChange = onEmailChanged,
                error = formState.email.displayError?.let {
                    when (it) {
                        EmailError.Empty -> "Email is required"
                        EmailError.Invalid -> "Invalid email address"
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                ),
                enabled = formState.submissionStatus.isInProgressOrSuccess.not()
            )

            PrimaryButton(
                label = "Send Email",
                onClick = onSendEmailClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = formState.submissionStatus.isInProgressOrSuccess.not()
            )
        }
    }
}
