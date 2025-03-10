//
//  DataController.swift
//  liftingDiceiOS
//
//  Created by Mykal Cuin on 2/11/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import CoreData
import liftingDiceShared

@Observable class DataController {
    
    static let shared = DataController()
    let container: NSPersistentContainer
    var viewContext: NSManagedObjectContext {
        return container.viewContext
    }
    
    init () {
        container = NSPersistentContainer(name: "LiftingDiceDataModel")
        container.loadPersistentStores { (description, error) in
            if let error = error as NSError? {
                fatalError("Core data failed to load: \(error), \(error.userInfo)")
            }
        }
    }
    
    func save() {
        do {
            try viewContext.save()
        } catch {
            viewContext.rollback()
            print(error.localizedDescription)
        }
    }
    
    func getSelectedEquipmentSettings() -> [EquipmentSettings] {
        
        let request = NSFetchRequest<EquipmentSettings>(entityName: "EquipmentSettings")
        request.returnsObjectsAsFaults = false
        
        do {
            return try viewContext.fetch(request)
        } catch {
            return []
        }
    }
    
    func saveSelectedEquipmentSettings(selectedIds: [Int]) {
        let equipmentSettings: EquipmentSettings
        let checkFetchRequest = NSFetchRequest<EquipmentSettings>(entityName: "EquipmentSettings")
        checkFetchRequest.fetchLimit = 1
        
        do {
            let fetchResults = try viewContext.fetch(checkFetchRequest)
            if fetchResults.count > 0 {
                equipmentSettings = fetchResults[0]
            } else {
                equipmentSettings = EquipmentSettings(context: viewContext)
            }
            equipmentSettings.selectedEquipmentIds = selectedIds
            save()
        } catch {
            print("Saving Core Data Failed: \(error)")
        }
    }
}
