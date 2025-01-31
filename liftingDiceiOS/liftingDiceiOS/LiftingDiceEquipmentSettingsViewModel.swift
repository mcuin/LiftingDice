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

@Observable final class LiftingDiceEquipmentSettingsViewModel {
    
    var equipmentSettings: [EquipmentSetting] = []
    private var equipmentSettingsSubscription = Set<AnyCancellable>()
    
    func startEquipmentSettingsObserving() {
        let firebaseHelper = FirebaseHelper()
        let equipmentSettingsSubscription = flow(firebaseHelper.getEquipmentSettings())
            .eraseToAnyPublisher()
            .receive(on: DispatchQueue.global(qos: .userInitiated))
            .sink(receiveCompletion: { completion in
                print(completion)
            }, receiveValue: { value in
                self.equipmentSettings = value as! [EquipmentSetting]
            })
            .store(in: &equipmentSettingsSubscription)
    }
}
