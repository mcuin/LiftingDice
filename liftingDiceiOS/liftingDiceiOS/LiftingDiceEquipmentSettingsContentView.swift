//
//  LiftingDiceEquipmentSettingsContentView.swift
//  liftingDiceiOS
//
//  Created by Mykal Cuin on 1/31/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import liftingDiceShared

struct LiftingDiceEquipmentSettingsContentView: View {
    
    @Binding var tabSelection: String
    @State var liftingDiceEquipmentSettingsViewModel = LiftingDiceEquipmentSettingsViewModel()
    
    var body: some View {
        NavigationStack {
        VStack {
            List(liftingDiceEquipmentSettingsViewModel.equipmentSettings, id: \.self, selection: $liftingDiceEquipmentSettingsViewModel.selectedEquipmentSettings) { equipmentSetting in
                Text(equipmentSetting.name.capitalized)
            }.environment(\.editMode, .constant(.active))
            BannerContentView()
        }
        .navigationTitle(Text("Equipment Settings"))
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button("Save") {
                    liftingDiceEquipmentSettingsViewModel.saveEquipmentSettings()
                    tabSelection = "MuscleGroups"
                }.disabled($liftingDiceEquipmentSettingsViewModel.selectedEquipmentSettings.wrappedValue.isEmpty)
            }
        }
        }.onAppear {
            liftingDiceEquipmentSettingsViewModel.startEquipmentSettingsObserving()
        }
    }
}
