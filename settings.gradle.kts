pluginManagement {
    includeBuild("plugins")
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

rootProject.name = "Navigation3"
include(":app")
include(":core:datastore-proto")
include(":feature:foryou")
include(":feature:forhe")
include(":feature:forit")
include(":core:navigation")
include(":core:model")
include(":core:ui")
include(":core:network")
include(":core:data")
include(":core:database")

