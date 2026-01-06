pluginManagement {
    repositories {
        google()  // ← ĐƠN GIẢN HÓA!
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()  // ← GIỐNG NHAU!
        mavenCentral()
    }
}

rootProject.name = "SmartStudy Planner"
include(":app")