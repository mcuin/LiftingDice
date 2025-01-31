//
//  LiftingDiceWorkoutChoiceViewModel.swift
//  liftingDiceiOS
//
//  Created by Mykal Cuin on 11/20/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Combine
import Foundation
import liftingDiceShared

@Observable final class LiftingDiceWorkoutChoiceViewModel {
    var muscleGroups: [MuscleGroup] = []
    private var muscleGroupSubscriptions = Set<AnyCancellable>()
    
    func startMuscleGroupObserving() {
        let firebaseHelper = FirebaseHelper()
        let muscleGroupsSubscription = flow(firebaseHelper.getMuscleGroups())
            .eraseToAnyPublisher()
            .receive(on: DispatchQueue.global(qos: .userInitiated))
            .sink(receiveCompletion: { completion in
                print(completion)
            }, receiveValue: { value in
                self.muscleGroups = value as! [MuscleGroup]
            })
            .store(in: &muscleGroupSubscriptions)
    }
}
