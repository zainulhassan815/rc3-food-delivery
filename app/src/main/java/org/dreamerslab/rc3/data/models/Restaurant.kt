package org.dreamerslab.rc3.data.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentId

@Keep
data class Restaurant(
    @DocumentId val id: String = "",
    val name: String = "",
    val image: String = "",
    val rating: Double = 0.0,
)
