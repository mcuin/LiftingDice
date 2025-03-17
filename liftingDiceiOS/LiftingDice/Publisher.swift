//
//  FlowPublisher.swift
//  liftingDiceiOS
//
//  Created by Mykal Cuin on 11/21/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import liftingDiceShared
import Combine

func flow<T>(_ flowAdapter: FlowWrapper<T>) -> AnyPublisher<T, KotlinError> {
//    return Deferred<Publishers.HandleEvents<PassthroughSubject<T, KotlinError>>> {
        let subject = PassthroughSubject<T, KotlinError>()
        let job = flowAdapter.collect { (item) in
            subject.send(item)
        } onCompletion: {_ in
            subject.send(completion: .finished)
        }
        return subject.handleEvents(receiveCancel: {
            flowAdapter.cancel()
        }).eraseToAnyPublisher()
//    }.eraseToAnyPublisher()
}

class PublishedFlow<T> : ObservableObject {
    @Published
    var output: T

    init<E>(_ publisher: AnyPublisher<T, E>, defaultValue: T) {
        output = defaultValue

        publisher
            .replaceError(with: defaultValue)
            .compactMap { $0 }
            .receive(on: DispatchQueue.main)
            .assign(to: &$output)
    }
}

class KotlinError: LocalizedError {
    let throwable: KotlinThrowable
    init(_ throwable: KotlinThrowable) {
        self.throwable = throwable
    }
    var errorDescription: String? {
        // swiftlint:disable implicit_getter
        get { throwable.message }
        // swiftlint:enable implicit_getter
    }
}
