package com.github.jokerpper.mavenprojectversion.util;

import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomParent;
import org.jetbrains.idea.maven.model.MavenId;

import java.util.List;

public class MatchUtils {

    /**
     * 获取是否为rootProject所匹配的project
     *
     * @param rootProjectGroupId
     * @param rootProjectAllArtifactIdList
     * @param mavenId
     * @return
     */
    public static boolean isMatchProject(String rootProjectGroupId, List<String> rootProjectAllArtifactIdList, MavenId mavenId) {
        return StringUtils.equals(rootProjectGroupId, StringUtils.trim(mavenId.getGroupId())) &&
                rootProjectAllArtifactIdList.contains(StringUtils.trim(mavenId.getArtifactId()));
    }

    public static boolean isMatchProjectParent(String rootProjectGroupId, List<String> rootProjectAllArtifactIdList, MavenDomParent mavenDomParent) {
        return rootProjectGroupId.equals(StringUtils.trim(mavenDomParent.getGroupId().getRawText())) &&
                rootProjectAllArtifactIdList.contains(StringUtils.trim(mavenDomParent.getArtifactId().getRawText()));
    }

    public static boolean isMatchMavenDomDependency(String rootProjectGroupId, List<String> rootProjectAllArtifactIdList, MavenDomDependency domDependency) {
        return rootProjectGroupId.equals(StringUtils.trim(domDependency.getGroupId().getRawText())) && rootProjectAllArtifactIdList.contains(StringUtils.trim(domDependency.getArtifactId().getRawText()));
    }
}
