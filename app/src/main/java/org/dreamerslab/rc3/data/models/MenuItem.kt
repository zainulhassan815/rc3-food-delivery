package org.dreamerslab.rc3.data.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentId

@Keep
data class MenuItem(
    @DocumentId val id: String = "",
    val name: String = "",
    val image: String = "",
    val price: Double = 0.0
)
