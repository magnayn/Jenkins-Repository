package com.nirima.jenkins;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.EntityUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Add this mojo into a build, so as it goes it reports
 * artifacts that have been created to Jenkins.
 *
 * @goal report-artifacts
 * @phase install
 */
public class MavenBuildJenkinsReporterMojo  extends AbstractMojo {
  /**
   * Location of the file.
   *
   * @parameter expression="${project.build.directory}"
   * @required
   */
  private File outputDirectory;


  /**
   * @parameter default-value="${project}"
   */
  private MavenProject project;

  /**
   * @parameter expression="${project.ciManagement.url}"
   */
  private String jenkinsUrl;

  /**
   * @parameter expression="${jenkinsProject}"
   */
  private String projectName;

  /**
   * @parameter expression="${jenkinsBuild}"
   */
  private String buildNumber;

  public void execute() throws MojoExecutionException, MojoFailureException {

    if( jenkinsUrl == null ||
        projectName == null ||
        buildNumber == null )
      return;

    if( !jenkinsUrl.endsWith("/") )
        jenkinsUrl += "/";


    jenkinsUrl += "plugin/repository/add_info";



    try {

      String data = getData();
      System.out.println("Sending " + data);





      URL url = new URL(jenkinsUrl);

      System.out.println(url.toURI());

      CloseableHttpClient httpclient = HttpClients.createDefault();

      HttpPost httpPost = new HttpPost(url.toURI());



      httpPost.setEntity(new StringEntity(data));
      CloseableHttpResponse response2 = httpclient.execute(httpPost);

      System.out.println(response2);

    } catch (Exception e) {
      throw new MojoExecutionException("Failed to parse", e);
    }

  }

  private String getData() throws IOException {
    String response = "";

    response = response + projectName + "\n" + buildNumber + "\n";
    response = response + "[pom]\n" + pom();

    if( project.getArtifact().getFile() != null )
      response = response + "[main]\n" + info(project.getArtifact());

    for( Artifact a : project.getAttachedArtifacts() )
    {
      response += "[artifact]\n" + info(a);
    }
    return response;
  }

  private String pom() throws IOException {

      File file = project.getFile();

      String data = file + "\n"
                    + project.getGroupId() +"\n"
                    + project.getArtifactId() +"\n"
                    + project.getVersion() +"\n"
                     +"\n"
                    + "pom\n"
                    + file.getName() + "\n"
                    +Util.getDigestOf(file) + "\n";
      return data;
  }

  private String info(Artifact artifact) throws IOException {
    String file = "";
    String filename = "";
    String digest = "";
    String classifier = "";
    if( artifact.getFile() != null ) {
      file = artifact.getFile().getAbsolutePath();
      filename = artifact.getFile().getName();
      digest = Util.getDigestOf(artifact.getFile());
    }

    if( artifact.getClassifier() != null )
    {
      classifier = artifact.getClassifier();
    }

    String data = file + "\n"
        + artifact.getGroupId() +"\n"
                  + artifact.getArtifactId() +"\n"
                  + artifact.getVersion() +"\n"
                  + classifier +"\n"
                  + artifact.getType() +"\n"
                  + filename + "\n"
                  +digest + "\n";
    return data;
  }
}
