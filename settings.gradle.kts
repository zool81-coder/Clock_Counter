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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // ⚠️ ЛОКАЛЬНЫЙ РЕПОЗИТОРИЙ: нужен из‑за недоступности dl.google.com
        // Без этой папки сборка падает с ошибкой про aapt2
        maven { url = uri("local-repo") }

        google()
        mavenCentral()
    }
}

rootProject.name = "Clock Counter"
include(":app")
 