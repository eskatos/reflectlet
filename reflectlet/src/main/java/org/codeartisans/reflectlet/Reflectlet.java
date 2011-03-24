package org.codeartisans.reflectlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class Reflectlet extends HttpServlet {

    private static final String HEADER = "<html><head><title>Reflectlet</title></head><body>";
    private static final String FOOTER = "</body></html>";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType( "text/html" );
        PrintWriter writer = response.getWriter();
        h1( writer, "Reflectlet" );

        Map<String,Object> data = new LinkedHashMap<String,Object>();

        h2( writer, "HTTP related data" );

        data.put( "LocalName", request.getLocalName() );
        data.put( "LocalAddr", request.getLocalAddr() );
        data.put( "LocalPort", request.getLocalPort() );
        data.put( "RemoteHost", request.getRemoteHost() );
        data.put( "RemotePort", request.getRemotePort() );
        data.put( "RemoteUser", request.getRemoteUser() );
        data.put( "Protocol", request.getProtocol() );
        data.put( "Method", request.getMethod() );
        table(writer, data);
        data.clear();

        h2(writer, "URI & Path related data");

        data.put( "RequestURL", request.getRequestURL() );
        data.put( "RequestURI", request.getRequestURI() );
        data.put( "ContextPath", request.getContextPath() );
        data.put( "PathInfo", request.getPathInfo() );
        data.put( "PathTranslated", request.getPathTranslated() );
        data.put( "QueryString", request.getQueryString() );
        data.put( "ServletPath", request.getServletPath() );
        table(writer, data);
        data.clear();

        h2( writer, "HTTP Headers" );

        Enumeration<String> headerNames = request.getHeaderNames();
        while ( headerNames.hasMoreElements() ) {
            String eachHeaderName = headerNames.nextElement();
            Enumeration<String> headerValues = request.getHeaders( eachHeaderName );
            StringBuilder sb = new StringBuilder();
            while ( headerValues.hasMoreElements() ) {
                sb.append(headerValues.nextElement()).append("\n");
            }
            data.put( eachHeaderName, sb.toString() );
        }
        table(writer, data);
        data.clear();

        h2(writer, "Security related data");

        data.put( "Secure", request.isSecure() );
        data.put( "ClientCertificate", getClientX509CertificateInfo( request ) );
        table(writer, data);
        data.clear();

        writer.close();
    }

    private void h1( PrintWriter writer, String title ) {
        wrap(writer, "h1", title);
    }

    private void h2( PrintWriter writer, String title ) {
        wrap( writer, "h2", title );
    }

    private void table( PrintWriter writer, Map<String,Object> data ) {
        writer.println( "<table border='1'>" );
        for (Map.Entry<String,Object> eachEntry : data.entrySet()) {
            writer.println( "<tr><td vertical-align='top'>" + eachEntry.getKey() + "</td><td vertical-align='top'><pre>" + eachEntry.getValue() + "</pre></td></tr>" );
        }
        writer.println( "</table>" );
    }

    private void wrap( PrintWriter writer, String tag, String text ) {
        writer.println( wrap( tag, text) );
    }

    private String wrap( String tag, String text ) {
        return "<" + tag + ">" + text + "</" + tag + ">";
    }

    private String getClientX509CertificateInfo( HttpServletRequest request ) {
        Object x509 = request.getAttribute( "javax.servlet.request.X509Certificate" );
        if ( x509 == null ) {
            return "none";
        }
        X509Certificate[] certChain = ( X509Certificate[] ) x509;
        StringBuffer sb = new StringBuffer();
        sb.append(certChain.length).append(" certificates in client chain\n");
        for ( X509Certificate eachCert : certChain ) {
            sb.append( eachCert );
        }
        return sb.toString();
    }

}
