package com.example.gym_tracker.core.export.di

import com.example.gym_tracker.core.export.ExportService
import com.example.gym_tracker.core.export.ExportServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for export functionality
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ExportModule {
    
    @Binds
    @Singleton
    abstract fun bindExportService(
        exportServiceImpl: ExportServiceImpl
    ): ExportService
}