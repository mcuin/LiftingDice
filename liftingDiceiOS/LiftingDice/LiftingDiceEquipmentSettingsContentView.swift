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
    
    @Binding var tabSelection: String?
    @State var liftingDiceEquipmentSettingsViewModel = LiftingDiceEquipmentSettingsViewModel()
    
    var body: some View {
        if ($liftingDiceEquipmentSettingsViewModel.equipmentSettings.isEmpty) {
            VStack {
                ProgressView().onAppear {
                    liftingDiceEquipmentSettingsViewModel.startEquipmentSettingsObserving()
                }
                Text("Loading Equipment Settings...")
            }
        } else  {
            NavigationStack {
                VStack {
                    List(liftingDiceEquipmentSettingsViewModel.equipmentSettings, id: \.self, selection: $liftingDiceEquipmentSettingsViewModel.selectedEquipmentSettings) { equipmentSetting in
                        Text(equipmentSetting.name.capitalized)
                    }.environment(\.editMode, .constant(.active))
                    BannerContentView(adUnitId: Bundle.main.object(forInfoDictionaryKey: "EQUIPMENT_SETTINGS_BANNER_AD_ID") as! String)
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
            }
        }
    }
}
