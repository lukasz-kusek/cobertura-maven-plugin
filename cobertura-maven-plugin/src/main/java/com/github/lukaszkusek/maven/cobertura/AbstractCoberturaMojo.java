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

package com.github.lukaszkusek.maven.cobertura;

import com.github.lukaszkusek.maven.cobertura.files.WorkingDirectory;
import com.github.lukaszkusek.maven.cobertura.project.ProjectHandler;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

import java.util.List;

public abstract class AbstractCoberturaMojo extends AbstractMojo {

    @Component
    private RepositorySystem repositorySystem;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${plugin.artifacts}", required = true, readonly = true)
    private List<Artifact> pluginArtifacts;

    protected WorkingDirectory workingDirectory;
    protected ProjectHandler projectHandler;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        prepareWorkingDirectory();

        if (canRun()) {
            try {
                createWorkingDirectory();
                createProjectHandler();

                doExecute();
            } catch (Throwable throwable) {
                throw new MojoExecutionException(
                        String.format("Failed while running cobertura: %s", throwable.getMessage()),
                        throwable);
            }
        }
    }

    private void prepareWorkingDirectory() {
        this.workingDirectory = new WorkingDirectory(project);
    }

    private void createWorkingDirectory() {
        this.workingDirectory.createDirectories();
    }

    private void createProjectHandler() {
        this.projectHandler = new ProjectHandler(project, pluginArtifacts, repositorySystem);
    }

    protected abstract void doExecute() throws Throwable;
    protected abstract boolean canRun();
}
