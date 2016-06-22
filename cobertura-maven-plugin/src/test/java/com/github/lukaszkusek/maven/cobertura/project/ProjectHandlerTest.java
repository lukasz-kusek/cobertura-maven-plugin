package com.github.lukaszkusek.maven.cobertura.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProjectHandlerTest {

    private static final String COBERTURA_GROUP_ID = "net.sourceforge.cobertura";
    private static final String COBERTURA_ARTIFACT_ID = "cobertura-runtime";

    @Captor
    private ArgumentCaptor<Set<Artifact>> artifactsCaptor;

    @Mock
    private MavenProject project;

    @Mock
    private Artifact coberturaArtifact;

    @Mock
    private RepositorySystem repositorySystem;

    private ProjectHandler projectHandler;

    @Before
    public void setup() {
        projectHandler = new ProjectHandler(project, ImmutableList.of(coberturaArtifact), repositorySystem);
        given(project.getBuild()).willReturn(mock(Build.class));
    }

    @Test
    public void shouldAddCoberturaToTestClasspath() throws MojoExecutionException {
        // given
        Artifact dependencyArtifact = prepareDependencyArtifacts();

        String version = "VERSION";
        String type = "TYPE";
        prepareCoberturaArtifact(version, type);

        Artifact coberturaArtifactWithScopeProvided = prepareRepositorySystem(version, type);

        // when
        projectHandler.addCoberturaArtifactToTestClasspath();

        // then
        assertThat(captureDependencyArtifacts()).containsOnly(dependencyArtifact, coberturaArtifactWithScopeProvided);
    }

    private Set<Artifact> captureDependencyArtifacts() {
        verify(project).setDependencyArtifacts(artifactsCaptor.capture());

        return artifactsCaptor.getValue();
    }

    private Artifact prepareDependencyArtifacts() {
        Artifact dependencyArtifact = mock(Artifact.class);
        given(project.getDependencyArtifacts()).willReturn(ImmutableSet.of(dependencyArtifact));

        return dependencyArtifact;
    }

    private Artifact prepareRepositorySystem(String version, String type) {
        Artifact coberturaArtifactWithScopeProvided = mock(Artifact.class);

        given(repositorySystem.createArtifact(
                COBERTURA_GROUP_ID, COBERTURA_ARTIFACT_ID, version, Artifact.SCOPE_PROVIDED, type)
        ).willReturn(coberturaArtifactWithScopeProvided);

        return coberturaArtifactWithScopeProvided;
    }

    private void prepareCoberturaArtifact(String version, String type) {
        given(coberturaArtifact.getGroupId()).willReturn(COBERTURA_GROUP_ID);
        given(coberturaArtifact.getArtifactId()).willReturn(COBERTURA_ARTIFACT_ID);
        given(coberturaArtifact.getVersion()).willReturn(version);
        given(coberturaArtifact.getType()).willReturn(type);
    }

    @Test
    public void shouldSetOutputDirectory() {
        // given
        String outputDirectory = "OUTPUT";

        // when
        projectHandler.setOutputDirectory(outputDirectory);

        // then
        verify(project.getBuild()).setOutputDirectory(outputDirectory);
    }

    @Test
    public void shouldReturnSourceDirectories() {
        // given
        @SuppressWarnings("unchecked")
        List<String> compileSourceRoots = mock(List.class);
        given(project.getCompileSourceRoots()).willReturn(compileSourceRoots);

        // when
        List<String> sourceDirectories = projectHandler.getSourcesDirectories();

        // then
        assertThat(sourceDirectories).isSameAs(compileSourceRoots);
    }
}
