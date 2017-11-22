# Replace hardcoded HTML layout with user-defined templates

Use a template engine to populate a user-defined template with fields provided by the documentation generator.

This allows full control over the presentation of the generated HTML site.

# Add site-wide text search

One or both of the following search approaches could be added:

## Approach 1

Type keyword(s) in a search box.  Find all instances in the HTML site, show count, and use arrows to step forward and
backwards through all instances.

## Approach 2

Type the keyword(s) in a search box.  List links to all pages with "hits", like a web search engine.

Use an offline javascript search engine, rather than recreating one: http://elasticlunr.com/.

## Notes

Make use of parallelization when searching:
https://stackoverflow.com/questions/20348012/jquery-how-should-i-search-multiple-html-files-for-results-in-realtime

## Prerequisites

- [Add developer support for Javascript](design.md#add-developer-spport-for-javascript)

# Notify user of inadequate browser

Check the browser type and version.  If it doesn't meet the requirements, show a message instructing what browsers can
be used.

# Automatically convert *.md links to *.html links

Right now, the *.html suffix must be hardcoded into the markdown source, in order for links to work in the generated
HTML.  This prevents links from working when viewing the markdown files on Github.

# Automatically check for missing images and broken links in the generated HTML site
This Gradle plug-in should be able to do this: https://github.com/aim42/htmlSanityCheck

# Testable documentation

## Place manual test plans alongside requirements
Create documentation that describes how a user can document a plan for manual testing in a file called
manualTestPlan.md that exists alongside any requirements.md file.  Also, describe how testing can be recorded in an
easily-parsable file called manualTestLog.yaml.

YAML Input Log
```
products:                       (List of products the test plan applies to)
	- name: H7S_Dryer	        (Name of product)
	  version: 0.0.1			(Last version tested on)
	  tested by: Ben Hammen		(Tester)
	  date: 2017-08-07			(Last date tested)
	  fail:						(Link to item or items in issue tracker; assume last test passed if unpopulated)
	  							(Any notes/comments should be recorded in the issue tracker, not here)
	-name: MGD_WX
	.
	.
	.
```

## Summarize manual test plan results
Generate a report of all tests that are not passing. Also, programmatically check current version versus last checked
version and report all that were not tested this version. Further group them into pre-determined categories so critical
items can be viewed separately from the non-critical items.

Provide a way to generate a "status" version of each section that include both the test plan and test status that
is related to that section.

Provide a way to bundle all failing tests and tests that weren't run into a single high-level report.

Test report
```
Not tested this version:

	Show stopper:

	ApplicationChecksum.md
	Bootloader.md

	Nice to have:

	none

Failed this version:

	Show stopper:

	none

	Nice to have:

	none
```

## Place automated tests alongside requirements
Allow high-level, end-to-end application test code to be placed in each documentation section's folder.

Elaborate (hard-to-maintain) build configurations should be avoided for this type of testing.  For deeper
testing, or software component testing, rely on unit and integration tests.  These tests should mimic the manual test
procedures that would be done if no automated tests were available.

## Summarize automated test plan results
Generate a report of all tests that are not passing.  Since the build will fail if a test fails, all tests that aren't
passing are expected to be ignored, with a link to an issue tracker item in the ignore message.

Provide a way to generate a "status" version of each section that include links to the test cases (but not the
full source), as well as the test status for those tests.

Provide a way to bundle all ignored tests and tests that weren't run into a single high-level report.

## Generate boilerplate code for automated tests
Parse all Unity test cases, and auto-generate the boilerplate code that adds test cases to test groups, and test groups to
the test runner.  This will avoid the need to do this manually, and ensure that all test cases are actually being run.

## Add support for automatically copying an existing section

Sometimes, there may be a desire to make one section match another.  For example, there may be two parent sections that
cover similar topics.  Some of their subsections will differ, but some might be identical.  Rather than duplicate the source
of the identical subsections, the user can put no section.md file in a section folder, and instead use the layout.yaml file to
specify another section's markdown file to use.

The tool then creates a second instance of the converted markdown file.