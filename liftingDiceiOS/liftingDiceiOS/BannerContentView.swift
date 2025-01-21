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
    
    init(adUnitId: String = "ca-app-pub-3940256099942544/2934735716") {
        self.adUnitId = adUnitId
    }
    
    var body: some View {
        GeometryReader { geometry in
            BannerView(GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth(geometry.size.width), adUnitId: adUnitId)
                .frame(height: geometry.size.height)
        }
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
