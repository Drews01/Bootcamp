// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.spotless)
}

spotless {
    // strict check for line endings
    lineEndings = com.diffplug.spotless.LineEnding.PLATFORM_NATIVE

    kotlin {
        // Target both kotlin and java source directories (required for Android)
        target("app/src/*/kotlin/**/*.kt", "app/src/*/java/**/*.kt")
        targetExclude("**/build/**")

        // Use ktlint for formatting
        ktlint("1.5.0")
            .editorConfigOverride(
                mapOf(
                    "indent_size" to 4,
                    "ktlint_code_style" to "intellij_idea",
                    "max_line_length" to 120,
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                    "ktlint_standard_no-wildcard-imports" to "disabled"
                )
            )
    }

    kotlinGradle {
        target("*.gradle.kts", "**/*.gradle.kts")
        targetExclude("**/build/**")
        ktlint("1.5.0")
    }

    format("misc") {
        target("*.md", ".gitignore", ".editorconfig")
        trimTrailingWhitespace()
        endWithNewline()
    }
}
