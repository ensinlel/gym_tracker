package com.example.gym_tracker.core.data.mapper

import com.example.gym_tracker.core.data.model.WeightHistory
import com.example.gym_tracker.core.database.entity.WeightHistoryEntity

/**
 * Convert database WeightHistoryEntity to domain WeightHistory model
 */
fun WeightHistoryEntity.toDomainModel(): WeightHistory {
    return WeightHistory(
        id = id,
        userProfileId = userProfileId,
        weight = weight,
        recordedDate = recordedDate,
        notes = notes
    )
}

/**
 * Convert domain WeightHistory model to database WeightHistoryEntity
 */
fun WeightHistory.toEntity(): WeightHistoryEntity {
    return WeightHistoryEntity(
        id = id,
        userProfileId = userProfileId,
        weight = weight,
        recordedDate = recordedDate,
        notes = notes
    )
}