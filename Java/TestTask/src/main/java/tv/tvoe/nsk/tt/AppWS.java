package tv.tvoe.nsk.tt;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class AppWS {

	public static final String APP_URI = "http://localhost:82/testtask/";
	public static final String XSD_URI = "TestTask.xsd";
	
	public static HttpServer startServer(String appuri, String xsduri) {
		Map<String,String> config = new HashMap<String, String>();
		config.put("xsd-uri", xsduri);

		final ResourceConfig rc = new ResourceConfig().packages("tv.tvoe.nsk.tt");
		rc.setProperties(config);

		return GrizzlyHttpServerFactory.createHttpServer(URI.create(appuri), rc);
	}

	public static void main(String[] args) throws Exception {
		final HttpServer server;
		if(args.length != 2) {
			System.out.println(
					"Usage: java AppWS [<Application URI> <XML Schema file>]\n\n" +
					"Web service application started with default parameters.\n" +
					"WADL available at " + APP_URI + "application.wadl\n" +
					"XML Schema file is " + XSD_URI + "\n\n" +
					"Press enter to stop application...\n");
			server = startServer(APP_URI, XSD_URI);
		} else {
			System.out.println(
					"Web service application started with user defined parameters.\n" +
					"WADL available at " + args[0] + "application.wadl\n" +
					"XML Schema file is " + args[1] + "\n\n" +
					"Press enter to stop application...\n");
			server = startServer(args[0], args[1]);
		}
        System.in.read();
        server.shutdownNow();
	}

}
