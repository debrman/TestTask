package tv.tvoe.nsk.tt;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import oracle.xml.parser.v2.DOMParser;
import oracle.xml.util.XMLUtil;

public class XMLProcessor {

	protected Document rootdoc;
	protected Element cielement;
	protected Element pelement;
	
	protected XMLProcessor() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		rootdoc = builder.newDocument();
		Node rootnode = rootdoc.createElement("root");
		rootdoc.appendChild(rootnode);
	}

	public void setClientInfo(String ipaddress, String useragent) {
		Element element; 
		cielement = rootdoc.createElement("client_info");

		element = rootdoc.createElement("ip-address");
		element.setTextContent(ipaddress);
		cielement.appendChild(element);
		
		element = rootdoc.createElement("user-agent");
		element.setTextContent(useragent);
		cielement.appendChild(element);
	}

	public void setMethod(String method) {
		if(pelement == null) {
			pelement = rootdoc.createElement("parameters");
			pelement.setAttribute("method", method);
		} else {
			pelement.setAttribute("method", method);
		}
	}
	
	public void setParameters(MultivaluedMap<String,String> parameters) {
		Element numeric = rootdoc.createElement("numeric_parameters");
		Element string = rootdoc.createElement("string_parameters");
		Pattern varpattern = Pattern.compile("^\\d{1,10}([\\.\\,]\\d{1,10}){0,1}$", Pattern.CASE_INSENSITIVE);
		parameters.forEach((key,array)->{
			for(String value : array) {
				Element parameter = rootdoc.createElement("parameter");
				parameter.setAttribute("name", key);
				parameter.setTextContent(value);
				Matcher varmatch = varpattern.matcher(value);
	            if(varmatch.matches()) {
	            	numeric.appendChild(parameter);
	            } else {
	            	string.appendChild(parameter);
	            }
	        }
		});
		if(pelement == null) {
			pelement = rootdoc.createElement("parameters");
		}
		pelement.appendChild(numeric);
		pelement.appendChild(string);
	}

	public void setSchema(String url) {
		try {
			URL xmlurl =  XMLUtil.createURL(url);

			DOMParser domp  = new DOMParser();
			domp.setPreserveWhitespace (true);
			domp.setErrorStream (System.out);
			domp.parse (xmlurl);

			Document xsddoc = domp.getDocument();
			Node rootnode = rootdoc.getFirstChild();
			Node xsdnode = rootdoc.importNode(xsddoc.getFirstChild(), true);
			rootnode.appendChild(xsdnode);
		} catch(Exception e) {
			System.out.println ("XSD append Schema exception:");
			e.printStackTrace();
		}
	}

	public Document getDocument() {
		Node rootnode = rootdoc.getFirstChild();
		try {
			Element requestdetail = rootdoc.createElement("request_detail");
			requestdetail.appendChild(cielement);
			requestdetail.appendChild(pelement);
			rootnode.appendChild(requestdetail);
		} catch(Exception e) {
			System.out.println ("XML append Document exception:");
			e.printStackTrace();
		}
		return rootdoc;
	}
}
