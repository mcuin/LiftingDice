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
    
    @State var liftingDiceEquipmentSettingsViewModel = LiftingDiceEquipmentSettingsViewModel()
    @State var selectedEquipment: Set<EquipmentSetting> = []
    
    var body: some View {
        NavigationStack {
            VStack {
                List(liftingDiceEquipmentSettingsViewModel.equipmentSettings, id: \.self, selection: $selectedEquipment) { equipmentSetting in
                    Text(equipmentSetting.name)
                }.environment(\.editMode, .constant(.active))
                BannerContentView()
            }
            .navigationTitle(Text("Equipment Settings"))
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Save") {
                        
                    }.disabled(selectedEquipment.isEmpty)
                }
                ToolbarItemGroup(placement: .bottomBar) {
                    Button("Muscle Groups") {
                        
                    }
                    Button("Settings") {
                       
                    }
                }
            }
        }.onAppear {
            liftingDiceEquipmentSettingsViewModel.startEquipmentSettingsObserving()
        }
    }
}
