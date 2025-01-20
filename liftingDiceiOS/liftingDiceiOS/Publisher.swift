//
//  FlowPublisher.swift
//  liftingDiceiOS
//
//  Created by Mykal Cuin on 11/21/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import liftingDiceShared
import Combine

extension KotlinThrowable: Error {}

class FlowPublisher<T: AnyObject>: Publisher {
    typealias Output = T
    typealias Failure = KotlinThrowable
    
    private let wrappedFlow: FlowWrapper<T>
    
    init(wrappedFlow: FlowWrapper<T>) {
        self.wrappedFlow = wrappedFlow
    }
    
    func receive<S>(subscriber: S) where S : Subscriber, KotlinThrowable == S.Failure, T == S.Input {
        let subscription = FlowSubscription(wrappedFlow: wrappedFlow)
        
        subscriber.receive(subscription: subscription)
        
        wrappedFlow.collect { value in
           let demand: Subscribers.Demand = subscriber.receive(value)
           // Dealing with demand is left as exercise for the reader
        } onCompletion: { throwable in
            subscriber.receive(completion: throwable == nil ? .finished : .failure(throwable!))
        }
    }
    
    class FlowSubscription: Subscription {
        
        private let wrappedFlow: FlowWrapper<T>
        
        init(wrappedFlow: FlowWrapper<T>) {
            self.wrappedFlow = wrappedFlow
        }
        
        func request(_ demand: Subscribers.Demand) {
            // Dealing with demand is left as exercise for the reader
        }
        
        //Progates the cancel
        func cancel() {
            wrappedFlow.cancel()
        }
    }
}

func flow<T>(_ wrapper: FlowWrapper<T>) -> FlowPublisher<T> {
    return FlowPublisher(wrappedFlow: wrapper)
}
