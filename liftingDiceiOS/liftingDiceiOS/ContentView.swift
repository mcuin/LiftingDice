import SwiftUI
import liftingDiceShared

struct ContentView: View {
	let greet = Greeting().greet()

	var body: some View {
        let muscleGroups = KoinModuleKt.fire.getMuscleGroups()
		Text(greet)
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
