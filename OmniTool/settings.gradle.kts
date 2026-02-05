pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OmniTool"

// App module
include(":app")

// Core modules
include(":core:theme")
include(":core:common")
include(":core:navigation")

// Feature modules
include(":features:text")
include(":features:file")
include(":features:converter")
include(":features:security")
include(":features:utilities")

// Data module
include(":data")

// UI module (shared components)
include(":ui")
