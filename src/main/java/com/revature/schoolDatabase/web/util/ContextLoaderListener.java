package com.revature.schoolDatabase.web.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.revature.schoolDatabase.datasource.repositories.UserRepository;
import com.revature.schoolDatabase.datasource.util.MongoClientFactory;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.web.servlets.AuthServlet;
import com.revature.schoolDatabase.web.servlets.HealthCheckServlet;
import com.revature.schoolDatabase.web.servlets.TestServlet;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

public class ContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        System.out.println("marwan-sean_api_p1 Context Initialized.");

        MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
        System.out.println("DB Connection Established!");
//        PasswordUtils passwordUtils = new PasswordUtils();
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

        UserRepository userRepo = new UserRepository(mapper);
        UserService userService = new UserService(userRepo);
        System.out.println("Services Created!");

        HealthCheckServlet healthCheckServlet = new HealthCheckServlet();
        TestServlet testServlet = new TestServlet();


        ServletContext servletContext = sce.getServletContext();
//        servletContext.addServlet("AuthServlet", authServlet).addMapping("/auth");
        servletContext.addServlet("HealthServlet", healthCheckServlet).addMapping("/health");
        servletContext.addServlet("TestServlet", testServlet).addMapping("/test");
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
