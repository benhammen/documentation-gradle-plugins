package com.alliancels.documentation

import spock.lang.Specification

class HtmlLinkParserTest extends Specification {

    def setup() {

    }

    def "finds <a href=... and image links in html file"() {
        when:
        File html = new File("src/test/resources/section.html")
        then:
        HtmlLinkParser.getLinks(html) == ["../path/to/section1.html", "../path/to/section2.html",
                                          "../path/to/section3.html", "../path/to/section4.html",
                                          "/path/to/image.png"]
    }

    def "finds heading id's in html file"() {
        when:
        File html = new File("src/test/resources/section.html")
        then:
        HtmlLinkParser.getHeadingIds(html) == ["heading-1", "heading-2"]
    }

    def "retrieve path from a link containing an anchor"() {
        when:
        String link = "../path/to/section1.html#heading-1"
        then:
        HtmlLinkParser.getLinkPath(link) == "../path/to/section1.html"
    }

    def "retrieve path from a link that doesn't contain an anchor"() {
        when:
        String link = "../path/to/section1.html"
        then:
        HtmlLinkParser.getLinkPath(link) == "../path/to/section1.html"
    }

    def "retrieve anchor from a link containing an anchor"() {
        when:
        String link = "../path/to/section1.html#heading-1"
        then:
        HtmlLinkParser.getLinkAnchor(link) == "heading-1"
    }

    def "returns null if retrieving an anchor from a link that doesn't contain an anchor"() {
        when:
        String link = "../path/to/section1.html"
        then:
        HtmlLinkParser.getLinkAnchor(link) == null
    }
}