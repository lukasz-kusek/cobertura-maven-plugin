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

package com.github.lukaszkusek.maven.cobertura.files;

import java.io.File;

import org.apache.maven.project.MavenProject;

public class WorkingDirectory {

    public static final String COBERTURA_DIRECTORY_NAME = "cobertura";
    public static final String INSTRUMENTED_CLASSES_DIRECTORY_NAME = "instrumented";
    public static final String DATA_FILE_NAME = "cobertura.ser";

    private final File outputDirectory;
    private final File coberturaDirectory;
    private final File instrumentedClassesDirectory;
    private final File dataFile;

    public WorkingDirectory(MavenProject project) {
        this.outputDirectory = new File(project.getBuild().getOutputDirectory());
        this.coberturaDirectory = new File(project.getBuild().getDirectory(), COBERTURA_DIRECTORY_NAME);
        this.instrumentedClassesDirectory = new File(coberturaDirectory, INSTRUMENTED_CLASSES_DIRECTORY_NAME);
        this.dataFile = new File(coberturaDirectory, DATA_FILE_NAME);
    }

    public void createDirectories() {
        createDirectoryIfDoesNotExist(outputDirectory);
        createDirectoryIfDoesNotExist(coberturaDirectory);
        createDirectoryIfDoesNotExist(instrumentedClassesDirectory);
    }

    private void createDirectoryIfDoesNotExist(File directory) {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Cannot create directory: " + directory.getAbsolutePath());
        }
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public String getOutputDirectoryPath() {
        return outputDirectory.getAbsolutePath();
    }

    public File getCoberturaDirectory() {
        return coberturaDirectory;
    }

    public String getCoberturaDirectoryPath() {
        return coberturaDirectory.getAbsolutePath();
    }

    public File getInstrumentedClassesDirectory() {
        return instrumentedClassesDirectory;
    }

    public String getInstrumentedClassesDirectoryPath() {
        return instrumentedClassesDirectory.getAbsolutePath();
    }

    public File getDataFile() {
        return dataFile;
    }

    public String getDataFilePath() {
        return dataFile.getAbsolutePath();
    }
}
