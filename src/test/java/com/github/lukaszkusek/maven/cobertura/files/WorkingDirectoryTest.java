package com.github.lukaszkusek.maven.cobertura.files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;

import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WorkingDirectoryTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private MavenProject project;

    private WorkingDirectory workingDirectory;

    @Before
    public void setup() throws IOException {
        setupProject();
        setupWorkingDirectory();
    }

    private void setupProject() throws IOException {
        File buildDirectory = temporaryFolder.newFolder();

        Build build = mock(Build.class);
        given(build.getOutputDirectory()).willReturn(new File(buildDirectory, "output").getAbsolutePath());
        given(build.getDirectory()).willReturn(new File(buildDirectory, "base").getAbsolutePath());

        given(project.getBuild()).willReturn(build);
    }

    private void setupWorkingDirectory() {
        workingDirectory = new WorkingDirectory(project);
    }

    @Test
    public void shouldCreateDirectoriesIfDoesNotExist() {
        // given

        // when
        workingDirectory.createDirectories();

        // then
        assertThat(new File(project.getBuild().getDirectory())).exists();
        assertThat(new File(project.getBuild().getOutputDirectory())).exists();
    }

    @Test
    public void shouldExecuteNormallyIfDirectoriesAlreadyExist() {
        // given
        new File(project.getBuild().getDirectory()).mkdirs();
        new File(project.getBuild().getOutputDirectory()).mkdirs();

        // when
        workingDirectory.createDirectories();

        // then
        assertThat(new File(project.getBuild().getDirectory())).exists();
        assertThat(new File(project.getBuild().getOutputDirectory())).exists();
    }

    @Test
    public void shouldReturnCorrectOutputDirectory() {
        // given
        WorkingDirectory workingDirectory = new WorkingDirectory(project);

        // when
        File outputDirectory = workingDirectory.getOutputDirectory();
        String outputDirectoryPath = workingDirectory.getOutputDirectoryPath();

        // then
        assertThat(outputDirectory.getAbsolutePath()).isEqualTo(outputDirectoryPath);
        assertThat(outputDirectory).isEqualTo(new File(project.getBuild().getOutputDirectory()));
    }

    @Test
    public void shouldReturnCorrectCoberturaDirectory() {
        // given

        // when
        File coberturaDirectory = workingDirectory.getCoberturaDirectory();
        String coberturaDirectoryPath = workingDirectory.getCoberturaDirectoryPath();

        // then
        assertThat(coberturaDirectory.getAbsolutePath()).isEqualTo(coberturaDirectoryPath);
        assertThat(coberturaDirectory).isEqualTo(
                new File(project.getBuild().getDirectory(), WorkingDirectory.COBERTURA_DIRECTORY_NAME));
    }

    @Test
    public void shouldReturnCorrectInstrumentedClassesDirectory() {
        // given

        // when
        File instrumentedClassesDirectory = workingDirectory.getInstrumentedClassesDirectory();
        String instrumentedClassesDiretoryPath = workingDirectory.getInstrumentedClassesDirectoryPath();

        // then
        assertThat(instrumentedClassesDirectory.getAbsolutePath()).isEqualTo(instrumentedClassesDiretoryPath);
        assertThat(instrumentedClassesDirectory).isEqualTo(
                new File(workingDirectory.getCoberturaDirectory(), WorkingDirectory.INSTRUMENTED_CLASSES_DIRECTORY_NAME));
    }

    @Test
    public void shouldReturnCorrectDataFile() {
        // given

        // when
        File dataFile = workingDirectory.getDataFile();
        String dataFilePath = workingDirectory.getDataFilePath();

        // then
        assertThat(dataFile.getAbsolutePath()).isEqualTo(dataFilePath);
        assertThat(dataFile).isEqualTo(
                new File(workingDirectory.getCoberturaDirectory(), WorkingDirectory.DATA_FILE_NAME));
    }
}
