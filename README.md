## Overview

Gradle plugin for helping with writing project requirements, tests plans for those requirements, and status reports for those test plans.

## Usage

### Gradle build setup

In the project gradle.build file, the Documentation library will need to be available to the build script:

	buildscript {
	    repositories {
	        maven {
	            url 'https://github.com/alliancels/maven-repo/raw/releases'
	        }
	    }
	    dependencies {
	        classpath 'com.alliancels:documentation-gradle-plugins:0.0.1'
	        // Optionally add gitlib to allow retrieving repo info
	        classpath 'com.alliancels:gitlib:0.0.1'
	    }
	}

Then the plugin will need to be applied.

	import com.alliancels.documentation.DocumentationPlugin
	apply plugin: DocumentationPlugin

Finally, the plugin will need to be configured for the project:

	// Optionally use gitlib to retrieve the version and date, rather than manually
	// setting them
	import com.alliancels.gitlib.*
	
	documentation {
	    projectName = 'Midas Requirements'
	    version = JGitGradle.getVersionTag(project) ?: "uncontrolled"
	    date = JGitGradle.getDateTime(project)
	}

### Requirements Source Folders

The following folders are expected to exist within the root of any Gradle project this plugin is applied to:

	UserRequirements
	DesignRequirements
	
### Requirements Section Folders

Under the top-level requirements folders, a hierarchy of subfolders for each requirements "section" can be created.

	UserRequirements\
		layout.yaml
		section.md
		Section1\
			layout.yaml
			section.md
			Images\
			Section1-1\
				layout.yaml
				section.md
				Images\
		Section2\
			section.md

#### requirements.md

Each folder must contain a markdown file containing the section contents.

Links to other sections should use relative paths, and should use the *.html suffix rather than
the *.md suffix.

UserRequirements\Section1\Section1-1\requirements.md example:

	[link to Section2](..\..\Section2\requirements.html)

#### layout.yaml

Each folder must contain a file describing the layout of the section.  This file must provide
a proper name for the section, otherwise the folder name will be used.

UserRequirements\Section1\layout.yaml content example:

	name: Section One
	
This file can optionally also specify the ordering of all of its immediate subfolders (subsections).
If this field is not defined, the sections will be ordered alphabetically based on their folder
name.  If only some of the subsections are listed, they will be ordered below the parent section first,
followed by all remaining subsections in alphabetical order based on their folder name.

UserRequirements\layout.yaml content example:

	orderedSubfolders:
	  - Section2
	  - Section1

#### Images\

This folder is optional.  All files in this folder will be copied into the output.  Links to images
from markdown should be relative.

requirements.md example:

	![example image](Images\example.png)
	
### Build Documentation

#### Assemble all requirements

To assemble user and design requirements, invoke the following Gradle command:

	gradlew assembleRequirements
	
Or abbreviate with:
	
	gradlew aR

The output will be in the <project>\build\requirements folder.

The root file that should be opened is called navigation.html.

#### Assemble only user requirements

To assemble only the user requirements, invoke the following Gradle command, which excludes the design
requirements from the output:

	gradlew assembleUserRequirementsOnly
	
Or abbreviate with:

	gradlew aURO

### Assemble only test requirements

To assemble only the test requirements, invoke the following Gradle command, which excludes the design
requirements from the output:

	gradlew assembleTestRequirementsOnly
	
Or abbreviate with:

	gradlew aTRO

#### Assemble all documentation with live preview

Manually invoking a rebuild of the HTML site after making a change is tedius.  To avoid this, it can
be auto-rebuilt whenver a requirements source file is saved.  To allow this type of "live preview" of
the generated HTML site, enable automatic continuous rebuild and enable browser
auto-refresh of the section HTML with the following command:

	gradlew assemblePreview --continuous

Or abbreviate with:

	gradlew aP -t

Changes to existing markdown files will be visible shortly after saving.  Changes to the the folder
structure of layout.yaml files will require the browser to be manually refreshed.

#### Assemble all documentation without live preview

Same as the preview section, but without browser auto-refresh disabled.

	gradlew assembleAll

Or abbreviate:

	gradlew aA

# Build

The project can be rebuilt by invoking the `gradlew build` command.

# Release new versions

Clone [the maven repo](https://github.com/alliancels/maven-repo) to the same root directory as this repo.

    Example:
        \repos\documentation-gradle-plugins
        \repos\maven-repo

Push new version to this project's repo.

Publish library by invoking the `gradlew publish` command.  This will install the library, source, and doc jars into the
local maven repo.

Commit and push changes to the maven repo.