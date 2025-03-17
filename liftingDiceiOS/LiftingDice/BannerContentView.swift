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

struct BannerContentView: View {
    
    let adUnitId: String
    var width = UIScreen.main.bounds.width
    var size: CGSize {
        return GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth(width).size
    }
    
    init(adUnitId: String) {
        self.adUnitId = adUnitId
    }
    
    var body: some View {
        let _ = print(adUnitId)
        BannerView(GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth(width), adUnitId: adUnitId).frame(height: size.height)
    }
}


private struct BannerView: UIViewRepresentable {
    let adSize: GADAdSize
    let adUnitId: String
    
    init(_ adSize: GADAdSize, adUnitId: String) {
        self.adSize = adSize
        self.adUnitId = adUnitId
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
        return BannerCoordinator(self, adUniutID: adUnitId)
    }
    
    class BannerCoordinator: NSObject, GADBannerViewDelegate {
        
        var adUniutID: String
        
        private(set) lazy var bannerView: GADBannerView = {
            let bannerView = GADBannerView(adSize: parent.adSize)
            bannerView.adUnitID = adUniutID
            bannerView.load(GADRequest())
            bannerView.delegate = self
            return bannerView
        }()
        
        let parent: BannerView
        
        init(_ parent: BannerView, adUniutID: String) {
            self.parent = parent
            self.adUniutID = adUniutID
        }
    }
}
