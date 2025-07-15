# Build Readiness Verification Script
Write-Host "=== Gym Tracker Modular Structure Verification ===" -ForegroundColor Green

# Check if all required files exist
$requiredFiles = @(
    "settings.gradle.kts",
    "build.gradle.kts", 
    "gradle/libs.versions.toml",
    "app/build.gradle.kts",
    "app/src/main/java/com/example/gym_tracker/MainActivity.kt",
    "app/src/main/java/com/example/gym_tracker/GymTrackerApplication.kt",
    "app/src/main/AndroidManifest.xml"
)

$coreModules = @(
    "core/common/build.gradle.kts",
    "core/database/build.gradle.kts", 
    "core/network/build.gradle.kts",
    "core/ui/build.gradle.kts",
    "core/testing/build.gradle.kts"
)

$featureModules = @(
    "feature/workout/build.gradle.kts",
    "feature/exercise/build.gradle.kts",
    "feature/statistics/build.gradle.kts", 
    "feature/profile/build.gradle.kts",
    "feature/ai-coaching/build.gradle.kts"
)

$allFiles = $requiredFiles + $coreModules + $featureModules

Write-Host "`nChecking required files..." -ForegroundColor Yellow
$missingFiles = @()
foreach ($file in $allFiles) {
    if (Test-Path $file) {
        Write-Host "✓ $file" -ForegroundColor Green
    } else {
        Write-Host "✗ $file" -ForegroundColor Red
        $missingFiles += $file
    }
}

Write-Host "`nChecking key database files..." -ForegroundColor Yellow
$dbFiles = @(
    "core/database/src/main/java/com/example/gym_tracker/core/database/GymTrackerDatabase.kt",
    "core/database/src/main/java/com/example/gym_tracker/core/database/dao/WorkoutDao.kt",
    "core/database/src/main/java/com/example/gym_tracker/core/database/dao/ExerciseDao.kt",
    "core/database/src/main/java/com/example/gym_tracker/core/database/di/DatabaseModule.kt"
)

foreach ($file in $dbFiles) {
    if (Test-Path $file) {
        Write-Host "✓ $file" -ForegroundColor Green
    } else {
        Write-Host "✗ $file" -ForegroundColor Red
        $missingFiles += $file
    }
}

Write-Host "`n=== Summary ===" -ForegroundColor Cyan
if ($missingFiles.Count -eq 0) {
    Write-Host "✅ All required files are present!" -ForegroundColor Green
    Write-Host "✅ Modular project structure is complete!" -ForegroundColor Green
    Write-Host "✅ Ready for build (requires JDK for compilation)" -ForegroundColor Green
} else {
    Write-Host "❌ Missing files:" -ForegroundColor Red
    foreach ($file in $missingFiles) {
        Write-Host "   - $file" -ForegroundColor Red
    }
}

Write-Host "`n=== Build Requirements ===" -ForegroundColor Cyan
Write-Host "• JDK 8 or higher (currently only JRE is available)" -ForegroundColor Yellow
Write-Host "• Android SDK (configured in Android Studio)" -ForegroundColor Yellow
Write-Host "• All Gradle dependencies will be downloaded automatically" -ForegroundColor Yellow

Write-Host "`n=== Next Steps ===" -ForegroundColor Cyan
Write-Host "1. Install JDK (Java Development Kit)" -ForegroundColor White
Write-Host "2. Open project in Android Studio" -ForegroundColor White
Write-Host "3. Let Android Studio sync the project" -ForegroundColor White
Write-Host "4. Build and run the application" -ForegroundColor White