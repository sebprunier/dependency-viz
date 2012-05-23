package fr.jcreigno.depsviz;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.maven.repository.internal.DefaultServiceLocator;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.connector.file.FileRepositoryConnectorFactory;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.util.DefaultRepositoryCache;
import org.sonatype.aether.util.DefaultRepositorySystemSession;

import com.google.common.collect.Lists;

public class BooterContextListener implements ServletContextListener {

    private ServletContext context = null;

    public void contextDestroyed(ServletContextEvent event) {
        this.context = null;
    }

    public void contextInitialized(ServletContextEvent event) {
        this.context = event.getServletContext();

        // setting repository system
        RepositorySystem sys = newRepositorySystem();
        this.context.setAttribute("repository-system", sys);

        // setting remote repositories
        String remoteReposParam = context.getInitParameter("remote-repos");
        List<RemoteRepository> remoteRepos = Lists.newArrayList();

        if (remoteReposParam == null) {
            remoteRepos.add(new RemoteRepository("central", "default", "http://repo1.maven.org/maven2/"));
            this.context.log("Using Maven Central by default : http://repo1.maven.org/maven2/");
        } else {
            String[] remoteReposUrls = remoteReposParam.split(";");
            for (int i = 0; i < remoteReposUrls.length; i++) {
                String repoUrl = remoteReposUrls[i];
                remoteRepos.add(new RemoteRepository("repo" + i, "default", repoUrl));
                this.context.log("Using remote repo : " + repoUrl);
            }
        }

        this.context.setAttribute("repositories", remoteRepos);

        // setting default session
        this.context.setAttribute("session", newSystemSession(sys));
    }

    private RepositorySystem newRepositorySystem() {
        /*
         * Aether's components implement org.sonatype.aether.spi.locator.Service to ease manual wiring and using the
         * prepopulated DefaultServiceLocator, we only need to register the repository connector factories.
         */
        DefaultServiceLocator locator = new DefaultServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class);
        locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
        locator.setServices(WagonProvider.class, new ManualWagonProvider());

        return locator.getService(RepositorySystem.class);
    }

    private RepositorySystemSession newSystemSession(RepositorySystem sys) {
        DefaultRepositorySystemSession session = new MavenRepositorySystemSession();

        // Local Repository
        File localRepoPath = new File(new File(System.getProperty("user.home"), ".m2"), "repository");
        LocalRepository localRepo = new LocalRepository(localRepoPath);
        session.setLocalRepositoryManager(sys.newLocalRepositoryManager(localRepo));

        // Cache
        session.setCache(new DefaultRepositoryCache());

        return session;
    }

}
