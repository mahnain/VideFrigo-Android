package jalal.android.authenticationtest.models

import com.google.firebase.firestore.PropertyName

data class Post(
    var description: String = "",
    var image:String = "",
    @get:PropertyName("creation_time_ms") @set:PropertyName("creation_time_ms") var creationTimeMs:Long = 0,
    var user:User? = null

)
