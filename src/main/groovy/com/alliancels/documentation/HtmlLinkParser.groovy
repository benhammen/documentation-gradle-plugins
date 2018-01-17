package com.alliancels.documentation

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * Parse for HTML linked-related elements.
 */
class HtmlLinkParser {

    static List<String> getLinks(File htmlFile) {
        Document document = Jsoup.parse(htmlFile, "UTF-8")

        // Get all links
        Elements links = document.select("a[href]")
        // Filter the actual link/path
        List<String> linksList = links.eachAttr("href")

        // Get all images
        Elements images = document.getElementsByTag("img")
        // Filter the image source/path
        linksList += images.eachAttr("src")

        return linksList
    }

    static List<String> getHeadingIds(File htmlFile) {
        Document document = Jsoup.parse(htmlFile, "UTF-8")

        // Get all headings
        Elements links = document.select("h1, h2, h3, h4, h5, h6")
        // Filter ID's
        List<String> headingIdList = links.eachAttr("id")

        return headingIdList
    }

    static boolean hasAnchor(String link) {

        if (link.contains("#"))
            return true
        else
            return false
    }

    static String getLinkPath(String link) {

        String path = link.split('#').first()

        if (path == "") {
            return null
        }
        else {
            return path
        }
    }

    static String getLinkAnchor(String link) {
        if (!hasAnchor(link))
            return null
        else
            return link.split('#').last()
    }

    static boolean isLinkExternal(String link) {
        if (link.contains("http"))
            return true
    }
}