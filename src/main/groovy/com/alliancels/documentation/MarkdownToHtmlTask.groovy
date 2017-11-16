package com.alliancels.documentation

import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension
import com.vladsch.flexmark.ext.definition.DefinitionExtension
import com.vladsch.flexmark.ext.toc.TocExtension
import com.vladsch.flexmark.ext.typographic.TypographicExtension
import com.vladsch.flexmark.parser.ParserEmulationProfile
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import com.vladsch.flexmark.ast.Node
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet

/**
 * Task for converting markdown files into html files.
 */
class MarkdownToHtmlTask extends SourceTask {

    static Parser parser
    static HtmlRenderer renderer

    @Input @OutputDirectory @Optional
    File outputDir

    @TaskAction
    void execute(IncrementalTaskInputs inputs) throws GradleException {

        if (!inputs.incremental) {
            project.delete(outputDir.listFiles())
        }

        inputs.outOfDate {

            File outputFile = getOutputFile(it.file)
            outputFile = convertMarkdownFileExtensionToHtml(outputFile)
            outputFile.parentFile.mkdirs()
            outputFile.createNewFile()
            outputFile.setBytes(convertToHtml(it.file).getBytes("UTF-8"))
        }

        inputs.removed {
            File outputFile = getOutputFile(it.file)

            if (outputFile.exists()) {
                outputFile.delete()
            }
        }
    }

    static String convertToHtml(File file) {
        MutableDataSet options = new MutableDataSet()
        options.setFrom(ParserEmulationProfile.GITHUB_DOC)
        options.set(Parser.EXTENSIONS, Arrays.asList(
                AbbreviationExtension.create(),
                DefinitionExtension.create(),
                StrikethroughExtension.create(),
                TablesExtension.create(),
                TocExtension.create(),
                TypographicExtension.create()
        ))



        // Re-use parser and renderer instances, if possible
        if (parser == null) {
            parser = Parser.builder(options).build()
        }
        if (renderer == null) {
            renderer = HtmlRenderer.builder(options).build()
        }

        Node document = parser.parse(file.getText('UTF-8'))
        return renderer.render(document)
    }

    File convertMarkdownFileExtensionToHtml(File file) {
        return new File(file.toString().replaceAll('.md', '.html'))
    }

    File getOutputFile(File inputFile) {
        String relativePath = getRelativePath(inputFile.parentFile, project.projectDir)
        File outputPath = new File (outputDir, relativePath)
        outputPath.mkdirs()
        return new File(outputPath, inputFile.name)
    }

    String getRelativePath(File file, File root) {
        return root.toPath().relativize(file.toPath()).toString()
    }
}