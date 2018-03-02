package com.manywho.services.email.test;

import com.amazonaws.services.s3.AmazonS3;
import com.fiftyonred.mock_jedis.MockJedisPool;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.inject.*;
import com.google.inject.util.Modules;
import com.manywho.sdk.api.jackson.ObjectMapperFactory;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.client.run.RunClient;
import com.manywho.sdk.services.ServiceApplicationModule;
import com.manywho.sdk.services.controllers.DefaultActionController;
import com.manywho.sdk.services.controllers.DefaultDescribeController;
import com.manywho.sdk.services.controllers.DefaultFileController;
import com.manywho.sdk.services.identity.AuthorizationEncoder;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.email.ApplicationModule;
import com.manywho.services.email.configuration.ServiceConfiguration;
import com.manywho.services.email.configuration.ServiceConfigurationDefault;
import com.manywho.services.email.email.MailerFactory;
import com.manywho.services.email.email.decisions.ReceiveDecision;
import com.manywho.services.email.email.decisions.TokenGenerator;
import com.manywho.services.email.guice.JedisPoolProvider;
import com.manywho.services.email.guice.RunClientProvider;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.plugins.guice.RequestScoped;
import org.json.JSONException;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.reflections.Reflections;
import org.simplejavamail.mailer.Mailer;
import org.skyscreamer.jsonassert.JSONAssert;
import redis.clients.jedis.JedisPool;

import javax.ws.rs.Path;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class EmailServiceFunctionalTest {

    protected Dispatcher dispatcher;
    protected Injector injector;
    protected MailerFactory mailerFactory;
    protected Mailer mailer;
    protected ServiceConfiguration serviceConfigurationDefault;
    protected AmazonS3 amazonS3;
    protected JedisPool jedisPool;
    protected JedisPool mockJedisPool;
    protected TokenGenerator tokenGenerator;
    protected RunClient runClient;
    protected UriInfo uriInfo;
    protected HttpHeaders httpHeadersTest;
    private AuthenticatedWhoProvider authenticatedWhoProvider;

    @Before
    public void setUp() {
        mailerFactory = Mockito.mock(MailerFactory.class);
        mailer = Mockito.mock(Mailer.class);
        amazonS3 = Mockito.mock(AmazonS3.class);
        serviceConfigurationDefault = Mockito.mock(ServiceConfiguration.class);
        jedisPool = Mockito.mock(JedisPool.class);
        mockJedisPool= new MockJedisPool(new GenericObjectPoolConfig(), "localhost");
        runClient = Mockito.mock(RunClient.class);
        uriInfo = Mockito.mock(UriInfo.class);
        tokenGenerator = Mockito.mock(TokenGenerator.class);
        httpHeadersTest = Mockito.mock(HttpHeaders.class);
        authenticatedWhoProvider = new AuthenticatedWhoProvider(httpHeadersTest, new AuthorizationEncoder(ObjectMapperFactory.create()));

        final List<Module> modules = Lists.newArrayList();

        modules.add(new ServiceApplicationModule("com.manywho.services.email", true));
        modules.add(new ApplicationModule());

        this.injector = Guice.createInjector(
                Modules.override(modules).with(createTestModule())
        );

        this.dispatcher = MockDispatcherFactory.createDispatcher();

        dispatcher.getRegistry().addSingletonResource(injector.getInstance(DefaultActionController.class));
        dispatcher.getRegistry().addSingletonResource(injector.getInstance(DefaultFileController.class));
        dispatcher.getRegistry().addSingletonResource(injector.getInstance(DefaultDescribeController.class));
        dispatcher.getRegistry().addSingletonResource(injector.getInstance(ReceiveDecision.class));

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
                bind(ServiceConfiguration.class).toProvider(() -> serviceConfigurationDefault);
                bind(JedisPool.class).toProvider(() -> mockJedisPool);
                bind(RunClient.class).toProvider(() -> runClient);
                bind(UriInfo.class).toProvider(() -> uriInfo);
                bind(TokenGenerator.class).toProvider(() -> tokenGenerator);
                bind(AuthenticatedWhoProvider.class).toProvider(() -> authenticatedWhoProvider);
                bind(HttpHeaders.class).toProvider(() -> httpHeadersTest).in(RequestScoped.class);

                bindScope(RequestScoped.class, new Scope() {
                    @Override
                    public <T> com.google.inject.Provider<T> scope(Key<T> key, com.google.inject.Provider<T> unscoped) {
                        return (com.google.inject.Provider<T>) authenticatedWhoProvider;
                    }
                });
            }
        };
    }
}
