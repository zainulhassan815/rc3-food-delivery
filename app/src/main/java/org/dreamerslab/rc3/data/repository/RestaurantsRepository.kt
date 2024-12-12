package org.dreamerslab.rc3.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import org.dreamerslab.rc3.data.models.MenuItem
import org.dreamerslab.rc3.data.models.Restaurant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        const val RESTAURANTS_COLLECTION = "restaurants"
        const val MENU_COLLECTION = "menu"
    }

    private val restaurantsCollection = firestore.collection(RESTAURANTS_COLLECTION)

    @OptIn(ExperimentalCoroutinesApi::class)
    val restaurants: Flow<List<Restaurant>> = restaurantsCollection
        .snapshots()
        .mapLatest { snapshot ->
            snapshot.documents.mapNotNull { it.toObject(Restaurant::class.java) }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMenu(
        restaurantId: String
    ): Flow<List<MenuItem>> {
        val path = "$RESTAURANTS_COLLECTION/$restaurantId/$MENU_COLLECTION"
        return firestore
            .collection(path)
            .snapshots()
            .mapLatest { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(MenuItem::class.java) }
            }
    }
}
