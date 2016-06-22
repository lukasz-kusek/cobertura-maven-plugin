/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Lukasz Kusek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.lukaszkusek.maven.cobertura.project;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableSet;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

public class ProjectHandler {

    public static final String COBERTURA_ARTIFACT_ID = "net.sourceforge.cobertura:cobertura-runtime";
    private final MavenProject project;
    private final List<Artifact> pluginArtifacts;
    private final RepositorySystem repositorySystem;

    public ProjectHandler(MavenProject project, List<Artifact> pluginArtifacts, RepositorySystem repositorySystem) {
        this.project = project;
        this.pluginArtifacts = pluginArtifacts;
        this.repositorySystem = repositorySystem;
    }

    public void addCoberturaArtifactToTestClasspath() throws MojoExecutionException {
        Artifact coberturaArtifact = getCoberturaArtifact();

        addArtifactToTestClasspath(coberturaArtifact);
    }

    private Artifact getCoberturaArtifact() throws MojoExecutionException {
        Map<String, Artifact> pluginArtifacts = ArtifactUtils.artifactMapByVersionlessId(this.pluginArtifacts);
        Artifact coberturaArtifact = pluginArtifacts.get(COBERTURA_ARTIFACT_ID);

        if (coberturaArtifact == null) {
            throw new MojoExecutionException(
                    "Couldn't find '" + COBERTURA_ARTIFACT_ID + "' artifact in plugin dependencies.");
        }
        return coberturaArtifact;
    }

    private void addArtifactToTestClasspath(Artifact coberturaArtifact) {
        project.setDependencyArtifacts(
                ImmutableSet.<Artifact>builder()
                        .addAll(project.getDependencyArtifacts())
                        .add(withProvidedScope(coberturaArtifact)).build());
    }

    private Artifact withProvidedScope(Artifact artifact) {
        return repositorySystem.createArtifact(
                artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getVersion(),
                Artifact.SCOPE_PROVIDED,
                artifact.getType());
    }

    public void setOutputDirectory(String outputDirectory) {
        project.getBuild().setOutputDirectory(outputDirectory);
    }

    public List<String> getSourcesDirectories() {
        return project.getCompileSourceRoots();
    }
}
