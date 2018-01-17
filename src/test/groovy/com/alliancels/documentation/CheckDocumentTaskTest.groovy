package com.alliancels.documentation

import spock.lang.Specification
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class CheckDocumentTaskTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    File section1
    File section2
    File image1

    def setup() {
        // Create build file
        buildFile = testProjectDir.newFile('build.gradle')

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

        // Create (empty) build output files
        File buildFolder = testProjectDir.newFolder('build')
        File documentationFolder = new File(buildFolder, 'documentation')
        File allOutputFolder = new File(documentationFolder, 'All')
        File userRequirementsOutputFolder = new File(allOutputFolder, 'UserRequirements')
        userRequirementsOutputFolder.mkdirs()
        section1 = new File(userRequirementsOutputFolder, 'section1.html')
        section1.createNewFile()
        section2 = new File(userRequirementsOutputFolder, 'section2.html')
        section2.createNewFile()
        image1 = new File(userRequirementsOutputFolder, 'image1.png')
        image1.createNewFile()
    }

    def "checkAll tasks succeeds for valid html"() {
        given:
        section1 << """
        <a href="section2.html#heading-1">Link</a>
        <a href="image1.png">Link</a>
        <a href="#heading-2">Link</a>
        <h6 id="heading-2"><a name="Heading 2"></a>Heading 2</h6>
        <a href="https://example.com#example">External link</a>
        """
        section2 << """
        <h6 id="heading-1"><a name="Heading 1"></a>Heading 1</h1>
        """
        image1 << "asdf"

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(['checkAll'])
                .withPluginClasspath()
                .build()

        then:
        result.task(":checkAll").outcome == SUCCESS
    }

    def "checkAll tasks fail for missing image"() {
        given:
        section1 << """
        <a href="section2.html#heading-1">Link</a>
        <a href="wrongimagename.png">Link</a>
        """
        section2 << """
        <h6 id="heading-1"><a name="Heading 1"></a>Heading 1</h1>
        """
        image1 << "asdf"

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(['checkAll'])
                .withPluginClasspath()
                .buildAndFail()

        then:
        result.getOutput().contains("Broken link in")
    }

    def "checkAll tasks fail for missing html file"() {
        given:
        section1 << """
        <a href="wronghtmlfilename.html#heading-1">Link</a>
        <a href="image1.png">Link</a>
        """
        section2 << """
        <h6 id="heading-1"><a name="Heading 1"></a>Heading 1</h1>
        """
        image1 << "asdf"

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(['checkAll'])
                .withPluginClasspath()
                .buildAndFail()

        then:
        result.getOutput().contains("Broken link in")
    }

    def "checkAll tasks fail for bad link anchor in another file"() {
        given:
        section1 << """
        <a href="section2.html#heading-1">Link</a>
        <a href="image1.png">Link</a>
        """
        section2 << """
        <h6 id="wrongheadingname"><a name="Heading 1"></a>Heading 1</h1>
        """
        image1 << "asdf"

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(['checkAll'])
                .withPluginClasspath()
                .buildAndFail()

        then:
        result.getOutput().contains("Broken link in")
    }

    def "checkAll tasks fail for bad link anchor in same file"() {
        given:
        section1 << """
        <a href="#heading-1">Link</a>
        <h6 id="wrongid"><a name="Heading 1"></a>Heading 1</h6>
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(['checkAll'])
                .withPluginClasspath()
                .buildAndFail()

        then:
        result.getOutput().contains("Broken link in")
    }
}
