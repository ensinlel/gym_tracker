pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Gym_Tracker"
include(":app")

// Core modules
include(":core:common")
include(":core:data")
include(":core:database")
include(":core:network")
include(":core:ui")
include(":core:testing")
include(":core:export")

// Feature modules
include(":feature:workout")
include(":feature:exercise")
include(":feature:statistics")
include(":feature:profile")
include(":feature:ai-coaching")
