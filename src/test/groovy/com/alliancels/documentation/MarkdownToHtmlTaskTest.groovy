package com.alliancels.documentation

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class MarkdownToHtmlTaskTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    String markdown = '# title\n\nUTF-8 content ₽'
    String html = '<h1 id="title">title</h1>\n<p>UTF-8 content ₽</p>\n'

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')

        File userRequirementsFolder = testProjectDir.newFolder('UserRequirements')
        userRequirementsFolder.mkdirs()
        File markdownFile1 = new File(userRequirementsFolder, 'section.md')
        markdownFile1.createNewFile()
        markdownFile1.setBytes('# title\n\nUTF-8 content ₽'.getBytes('UTF-8'))
    }

    def "converts markdown file to html"() {
        given:
        buildFile << """
         plugins {
            id 'com.alliancels.documentation'
        }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(['markdownToHtml'])
                .withPluginClasspath()
                .build()

        and:
        File outputHtml1 = new File(testProjectDir.root, 'build/documentation/convertedMarkdown/UserRequirements/section.html')

        then:
        result.task(":markdownToHtml").outcome == SUCCESS

        and:
        outputHtml1.exists()

        and:
        outputHtml1.getText('UTF-8') == html
    }
}