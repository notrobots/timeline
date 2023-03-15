package dev.notrobots.timeline.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.notrobots.timeline.models.Profile

@Dao
interface ProfileDao : BaseDao<Profile> {
    @Query("SELECT * FROM Profile")
    suspend fun getProfiles(): List<Profile>

    @Query("SELECT * FROM Profile")
    fun getProfilesLive(): LiveData<List<Profile>>

    @Query("SELECT * FROM Profile WHERE social = :social")
    suspend fun getProfiles(social: String): List<Profile>

    @Query("SELECT * FROM Profile WHERE social = :social")
    fun getProfilesLive(social: String): LiveData<List<Profile>>

    @Query("SELECT * FROM Profile WHERE username = :username AND social = :social")
    fun getProfile(username: String, social: String): Profile?

    @Query("SELECT EXISTS(SELECT * FROM Profile WHERE username = :username AND social = :social)")
    suspend fun exists(username: String, social: String): Boolean

    @Transaction
    suspend fun exists(profile: Profile): Boolean {
        return exists(profile.username, profile.social)
    }
}