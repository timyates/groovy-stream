/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package groovy.stream.functions ;

/**
 * Describes a function which takes a value of type {@code T} plus an {@link Integer} index,
 * and returns a value of type {@code S}
 *
 * @author Tim Yates
 * @since 0.8
 * @param <T> The input type
 * @param <S> The output type
 */
public interface IndexedFunction<T,S> {
    S call( T value, Integer index ) ;
}