 /*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch") // Old package for compatibility
package org.jetbrains.kotlin.gradle.plugin.mpp

import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

class KotlinJvmAndroidCompilation(
    target: KotlinAndroidTarget,
    name: String
) : AbstractKotlinCompilationToRunnableFiles<KotlinJvmOptions>(target, name) {
    override val compileKotlinTask: org.jetbrains.kotlin.gradle.tasks.KotlinCompile
        get() = super.compileKotlinTask as org.jetbrains.kotlin.gradle.tasks.KotlinCompile

    override val relatedConfigurationNames: List<String>
        get() = super.relatedConfigurationNames + listOf("${name}ApiElements", "${name}RuntimeElements")
}