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
        container.viewContext.automaticallyMergesChangesFromParent = true
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
    
    func getUserRerolls() -> Int {
        let request = NSFetchRequest<UserData>(entityName: "UserData")
        request.returnsObjectsAsFaults = false
        
        do {
            let userData = try viewContext.fetch(request)
            return Int(userData.first?.rerolls ?? 5)
        } catch {
            return 5
        }
    }
    
    func saveUserRerolls(rerolls: Int) {
        let userData: UserData
        let checkRequest = NSFetchRequest<UserData>(entityName: "UserData")
        
        do {
            let fetchResults = try viewContext.fetch(checkRequest)
            if fetchResults.count > 0 {
                userData = fetchResults[0]
            } else {
                userData = UserData(context: viewContext)
            }
            userData.rerolls = Int32(rerolls)
            save()
        } catch {
            print("Saving Core Data Failed: \(error)")
        }
    }
}
