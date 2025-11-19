import com.android.tools.r8.internal.en

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.samyak.smalitm"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    
    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
    
    // OkHttp for HTTP requests
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // EventSource for SSE (using older version with compatible API)
    implementation("com.launchdarkly:okhttp-eventsource:2.5.0")
    
    // SLF4J for logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("com.github.tony19:logback-android:3.0.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// Maven publishing configuration for JitPack
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                
                groupId = "com.github.samyak2403"
                artifactId = "smalitm"
                version = "1.0.0"
                
                pom {
                    name.set("SMaliTM")
                    description.set("Kotlin Mail.tm API Wrapper for Android")
                    url.set("https://github.com/samyak2403/SMaliTM")
                    
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set("yourusername")
                            name.set("Samyak")
                            email.set("your.email@example.com")
                        }
                    }
                    
                    scm {
                        connection.set("scm:git:git://github.com/yourusername/SMaliTM.git")
                        developerConnection.set("scm:git:ssh://github.com/yourusername/SMaliTM.git")
                        url.set("https://github.com/yourusername/SMaliTM")
                    }
                }
            }
        }
    }
}
