package com.alliancels.documentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import groovy.io.FileType
import org.gradle.api.GradleException

/**
 * Parses the documentation layout defined in a layout.yaml file.
 */
class LayoutParser {

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory())

    Section createSection(File layoutYaml) {

        SectionLayout sectionLayout = parseLayoutYaml(layoutYaml)

        def section = new Section(sectionLayout.name, layoutYaml.parentFile)

        // Add explicitly ordered subfolders; throw an exception if any don't exist
        sectionLayout.orderedSubfolders.each {

            File subfolder = new File(layoutYaml.parentFile, it)
            File subfolderYaml = new File(subfolder, 'layout.yaml')

            if (subfolderYaml.exists()) {
                section.subsectionFolderNames.add(subfolder.name)
            } else {
                GradleException("The layout file for explicitly ordered subfolder " + subfolder.toString() + " in " +
                        layoutYaml.toString() + " does not exist!  Remove the subfolder from the layout file, or" +
                        "create a layout file for the subfolder.")
            }
        }

        // Add all subfolders, alphabetically
        layoutYaml.parentFile.eachFile(FileType.DIRECTORIES) {

            File yamlFile = new File(it, 'layout.yaml')
            if (yamlFile.exists()) {
                section.subsectionFolderNames.add(it.name)
            }
        }

        // Remove duplicates, which creates a list of the explicitly ordered
        // subfolders, followed by the remaining alphabetized subfolders
        section.subsectionFolderNames.unique()

        return section
    }

    SectionLayout parseLayoutYaml(File file) {

        SectionLayout sectionLayout

        try {
            sectionLayout = mapper.readValue(file, SectionLayout.class)
        } catch (Exception e) {
            throw new GradleException(e.toString())
        }

        return sectionLayout
    }
}
