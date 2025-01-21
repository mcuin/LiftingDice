import SwiftUI
import liftingDiceShared
import GoogleMobileAds

struct ContentView: View {
    
    @State private var liftingDiceWorkoutChoiceViewModel = LiftingDiceWorkoutChoiceViewModel()
    @State private var selectedGroups = Set<MuscleGroup>()


	var body: some View {
        NavigationStack {
            VStack {
                List(liftingDiceWorkoutChoiceViewModel.muscleGroups, id: \.self, selection: $selectedGroups) { muscleGroup in
                    Text(muscleGroup.name)
                }.environment(\.editMode, .constant(.active))
                Spacer().frame(height: .infinity)
            BannerContentView().frame(maxHeight: .infinity, alignment: .bottom)
            }
            .navigationTitle("Muscle Groups")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Next") {
                        
                    }.disabled(selectedGroups.isEmpty)
                }
                ToolbarItemGroup(placement: .bottomBar) {
                    Button("Muscle Groups") {
                        
                    }
                    Button("Settings") {
                        
                    }
                }
            }
        }
        .onAppear{
                liftingDiceWorkoutChoiceViewModel.startMuscleGroupObserving()
            }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
