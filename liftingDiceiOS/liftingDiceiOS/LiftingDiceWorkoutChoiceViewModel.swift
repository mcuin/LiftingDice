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
        /*flow(firebaseHelper.getMuscleGroups()).handleEvents(receiveSubscription: { _ in print("subscribed to muscle groups") },
            receiveCancel: { print("cancelled muscle groups subscription")})
            .sink(receiveCompletion: { completion in switch completion {
            case .finished:
                print("finished muscle groups subscription")
                break
            case let .failure(error):
                print("error muscle groups subscription: \(error)")
                break
            }}, receiveValue: { response in
                print(response)
                self.muscleGroups = response as! [MuscleGroup]
            })
        
        
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
            print("cancelling muscle groups subscription")
            muscleGroupsSubscription.cancel()
        }*/
    }
}
