package com.revature.portal.web.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.revature.portal.web.filters.AuthFilter;
import com.revature.portal.web.servlets.UserServlet;
import com.revature.portal.web.util.security.JwtConfig;
import com.revature.portal.web.util.security.TokenGenerator;
import com.revature.portal.datasource.repositories.UserRepository;
import com.revature.portal.datasource.util.MongoClientFactory;
import com.revature.portal.services.UserService;
import com.revature.portal.web.servlets.HealthCheckServlet;
import com.revature.portal.web.servlets.RegisterServlet;
import com.revature.portal.web.servlets.LoginServlet;
import com.revature.portal.web.servlets.TestServlet;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.EnumSet;

public class ContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        System.out.println("marwan-sean_api_p1 Context Initialized.");

        MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
        System.out.println("DB Connection Established!");
//        PasswordUtils passwordUtils = new PasswordUtils();
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        JwtConfig jwtConfig = new JwtConfig();
        TokenGenerator tokenGenerator = new TokenGenerator(jwtConfig);

        UserRepository userRepo = new UserRepository(mapper);
        UserService userService = new UserService(userRepo);
        System.out.println("Services Created!");

        AuthFilter authFilter = new AuthFilter(jwtConfig);

        LoginServlet loginServlet = new LoginServlet(userService, mapper, tokenGenerator);
        HealthCheckServlet healthCheckServlet = new HealthCheckServlet();
        TestServlet testServlet = new TestServlet();
        UserServlet userServlet = new UserServlet(userService, mapper);


        ServletContext servletContext = sce.getServletContext();

        servletContext.addFilter("AuthFilter", authFilter).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
        servletContext.addServlet("LoginServlet", loginServlet).addMapping("/login");
        servletContext.addServlet("HealthServlet", healthCheckServlet).addMapping("/health");
        servletContext.addServlet("TestServlet", testServlet).addMapping("/test");
        servletContext.addServlet("UserServlet", userServlet).addMapping("/users");
        System.out.println("TestServlet Context Added!");

        configureLogback(servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("marwan-sean_api_p1 Context Destroyed.");
        System.out.println("Goodbye, cruel world!!!");
        MongoClientFactory.getInstance().cleanUp();
    }

    private void configureLogback(ServletContext servletContext) {

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator logbackConfig = new JoranConfigurator();
        logbackConfig.setContext(loggerContext);
        loggerContext.reset();

        String logbackConfigFilePath = servletContext.getRealPath("") + File.separator + servletContext.getInitParameter("logback-config");

        try {
            logbackConfig.doConfigure(logbackConfigFilePath);
        } catch (JoranException e) {
            e.printStackTrace();
            System.out.println("An unexpected exception occurred. Unable to configure Logback.");
        }

    }
}
