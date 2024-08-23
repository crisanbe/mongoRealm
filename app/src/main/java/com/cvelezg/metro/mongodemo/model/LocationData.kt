// LocationData.kt
package com.cvelezg.metro.mongodemo.model

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class LocationData : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var owner_id: String = ""
    var timestamp: RealmInstant = RealmInstant.now()
}
