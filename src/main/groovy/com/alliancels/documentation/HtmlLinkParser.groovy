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
}