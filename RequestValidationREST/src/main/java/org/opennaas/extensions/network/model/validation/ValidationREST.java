package org.opennaas.extensions.network.model.validation;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.header.FormDataContentDisposition;
//import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;
import org.opennaas.extensions.network.model.TopologyValidator;
//import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import java.io.*;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

/**
 * Created by Mohamed Morsey on 03/10/14.
 * RESTful web service to validate a NaaS request
 */

@Path("/validate")
public class ValidationREST {

    private static Logger logger = Logger.getLogger(ValidationREST.class);

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces(MediaType.TEXT_PLAIN)
    public String getClichedMessage() {
        // Return some cliched textual content
        return "Hello World using plain text";
    }

    // This method is called if HTML is request
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getClichedMessageHTML() {
        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
                + "<body><h1>" + "Hello World using HTML" + "</body></h1>" + "</html> ";
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    public String getClichedMessageXML() {
        return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
    }

    @Path("{contact}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getContact(@PathParam("contact") String contact) {
        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
                + "<body><h1>" + "Passed number = " + contact + "</body></h1>" + "</html> ";
    }

    @GET
    @Path("/parameters")
    @Produces(MediaType.TEXT_HTML)
    public String responseMsg( @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") List<String> parameter2) {

        String output = "Prameter1: " + parameter1 + "\nParameter2: " + parameter2.toString();

        return output;

    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_XML)
    public Response uploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("format") String fileFormat) {

        String uploadedFileLocation = "/tmp/" + fileDetail.getFileName();

        // save it
        writeToFile(uploadedInputStream, uploadedFileLocation);

        logger.info("File uploaded to : " + uploadedFileLocation);

        try {
            Model inputModel = ModelFactory.createDefaultModel();
            inputModel.read(new FileInputStream(uploadedFileLocation), null, fileFormat);
            List<ValidityReport.Report> errors = TopologyValidator.validate(inputModel);

            for(ValidityReport.Report error:errors){
                logger.info(error.getDescription());
            }

            /*
            //Just for testing marshaling
            JAXBContext personContext = JAXBContext.newInstance(ValidityReport.Report.class);
            StringWriter sw = new StringWriter();
            final Marshaller personMarshaller = personContext.createMarshaller();
            JAXBElement<ValidityReport.Report> jaxbElement = new JAXBElement<ValidityReport.Report>(new QName("person"), ValidityReport.Report.class, errors.get(0));
            personMarshaller.marshal(jaxbElement, sw);

            final StreamingOutput output = new StreamingOutput()
            {
                public void write(final OutputStream outputStream)
                        throws IOException, WebApplicationException
                {
                    try
                    {
                        personMarshaller.marshal(personMarshaller, outputStream);
                    }
                    catch (Exception e)
                    {
//                        Debug.debugException(e);
//                        throw new WebApplicationException(
//                                e, Response.Status.INTERNAL_SERVER_ERROR);
                    }
                }
            };
            /////////////////////////////


            GenericEntity<List<ValidityReport.Report>> entity = new GenericEntity<List<ValidityReport.Report>>(errors){};
*/
            // Create the response
//            Response.ResponseBuilder builder = Response.ok("Uploaded successfully");

            ValidationReport report = new ValidationReport(errors);
            Response.ResponseBuilder builder = Response.status(200).type(MediaType.TEXT_XML);
            builder.entity(report);
//            builder.entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?> <note> <to> Tove</to> <from>Jani</from> <heading>Reminder</heading> <body>Don't forget me this weekend!</body> </note>");
            //            builder.entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?> <note> <to> Tove</to> <from>Jani</from> <heading>Reminder</heading> <body>Don't forget me this weekend!</body> </note>");

            return builder.build();

        }
        catch (FileNotFoundException exp){

        }
        catch (Exception exp){

        }


        return Response.status(200).entity("File uploaded to : " + uploadedFileLocation).build();

    }


    public static void main(String[] args) throws IOException {
//        WadlApplicationContext app= new WadlApplicationContextImpl(null, null);
        //HttpServer server = HttpServerFactory.create("http://localhost:9998/");

        ResourceConfig rc = new ClassNamesResourceConfig(ValidationREST.class.getName());


        HttpServer server = HttpServerFactory.create("http://localhost:9998/", rc);

        server.start();

//        ClientConfig config = new DefaultClientConfig();

        // Client client = Client.create(config);

        System.out.println("Server running");
        System.out.println("Visit: http://localhost:9998/validate");
        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
            System.out.println("Server stopped");


    }

    // save uploaded file to new location
    private void writeToFile(InputStream uploadedInputStream,
                             String uploadedFileLocation) {

        try {
            OutputStream out = new FileOutputStream(new File(
                    uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }


}
