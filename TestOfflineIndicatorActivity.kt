// You can create a simple test activity to see the offline indicators in action:

@Composable
fun TestOfflineIndicatorScreen() {
    var isOnline by remember { mutableStateOf(true) }
    var isSyncing by remember { mutableStateOf(false) }
    var pendingChanges by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Test controls
        Row {
            Button(onClick = { isOnline = !isOnline }) {
                Text("Toggle Online: $isOnline")
            }
        }
        
        Row {
            Button(onClick = { isSyncing = !isSyncing }) {
                Text("Toggle Sync: $isSyncing")
            }
        }
        
        Row {
            Button(onClick = { pendingChanges = if (pendingChanges == 0) 5 else 0 }) {
                Text("Toggle Changes: $pendingChanges")
            }
        }
        
        // Offline indicators
        OfflineIndicator(
            isOnline = isOnline,
            isSyncing = isSyncing,
            pendingChanges = pendingChanges,
            onSyncClick = { /* Handle sync */ }
        )
        
        OfflineBanner(isOnline = isOnline)
        
        // Sample content
        Text("Sample app content here...")
    }
}