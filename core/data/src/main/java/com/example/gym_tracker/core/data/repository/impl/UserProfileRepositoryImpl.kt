package com.example.gym_tracker.core.data.repository.impl

import com.example.gym_tracker.core.data.mapper.toDomainModel
import com.example.gym_tracker.core.data.mapper.toEntity
import com.example.gym_tracker.core.data.model.UserProfile
import com.example.gym_tracker.core.data.repository.UserProfileRepository
import com.example.gym_tracker.core.database.dao.UserProfileDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserProfileRepository using local database
 */
@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile().map { entity ->
            entity?.toDomainModel()
        }
    }

    override suspend fun getUserProfileById(id: String): UserProfile? {
        return userProfileDao.getUserProfileById(id)?.toDomainModel()
    }

    override suspend fun insertUserProfile(userProfile: UserProfile) {
        userProfileDao.insertUserProfile(userProfile.toEntity())
    }

    override suspend fun updateUserProfile(userProfile: UserProfile) {
        userProfileDao.updateUserProfile(userProfile.toEntity())
    }

    override suspend fun deleteUserProfile(userProfile: UserProfile) {
        userProfileDao.deleteUserProfile(userProfile.toEntity())
    }

    override suspend fun deleteAllProfiles() {
        userProfileDao.deleteAllProfiles()
    }

    override suspend fun getProfileCount(): Int {
        return userProfileDao.getProfileCount()
    }
}