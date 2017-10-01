package com.manywho.services.email.test;

import com.amazonaws.services.s3.AmazonS3;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.manywho.sdk.services.ServiceApplicationModule;
import com.manywho.sdk.services.controllers.DefaultActionController;
import com.manywho.sdk.services.controllers.DefaultDescribeController;
import com.manywho.sdk.services.controllers.DefaultFileController;
import com.manywho.services.email.ApplicationModule;
import com.manywho.services.email.email.MailerFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.json.JSONException;
import org.junit.Before;
import org.mockito.Mockito;
import org.reflections.Reflections;
import org.simplejavamail.mailer.Mailer;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class EmailServiceFunctionalTest {

    protected Dispatcher dispatcher;
    protected Injector injector;

    protected MailerFactory mailerFactory;
    protected Mailer mailer;
    protected AmazonS3 amazonS3;

    @Before
    public void setUp() throws Exception {
        this.mailerFactory = Mockito.mock(MailerFactory.class);
        this.mailer = Mockito.mock(Mailer.class);
        this.amazonS3 = Mockito.mock(AmazonS3.class);

        final List<Module> modules = Lists.newArrayList();

        modules.add(new ServiceApplicationModule("com.manywho.services.email", false));
        modules.add(new ApplicationModule());

        this.injector = Guice.createInjector(
                Modules.override(modules).with(createTestModule())
        );

        this.dispatcher = MockDispatcherFactory.createDispatcher();

        dispatcher.getRegistry().addSingletonResource(injector.getInstance(DefaultActionController.class));
        dispatcher.getRegistry().addSingletonResource(injector.getInstance(DefaultFileController.class));
        dispatcher.getRegistry().addSingletonResource(injector.getInstance(DefaultDescribeController.class));

        Reflections reflections = injector.getInstance(Reflections.class);

        addInstances(dispatcher, reflections.getTypesAnnotatedWith(Path.class));
        addInstances(dispatcher, reflections.getTypesAnnotatedWith(Provider.class));
    }

    private void addInstances(Dispatcher dispatcher, Set<Class<?>> classes) {
        String servicePackage = "com.manywho.services.email";

        // Only create instances of classes that are in the service or in the SDK
        classes.stream()
                .filter(c -> c.getPackage().getName().startsWith(servicePackage) || c.getPackage().getName().startsWith("com.manywho.sdk.services"))
                .forEach(c -> dispatcher.getProviderFactory().register(injector.getInstance(c)));
    }

    protected static InputStream getFile(String fileResourcePath) {
        try {
            return Resources.getResource(fileResourcePath).openStream();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load the test file", e);
        }
    }

    protected static String getJsonFormatFileContent(String filePath) {
        return new Scanner(getFile(filePath)).useDelimiter("\\Z").next();
    }

    protected static void assertJsonSame(String expected, String actual) throws JSONException {
        JSONAssert.assertEquals(expected, actual, false);
    }

    protected Module createTestModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(AmazonS3.class).toProvider(() -> amazonS3);
                bind(MailerFactory.class).toProvider(() -> mailerFactory);
            }
        };
    }
}
