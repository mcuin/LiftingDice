//
//  LiftingDiceAppViewModel.swift
//  liftingDiceiOS
//
//  Created by Mykal Cuin on 3/13/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import CoreData


@Observable final class LiftingDiceAppViewModel {
    
    func hasEquipmentSettings(completion: @escaping (String?) -> Void) {
        
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now()) {
            let equipmentSettings = DataController.shared.getSelectedEquipmentSettings().first?.selectedEquipmentIds ?? []
            if (equipmentSettings.isEmpty) {
                completion("onboarding")
            } else {
                completion("MuscleGroups")
            }
        }
    }
}


