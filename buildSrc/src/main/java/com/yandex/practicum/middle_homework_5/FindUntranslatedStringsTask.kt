package com.yandex.practicum.middle_homework_5

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

abstract class FindUntranslatedStringsTask : DefaultTask() {

    @TaskAction
    fun findUntranslatedStrings() {
        val resDir = File(project.projectDir, RESOURCES_PATH)
        val defaultStrings = File(resDir, "$DEFAULT_VALUES_FOLDER/$STRINGS_FILE_NAME")
        val defaultStringIdentities = extractStringNames(defaultStrings)

        val fileName = STRINGS_FILE_NAME
        val foldersStartingWithValues = resDir.listFiles { file ->
            file.isDirectory && file.name.startsWith("values-")
                    && (file.listFiles()?.any { it.isFile && it.name == fileName } == true)
        }

        val stringBuilderErrorText = StringBuilder("Missing translations").appendLine()
        var missingTranslationsFound = false

        foldersStartingWithValues?.let { array ->
            for (folder in array) {
                val stringsFile = File(resDir, "${folder.name}/$STRINGS_FILE_NAME")
                val stringIdentities = extractStringNames(stringsFile)

                defaultStringIdentities.forEach { missing ->
                    if (!stringIdentities.contains(missing)) {
                        missingTranslationsFound = true
                        stringBuilderErrorText
                            .append("=== $missing in ${folder.name} ===")
                            .appendLine()
                    }
                }
            }
        }

        if (missingTranslationsFound) {
            throw GradleException(stringBuilderErrorText.toString())
        }
    }

    private fun extractStringNames(file: File): List<String> {
        val nodeList = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(file)
            .getElementsByTagName("string")

        return (0 until nodeList.length).map { i ->
            val node = nodeList.item(i)
            node.attributes?.getNamedItem("name")?.nodeValue ?: ""
        }
    }

    private companion object {
        const val RESOURCES_PATH = "src/main/res"
        const val DEFAULT_VALUES_FOLDER = "values"
        const val STRINGS_FILE_NAME = "strings.xml"
    }
}
