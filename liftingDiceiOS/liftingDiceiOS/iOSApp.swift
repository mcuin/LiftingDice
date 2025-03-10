import SwiftUI
import Firebase
import GoogleMobileAds
import liftingDiceShared

@main
struct iOSApp: App {
    
    init() {
        FirebaseHelperKt.doInitKoin()
        FirebaseApp.configure()
        GADMobileAds.sharedInstance().start(completionHandler: nil)
    }
    
    @State private var tabSelection = "MuscleGroups"
    
	var body: some Scene {
		WindowGroup {
            
            TabView(selection: $tabSelection) {
                LiftingDiceWorkoutChoiceContentView()
                    .tabItem {
                        Label("Muscle Groups", systemImage: "")
                    }.tag("MuscleGroups")
                LiftingDiceEquipmentSettingsContentView(tabSelection: $tabSelection)
                    .tabItem {
                        Label("Settings", systemImage: "")
                    }.tag("Serttings")
            }
		}
	}
}
