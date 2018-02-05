package com.alliancels.documentation

/**
 * HTML presentation via templates.
 */
class HtmlPresenter {

    String navigationPaneContents

    HtmlPresenter() {
        navigationPaneContents = ""
    }

    String createNavigationPage(String projectName, String version, String date, String navigationPaneContents) {

    return """\
    <!DOCTYPE html>
    <html>
        <head>
            <title>
                ${projectName}
            </title>
            <style>

                * {
                    margin: 0;
                    padding: 0;
                    border: 0;
                    outline: 0;
                    vertical-align: baseline;
                    background: transparent;
                }

                html, body {
                    height: 100%;
                    width: 100%;
                    overflow: hidden;
                    margin: 0px;
                }

                #container {
                    width: 100%;
                    height: 100%;
                }

                #nav {
                    position: relative;
                    width: 20%;
                    height: 90%;
                    float: left;
                    overflow: auto;
                    border-style: solid;
                    border-width: 1px;
                }

                #content {
                    position: relative;
                    width: 79%;
                    height: 90%;
                    float: left;
                    overflow: hidden;
                }

                header {
                    background-color:lightgrey;
                    width: 100%;
                    height: 10%;
                    margin: 0px;
                    clear: both;
                }

                iframe {
                    height: 100%;
                    width: 100%;
                }

            </style>
        </head>
        <div id="container">
            <header>
                <h1>
                    ${projectName}
                </h1>
                    Version ${version}, ${date}
            </header>
            <div id="nav">
                <a href="#/" id="expAll" class="exp">EXPAND/COLLAPSE ALL</a>
                <script type="text/javascript">
                    // Reference the toggle link
                    var xa = document.getElementById('expAll');

                    // Register link on click event
                    xa.addEventListener('click', function(e) 
                    {
                        // Toggle the two classes that represent "state" determined when link is clicked
                        e.target.classList.toggle('exp');
                        e.target.classList.toggle('col');

                        // Collect all <details> into a NodeList
                        var details = document.querySelectorAll('details');

                        // Convert NodeList into an array then iterate through it...
                        Array.from(details).forEach(function(obj, idx) 
                        {
                            // If the link has the class .exp, make each <detail>'s  attribute false
                            if (e.target.classList.contains('exp')) 
                            {
                                obj.open = false;
                            } 
                            // Otherwise make it true 
                            else 
                            {
                                obj.open = true;
                            }
                        });
                    }, false);
                </script>
                ${navigationPaneContents}
            </div>
            <div id="content">
                <iframe name="sectionFrame"></iframe>
            </div>
        </div>
    </html>
    """
    }

    static String createSectionContentPage(String pathHtml, String contentsHtml, boolean metaRefreshEnable) {

        String metaRefresh = ''

        if (metaRefreshEnable) {
            metaRefresh = """<meta http-equiv="refresh" content="1" />"""
        }

        return """\
        <!DOCTYPE html>
        <html>
            <meta charset="UTF-8">
            <!-- Make browser continually refresh page content, so updates can be seen without a manual or server-side refresh -->
                ${metaRefresh}
            <style>
            header {
                top:0px;
                position: fixed;
                color: black;
                width: 100%;
                height: 5%;
            }
            body {
                margin-top:5%;
            }
            table {
                border-collapse: collapse;
            }
            table, th, td {
                border: 1px solid black;
            }
            </style>
            <header>
                ${pathHtml}
            </header>
            <body>
                ${contentsHtml}
            </body>
        </html>
        """
    }

    static String getNavigationLink(String link, String description) {

        return """\
                <summary>
                <a href="${link}" target="sectionFrame">${description}</a>
                </summary>
            """
    }

    static String getPreviousNextLinks(String previousLink, String nextLink) {
        """
        <div style="text-align:center">
        <a href="${previousLink}">Previous</a> <a href="${nextLink}">Next</a>
        </div>
        """
    }

    static String getPreviousLink(String previousLink) {
        """
        <div style="text-align:center">
        <a href="${previousLink}">Previous</a>
        </div>
        """
    }

    static String getNextLink(String nextLink) {
        """
        <div style="text-align:center">
        <a href="${nextLink}">Next</a>
        </div>
        """
    }

    String getNavigationNodeStart() {
        return '<details style="margin-left: 20px">'
    }

    String getNavigationNodeEnd() {
        return "</details>"
    }
}