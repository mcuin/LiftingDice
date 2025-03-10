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

@Observable final class LiftingDiceEquipmentSettingsViewModel: NSObject {
    
    var equipmentSettings: [EquipmentSetting] = []
    var selectedEquipmentSettings: Set<EquipmentSetting> = Set()
    private var equipmentSettingsSubscription = Set<AnyCancellable>()
    
    func saveEquipmentSettings() {
        let selectedIds = selectedEquipmentSettings.compactMap { equipmentSetting in
            print(equipmentSetting.id)
            return Int(equipmentSetting.id)
        }
        selectedEquipmentSettings.removeAll()
        DataController.shared.saveSelectedEquipmentSettings(selectedIds: selectedIds)
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
                let selectedEquipmentIds = DataController.shared.getSelectedEquipmentSettings().first?.selectedEquipmentIds ?? []
                print(DataController.shared.getSelectedEquipmentSettings().count)
                let selectedEquipment = self.equipmentSettings.filter { equipmentSetting in
                    selectedEquipmentIds.contains(where: { selectedId in
                        selectedId == equipmentSetting.id
                    }) }
                self.selectedEquipmentSettings.formUnion(selectedEquipment)
            })
            .store(in: &equipmentSettingsSubscription)
    }
}
