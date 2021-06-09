plugins {
    id("com.android.library")
    // kotlin
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    // maven
    `maven-publish`
}

group = "com.github.superyao541"

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFile("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    lintOptions {
        disable("ContentDescription")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.10")
    implementation("androidx.core:core-ktx:1.5.0")

    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.appcompat:appcompat:1.3.0")

    implementation("androidx.activity:activity-ktx:1.2.3")
    implementation("androidx.fragment:fragment-ktx:1.3.4")

    val coroutinesVersion = "1.3.9"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    implementation("androidx.exifinterface:exifinterface:1.3.2")

    /*
    test
     */

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    // =============================================================================================
    // third-party
    // =============================================================================================

    implementation("com.google.code.gson:gson:2.8.7")
}

// Because the components are created only during the afterEvaluate phase, you must
// configure your publications using the afterEvaluate() lifecycle method.
afterEvaluate {
    publishing {
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>("release") {
                // Applies the component for the release build variant.
                from(components["release"])
                // You can then customize attributes of the publication as shown below.
                groupId = "com.github.superyao541"
                artifactId = "android-toolkit"
                version = "0.1.0"
            }
        }
    }
}