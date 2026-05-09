package com.example.navigation3

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import org.gradle.kotlin.dsl.invoke

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType,
}

@Suppress("EnumEntryName")
enum class NiaFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    demo(FlavorDimension.contentType, applicationIdSuffix = ".demo"),
    prod(FlavorDimension.contentType),
}

fun configureFlavors(
    commonExtension: CommonExtension,
    flavorConfigurationBlock: ProductFlavor.(flavor: NiaFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.entries.forEach { flavorDimension ->
            // 定义不同的维度 可以和 下面的 niaFlavor.name组合 生成不同的打包文件，目前该维度只有一个就是contentType
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            NiaFlavor.entries.forEach { niaFlavor ->
                register(niaFlavor.name) {
                    // dimension 必须在上面 flavorDimensions 中存在
                    dimension = niaFlavor.dimension.name
                    flavorConfigurationBlock(this, niaFlavor)
                    if (commonExtension is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (niaFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = niaFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}
