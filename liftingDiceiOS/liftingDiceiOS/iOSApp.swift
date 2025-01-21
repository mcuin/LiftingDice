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
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
