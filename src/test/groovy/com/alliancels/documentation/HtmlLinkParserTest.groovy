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
}