package com.alliancels.documentation

import spock.lang.Specification

class SectionTest extends Specification {

    List<Section> Sections = []

    def group1_file = new File('group1')
    def group1 = new Section('group1', group1_file)
    def group1_1_file = new File(group1_file, 'group1_1')
    def group1_1 = new Section('group1_1', group1_1_file)
    def group1_1_1_file = new File(group1_1_file, 'group1_1_1')
    def group1_1_1 = new Section('group1_1_1', group1_1_1_file)
    def group1_1_2_file = new File(group1_1_file, 'group1_1_2')
    def group1_1_2 = new Section('group1_1_2', group1_1_2_file)
    def group1_2_file = new File(group1_file, 'group1_2')
    def group1_2 = new Section('group1_2', group1_2_file)

    def setup() {
        // Create a flat list describing the following requirements group hierarchy:
        // group1
        //   group1_1
        //     group1_1_1
        //     group1_1_2
        //   group 1_2

        group1.subsectionFolderNames = ['group1_1', 'group1_2']
        group1_1.subsectionFolderNames = ['group1_1_1', 'group1_1_2']

        Sections.add(group1)
        Sections.add(group1_1)
        Sections.add(group1_1_1)
        Sections.add(group1_1_2)
        Sections.add(group1_2)
    }

    def "retrieves group, given list of groups and the target group file"() {
        when:
        def Section1 = Section.findSection(Sections, group1_file)
        def Section1_1 = Section.findSection(Sections, group1_1_file)
        def Section1_1_1 = Section.findSection(Sections, group1_1_1_file)
        def Section1_1_2 = Section.findSection(Sections, group1_1_2_file)
        def Section1_2 = Section.findSection(Sections, group1_2_file)

        then:
        group1 == Section1
        group1_1 == Section1_1
        group1_1_1 == Section1_1_1
        group1_1_2 == Section1_1_2
        group1_2 == Section1_2
    }

    def "retrieves parent group, given list of all groups"() {
        when:
        def group1Parent = group1.getParent(Sections)
        def group1_1Parent = group1_1.getParent(Sections)
        def group1_1_1Parent = group1_1_1.getParent(Sections)
        def group1_1_2Parent = group1_1_2.getParent(Sections)
        def group1_2Parent = group1_2.getParent(Sections)


        then:
        group1Parent == null
        group1_1Parent == group1
        group1_1_1Parent == group1_1
        group1_1_2Parent == group1_1
        group1_2Parent == group1
    }

    def "retrieves next group, given list of all groups"() {
        when:
        def group1Next = group1.getNext(Sections)
        def group1_1Next = group1_1.getNext(Sections)
        def group1_1_1Next = group1_1_1.getNext(Sections)
        def group1_1_2Next = group1_1_2.getNext(Sections)
        def group1_2Next = group1_2.getNext(Sections)

        then:
        group1Next == group1_1
        group1_1Next == group1_1_1
        group1_1_1Next == group1_1_2
        group1_1_2Next == group1_2
        group1_2Next == null
    }

    def "retrieves previous group, given list of all groups"() {
        when:
        def group1Previous = group1.getPrevious(Sections)
        def group1_1Previous = group1_1.getPrevious(Sections)
        def group1_1_1Previous = group1_1_1.getPrevious(Sections)
        def group1_1_2Previous = group1_1_2.getPrevious(Sections)
        def group1_2Previous = group1_2.getPrevious(Sections)

        then:
        group1Previous == null

        group1_1Previous == group1
        group1_1_1Previous == group1_1
        group1_1_2Previous == group1_1_1
        group1_2Previous == group1_1_2
    }
}
