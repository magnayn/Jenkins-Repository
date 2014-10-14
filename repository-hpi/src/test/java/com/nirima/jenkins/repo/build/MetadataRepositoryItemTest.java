package com.nirima.jenkins.repo.build;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.reporters.MavenArtifact;
import hudson.model.Run;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.hamcrest.object.HasToString;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
@RunWith(MockitoJUnitRunner.class)
public class MetadataRepositoryItemTest {
    private Calendar refDate;
    private MavenModule job = mock(MavenModule.class);
    @Rule
    public TemporaryFolder folder = new TemporaryFolder(new File("target"));

    @Before
    public void defineRefDate() {
        refDate = Calendar.getInstance();
        refDate.setTimeInMillis(61373977200000L);
    }

    @Before
    public void stubJob() throws IOException {
        job = mock(MavenModule.class);
        when(job.getBuildDir()).thenReturn(folder.newFolder("repoItemTest"));
    }

    @Test
    public void generateContent_oneEntry_asExpected() {
        //arrange
        final MavenBuild build = new MavenBuild(job, refDate);
        final MavenArtifact artifact = new MavenArtifact("FOO", "BAR", "0.0.1-SNAPSHOT", "", "pom", "BAZ", "");
        final MetadataRepositoryItem testee = new MetadataRepositoryItem(build, artifact);
        final MavenArtifact artifactTest = new MavenArtifact("FOO", "BAR", "0.0.1-SNAPSHOT", "test", "pom", "BAZ", "");
        final ArtifactRepositoryItem artifactRepositoryItem = mock(ArtifactRepositoryItem.class);
        when(artifactRepositoryItem.getBuild()).thenReturn(build);
        when(artifactRepositoryItem.getLastModified()).thenReturn(new Date(refDate.getTimeInMillis() + 8000));
        testee.addArtifact(artifactTest, artifactRepositoryItem);
        //act
        final String generateContent = testee.generateContent();
        //assert
        final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
            "<metadata modelVersion=\"1.1.0\">\n" + //
            "  <groupId>FOO</groupId>\n" + //
            "  <artifactId>BAR</artifactId>\n" + //
            "  <version>0.0.1-SNAPSHOT</version>\n" + //
            "  <versioning>\n" + //
            "    <snapshotVersions>\n" + //
            "      <snapshotVersion>\n" + //
            "        <classifier>test</classifier>\n" + //
            "        <extension>pom</extension>\n" + //
            "        <value>0.0.1-39141113.000000-0</value>\n" + //
            "        <updated>39141113000008</updated>\n" + //
            "      </snapshotVersion>\n" + //
            "    </snapshotVersions>\n" + //
            "  </versioning>\n" + //
            "</metadata>\n";
        assertEquals(expected, generateContent);
    }

    @Test
    public void formatDateVersion_concurrent_alwaysRight() throws InterruptedException {
        final FormatDateVersionActor actor0 = new FormatDateVersionActor(createRunWithOffset(0));
        final FormatDateVersionActor actor1 = new FormatDateVersionActor(createRunWithOffset(1));
        final FormatDateVersionActor actor2 = new FormatDateVersionActor(createRunWithOffset(2));
        final FormatDateVersionActor actor3 = new FormatDateVersionActor(createRunWithOffset(3));
        final FormatDateVersionActor actor4 = new FormatDateVersionActor(createRunWithOffset(4));
        final FormatDateVersionActor actor5 = new FormatDateVersionActor(createRunWithOffset(5));
        final FormatDateVersionActor actor6 = new FormatDateVersionActor(createRunWithOffset(6));
        final FormatDateVersionActor actor7 = new FormatDateVersionActor(createRunWithOffset(7));
        final FormatDateVersionActor actor8 = new FormatDateVersionActor(createRunWithOffset(8));
        final FormatDateVersionActor actor9 = new FormatDateVersionActor(createRunWithOffset(9));
        final Iterable<FormatDateVersionActor> allActors = ImmutableList.of(actor0, actor1, actor2, actor3, actor4, actor5, actor6, actor7, actor8, actor9);
        //act
        ConcurrentTestUtil.executeConcurrent("MetadataRepositoryItem#formatDateVersion should be threadsafe", allActors, 5);
        //assert
        assertThat(actor0, HasToString.<FormatDateVersionActor> hasToString(CoreMatchers.equalTo("39141113.000000-0")));
        assertThat(actor1, HasToString.<FormatDateVersionActor> hasToString(CoreMatchers.equalTo("39141113.000001-0")));
        assertThat(actor2, HasToString.<FormatDateVersionActor> hasToString(CoreMatchers.equalTo("39141113.000002-0")));
        assertThat(actor3, HasToString.<FormatDateVersionActor> hasToString(CoreMatchers.equalTo("39141113.000003-0")));
        assertThat(actor4, HasToString.<FormatDateVersionActor> hasToString(CoreMatchers.equalTo("39141113.000004-0")));
        assertThat(actor5, HasToString.<FormatDateVersionActor> hasToString(CoreMatchers.equalTo("39141113.000005-0")));
        assertThat(actor6, HasToString.<FormatDateVersionActor> hasToString(CoreMatchers.equalTo("39141113.000006-0")));
        assertThat(actor7, HasToString.<FormatDateVersionActor> hasToString(CoreMatchers.equalTo("39141113.000007-0")));
        assertThat(actor8, HasToString.<FormatDateVersionActor> hasToString(CoreMatchers.equalTo("39141113.000008-0")));
        assertThat(actor9, HasToString.<FormatDateVersionActor> hasToString(CoreMatchers.equalTo("39141113.000009-0")));
    }

    private MavenBuild createRunWithOffset(final int offsetInSec) {
        final Calendar refDate2 = Calendar.getInstance();
        refDate2.setTimeInMillis(refDate.getTimeInMillis());
        refDate2.add(Calendar.SECOND, offsetInSec);
        final MavenBuild run2 = new MavenBuild(job, refDate2);
        return run2;
    }

    private static class FormatDateVersionActor implements Runnable {
        private final Run<?, ?> run;
        private String formatedDate;

        public FormatDateVersionActor(final Run<?, ?> run) {
            this.run = run;
        }

        public void run() {
            //act
            formatedDate = MetadataRepositoryItem.formatDateVersion(run);
        }

        @Override
        public String toString() {
            return formatedDate;
        }
    }
}