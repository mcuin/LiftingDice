//
//  BannerView.swift
//  liftingDiceiOS
//
//  Created by Mykal Cuin on 1/15/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import GoogleMobileAds
import SwiftUI

private struct BannerView: UIViewRepresentable {
    let adSize: GADAdSize
    
    init(_ adSize: GADAdSize) {
        self.adSize = adSize
    }
    
    func makeUIView(context: Context) -> UIView {
        
        let view = UIView()
        view.addSubview(context.coordinator.bannerView)
        return view
    }
    
    func updateUIView(_ uiView: UIView, context: Context) {
        context.coordinator.bannerView.adSize = adSize
    }
    
    func makeCoordinator() -> BannerCoordinator {
        return BannerCoordinator(self)
    }
    
    class BannerCoordinator: NSObject, GADBannerViewDelegate {
        
        var bannerView(adUnitId: String = "ca-app-pub-3940256099942544/2934735716"): GADBannerView {
            let bannerView = GADBannerView(adSize: parent.adSize)
            bannerView.adUnitID = adUnitId
            bannerView.load(GADRequest())
            bannerView.delegate = self
            return bannerView
        }
        
        let parent: BannerView
        
        init(_ parent: BannerView) {
            self.parent = parent
        }
    }
}
