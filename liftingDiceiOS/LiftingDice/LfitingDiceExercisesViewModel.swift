//
//  LfitingDiceExercisesViewModel.swift
//  liftingDiceiOS
//
//  Created by Mykal Cuin on 3/6/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import Combine
import liftingDiceShared
import CoreData
import GoogleMobileAds

@Observable final class LiftingDiceExercisesViewModel: NSObject, NSFetchedResultsControllerDelegate, GADFullScreenContentDelegate {
    
    var filteredExercises: [Exercise] = []
    var randomizedExercises: [Exercise] = []
    private var exercisesSubscription = Set<AnyCancellable>()
    private let equipmentSettingsFetchedResultsController: NSFetchedResultsController<EquipmentSettings>
    private let userDataFetchedResultsController: NSFetchedResultsController<UserData>
    private let dataController: DataController = .init()
    var rerolls = 0
    private var rewardedAd: GADRewardedAd?
    var adFailedToLoad = false
    
    override init() {
        let equipmentSettingsrequest = NSFetchRequest<EquipmentSettings>(entityName: "EquipmentSettings")
        equipmentSettingsrequest.sortDescriptors = [NSSortDescriptor(key: "selectedEquipmentIds", ascending: true)]
        self.equipmentSettingsFetchedResultsController = NSFetchedResultsController<EquipmentSettings>(fetchRequest: equipmentSettingsrequest, managedObjectContext: dataController.container.viewContext, sectionNameKeyPath: nil, cacheName: nil)
        let userDataRequest = NSFetchRequest<UserData>(entityName: "UserData")
        userDataRequest.sortDescriptors = [NSSortDescriptor(key: "rerolls", ascending: true)]
        self.userDataFetchedResultsController = NSFetchedResultsController<UserData>(fetchRequest: userDataRequest, managedObjectContext: dataController.container.viewContext, sectionNameKeyPath: nil, cacheName: nil)
        
        super.init()
        
        equipmentSettingsFetchedResultsController.delegate = self
        userDataFetchedResultsController.delegate = self
    }
    
    func getRerolls() {
        try! self.userDataFetchedResultsController.performFetch()
        let userData = self.userDataFetchedResultsController.fetchedObjects?.first
        self.rerolls = Int(truncating: userData.flatMap { $0.rerolls as NSNumber } ?? 5)
    }
    
    func rollNames() async -> Exercise {
        return filteredExercises.randomElement()!
    }
    
    func rerollExercise(exercise: Exercise) {
        let exerciseIndex = randomizedExercises.firstIndex(of: exercise)
        var tempExercises = randomizedExercises
        tempExercises.remove(at: exerciseIndex!)
        while (tempExercises.count < randomizedExercises.count) {
            let randomExercise = filteredExercises.randomElement()!
            if (randomExercise != exercise && !randomizedExercises.contains(randomExercise)) {
                randomizedExercises[exerciseIndex!] = randomExercise
                tempExercises.insert(randomExercise, at: exerciseIndex!)
            }
        }
        updateRerolls()
    }
    
    func rerollAllExercises() {
        let diceAmount = randomizedExercises.count
        var tempExercises: Set<Exercise> = []
        while (tempExercises.count < diceAmount) {
            let randomExercise = filteredExercises.randomElement()!
            tempExercises.insert(randomExercise)
        }
        randomizedExercises = Array(tempExercises)
        updateRerolls()
    }
    
    func updateRerolls() {
        DataController.shared.saveUserRerolls(rerolls: self.rerolls - 1)
        self.rerolls -= 1
    }
    
    func resetRerolls(rewardedRerolls: Int, rerollExerciseItem: Exercise?) {
        DataController.shared.saveUserRerolls(rerolls: rewardedRerolls)
        self.rerolls = rewardedRerolls
        if (rerollExerciseItem != nil) {
            rerollExercise(exercise: rerollExerciseItem!)
        } else {
            rerollAllExercises()
        }
    }
    
    func updateAdFailedLoad() {
        adFailedToLoad = false
    }
    
    func startExercisesObserving(selectedMuscleGroups: Set<MuscleGroup>) {
        let firebaseHelper = FirebaseHelper()
        let exercisesSubscription: () = flow(firebaseHelper.getExercises())
            .eraseToAnyPublisher()
            .receive(on: DispatchQueue.global(qos: .userInitiated))
            .sink(receiveCompletion: { completion in
                print(completion)
            }, receiveValue: { value in
                if (!(value as! [Exercise]).isEmpty) {
                    self.getRerolls()
                    try! self.equipmentSettingsFetchedResultsController.performFetch()
                    let selectedEquipmentIds = self.equipmentSettingsFetchedResultsController.fetchedObjects?.first.flatMap { $0.selectedEquipmentIds } ?? []
                    print(selectedEquipmentIds)
                    if (!selectedEquipmentIds.isEmpty) {
                        self.filteredExercises = (value as! [Exercise]).filter { exercise in exercise.muscleGroupIds.contains(where: { muscleGroupId in
                            selectedMuscleGroups.contains(where: { selectedMuscleGroup in
                                selectedMuscleGroup.id == Int(truncating: muscleGroupId)
                            })}
                        )}.filter { exercise in exercise.equipmentIds.contains(where: { equipmentId in
                            selectedEquipmentIds.contains(where: { selectedEquipmentId in
                                selectedEquipmentId == Int(truncating: equipmentId)
                            })})}
                        var diceAmount = 0
                        switch self.filteredExercises.count {
                        case _ where self.filteredExercises.count >= 6 && selectedMuscleGroups.count <= 6:
                            diceAmount = 6
                        case _ where self.filteredExercises.count < 6:
                            diceAmount = self.filteredExercises.count
                        case _ where selectedMuscleGroups.count > 6 && self.filteredExercises.count >= selectedMuscleGroups.count:
                            diceAmount = selectedMuscleGroups.count
                        default:
                            diceAmount = 6
                        }
                        var _randomizedExercises: Set<Exercise> = []
                        while (_randomizedExercises.count < diceAmount) {
                            let randomExercise = self.filteredExercises.randomElement()
                            _randomizedExercises.insert(randomExercise!)
                        }
                        self.randomizedExercises = Array(_randomizedExercises)
                    } else {
                        self.filteredExercises = (value as! [Exercise]).filter { exercise in exercise.muscleGroupIds.contains(where: { muscleGroupId in
                            selectedMuscleGroups.contains(where: { selectedMuscleGroup in
                                selectedMuscleGroup.id == Int(truncating: muscleGroupId)
                            })}
                        )}.filter { exercise in exercise.equipmentIds.contains(where: { equipmentId in
                            selectedEquipmentIds.contains(where: { selectedEquipmentId in
                                selectedEquipmentId == 0
                            })})}
                    }
                } else {
                    print("Failed to fetch exercises")
                }
            })
            .store(in: &self.exercisesSubscription)
    }
    
    func loadAd() async {
        do {
            rewardedAd = try await GADRewardedAd.load(withAdUnitID: Bundle.main.object(forInfoDictionaryKey: "EXERCISE_REROLL_AD_ID") as! String, request: GADRequest())
            rewardedAd?.fullScreenContentDelegate = self
        } catch {
            print("Failed to load rewarded ad: \(error)")
        }
    }
    
    func showAd(rerollExercise: Exercise?) {
        guard let rewardedAd = rewardedAd else {
            adFailedToLoad = true
            return print("Ad wasn't loaded yet.")
        }
        
        rewardedAd.present(fromRootViewController: nil) {
            let reward = rewardedAd.adReward
            self.resetRerolls(rewardedRerolls: Int(truncating: reward.amount), rerollExerciseItem: rerollExercise)
        }
    }
    
    func adDidDismissFullScreenContent(_ ad: GADFullScreenPresentingAd) {
        rewardedAd = nil
    }
}
