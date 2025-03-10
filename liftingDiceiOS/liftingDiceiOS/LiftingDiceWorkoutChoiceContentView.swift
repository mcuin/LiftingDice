import SwiftUI
import liftingDiceShared
import GoogleMobileAds

struct LiftingDiceWorkoutChoiceContentView: View {
    
    @State private var liftingDiceWorkoutChoiceViewModel = LiftingDiceWorkoutChoiceViewModel()
    @State private var selectedGroups = Set<MuscleGroup>()


	var body: some View {
        NavigationStack {
            VStack {
                List(liftingDiceWorkoutChoiceViewModel.muscleGroups, id: \.self, selection: $selectedGroups) { muscleGroup in
                    Text(muscleGroup.name.capitalized)
                }.environment(\.editMode, .constant(.active))
                BannerContentView()
            }
            .navigationTitle("Muscle Groups")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    NavigationLink("Next") {
                        LiftingDiceExercisesContentView(selectedMusleGroups: selectedGroups)
                    }.disabled(selectedGroups.isEmpty)
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
		LiftingDiceWorkoutChoiceContentView()
	}
}
