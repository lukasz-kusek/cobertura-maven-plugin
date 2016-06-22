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

import com.github.lukaszkusek.maven.cobertura.properties.PropertiesFile;
import net.sourceforge.cobertura.dsl.Arguments;
import net.sourceforge.cobertura.dsl.ArgumentsBuilder;
import net.sourceforge.cobertura.dsl.Cobertura;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

import java.io.IOException;

@Mojo(name = "instrument", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class InstrumentCoberturaMojo extends AbstractCoberturaMojo {

    @Parameter
    private String ignoreRegex;

    @Parameter
    private String ignoreMethodAnnotation;

    @Parameter(defaultValue = "false")
    private boolean ignoreTrivial;

    @Parameter(defaultValue = "**/*.class")
    private String includeClassesRegex;

    @Parameter
    private String excludeClassesRegex;

    @Parameter(defaultValue = "false")
    private boolean failOnError;

    @Parameter(defaultValue = "false")
    private boolean threadsafeRigorous;

    @Override
    protected boolean canRun() {
        return workingDirectory.getOutputDirectory().exists();
    }

    @Override
    protected void doExecute() throws Throwable {
        copyAllClassesFromOutputDirectoryToInstrumentedClassesDirectory();
        instrumentCode();
        addCoberturaArtifactToTestClasspath();
        createCoberturaPropertiesFile();
        replaceOutputDirectoryWithInstrumentedDirectory();
    }

    private void copyAllClassesFromOutputDirectoryToInstrumentedClassesDirectory() throws MojoExecutionException {
        try {
            FileUtils.copyDirectoryStructure(
                    workingDirectory.getOutputDirectory(), workingDirectory.getInstrumentedClassesDirectory());
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to prepare instrumentation directory.", e);
        }
    }

    private void instrumentCode() throws Throwable {
        new Cobertura(createArgumentsFromParameters()).instrumentCode();
    }

    private Arguments createArgumentsFromParameters() {
        ArgumentsBuilder builder = new ArgumentsBuilder();

        builder.setBaseDirectory(workingDirectory.getOutputDirectoryPath());
        builder.setDataFile(workingDirectory.getDataFilePath());
        builder.setDestinationDirectory(workingDirectory.getInstrumentedClassesDirectoryPath());

        if (ignoreRegex != null) {
            builder.addIgnoreRegex(ignoreRegex);
        }
        builder.addIgnoreMethodAnnotation(ignoreMethodAnnotation);
        builder.ignoreTrivial(ignoreTrivial);

        if (includeClassesRegex != null) {
            builder.addIncludeClassesRegex(includeClassesRegex);
        }

        if (excludeClassesRegex != null) {
            builder.addExcludeClassesRegex(excludeClassesRegex);
        }
        builder.failOnError(failOnError);
        builder.threadsafeRigorous(threadsafeRigorous);

        for (String file : getFilesToInstrument()) {
            builder.addFileToInstrument(file);
        }

        return builder.build();
    }

    private String[] getFilesToInstrument() {
        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir(workingDirectory.getOutputDirectory());
        scanner.scan();

        return scanner.getIncludedFiles();
    }

    private void addCoberturaArtifactToTestClasspath() throws MojoExecutionException {
        projectHandler.addCoberturaArtifactToTestClasspath();
    }

    private void createCoberturaPropertiesFile() throws MojoExecutionException {
        PropertiesFile.locatedIn(workingDirectory.getInstrumentedClassesDirectory())
                .create()
                .withDataFilePath(workingDirectory.getDataFilePath())
                .withOriginalOutputDirectoryPath(workingDirectory.getOutputDirectoryPath())
                .saveToDisk();
    }

    public void replaceOutputDirectoryWithInstrumentedDirectory() {
        projectHandler.setOutputDirectory(workingDirectory.getInstrumentedClassesDirectoryPath());
    }
}
