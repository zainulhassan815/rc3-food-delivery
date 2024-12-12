package org.dreamerslab.rc3.ui.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.dreamerslab.rc3.R
import org.dreamerslab.rc3.design_system.components.PasswordTextField
import org.dreamerslab.rc3.design_system.components.PrimaryButton
import org.dreamerslab.rc3.design_system.components.SimpleTextField
import org.dreamerslab.rc3.design_system.theme.RC3Theme
import org.dreamerslab.rc3.design_system.theme.spacing
import org.dreamerslab.rc3.utils.form.isInProgressOrSuccess
import org.dreamerslab.rc3.utils.formfields.Email
import org.dreamerslab.rc3.utils.formfields.EmailError
import org.dreamerslab.rc3.utils.formfields.Password
import org.dreamerslab.rc3.utils.formfields.PasswordError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onSignupClick: () -> Unit,
    onResetPasswordClick: (email: String?) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
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
                title = {
                    Text(stringResource(R.string.app_name))
                }
            )
        }
    ) { innerPadding ->


        LoginPageContent(
            onSignupClick = onSignupClick,
            onLoginClick = viewModel::login,
            onEmailChange = viewModel::updateEmail,
            onPasswordChange = viewModel::updatePassword,
            onResetPasswordClick = { onResetPasswordClick(formState.email.value) },
            formState = formState,
            modifier = Modifier
                .padding(innerPadding)
                .padding(MaterialTheme.spacing.large)
        )
    }
}

@Composable
private fun LoginPageContent(
    onSignupClick: () -> Unit,
    onResetPasswordClick: () -> Unit,
    formState: LoginFormState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        LoginFormFields(
            email = formState.email,
            onEmailChange = onEmailChange,
            password = formState.password,
            onPasswordChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = MaterialTheme.spacing.large,
                ),
            formEnabled = formState.submissionStatus.isInProgressOrSuccess.not()
        )

        ResetPasswordRow(
            onResetPasswordClick = onResetPasswordClick,
            modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.large)
        )

        Spacer(Modifier.weight(1f))

        LoginFormActions(
            onLoginClick = onLoginClick,
            onSignupClick = onSignupClick,
        )
    }
}

@Composable
private fun ResetPasswordRow(
    modifier: Modifier = Modifier,
    onResetPasswordClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
    ) {
        Text(
            text = "Did you forget your password?",
            style = MaterialTheme.typography.bodyMedium,
        )

        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(
                onClick = onResetPasswordClick
            )
        )
    }
}

@Composable
private fun LoginFormActions(
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionsEnable: Boolean = true,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            label = "Login",
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = actionsEnable
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
        ) {
            Text(
                text = "Don't have an account?",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = "Signup",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    onClick = onSignupClick,
                    enabled = actionsEnable
                )
            )
        }
    }
}

@Composable
private fun LoginFormFields(
    email: Email,
    onEmailChange: (String) -> Unit,
    password: Password,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    formEnabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
    ) {
        SimpleTextField(
            label = "Email",
            text = email.value,
            onChange = onEmailChange,
            error = email.displayError?.let {
                when (it) {
                    EmailError.Empty -> "Email is required"
                    EmailError.Invalid -> "Invalid email address"
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
            ),
            enabled = formEnabled
        )

        PasswordTextField(
            label = "Password",
            text = password.value,
            onChange = onPasswordChange,
            error = password.displayError?.let {
                when (it) {
                    PasswordError.Empty -> "Password is required"
                    PasswordError.TooShort -> "Password must be at least 8 characters long"
                }
            },
            enabled = formEnabled
        )
    }
}
