package com.nirima.jenkins;

import com.nirima.jenkins.bridge.BridgeRepository;
import com.nirima.jenkins.repo.build.ProjectBuildRepositoryRoot;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.TransientBuildActionFactory;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.inject.Inject;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * Exposes a per-build repository under the URL of a build.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class TransientBuildActionFactoryImpl extends TransientBuildActionFactory {
    @Inject
    RepositoryPlugin plugin;

    public Collection<? extends Action> createFor(AbstractBuild build) {
        return Collections.singleton(new BuildActionImpl(build));
    }

    public class BuildActionImpl implements Action {
        private final AbstractBuild build;

        public BuildActionImpl(AbstractBuild build) {
            this.build = build;
        }

        public String getIconFileName() {
            return plugin.getIconFileName();
        }

        public String getDisplayName() {
            return "Build Artifacts As Maven Repository";
        }

        public String getUrlName() {
            return "maven-repository";
        }

        public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
            plugin.serveRequest(
                    new BridgeRepository(new ProjectBuildRepositoryRoot(null, build, build.getFullDisplayName()), null),
                    req.findAncestor(this).getUrl());
        }
    }
}
