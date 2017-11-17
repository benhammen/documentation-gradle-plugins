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
        
        import com.alliancels.documentation.Document
        Document all = new Document()
        all.with {
            name = "All"
            sourceFolders = ['UserRequirements']
        }
        
        documentation {
            documents = [all]
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

    def "converts UTF-8 codes to symbols"() {
        given:
        File markdown = testProjectDir.newFile('markdown')
        String utf8Code = "&#8381;"
        markdown.write(utf8Code)

        when:
        String html = MarkdownToHtmlTask.convertToHtml(markdown)

        then:
        html == "<p>₽</p>\n"
    }

    def "retains UTF-8 symbols"() {
        given:
        File markdown = testProjectDir.newFile('markdown')
        String symbol = "₽"
        markdown.setBytes(symbol.getBytes("UTF-8"))

        when:
        String html = MarkdownToHtmlTask.convertToHtml(markdown)

        then:
        html == "<p>₽</p>\n"
    }

    def "converts UTF-8 codes in markdown tables to symbols"() {
        given:
        File markdown = testProjectDir.newFile('markdown')

        markdown.setBytes("""
        column1 | column2
        ---     | ---
        &#8381; | &#8381;
        """.stripIndent().getBytes("UTF-8"))

        when:
        String html = MarkdownToHtmlTask.convertToHtml(markdown)

        then:
        html == """
                <table>
                <thead>
                <tr><th>column1</th><th>column2</th></tr>
                </thead>
                <tbody>
                <tr><td>₽</td><td>₽</td></tr>
                </tbody>
                </table>
                """.stripIndent().trim() + "\n"
    }

    def "retains UTF-8 symbols in markdown tables"() {
        given:
        File markdown = testProjectDir.newFile('markdown')

        markdown.setBytes("""
        column1 | column2
        ---     | ---
        ₽       | ₽
        """.stripIndent().getBytes("UTF-8"))

        when:
        String html = MarkdownToHtmlTask.convertToHtml(markdown)

        then:
        html == """
                <table>
                <thead>
                <tr><th>column1</th><th>column2</th></tr>
                </thead>
                <tbody>
                <tr><td>₽</td><td>₽</td></tr>
                </tbody>
                </table>
                """.stripIndent().trim() + "\n"
    }
}