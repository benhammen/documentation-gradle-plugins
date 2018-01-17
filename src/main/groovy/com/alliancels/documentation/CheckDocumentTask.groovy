package com.alliancels.documentation

import org.gradle.api.GradleException
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

/**
 * Task for checking an assembled document for problems.
 */
class CheckDocumentTask extends SourceTask {

    @TaskAction
    void exec() throws GradleException {

        Set<File> sourceFiles = getSource().getFiles()

        // Check links in each source file
        sourceFiles.each { File file ->
            checkLinks(file)
        }
    }

    void checkLinks(File file) {

        HtmlLinkParser.getLinks(file).each { link ->

            String linkPath = HtmlLinkParser.getLinkPath(link)
            String linkAnchor = HtmlLinkParser.getLinkAnchor(link)

            if (HtmlLinkParser.isLinkExternal(link)) {

            } else if (linkPath == null) {
                // Check links to anchor within same file
                if (!HtmlLinkParser.getHeadingIds(file).contains(linkAnchor)) {
                    throw new GradleException("Broken link in $file.  Target anchor $linkAnchor in same file not found.")
                }
            } else {
                // Check link to other file
                File targetFile = new File(file.parentFile, linkPath)
                if (!targetFile.exists()) {
                    throw new GradleException("Broken link in $file. Target file $targetFile not found.")
                // Check link to anchors in other file
                } else if (linkAnchor != null) {
                    if (!HtmlLinkParser.getHeadingIds(targetFile).contains(linkAnchor)) {
                        throw new GradleException("Broken link in $file.  Target anchor $linkAnchor in target file $targetFile " +
                                "not found.")
                    }
                }
            }
        }
    }
}
