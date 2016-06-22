package com.github.lukaszkusek.maven.cobertura.properties;

import static com.github.lukaszkusek.maven.cobertura.properties.PropertiesFile.COBERTURA_DATA_FILE_KEY;
import static com.github.lukaszkusek.maven.cobertura.properties.PropertiesFile.ORIGINAL_OUTPUT_DIRECTORY_KEY;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PropertiesFileTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldSaveEmptyCoberturaPropertiesFile() throws MojoExecutionException, IOException {
        // given
        File coberturaPropertiesFileDirectory = temporaryFolder.newFolder();

        // when
        PropertiesFile.locatedIn(coberturaPropertiesFileDirectory)
                .create()
                .saveToDisk();

        // then
        assertThat(loadProperties(coberturaPropertiesFileDirectory)).isEmpty();
    }

    @Test
    public void shouldSaveCoberturaPropertiesFileWithGivenEntries() throws MojoExecutionException, IOException {
        // given
        File coberturaPropertiesFileDirectory = temporaryFolder.newFolder();
        String dataFilePath = "DATA FILE PATH";
        String originalOutputDirectoryPath = "ORIGINAL OUTPUT DIRECTORY PATH";

        // when
        PropertiesFile.locatedIn(coberturaPropertiesFileDirectory)
                .create()
                .withDataFilePath(dataFilePath)
                .withOriginalOutputDirectoryPath(originalOutputDirectoryPath)
                .saveToDisk();

        // then
        Properties coberturaProperties = loadProperties(coberturaPropertiesFileDirectory);

        assertThat(coberturaProperties).hasSize(2);
        assertThat(coberturaProperties.getProperty(COBERTURA_DATA_FILE_KEY)).isEqualTo(dataFilePath);
        assertThat(coberturaProperties.getProperty(ORIGINAL_OUTPUT_DIRECTORY_KEY)).isEqualTo(originalOutputDirectoryPath);
    }

    private Properties loadProperties(File coberturaPropertiesFileDirectory) throws IOException {
        File coberturaPropertiesFile = new File(coberturaPropertiesFileDirectory, PropertiesFile.NAME);
        Properties coberturaProperties = new Properties();

        try (FileInputStream fis = new FileInputStream(coberturaPropertiesFile)) {
            coberturaProperties.load(fis);
        }

        return coberturaProperties;
    }

    @Test
    public void shouldLoadPropertiesFromFile() throws IOException, MojoExecutionException {
        // given
        File coberturaPropertiesFileDirectory = temporaryFolder.newFolder();
        String dataFilePath = "DATA FILE PATH";
        String originalOutputDirectoryPath = "ORIGINAL OUTPUT DIRECTORY PATH";

        createCoberturaPropertiesFile(
                coberturaPropertiesFileDirectory, createProperties(dataFilePath, originalOutputDirectoryPath));

        // when
        LoadedPropertiesFile loadedPropertiesFile = PropertiesFile.locatedIn(coberturaPropertiesFileDirectory).load();

        // then
        assertThat(loadedPropertiesFile.getDataFilePath()).isEqualTo(dataFilePath);
        assertThat(loadedPropertiesFile.getOriginalOutputDirectoryPath()).isEqualTo(originalOutputDirectoryPath);
    }

    @Test
    public void shouldLoadPropertiesFromEmptyFile() throws IOException, MojoExecutionException {
        // given
        File coberturaPropertiesFileDirectory = temporaryFolder.newFolder();

        createCoberturaPropertiesFile(coberturaPropertiesFileDirectory, createProperties(null, null));

        // when
        LoadedPropertiesFile loadedPropertiesFile = PropertiesFile.locatedIn(coberturaPropertiesFileDirectory).load();

        // then
        assertThat(loadedPropertiesFile.getDataFilePath()).isNull();
        assertThat(loadedPropertiesFile.getOriginalOutputDirectoryPath()).isNull();
    }

    private Properties createProperties(String dataFilePath, String originalOutputDirectoryPath) {
        Properties coberturaProperties = new Properties();

        if (dataFilePath != null) {
            coberturaProperties.setProperty(COBERTURA_DATA_FILE_KEY, dataFilePath);
        }

        if (originalOutputDirectoryPath != null) {
            coberturaProperties.setProperty(ORIGINAL_OUTPUT_DIRECTORY_KEY, originalOutputDirectoryPath);
        }

        return coberturaProperties;
    }

    private void createCoberturaPropertiesFile(File coberturaPropertiesFileDirectory, Properties coberturaProperties)
            throws MojoExecutionException, IOException {

        File coberturaPropertiesFile = new File(coberturaPropertiesFileDirectory, PropertiesFile.NAME);

        try (FileOutputStream fos = new FileOutputStream(coberturaPropertiesFile)) {
            coberturaProperties.store(fos, "");
        }
    }
}
