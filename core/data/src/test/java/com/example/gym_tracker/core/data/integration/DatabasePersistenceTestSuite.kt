package com.example.gym_tracker.core.data.integration

import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test suite for all database persistence integration tests
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    DatabasePersistenceIntegrationTest::class,
    DataMigrationIntegrationTest::class
)
class DatabasePersistenceTestSuite