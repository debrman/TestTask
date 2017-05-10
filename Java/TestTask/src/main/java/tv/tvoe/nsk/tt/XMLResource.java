package tv.tvoe.nsk.tt;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.glassfish.grizzly.http.server.Request;

@Path("xmlresource")
public class XMLResource {

	XMLProcessor xmlp;
	String xsduri;
		
	@Context
    public void requestData(Request request, HttpHeaders headers, Configuration config) throws Exception {
		xsduri = (String)config.getProperty("xsd-uri");
		xmlp = new XMLProcessor(); 
		xmlp.setClientInfo(request.getRemoteAddr(), headers.getRequestHeader("user-agent").get(0));
		xmlp.setMethod(request.getMethod().toString());
	}

	@GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getData(@Context UriInfo info) {
		return getResponse(info.getQueryParameters());
	}

	@POST
    @Produces(MediaType.APPLICATION_XML)
    public Response postData(MultivaluedMap<String,String> mvmap){
		return getResponse(mvmap);
	}

	public Response getResponse(MultivaluedMap<String,String> mvmap){
		OutputStream outstm = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(outstm);

		try {
			xmlp.setParameters(mvmap);
			xmlp.setSchema(xsduri);

    		Source source = new DOMSource(xmlp.getDocument());
    		Transformer xformer = TransformerFactory.newInstance().newTransformer();
    		xformer.setOutputProperty(OutputKeys.INDENT, "yes");
    		xformer.transform(source, result);
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
        	return Response.serverError().entity(e.getMessage()).build();
        }
    	return Response.ok(result.getOutputStream().toString(), MediaType.APPLICATION_XML).build();
	}
}
