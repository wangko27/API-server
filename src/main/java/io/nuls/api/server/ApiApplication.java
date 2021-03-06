package io.nuls.api.server;

import io.nuls.api.constant.Constant;
import io.nuls.api.utils.log.Log;
import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.grizzly.strategies.WorkerThreadIOStrategy;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.grizzly.utils.Charsets;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.IOException;

public enum ApiApplication {
    ENTRY;

    private HttpServer server;
    private String serverUri;

    public synchronized String start(String ip, int port, String moduleUrl) {
        if(isStarted()) {
            return serverUri;
        }
        // Create test web application context.
        WebappContext webappContext = new WebappContext("NULS-API-SERVER", "/"+moduleUrl);
        webappContext.addContextInitParameter("contextClass","org.springframework.web.context.support.XmlWebApplicationContext");
        webappContext.addContextInitParameter("contextConfigLocation","classpath:ApplicationContext.xml");
        webappContext.addListener("org.springframework.web.context.ContextLoaderListener");

        // Create a servlet registration for the web application in order to wire up Spring managed collaborators to Jersey resources.
        ServletRegistration servletRegistration = webappContext.addServlet("jersey-servlet", ServletContainer.class);

        servletRegistration.setInitParameter("javax.ws.rs.Application","io.nuls.api.server.resources.impl.NulsResourceConfig");
        servletRegistration.setInitParameter("jersey.config.server.provider.packages", Constant.PACKAGES);
        servletRegistration.addMapping("/*");

        server = new HttpServer();
        NetworkListener listener = new NetworkListener("grizzly2", ip, port);
        TCPNIOTransport transport = listener.getTransport();
        ThreadPoolConfig workerPool = ThreadPoolConfig.defaultConfig()
                .setCorePoolSize(8)
                .setMaxPoolSize(16)
                .setQueueLimit(1000)
                .setThreadFactory((new ThreadFactoryBuilder()).setNameFormat("grizzly-http-server-%d").build());
        transport.configureBlocking(false);
        transport.setSelectorRunnersCount(2);
        transport.setWorkerThreadPoolConfig(workerPool);
        transport.setIOStrategy(WorkerThreadIOStrategy.getInstance());
        transport.setTcpNoDelay(true);
        listener.setSecure(false);
        server.addListener(listener);

        ServerConfiguration config = server.getServerConfiguration();
        config.setDefaultQueryEncoding(Charsets.UTF8_CHARSET);

        webappContext.deploy(server);
        try {
            server.start();
            serverUri = Constant.HTTP + ip + Constant.COLON + port + Constant.URI_SEPARATOR + moduleUrl;
            Log.info("http restFul server is started!url is " + serverUri);
        } catch (IOException e) {
            Log.error(e);
            server.shutdownNow();
        }
        return serverUri;
    }

    public boolean isStarted() {
        if (null == this.server) {
            return false;
        }
        return this.server.isStarted();
    }

    public void shutdown() {
        server.shutdown();
    }
}
