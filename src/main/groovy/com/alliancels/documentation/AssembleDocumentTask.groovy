package com.alliancels.documentation

import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

/**
 * Final task for creating a document.
 * (This task must be chained to other subtasks that generate the output.)
 */
class AssembleDocumentTask extends SourceTask {

    @Input @OutputDirectory
    File outputDir

    @Input
    File linkDir

    @Input
    File convertedMarkdownDir

    @Input
    boolean previewEnabled

    @TaskAction
    void execute(IncrementalTaskInputs inputs) throws GradleException {

        if (!inputs.incremental) {

        }

        inputs.outOfDate {

            def relativePath = getRelativePath(it.file)

            def links = new File(linkDir, relativePath).getText('UTF-8')
            def content = new File(convertedMarkdownDir, relativePath).getText('UTF-8')

            // Structure each section like this:
            // <nav links>
            // <content>
            // <nav links>
            File outputFile = getOutputFile(it.file)
            String relativeOutputFilePath = outputDir.toPath().relativize(outputFile.toPath()).toString()
            def completeSection = HtmlPresenter.createSectionContentPage(relativeOutputFilePath,
                    links + content + links, previewEnabled)
            outputFile.parentFile.mkdirs()
            outputFile.createNewFile()
            outputFile.write(completeSection)
        }

        inputs.removed {
            File outputFile = getOutputFile(it.file)

            if (outputFile.exists()) {
                outputFile.delete()
            }
        }
    }

    File getOutputFile(File inputFile) {

        def relativePath = getRelativePath(inputFile)
        File outputPath = new File (outputDir, relativePath).parentFile
        outputPath.mkdirs()
        return new File(outputPath, 'section.html')
    }

    String getRelativePath(File file) {

        def rootPath

        if (file.toPath().startsWith(linkDir.toPath())) {
            rootPath = linkDir
        } else {
            rootPath = convertedMarkdownDir
        }

        return rootPath.toPath().relativize(file.toPath()).toString()
    }
}