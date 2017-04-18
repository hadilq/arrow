/*
 * Copyright (C) 2017 The Katz Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package katz

interface FreeMonad<S> : Monad<FreeF<S>>, Typeclass {
    override fun <A> pure(a: A): Free<S, A> =
            Free.pure(a)

    override fun <A, B> map(fa: FreeKind<S, A>, f: (A) -> B): HK<FreeF<S>, B> =
            fa.ev().map(f)

    override fun <A, B> flatMap(fa: FreeKind<S, A>, f: (A) -> FreeKind<S, B>): Free<S, B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> FreeKind<S, Either<A, B>>): Free<S, B> {
        return f(a).ev().flatMap {
            when (it) {
                is Either.Left -> tailRecM(it.a, f)
                is Either.Right -> pure(it.b)
            }
        }
    }
}