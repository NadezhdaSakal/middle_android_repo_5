package com.yandex.practicum.middle_homework_5

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

abstract class FindUntranslatedStringsTask : DefaultTask() {
    @TaskAction
    fun findUntranslatedStrings() {
        val resDir = File(project.projectDir, "src/main/res")
        val defaultStrings = File(resDir, "values/strings.xml")
        val defaultStringsFromXml = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(defaultStrings)
            .getElementsByTagName("string")
        val defaultStringIdentities = defaultStringsFromXml.let { nodeList ->
            (0 until nodeList.length).map { i ->
                val node = nodeList.item(i)
                val name = node.attributes?.getNamedItem("name")?.nodeValue ?: ""
                name
            }
        }
        val fileName = "strings.xml"
        val foldersStartingWithValues = resDir.listFiles { file ->
            file.isDirectory && file.name.startsWith("values-")
                    && file.listFiles()?.any { it.isFile && it.name == fileName } ?: false
        }
        foldersStartingWithValues?.let { array ->
            val stringBuilderErrorText = StringBuilder("Missing translations").append(System.lineSeparator())
            for (folder in array) {
                val strings = File(resDir, "${folder.name}/strings.xml")
                val stringsFromXml = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(strings)
                    .getElementsByTagName("string")
                val stringIdentities = stringsFromXml.let { nodeList ->
                    (0 until nodeList.length).map { i ->
                        val node = nodeList.item(i)
                        val name = node.attributes?.getNamedItem("name")?.nodeValue ?: ""
                        name
                    }
                }
                defaultStringIdentities.forEach { missing ->
                    if (!stringIdentities.contains(missing)) {
                        stringBuilderErrorText
                            .append("=== $missing in ${folder.name} ===")
                            .append(System.lineSeparator())
                    }
                }
            }
            throw GradleException(stringBuilderErrorText.toString())
        }
    }
}
