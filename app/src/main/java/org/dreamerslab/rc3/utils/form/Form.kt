package org.dreamerslab.rc3.utils.form

/**
 * An interface that automatically handles validation of all
 * [FormInput]s present in the [inputs].
 *
 * ```kotlin
 * // Sample Usage
 * data class SignupFormState(
 *     val name: Name = Name(""),
 *     val email: Email = Email(""),
 *     val password: Password = Password(""),
 *     val submissionStatus: FormSubmissionStatus = FormSubmissionStatus.Initial
 * ) : Form {
 *     override val inputs = listOf(name, email, password)
 * }
 * ```
 */
interface Form {
    /**
     * Returns all [FormInput] instances.
     *
     * Override this and give it all [FormInput]s in your class that should be
     * validated automatically.
     */
    val inputs: List<FormInput<*, *>>

    /**
     * Returns true if all [FormInput] values are valid.
     */
    val isValid: Boolean get() = inputs.validate()

    /**
     * Returns true if any of the [FormInput] values is not valid.
     */
    val isNotValid: Boolean get() = !isValid

    /**
     * Returns true if all the [FormInput] values are pure.
     */
    val isPure: Boolean get() = inputs.isPure()

    /**
     * Returns true if any of the [FormInput] values is modified.
     */
    val isDirty: Boolean get() = !isPure
}

/**
 * Returns true if all the [FormInput]s are valid.
 */
fun List<FormInput<*, *>>.validate(): Boolean = all { it.isValid }

/**
 * Returns true if all the [FormInput]s are pure.
 */
fun List<FormInput<*, *>>.isPure(): Boolean = all { it.isPure }
