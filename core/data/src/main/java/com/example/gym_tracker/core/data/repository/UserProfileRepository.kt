package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for User Profile data operations
 */
interface UserProfileRepository {
    
    /**
     * Get user profile as a reactive stream
     */
    fun getUserProfile(): Flow<UserProfile?>
    
    /**
     * Get user profile by ID
     */
    suspend fun getUserProfileById(id: String): UserProfile?
    
    /**
     * Insert or update user profile
     */
    suspend fun insertUserProfile(userProfile: UserProfile)
    
    /**
     * Update user profile
     */
    suspend fun updateUserProfile(userProfile: UserProfile)
    
    /**
     * Delete user profile
     */
    suspend fun deleteUserProfile(userProfile: UserProfile)
    
    /**
     * Delete all profiles
     */
    suspend fun deleteAllProfiles()
    
    /**
     * Get profile count
     */
    suspend fun getProfileCount(): Int
}