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

@Observable final class LiftingDiceExercisesViewModel: NSObject, NSFetchedResultsControllerDelegate {
    
    var filteredExercises: [Exercise] = []
    var randomizedExercises: [Exercise] = []
    private var exercisesSubscription = Set<AnyCancellable>()
    private let fetchedResultsController: NSFetchedResultsController<EquipmentSettings>
    private let dataController: DataController = .init()
    
    override init() {
        let request = NSFetchRequest<EquipmentSettings>(entityName: "EquipmentSettings")
        request.sortDescriptors = [NSSortDescriptor(key: "selectedEquipmentIds", ascending: true)]
        self.fetchedResultsController = NSFetchedResultsController<EquipmentSettings>(fetchRequest: request, managedObjectContext: dataController.container.viewContext, sectionNameKeyPath: nil, cacheName: nil)
        
        super.init()
        
        fetchedResultsController.delegate = self
    }
    
    func rollNames() async -> Exercise {
        return filteredExercises.randomElement()!
    }
    
    func rerollExercise(exercise: Exercise) {
        let exerciseIndex = randomizedExercises.firstIndex(where: { $0.id == exercise.id })
        var tempExercises = randomizedExercises
        tempExercises.remove(at: exerciseIndex!)
        while (tempExercises.count < randomizedExercises.count) {
            let randomExercise = filteredExercises.randomElement()!
            if (randomExercise != exercise && !randomizedExercises.contains(randomExercise)) {
                randomizedExercises[exerciseIndex!] = randomExercise
                tempExercises.insert(randomExercise, at: exerciseIndex!)
            }
        }
    }
    
    func rerollAllExercises() {
        let diceAmount = randomizedExercises.count
        var tempExercises: Set<Exercise> = []
        while (tempExercises.count < diceAmount) {
            let randomExercise = filteredExercises.randomElement()!
            tempExercises.insert(randomExercise)
        }
        randomizedExercises = Array(tempExercises)
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
                    let muscleGroupsIds = selectedMuscleGroups.map { $0.id }
                    try! self.fetchedResultsController.performFetch()
                    let selectedEquipmentIds = self.fetchedResultsController.fetchedObjects?.first.flatMap { $0.selectedEquipmentIds } ?? []
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
}
