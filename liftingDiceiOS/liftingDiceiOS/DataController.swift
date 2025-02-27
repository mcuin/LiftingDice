//
//  DataController.swift
//  liftingDiceiOS
//
//  Created by Mykal Cuin on 2/11/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import CoreData

@Observable class DataController {
    
    let container = NSPersistentContainer(name: "LiftingDiceDataModel")
    
    init () {
        container.loadPersistentStores { (description, error) in
            if let error = error as NSError? {
                fatalError("Core data failed to load: \(error), \(error.userInfo)")
            }
        }
    }
}
