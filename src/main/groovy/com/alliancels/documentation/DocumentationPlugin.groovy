package com.alliancels.documentation

import org.gradle.api.Project
import org.gradle.api.Plugin

class DocumentationPlugin implements Plugin<Project> {

    File convertedMarkdown
    File links
    File documentationOutput

    String userRequirementsDir
    String designRequirementsDir
    String testRequirementsDir
    String developerDocumentationDir
    List<String> allRequirementsDirs
    List<String> allDocumentationDirs

    void apply(Project project) {

        userRequirementsDir = 'UserRequirements'
        designRequirementsDir = 'DesignRequirements'
        developerDocumentationDir = 'DeveloperGuide'
        testRequirementsDir = 'TestRequirements'

        allRequirementsDirs = [userRequirementsDir, designRequirementsDir, testRequirementsDir]
        allDocumentationDirs = [*allRequirementsDirs, developerDocumentationDir]

        documentationOutput = new File(project.buildDir, 'documentation/')
        convertedMarkdown = new File(documentationOutput, 'convertedMarkdown')
        links = new File(documentationOutput, 'links')

        project.extensions.create('documentation', DocumentationPluginExtension.class)

        project.task('markdownToHtml', type: MarkdownToHtmlTask) {
            description = "Convert all markdown files to html files"
            include "**/*.md"
            outputDir = convertedMarkdown
            source allDocumentationDirs
        }

        createTasksUserRequirementsOnly(project)
        createTasksTestRequirementsOnly(project)
        createTasksRequirements(project)
        createTasksPreview(project)

        project.tasks.each {
            it.group = "DocumentationPlugin"
        }
    }

    void createTasksRequirements(Project project) {

        def allRequirementsOutput = new File(documentationOutput, 'allRequirements/')

        project.task('copyImagesAllRequirements', type: FileCopyTask) {
            description = "Copy images into the requirements output."
            include "**/Images/**"
            outputDir = allRequirementsOutput
            source allRequirementsDirs
        }

        project.task('navigationAllRequirements', type: NavigationHtmlTask) {
            description = "Create navigation page and navigation links."
            include "**/layout.yaml"
            linkOutputDir = links
            navigationOutputDir = allRequirementsOutput
            documentSourceDirs = allRequirementsDirs
            source allRequirementsDirs
        }

        project.task('assembleRequirements', type: AssembleDocumentTask,
                dependsOn: ['markdownToHtml', 'navigationAllRequirements', 'copyImagesAllRequirements']) {
            description = "Assemble User and Design requirements, along with tests and status reports."
            outputDir = allRequirementsOutput
            linkDir = links
            convertedMarkdownDir = convertedMarkdown
            source links, convertedMarkdown
            include "DesignRequirements/**/section.html"
            include "UserRequirements/**/section.html"
            include "TestRequirements/**/section.html"
            previewEnabled = false
        }
    }

    void createTasksUserRequirementsOnly(Project project) {

        def userRequirementsOnlyOutput = new File(documentationOutput, 'userRequirementsOnly/')

        project.task('copyImagesUserRequirementsOnly', type: FileCopyTask) {
            description = "Copy images into the user requirements output."
            include "**/Images/**"
            outputDir = userRequirementsOnlyOutput
            source userRequirementsDir
        }

        project.task('navigationUserRequirementsOnly', type: NavigationHtmlTask) {
            description = "Create navigation page and navigation links."
            include "**/layout.yaml"
            linkOutputDir = links
            navigationOutputDir = userRequirementsOnlyOutput
            documentSourceDirs = [userRequirementsDir]
            source userRequirementsDir
        }

        project.task('assembleUserRequirementsOnly', type: AssembleDocumentTask,
                dependsOn: ['markdownToHtml', 'navigationUserRequirementsOnly', 'copyImagesUserRequirementsOnly']) {
            description = "Assemble User Requirements, with no test or status reports."
            outputDir = userRequirementsOnlyOutput
            linkDir = links
            convertedMarkdownDir = convertedMarkdown
            source links, convertedMarkdown
            include "UserRequirements/**/section.html"
            previewEnabled = false
        }
    }

    void createTasksTestRequirementsOnly(Project project) {

        def testRequirementsOnlyOutput = new File(documentationOutput, 'testRequirementsOnly/')

        project.task('copyImagesTestRequirementsOnly', type: FileCopyTask) {
            description = "Copy images into the functional test requirements output."
            include "**/Images/**"
            outputDir = testRequirementsOnlyOutput
            source testRequirementsDir
        }

        project.task('navigationTestRequirementsOnly', type: NavigationHtmlTask) {
            description = "Create navigation page and navigation links."
            include "**/layout.yaml"
            linkOutputDir = links
            navigationOutputDir = testRequirementsOnlyOutput
            documentSourceDirs = [testRequirementsDir]
            source testRequirementsDir
        }

        project.task('assembleTestRequirementsOnly', type: AssembleDocumentTask,
                dependsOn: ['markdownToHtml', 'navigationTestRequirementsOnly', 'copyImagesTestRequirementsOnly']) {
            description = "Assemble Product Functional Test Requirements, with no test or status reports."
            outputDir = testRequirementsOnlyOutput
            linkDir = links
            convertedMarkdownDir = convertedMarkdown
            source links, convertedMarkdown
            include "TestRequirements/**/section.html"
            previewEnabled = false
        }
    }

    void createTasksPreview(Project project) {

        def previewOutput = new File(documentationOutput, 'previewAll/')

        project.task('copyImagesPreview', type: FileCopyTask) {
            description = "Copy images into the requirements preview output"
            include "**/Images/**"
            outputDir = previewOutput
            source allDocumentationDirs
        }
        project.task('navigationPreview', type: NavigationHtmlTask) {
            description = "Create navigation page and navigation links"
            include "**/layout.yaml"
            linkOutputDir = links
            navigationOutputDir = previewOutput
            documentSourceDirs = allDocumentationDirs
            source allDocumentationDirs
        }
        project.task('assemblePreview', type: AssembleDocumentTask,
                dependsOn: ['markdownToHtml', 'navigationPreview', 'copyImagesPreview']) {
            description = "Same as assemble requirements, but with browser auto-refresh enabled.  Intended to be" +
                    "used in conjunction with Gradle's continuous build option (-t) to allow a live preview to be shown" +
                    "whenever the source markdown is edited."
            outputDir = previewOutput
            linkDir = links
            convertedMarkdownDir = convertedMarkdown
            source links, convertedMarkdown
            include "**/section.html"
            previewEnabled = true
        }
    }
}