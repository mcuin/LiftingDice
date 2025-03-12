//
//  LiftingDiceExercisesContentView.swift
//  liftingDiceiOS
//
//  Created by Mykal Cuin on 3/6/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import liftingDiceShared

struct LiftingDiceExercisesContentView: View {
    
    let columns = [
        GridItem(.flexible()),
        GridItem(.flexible())
    ]
    
    let selectedMusleGroups: Set<MuscleGroup>
    @State var liftingDiceExerciseViewModel = LiftingDiceExercisesViewModel()
    @State private var showRerollAllAlert = false
    @State private var showRerollExerciseAlert = false
    @State private var viewDidLoad = false
    
    var body: some View {
            VStack {
                ViewThatFits(in: .vertical) {
                    ScrollView(.vertical) {
                        VStack {
                            Text("Dislike one of your options? Reroll that exercise, or reroll them all.").padding(16)
                            LazyVGrid(columns: columns) {
                                ForEach(Array(liftingDiceExerciseViewModel.randomizedExercises.enumerated()), id: \.element) { index, exercise in
                                    ExerciseCardView(liftingDiceExerciseViewModel: liftingDiceExerciseViewModel, exercise: exercise, exerciseIndex: index)
                                }
                            }
                            Text("All these exercises are only suggestions. Perform only what you are comfortable with. Never push yourself over your limits, and listen to your body.").padding(16)
                        }
                    }
                }
                BannerContentView()
            }
            .navigationTitle(Text("Exercises"))
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Reroll All") {
                        if (liftingDiceExerciseViewModel.rerolls <= 0) {
                            showRerollAllAlert = true
                        } else {
                            liftingDiceExerciseViewModel.rerollAllExercises()
                        }
                    }
                    .alert("Out of Rerolls", isPresented: $showRerollAllAlert) {
                        Button("Watch Ad") {
                            liftingDiceExerciseViewModel.showAd(rerollExercise: nil)
                        }
                        Button("Cancel", role: .cancel) {}
                    } message: {
                        Text("You are out of rerolls. Would you like to watch and ad for more rerolls?")
                    }
                    .disabled(liftingDiceExerciseViewModel.filteredExercises.count <= 6)
                }
        }.onAppear {
            if (viewDidLoad == false) {
                liftingDiceExerciseViewModel.startExercisesObserving(selectedMuscleGroups: self.selectedMusleGroups)
                viewDidLoad = true
            }
        }.task {
            await liftingDiceExerciseViewModel.loadAd()
        }
    }
}

#Preview {
    LiftingDiceExercisesContentView(selectedMusleGroups: [])
}

struct ExerciseCardView: View {
    
    let liftingDiceExerciseViewModel: LiftingDiceExercisesViewModel
    let exercise: Exercise
    let exerciseIndex: Int
    @State private var showRerollExerciseAlert = false
    @State private var exerciseName = "Push ups"
    let timer = Timer.publish(every: 0.25, on: .main, in: .common).autoconnect()
    @State private var counter = 0
    
    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 25).fill(Color.accentColor).shadow(radius: 10)
            let _ = print(exercise.name)
            let _ = print(exerciseIndex)
            VStack {
                HStack {
                    Text(exerciseName.capitalized)
                        .font(.headline)
                        .onReceive(timer) { _ in
                            if counter == 5 {
                                timer.upstream.connect().cancel()
                                self.exerciseName = exercise.name
                            } else {
                                exerciseName = liftingDiceExerciseViewModel.filteredExercises.randomElement()!.name
                                counter += 1
                            }
                        }.frame(alignment: .leading).padding(8)
                    Spacer()
                    VStack(alignment: .trailing) {
                        Button {
                            let searchString = "x-web-search://?\(exercise.name)"
                            if let url = URL(string: searchString) {
                                if UIApplication.shared.canOpenURL(url) {
                                    UIApplication.shared.open(url)
                                } else {
                                    print("Open url error")
                                }
                            } else {
                                print("No url")
                            }
                        } label: {
                            Image(systemName: "info.circle")
                        }.padding(.trailing, 12).padding(.top, 8)
                        Button {
                            if (liftingDiceExerciseViewModel.rerolls <= 0) {
                                showRerollExerciseAlert = true
                            } else {
                                liftingDiceExerciseViewModel.rerollExercise(exercise: exercise)
                            }
                        } label: {
                            Image("rerollIcon").resizable().aspectRatio(contentMode: .fit).frame(width: 24, height: 24)
                        }
                        .alert("Out of Rerolls", isPresented: $showRerollExerciseAlert) {
                            Button("Watch Ad") {
                                liftingDiceExerciseViewModel.showAd(rerollExercise: exercise)
                            }
                            Button("Cancel", role: .cancel) {}
                        } message: {
                            Text("You are out of rerolls. Would you like to watch and ad for more rerolls?")
                        }
                        .padding(.trailing, 12).padding(.bottom, 8).opacity(liftingDiceExerciseViewModel.filteredExercises.count > 6 ? 1: 0)
                    }
                }
            }
        }.frame(height: 100).padding(.leading, 8).padding(.trailing, 8).padding(.bottom, 4).padding(.top, 4)
    }
}
