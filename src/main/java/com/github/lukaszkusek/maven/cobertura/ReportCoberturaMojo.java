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
import net.sourceforge.cobertura.dsl.ReportFormat;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "report", defaultPhase = LifecyclePhase.TEST)
public class ReportCoberturaMojo extends AbstractCoberturaMojo {

    @Parameter(defaultValue = "xml")
    private String format;

    @Parameter(defaultValue = "UTF-8")
    private String encoding;

    @Override
    protected boolean canRun() {
        return workingDirectory.getCoberturaDirectory().exists();
    }

    @Override
    protected void doExecute() throws MojoExecutionException {
        exportReport();
        restoreOriginalOutputDirectory();
    }

    private void exportReport() {
        new Cobertura(createArgumentsFromParameters())
                .report()
                .export(ReportFormat.getFromString(format));
    }

    private Arguments createArgumentsFromParameters() {
        ArgumentsBuilder builder = new ArgumentsBuilder();

        builder.setBaseDirectory(workingDirectory.getInstrumentedClassesDirectoryPath());
        builder.setDataFile(workingDirectory.getDataFilePath());
        builder.setDestinationDirectory(workingDirectory.getCoberturaDirectoryPath());
        builder.setEncoding(encoding);

        addAllCompileSourceRootsAsSources(builder);

        return builder.build();
    }

    private void addAllCompileSourceRootsAsSources(ArgumentsBuilder builder) {
        for (String sourceDirectory : projectHandler.getSourcesDirectories()) {
            builder.addSources(sourceDirectory, true);
        }
    }

    private void restoreOriginalOutputDirectory() throws MojoExecutionException {
        projectHandler.setOutputDirectory(
                PropertiesFile.locatedIn(workingDirectory.getInstrumentedClassesDirectory())
                        .load()
                        .getOriginalOutputDirectoryPath());
    }
}
