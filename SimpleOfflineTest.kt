// Simple test to verify offline functionality
@Composable
fun SimpleOfflineTest() {
    // This would be injected in a real app
    val networkMonitor = remember { 
        NetworkStateMonitorImpl(LocalContext.current) 
    }
    
    val isOnline by networkMonitor.isOnline.collectAsState(initial = true)
    
    Column {
        Text("Network Status: ${if (isOnline) "Online" else "Offline"}")
        
        OfflineIndicator(
            isOnline = isOnline,
            isSyncing = false,
            pendingChanges = if (!isOnline) 3 else 0
        )
        
        Button(
            onClick = { 
                // Test sync functionality
                if (isOnline) {
                    // Trigger sync
                }
            }
        ) {
            Text("Test Sync")
        }
    }
}