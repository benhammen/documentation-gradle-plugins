package com.alliancels.documentation

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonCreator

/**
 * YAML-parseable object.
 * If explicit ordering of subfolders is not provided, they will be alphabetically ordered by their path name.
 */
class SectionLayout {

    String name
    List<String> orderedSubfolders

    @JsonCreator
    SectionLayout(@JsonProperty(value = 'name', required=true) String name,
                  @JsonProperty(value = 'orderedSubfolders', required=false) List<String> orderedSubfolders) {
        this.name = name
        this.orderedSubfolders = orderedSubfolders
    }
}
