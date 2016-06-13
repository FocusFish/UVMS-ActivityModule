package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.uvms.BaseArquillianTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

import java.io.File;

/**
 * Created by padhyad on 5/19/2016.
 */
public class BaseActivityArquillianTest extends BaseArquillianTest {

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class).addPackages(true, "eu.europa.ec.fisheries.ers", "eu.europa.ec.fisheries.mdr", "eu.europa.ec.fisheries.uvms.activity")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/orm.xml")
                .addAsResource("config.properties")
                .addAsResource("fa_flux_message.xml")
                .addAsResource("logback.xml");

        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importDependencies(ScopeType.COMPILE, ScopeType.RUNTIME, ScopeType.TEST).resolve().withTransitivity().asFile();
        webArchive = webArchive.addAsLibraries(libs);
        System.out.println(webArchive.toString(true));
        return webArchive;
    }
}