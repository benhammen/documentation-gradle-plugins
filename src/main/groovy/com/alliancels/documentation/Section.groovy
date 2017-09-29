package com.alliancels.documentation

import groovy.transform.AutoClone

/**
 * Properties of a document section.
 */
@AutoClone
class Section {
    String name
    File folder
    List<String> subsectionFolderNames = []

    Section(String name, File folder) {
        this.name = name
        this.folder = folder
    }

    boolean isParent() {
        return !this.subsectionFolderNames.isEmpty()
    }

    static Section findSection(List<Section> sections, File folder) {

        Section section

        sections.any {
            if (it.folder == folder) {
                section = it
                return true
            }
        }

        return section
    }

    Section getParent(List<Section> sections) {

        Section section

        sections.any {
            if (this.folder.parentFile == it.folder) {
                section = it
                return true
            }
        }

        return section
    }

    Section getNext(List<Section> sections) {

        Section nextSection

        // If it has children, the first child will be the next one
        if (this.isParent()) {
            File firstChild = new File(this.folder, this.subsectionFolderNames.first())
            nextSection = findSection(sections, firstChild)
        } else {
            // If it has a parent
            Section parent = getParent(sections)
            if (parent != null) {
                // If this is the last group under the parent, find the parent's next group without re-entering its
                // subgroups
                if (this.folder.name == parent.subsectionFolderNames.last()) {
                    def parentGroupWithoutChildren = parent.clone()
                    parentGroupWithoutChildren.subsectionFolderNames = []
                    nextSection = parentGroupWithoutChildren.getNext(sections)
                // If not the last subgroup under the parent, get the next one
                } else {
                    Integer currentIndex = getSubSectionIndex(parent, this.folder.name)
                    Integer nextIndex = currentIndex + 1
                    String folderName = parent.subsectionFolderNames[nextIndex]
                    File nextSubfolder = new File(parent.folder, folderName)
                    nextSection = findSection(sections, nextSubfolder)
                }
            }
            // Else null; this was the last folder in the depth-first folder structure
        }

        return nextSection
    }

    Section getPrevious(List<Section> sections) {

        Section previousSection

        // If it has a parent
        Section parent = getParent(sections)
        if (parent != null) {
            // If this is the first group under the parent
            if (this.folder.name == parent.subsectionFolderNames.first()) {
                previousSection = parent
            // If not the first group under the parent, get the deepest, last subgroup of the previous group
            } else {
                Integer currentIndex = getSubSectionIndex(parent, this.folder.name)
                Integer previousIndex = currentIndex - 1
                String folderName = parent.subsectionFolderNames[previousIndex]
                File previousSubfolder = new File(parent.folder, folderName)
                def previousSubfolderGroup = findSection(sections, previousSubfolder)
                previousSection = previousSubfolderGroup.getDeepestLastChild(sections)
            }
        }
        // Else null; this was the top-level folder

        return previousSection
    }

    Section getDeepestLastChild(List<Section> sections) {

        if (this.isParent()) {
            def lastSubfolderName = this.subsectionFolderNames.last()
            def lastSubfolder = new File(this.folder, lastSubfolderName)
            def lastGroup = findSection(sections, lastSubfolder)
            return lastGroup.getDeepestLastChild(sections)
        } else {
            return this
        }
    }

    File getSubsectionFolderFile(String folder) {
        new File(this.folder, folder)
    }

    static int getSubSectionIndex(Section section, subfolderName) {

        Integer index

        section.subsectionFolderNames.eachWithIndex{ String entry, int i ->
            if (entry == subfolderName) {
                index = i
            }
        }

        return index
    }
}