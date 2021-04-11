package com.github.jokerpper.mavenprojectversion.util;

import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomParent;
import org.jetbrains.idea.maven.model.MavenId;

import java.util.List;

public class MatchUtils {

    public static boolean isMatchProject(String rootProjectGroupId, List<String> projectAllArtifactIdList, MavenId mavenId) {
        return StringUtils.equals(rootProjectGroupId, StringUtils.trim(mavenId.getGroupId())) &&
                projectAllArtifactIdList.contains(StringUtils.trim(mavenId.getArtifactId()));
    }

    public static boolean isMatchProjectParent(String rootProjectGroupId, List<String> projectAllArtifactIdList, MavenDomParent mavenDomParent) {
        return rootProjectGroupId.equals(StringUtils.trim(mavenDomParent.getGroupId().getRawText())) &&
                projectAllArtifactIdList.contains(StringUtils.trim(mavenDomParent.getArtifactId().getRawText()));
    }

    public static boolean isMatchMavenDomDependency(String rootProjectGroupId, List<String> projectAllArtifactIdList, MavenDomDependency domDependency) {
        return rootProjectGroupId.equals(StringUtils.trim(domDependency.getGroupId().getRawText())) && projectAllArtifactIdList.contains(StringUtils.trim(domDependency.getArtifactId().getRawText()));
    }
}
