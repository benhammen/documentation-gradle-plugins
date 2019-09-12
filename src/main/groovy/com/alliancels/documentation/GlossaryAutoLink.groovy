package com.alliancels.documentation

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


/**
 * Automatically find and replace glossary terms with glossary links
 */
class GlossaryAutoLink {
    
    static void autoLinkGlossary(List<Section> sectionList, File buildDirectory, File sourceDirectory) {

        List<Section> glossarySectionList = findGlossarySections(sectionList)
        if(glossarySectionList.size() > 0)
        {
            println("Glossary found!")
            
            //
            createLinkAndTermLists(sectionList, glossarySectionList, buildDirectory, sourceDirectory)
        }
        else
        {
            println("Glossary not found!")
        }
    }

    static List<Section> findGlossarySections(List<Section> sectionList) {
    
        List<Section> glossarySectionList = []
        
        sectionList.each {
            if(it.name == "Glossary")
            {
                glossarySectionList.add(it)
            }
        }
        
        if(glossarySectionList.size() > 0)
        {
            println("Number of glossaries: " + glossarySectionList.size())
        }
        
        return glossarySectionList
    }
    
    static void createLinkAndTermLists(List<Section> sectionList, List<Section> glossarySectionList, File buildDirectory, File sourceDirectory) {
    
        println("Creating glossary term list...")
        
        List<String> listOfTerms = []
        List<String> listOfLinks = []
        
        glossarySectionList.each {
            File glossaryFile = it.folder
            println("Gls: " + glossaryFile)
            println("Src: " + sourceDirectory)
            println("Dst: " + buildDirectory)
            
            String glsString = glossaryFile.toString()
            String srcString = sourceDirectory.toString()
            String dstString = buildDirectory.toString()
            
            println("glsString: " + glsString)
            println("srcString: " + srcString)
            println("dstString: " + dstString)
            
            String glossaryFileString = glsString - srcString
            println("Glossary file postfix: " + glossaryFileString) 
            
            glossaryFileString = dstString + "/documentation/All" + glossaryFileString
            println("Glossary file: " + glossaryFileString)
            
            glossaryFileString = glossaryFileString + "/Glossary.html"
            println("Glossary file complete: " + glossaryFileString)
            
            glossaryFile = new File(glossaryFileString)
            println(glossaryFile)
        
            String glossaryFileText = glossaryFile.getText()
            
            Document document = Jsoup.parse(glossaryFileText, "UTF-8")
            
            //Get values in first column
            Elements terms = document.select("td:eq(0)").select("td")
            Elements links = document.select("td:eq(1)").select("td")
            println(terms)
            println(links)
            
            listOfTerms += terms.eachText()
            listOfLinks += links.eachText()
            println(listOfTerms)
            println(listOfLinks)
        }
        
        //autoLinkGlossaries(sectionList, listOfTerms, listOfLinks)
        
        
        println("++++++++++++++++++++++++++++++++++++++")
        println("++++++++++++++++++++++++++++++++++++++")
        println("++++++++++++++++++++++++++++++++++++++")
        println("======================================")
        println("======================================")
        println("======================================")
        
        
        
        //FUNCTION IN LINE
        println("Auto linking terms...")
        
        sectionList.each {
            File eachHypertextFile = it.folder
            println("Hyp: " + eachHypertextFile)
            println("Src: " + sourceDirectory)
            println("Dst: " + buildDirectory)
            
            String hypString = eachHypertextFile.toString()
            String srcString = sourceDirectory.toString()
            String dstString = buildDirectory.toString()
            
            println("hypString: " + hypString)
            println("srcString: " + srcString)
            println("dstString: " + dstString)
            
            String eachHypertextString = hypString - srcString
            println("Hypertext file postfix: " + eachHypertextString) 
            
            eachHypertextString = dstString + "/documentation/All" + eachHypertextString
            println("Hypertext file: " + eachHypertextString)
            
            //eachHypertextString = eachHypertextString + "/section.html"
            //println("Hypertext file complete: " + eachHypertextString)
            
            eachHypertextFile = new File(eachHypertextString)
            println("Each hypertext file: " + eachHypertextFile)
            
            boolean testIsFile = eachHypertextFile.isFile()
            println("File? " + testIsFile)
            println("Folder? " + !testIsFile)
            
            println(eachHypertextFile.listFiles())
            
            //TODO:
            //-Find the html file in the folder resolved above
            //-Find and replace each term with each link in each html file
        }
    }
    
    static void autoLinkGlossaries(List<Section> sectionList, List<String> listOfTerms, List<String> listOfLinks) {

        println("Auto linking terms...")
        
        //sectionList.each {
        //    println(it.folder)
            
            
        //}
    }
}
