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
        GADMobileAds.sharedInstance().applicationMuted = true
    }
    
    @State private var liftingDiceAppViewModel = LiftingDiceAppViewModel()
    @State private var tabSelection: String?
    
    var body: some Scene {
        WindowGroup {
            
            Group {
                switch (tabSelection) {
                case "onboarding":
                    LiftingDiceEquipmentSettingsContentView(tabSelection: $tabSelection)
                case "MuscleGroups", "Settings":
                    TabView(selection: $tabSelection) {
                        LiftingDiceWorkoutChoiceContentView()
                            .tabItem {
                                Label("Muscle Groups", systemImage: "dumbbell")
                            }.tag("MuscleGroups")
                        LiftingDiceEquipmentSettingsContentView(tabSelection: $tabSelection)
                            .tabItem {
                                Label("Settings", systemImage: "gearshape")
                            }.tag("Settings")
                    }
                default:
                    Text("")
                }
            }.onAppear {
                liftingDiceAppViewModel.hasEquipmentSettings { tabSelection in
                    self.tabSelection = tabSelection
                }
            }
        }
    }
}
