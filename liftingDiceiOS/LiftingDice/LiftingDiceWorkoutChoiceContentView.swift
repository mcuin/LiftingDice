import SwiftUI
import liftingDiceShared
import GoogleMobileAds

struct LiftingDiceWorkoutChoiceContentView: View {
    
    @State private var liftingDiceWorkoutChoiceViewModel = LiftingDiceWorkoutChoiceViewModel()
    @State private var selectedGroups = Set<MuscleGroup>()


	var body: some View {
        if ($liftingDiceWorkoutChoiceViewModel.muscleGroups.isEmpty) {
            VStack {
                ProgressView().onAppear{
                    liftingDiceWorkoutChoiceViewModel.startMuscleGroupObserving()
                }
                Text("Loading Muscle Groups...")
            }
        } else {
            NavigationStack {
                VStack {
                    List(liftingDiceWorkoutChoiceViewModel.muscleGroups, id: \.self, selection: $selectedGroups) { muscleGroup in
                        Text(muscleGroup.name.capitalized)
                    }.environment(\.editMode, .constant(.active))
                    BannerContentView(adUnitId: Bundle.main.object(forInfoDictionaryKey: "MUSCLE_GROUPS_BANNER_AD_ID") as! String)
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
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		LiftingDiceWorkoutChoiceContentView()
	}
}
