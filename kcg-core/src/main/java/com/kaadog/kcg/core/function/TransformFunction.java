/*
 * Copyright 2020-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaadog.kcg.core.function;

import java.util.function.BiConsumer;

/**
 * 数据转换接口
 */
public interface TransformFunction<T, U> extends BiConsumer<T, U> {

    public static final Integer DEFAULT_ORDER      = 10;

    public static final Integer HIGHEST_PRECEDENCE = Integer.MIN_VALUE;
    public static final Integer LOWEST_PRECEDENCE  = Integer.MAX_VALUE;

    public static Integer next(Integer order) {
        return order + 100;
    }

    default int getOrder() {
        return DEFAULT_ORDER;
    }
}
