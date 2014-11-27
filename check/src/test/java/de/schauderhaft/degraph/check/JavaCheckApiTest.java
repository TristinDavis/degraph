package de.schauderhaft.degraph.check;

import de.schauderhaft.degraph.configuration.NamedPattern;

import static de.schauderhaft.degraph.check.JCheck.*;

import org.junit.Test;

import static org.hamcrest.core.Is.*;

import static org.junit.Assert.assertThat;
import static de.schauderhaft.degraph.check.Check.classpath;

public class JavaCheckApiTest {

    @Test
    public void canAccessClasspathConfigurationBuilder() {
        classpath();
    }

    @Test
    public void canAccessNoJars() {
        classpath().noJars();
    }

    @Test
    public void canDefineSlices() {
        classpath().withSlicing("blah", "whatever");
    }

    @Test
    public void canUseAllow() {
        classpath().withSlicing("blah", "whatever").allow("a", "b", "c");
    }

    @Test
    public void canUseAllowDirect() {
        classpath().withSlicing("blah", "whatever").allowDirect("a", "b", "c");
    }

    @Test
    public void canUseAnyOf() {
        JLayer.anyOf("b", "c", "d");
    }

    @Test
    public void canUseOneOf() {
        JLayer.oneOf("b", "c", "d");
    }

    @Test
    public void degraphHonoursItsConstraintsJavaStyle() {
        assertThat(
                classpath()
                        .printTo("degraphHonoursItsConstraintsJavaStyle.graphml")
                        .including("de.schauderhaft.**")
                        .withSlicing("part", "de.schauderhaft.*.(*).**")
                        .withSlicing(
                                "lib",
                                "de.schauderhaft.**(Test)",
                                new NamedPattern("de.schauderhaft.*.**", "main"))
                        .withSlicing(
                                "internalExternal",
                                new NamedPattern("internal",
                                        "de.schauderhaft.**"),
                                new NamedPattern("**", "external")),
                is(violationFree()));
    }
}
