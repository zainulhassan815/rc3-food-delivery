package org.dreamerslab.rc3.ui.auth.signup

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.dreamerslab.rc3.R
import org.dreamerslab.rc3.design_system.components.PasswordTextField
import org.dreamerslab.rc3.design_system.components.PrimaryButton
import org.dreamerslab.rc3.design_system.components.SimpleTextField
import org.dreamerslab.rc3.design_system.theme.spacing
import org.dreamerslab.rc3.utils.form.isInProgressOrSuccess
import org.dreamerslab.rc3.utils.formfields.ConfirmPassword
import org.dreamerslab.rc3.utils.formfields.ConfirmPasswordError
import org.dreamerslab.rc3.utils.formfields.Email
import org.dreamerslab.rc3.utils.formfields.EmailError
import org.dreamerslab.rc3.utils.formfields.Password
import org.dreamerslab.rc3.utils.formfields.PasswordError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onLoginClick: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel(),
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
        SignupPageContent(
            onLoginClick = onLoginClick,
            onSignupClick = viewModel::signup,
            onEmailChange = viewModel::updateEmail,
            onPasswordChange = viewModel::updatePassword,
            onConfirmPasswordChange = viewModel::updateConfirmPassword,
            formState = formState,
            modifier = Modifier
                .padding(innerPadding)
                .padding(MaterialTheme.spacing.large)
        )
    }
}

@Composable
private fun SignupPageContent(
    onSignupClick: () -> Unit,
    formState: SignupFormState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        SignupFormFields(
            email = formState.email,
            onEmailChange = onEmailChange,
            password = formState.password,
            onPasswordChange = onPasswordChange,
            confirmPassword = formState.confirmPassword,
            onConfirmPasswordChange = onConfirmPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = MaterialTheme.spacing.large,
                ),
            formEnabled = formState.submissionStatus.isInProgressOrSuccess.not()
        )

        Spacer(Modifier.weight(1f))

        SignupFormActions(
            onLoginClick = onLoginClick,
            onSignupClick = onSignupClick,
        )
    }
}

@Composable
private fun SignupFormActions(
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
            label = "Signup",
            onClick = onSignupClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = actionsEnable
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
        ) {
            Text(
                text = "Already have an account?",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = "Login",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    onClick = onLoginClick,
                    enabled = actionsEnable
                )
            )
        }
    }
}

@Composable
private fun SignupFormFields(
    email: Email,
    onEmailChange: (String) -> Unit,
    password: Password,
    onPasswordChange: (String) -> Unit,
    confirmPassword: ConfirmPassword,
    onConfirmPasswordChange: (String) -> Unit,
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

        PasswordTextField(
            label = "Confirm Password",
            text = confirmPassword.value,
            onChange = onConfirmPasswordChange,
            error = confirmPassword.displayError?.let {
                when (it) {
                    ConfirmPasswordError.Empty -> "Confirm your password"
                    ConfirmPasswordError.PasswordsNotSame -> "Passwords don't match"
                }
            },
            enabled = formEnabled
        )
    }
}
