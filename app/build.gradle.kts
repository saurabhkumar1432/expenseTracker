plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = "com.saurabhkumar.expensetracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.saurabhkumar.expensetracker"
        minSdk = 24
        targetSdk = 35
        versionCode = 231
        versionName = "2.3.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("SIGNING_KEYSTORE_PATH")
            val keystoreFile = if (keystorePath != null) file(keystorePath) else null
            val storePass = System.getenv("SIGNING_STORE_PASSWORD")
            val alias = System.getenv("SIGNING_KEY_ALIAS")
            val keyPass = System.getenv("SIGNING_KEY_PASSWORD")

            // Only configure signing if ALL required values are present
            if (keystoreFile != null && keystoreFile.exists() &&
                !storePass.isNullOrBlank() && !alias.isNullOrBlank() && !keyPass.isNullOrBlank()
            ) {
                storeFile = keystoreFile
                storePassword = storePass
                keyAlias = alias
                keyPassword = keyPass
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            val signingConfigRelease = signingConfigs.getByName("release")
            // Use release signing if properly configured, otherwise fall back to debug signing
            // This ensures APKs are always installable (debug-signed if release signing unavailable)
            if (signingConfigRelease.storeFile != null && signingConfigRelease.storeFile!!.exists()) {
                signingConfig = signingConfigRelease
            } else {
                // Fall back to debug signing config for installable APKs
                signingConfig = signingConfigs.getByName("debug")
            }
        }
        debug {
            // keep debug lightweight
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources.excludes += listOf("META-INF/{AL2.0,LGPL2.1}")
    }
    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

detekt {
    // Use the centralized detekt config
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
}

ktlint {
    android.set(true)
    ignoreFailures.set(false)
    disabledRules.set(
        setOf(
            "filename",
            "max-line-length",
            "argument-list-wrapping",
            "trailing-comma-on-call-site",
            "trailing-comma-on-declaration-site",
            "multiline-expression-wrapping",
            "parameter-list-wrapping",
            "function-signature",
            "indent",
            "no-wildcard-imports",
            "final-newline"
        )
    )
}
