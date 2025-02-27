//
//  LiftingDiceEquipmentSettingsViewModel.swift
//  liftingDiceiOS
//
//  Created by Mykal Cuin on 1/31/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import Combine
import liftingDiceShared
import CoreData

@Observable final class LiftingDiceEquipmentSettingsViewModel: NSObject, NSFetchedResultsControllerDelegate {
    
    var equipmentSettings: [EquipmentSetting] = []
    var selectedEquipmentSettings: Set<EquipmentSetting> = Set()
    private var equipmentSettingsSubscription = Set<AnyCancellable>()
    private let fetchedResultsController: NSFetchedResultsController<EquipmentSettings>
    private let dataController: DataController = .init()
    
    override init() {
        let request = NSFetchRequest<EquipmentSettings>(entityName: "EquipmentSettings")
        request.sortDescriptors = [NSSortDescriptor(key: "selectedEquipmentIds", ascending: true)]
        self.fetchedResultsController = NSFetchedResultsController<EquipmentSettings>(fetchRequest: request, managedObjectContext: dataController.container.viewContext, sectionNameKeyPath: nil, cacheName: nil)
        
        super.init()
        
        fetchedResultsController.delegate = self
    }
    
    func saveEquipmentSettings() {
        let newEquipmentSettingsEntity = EquipmentSettings(context: dataController.container.viewContext)
        newEquipmentSettingsEntity.selectedEquipmentIds = selectedEquipmentSettings.compactMap { equipmentSetting in
            print(equipmentSetting.id)
            return Int(equipmentSetting.id)
        }
        do {
            try dataController.container.viewContext.save()
        } catch let error as NSError {
            print("Could not save. \(error), \(error.userInfo)")
        }
    }
    
    func startEquipmentSettingsObserving() {
        let firebaseHelper = FirebaseHelper()
        let equipmentSettingsSubscription = flow(firebaseHelper.getEquipmentSettings())
            .eraseToAnyPublisher()
            .receive(on: DispatchQueue.global(qos: .userInitiated))
            .sink(receiveCompletion: { completion in
                print(completion)
            }, receiveValue: { value in
                self.equipmentSettings = value as! [EquipmentSetting]
                try! self.fetchedResultsController.performFetch()
                let ids = self.fetchedResultsController.fetchedObjects?.first.flatMap { $0.selectedEquipmentIds } ?? []
                let selectedEquipment = self.equipmentSettings.filter { ids.contains(Int($0.id)) }
                self.selectedEquipmentSettings.formUnion(selectedEquipment)
            })
            .store(in: &equipmentSettingsSubscription)
    }
}
