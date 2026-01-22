package com.example.bootcamp.domain.usecase.base

/**
 * Base UseCase interface for operations without parameters. Follows Single Responsibility Principle
 * - each UseCase does one thing.
 *
 * @param R The return type of the use case
 */
interface UseCase<R> {
    suspend operator fun invoke(): R
}

/**
 * Base UseCase interface for operations with parameters.
 *
 * @param P The parameter type
 * @param R The return type of the use case
 */
interface UseCaseWithParams<P, R> {
    suspend operator fun invoke(params: P): R
}

/**
 * Base UseCase interface for Flow-based operations without parameters. Used for observing data
 * streams.
 *
 * @param R The return type wrapped in a Flow
 */
interface FlowUseCase<R> {
    operator fun invoke(): kotlinx.coroutines.flow.Flow<R>
}
