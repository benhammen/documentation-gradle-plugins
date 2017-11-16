package com.alliancels.documentation

import spock.lang.Specification
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class AssembleDocumentTaskTest extends Specification {

    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    String utf8Content = "UTF-8 content ₽"
    String linksContent = ""

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')

        File buildFolder = testProjectDir.newFolder('build')
        File documentationFolder = new File(buildFolder, 'documentation')

        File linksFolder = new File(documentationFolder, 'links')
        linksFolder.mkdirs()
        File linkFile = new File(linksFolder, 'section.html')
        linkFile.createNewFile()
        linkFile <<  linksContent

        File convertedMarkdownFolder = new File(documentationFolder, 'convertedMarkdown')
        convertedMarkdownFolder.mkdirs()
        File convertedMarkdownFile = new File(convertedMarkdownFolder, 'section.html')
        convertedMarkdownFile.createNewFile()
        convertedMarkdownFile.setBytes(utf8Content.getBytes('UTF-8'))
    }

    def "assembleAll task preserves UTF-8"() {
        given:
        buildFile << """
         plugins {
            id 'com.alliancels.documentation'
        }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(['assembleAll'])
                .withPluginClasspath()
                .build()

        then:
        result.task(":assembleAll").outcome == SUCCESS

        and:
        File outputHtml = new File(testProjectDir.root, 'build/documentation/all/section.html')
        outputHtml.exists()

        and:
        outputHtml.getText('UTF-8').contains(utf8Content)
    }

    def "preserves UTF-8"() {
        given:
        String symbol = "₽"

        when:
        String html = AssembleDocumentTask.createCompleteSection("", "", symbol, false)

        then:
        html.contains("₽")
    }
}