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

package com.github.lukaszkusek.maven.cobertura.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;

public class PropertiesFile implements PropertiesFileBuilder, NewPropertiesFileBuilder, LoadedPropertiesFile {

    public static final String NAME = "cobertura.properties";
    public static final String ORIGINAL_OUTPUT_DIRECTORY_KEY = "com.github.lukaszkusek.maven.cobertura.originalOutputDirectory";
    public static final String COBERTURA_DATA_FILE_KEY = "net.sourceforge.cobertura.datafile";

    private Properties properties;
    private File coberturaPropertiesFile;

    private PropertiesFile(File coberturaPropertiesFileDirectory) {
        this.coberturaPropertiesFile = new File(coberturaPropertiesFileDirectory, NAME);
        this.properties = new Properties();
    }

    public static PropertiesFileBuilder locatedIn(File coberturaPropertiesFileDirectory) {
        return new PropertiesFile(coberturaPropertiesFileDirectory);
    }

    @Override
    public String getDataFilePath() {
        return properties.getProperty(COBERTURA_DATA_FILE_KEY);
    }

    @Override
    public String getOriginalOutputDirectoryPath() {
        return properties.getProperty(ORIGINAL_OUTPUT_DIRECTORY_KEY);
    }

    @Override
    public NewPropertiesFileBuilder withDataFilePath(String dataFilePath) {
        properties.setProperty(COBERTURA_DATA_FILE_KEY, dataFilePath);

        return this;
    }

    @Override
    public NewPropertiesFileBuilder withOriginalOutputDirectoryPath(String originalOutputDirectoryPath) {
        properties.setProperty(ORIGINAL_OUTPUT_DIRECTORY_KEY, originalOutputDirectoryPath);

        return this;
    }

    @Override
    public LoadedPropertiesFile saveToDisk() throws MojoExecutionException {
        createCoberturaPropertiesFile();

        return this;
    }

    private void createCoberturaPropertiesFile() throws MojoExecutionException {
        try (FileOutputStream fos = new FileOutputStream(coberturaPropertiesFile)) {
            properties.store(fos, "Genereted by " + PropertiesFile.class.getName());
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to write " + NAME + " file.", e);
        }
    }

    @Override
    public NewPropertiesFileBuilder create() {
        return this;
    }

    @Override
    public LoadedPropertiesFile load() throws MojoExecutionException {
        loadCoberturaPropertiesFile();

        return this;
    }

    private void loadCoberturaPropertiesFile() throws MojoExecutionException {
        try (FileInputStream fis = new FileInputStream(coberturaPropertiesFile)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to read " + NAME + " file.", e);
        }
    }
}
