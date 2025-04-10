plugins{
    `kotlin-dsl`
}

dependencies{
    implementation(gradleApi())
}

allprojects {
    repositories {
        mavenCentral()
    }
}

gradlePlugin {
    plugins {
        create("find_untranslated_strings_plugin") {
            id = "find_untranslated_strings_plugin"
            implementationClass = "com.yandex.practicum.middle_homework_5.FindUntranslatedStringsPlugin"
        }
    }
}
