package com.alliancels.documentation

import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction


/**
 * Task for generating navigation page and section previous/next links.
 */
class NavigationHtmlTask extends SourceTask {

    @Input @OutputDirectory
    File linkOutputDir

    @Input @OutputDirectory
    File navigationOutputDir

    @Input
    List<String> documentSourceDirs

    List<Section> sections

    private HtmlPresenter htmlPresenter = new HtmlPresenter()

    @TaskAction
    void exec() throws GradleException {

        sections = []

        source.each {
            sections.add((new LayoutParser()).createSection(it))
        }

        createNextPreviousLinkFiles()

        sections = []

        source.each {
            sections.add((new LayoutParser()).createSection(it))
        }
        createNavigationPage()
    }

    void createNextPreviousLinkFiles() {

        sections.each {

            String navigationLinkHtml = ''

            Section previousGroup = it.getPrevious(sections)
            Section nextGroup = it.getNext(sections)

            // Get link HTML
            // Do not show Previous link if the first section
            // Do not show Next link if the last section
            if (previousGroup != null && nextGroup != null) {
                String previousLink = getRelativePath(previousGroup.folder, it.folder)
                String nextLink = getRelativePath(nextGroup.folder, it.folder)
                previousLink += '/section.html'
                nextLink += '/section.html'
                navigationLinkHtml = HtmlPresenter.getPreviousNextLinks(previousLink, nextLink)
            } else if (previousGroup == null && nextGroup != null) {
                String nextLink = getRelativePath(nextGroup.folder, it.folder)
                nextLink += '/section.html'
                navigationLinkHtml = HtmlPresenter.getNextLink(nextLink)
            } else if (previousGroup != null && nextGroup == null) {
                String previousLink = getRelativePath(previousGroup.folder, it.folder)
                previousLink += '/section.html'
                navigationLinkHtml = HtmlPresenter.getPreviousLink(previousLink)
            } else {
                GradleException("No next or previous section was found for " + it.folder.toString())
            }

            // Create html file with links
            File outputFile = getLinkOutputFile(it.folder)
            outputFile.createNewFile()
            outputFile.write(navigationLinkHtml)
        }
    }

    void createNavigationPage() {

        def navigationPaneContents = ''

        documentSourceDirs.each {
            def rootSection = Section.findSection(sections,
                    new File(project.projectDir, it))
            navigationPaneContents += createNavigationNodes(rootSection)
        }

        def extensions = (DocumentationPluginExtension) getProject()
                .getExtensions().findByName("documentation")

        def navigationPage = htmlPresenter.createNavigationPage(extensions.projectName, extensions.version, extensions.date, navigationPaneContents)
        navigationOutputDir.mkdirs()
        def outputFile = new File(navigationOutputDir, 'navigation.html')
        outputFile.createNewFile()
        outputFile.write(navigationPage)
    }

    String createNavigationNodes(Section section) {

        String navigationPaneContents = ''

        String relativePath = getRelativePath(section.folder, project.projectDir)

        String link = relativePath + "/section.html"
        navigationPaneContents += htmlPresenter.getNavigationNodeStart() +
                htmlPresenter.getNavigationLink(link, section.name)

        Section nextGroup = section.getNext(sections)

        // If not the last group
        if (nextGroup != null) {
            // If the next group is a child of the current group, get the next group
            if (nextGroup.getParent(sections) == section) {
                navigationPaneContents += createNavigationNodes(nextGroup)
                navigationPaneContents += htmlPresenter.getNavigationNodeEnd()
            // If the next group is not a child of the current group
            } else {
                int depthChange = getDepthChange(section.folder, nextGroup.folder)
                navigationPaneContents += getNavigationNodeEnds(depthChange + 1)
                navigationPaneContents += createNavigationNodes(nextGroup)
            }
        }
        // If the last group, end any nodes not ended yet
        else {
            int depthChange = getDepthChange(section.folder, project.projectDir)
            navigationPaneContents += getNavigationNodeEnds(depthChange - 1)
        }

        return navigationPaneContents
    }

    File getLinkOutputFile(File inputFolder) {
        String relativePath = getRelativePath(inputFolder, project.projectDir)
        File outputPath = new File (linkOutputDir, relativePath)
        outputPath.mkdirs()
        return new File(outputPath, 'section.html')
    }

    String getRelativePath(File file, File root) {
        String relativePath = root.toPath().relativize(file.toPath()).toString()
        String relativePathCrossPlatform = relativePath.replace("\\", "/")
        return relativePathCrossPlatform
    }

    int getDepth(File file, File root) {
        return getRelativePath(file, root).split('/').size()
    }

    int getDepthChange(File current, File next) {
        int change = getDepth(current, project.projectDir) -
                getDepth(next, project.projectDir)
        return change
    }

    String getNavigationNodeEnds(int count) {

        String nodeEnds = ''

        while (count > 0) {
            count--
            nodeEnds += htmlPresenter.getNavigationNodeEnd()
        }

        return nodeEnds
    }
}
