/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.network.model

import android.annotation.SuppressLint
import com.example.model.NewsResource
import kotlinx.serialization.Serializable

/**
 * 当从 /newsresources （这个接口/路径）获取数据时，[新闻资源] 的网络表示形式
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class NetworkNewsResource(
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    val headerImageUrl: String,
    val type: String,
    val topics: List<String> = emptyList(),
)
