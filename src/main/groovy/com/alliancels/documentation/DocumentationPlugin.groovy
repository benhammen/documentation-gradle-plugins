package com.alliancels.documentation

import org.gradle.api.Project
import org.gradle.api.Plugin

class DocumentationPlugin implements Plugin<Project> {

    void apply(Project project) {

        String userRequirementsDir = 'UserRequirements'
        String designRequirementsDir = 'DesignRequirements'
        String developerDocumentationDir = 'DeveloperDocumentation'

        String functionalTestSpecificationsDir = 'DesignRequirements/FunctionalTest'

        List<String> testableDocumentationDirs = [userRequirementsDir, designRequirementsDir]
        List<String> allDocumentationDirs = [userRequirementsDir, designRequirementsDir, developerDocumentationDir]

        def documentationOutput = new File(project.buildDir, 'documentation/')
        def allRequirementsOutput = new File(documentationOutput, 'allRequirements/')
        def previewRequirementsOutput = new File(documentationOutput, 'previewRequirements/')
        def userRequirementsOnlyOutput = new File(documentationOutput, 'userRequirementsOnly/')
        def convertedMarkdown = new File(documentationOutput, 'convertedMarkdown')
        def links = new File(documentationOutput, 'links')

        project.extensions.create('documentation', DocumentationPluginExtension.class)

        project.task('markdownToHtml', type: MarkdownToHtmlTask) {
            description = "Convert all markdown files to html files"
            include "**/*.md"
            outputDir = convertedMarkdown
            source allDocumentationDirs
        }

        project.task('copyImagesAllRequirements', type: FileCopyTask) {
            description = "Copy images into the requirements output"
            include "**/Images/**"
            outputDir = allRequirementsOutput
            source allDocumentationDirs
        }

        project.task('copyImagesPreviewRequirements', type: FileCopyTask) {
            description = "Copy images into the requirements preview output"
            include "**/Images/**"
            outputDir = previewRequirementsOutput
            source allDocumentationDirs
        }

        project.task('copyImagesUserRequirementsOnly', type: FileCopyTask) {
            description = "Copy images into the user requirements output"
            include "**/Images/**"
            outputDir = userRequirementsOnlyOutput
            source userRequirementsDir
        }

        project.task('navigationUserRequirements', type: NavigationHtmlTask) {
            description = "Create navigation page and navigation links"
            include "**/layout.yaml"
            exclude "DesignRequirements/**"
            linkOutputDir = links
            navigationOutputDir = userRequirementsOnlyOutput
            documentSourceDirs = [userRequirementsDir]
            source designRequirementsDir, userRequirementsDir
        }

        project.task('navigationAllRequirements', type: NavigationHtmlTask) {
            description = "Create navigation page and navigation links"
            include "**/layout.yaml"
            linkOutputDir = links
            navigationOutputDir = allRequirementsOutput
            documentSourceDirs = [designRequirementsDir, userRequirementsDir]
            source designRequirementsDir, userRequirementsDir
        }

        project.task('navigationPreviewRequirements', type: NavigationHtmlTask) {
            description = "Create navigation page and navigation links"
            include "**/layout.yaml"
            linkOutputDir = links
            navigationOutputDir = previewRequirementsOutput
            documentSourceDirs = [designRequirementsDir, userRequirementsDir]
            source designRequirementsDir, userRequirementsDir
        }

        project.task('assembleUserRequirementsOnly', type: AssembleDocumentTask,
                dependsOn: ['markdownToHtml', 'navigationUserRequirements', 'copyImagesUserRequirementsOnly']) {
            description = "Assemble User Requirements, with no test or status reports."
            outputDir = userRequirementsOnlyOutput
            linkDir = links
            convertedMarkdownDir = convertedMarkdown
            source links, convertedMarkdown
            include "**/section.html"
            exclude "DesignRequirements/**"
            previewEnabled = false
        }

        project.task('assembleRequirements', type: AssembleDocumentTask,
                dependsOn: ['markdownToHtml', 'navigationAllRequirements', 'copyImagesAllRequirements']) {
            description = "Assemble User and Design requirements, along with tests and status reports."
            outputDir = allRequirementsOutput
            linkDir = links
            convertedMarkdownDir = convertedMarkdown
            source links, convertedMarkdown
            include "**/section.html"
            previewEnabled = false
        }

        project.task('previewRequirements', type: AssembleDocumentTask,
                dependsOn: ['markdownToHtml', 'navigationPreviewRequirements', 'copyImagesPreviewRequirements']) {
            description = "Same as assemble requirements, but with browser auto-refresh enabled.  Intended to be" +
                    "used in conjunction with Gradle's continuous build option (-t) to allow a live preview to be shown" +
                    "whenever the source markdown is edited."
            outputDir = previewRequirementsOutput
            linkDir = links
            convertedMarkdownDir = convertedMarkdown
            source links, convertedMarkdown
            include "**/section.html"
            previewEnabled = true
        }

        project.tasks.each {
            it.group = "DocumentationPlugin"
        }
    }
}