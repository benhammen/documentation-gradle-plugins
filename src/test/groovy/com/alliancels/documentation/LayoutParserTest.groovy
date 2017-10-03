package com.alliancels.documentation

import spock.lang.Specification
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class LayoutParserTest extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()
    File yamlFile

    def setup() {
        yamlFile = testProjectDir.newFile('layout.yaml')
    }

    def "loads Section when name is specified"() {
        given:
        yamlFile << "name: testSection"

        when:
        Section section = (new LayoutParser()).createSection(yamlFile)

        then:
        section.name == "testSection"
        section.folder == yamlFile.parentFile
        section.subsectionFolderNames == []
    }

    def "loads Section when name and subsection folders are specified"() {
        given:
        yamlFile << """
        name: testSection
        orderedSubfolders:
          - testFolder1
          - testFolder2
        """

        File testFolder1 = testProjectDir.newFolder('testFolder1')
        File layoutFile1 = new File(testFolder1, 'layout.yaml')
        layoutFile1.createNewFile()
        File testFolder2 = testProjectDir.newFolder('testFolder2')
        File layoutFile2 = new File(testFolder2, 'layout.yaml')
        layoutFile2.createNewFile()

        when:
        Section section = (new LayoutParser()).createSection(yamlFile)

        then:
        section.name == "testSection"
        section.folder == yamlFile.parentFile
        section.subsectionFolderNames == ["testFolder1", "testFolder2"]
    }
}