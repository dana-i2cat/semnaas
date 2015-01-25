import com.google.gwt.resources.client.DataResource;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.vaadin.ui.Upload;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import javax.print.DocFlavor;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mohamed Morsey on 07/10/14.
 */
public class RDFFileUploader implements Upload.Receiver, Upload.SucceededListener {

    File  file;         // File to write to.

    @Override
    public OutputStream receiveUpload(String filename,
                                      String MIMEType) {
        FileOutputStream fos = null; // Output stream to write to
        file = new File("/tmp/uploads/" + filename);
        try {
            // Open the file for writing.
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            // Error while opening the file. Not reported here.
            e.printStackTrace();
            return null;
        }

        return fos; // Return the output stream to write to
    }

    @Override
    public void uploadSucceeded(Upload.SucceededEvent succeededEvent) {

        Reasoner r1 = ReasonerRegistry.getOWLReasoner();

        Reasoner r = PelletReasonerFactory.theInstance().create();

        Model uploadedFileModel = ModelFactory.createDefaultModel();

        uploadedFileModel.read("file://" + file.getAbsolutePath()) ;

        PelletOptions.USE_TRACING = true;

        InfModel reasoningModel = ModelFactory.createInfModel(r1, uploadedFileModel);


//        UploaderView.errorsTable.setContainerDataSource(reasoningModel.validate().getReports());
//        UploaderView.errorsTable.setVisibleColumns(new Object[] {
//                "No.", "Error" });

//        UploaderView.errorsTable.addContainerProperty("No.", Integer.class, null);
//        UploaderView.errorsTable.addContainerProperty( "Error" ,  String.class, null);
//
//        UploaderView.errorsTable.setColumnHeaders(new String[]{"No.", "Error"});
//
//        UploaderView.errorsTable.removeAllItems();
//        Iterator<ValidityReport.Report> errors = reasoningModel.validate().getReports();
//
//        int errorCounter = 0;
//        while(errors.hasNext()){
//            ValidityReport.Report error = errors.next();
//            UploaderView.errorsTable.addItem(new Object[]{errorCounter+1 , error.description}, errorCounter);
//            errorCounter++;
//        }

//        PelletInfGraph pellet = (PelletInfGraph) reasoningModel.getGraph();
//
//        // create an inferencing model using Pellet reasoner
//        if( !pellet.isConsistent() ) {
//            // create an inferencing model using Pellet reasoner
//            Model explanation = pellet.explainInconsistency();
//            // print the explanation
//            explanation.write( System.out );
//        }

/*

        try {
            ClientConfig config = new DefaultClientConfig();

//        Logger logger;

            Client client = Client.create(config);
            WebResource service = client.resource(getBaseURI());
//
////        ClientResponse response = service.path("parameters").path("todos")
////                .path(todo.getId()).accept(MediaType.APPLICATION_XML)
////                .put(ClientResponse.class, todo);
//
//        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
//        params.add("file", file.getAbsolutePath());
////        params.add("parameter2", "xyz");
//        ClientResponse response = service.path("upload").type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class, params);

            InputStream stream = getClass().getClassLoader().getResourceAsStream(file.getAbsolutePath());

            String mimeType = URLConnection.guessContentTypeFromName(file.getName());

//        FormDataMultiPart part = new FormDataMultiPart().field("file", stream, MediaType.TEXT_PLAIN_TYPE);
            FormDataMultiPart formParam1 = new FormDataMultiPart();
            formParam1.field("file", new FileInputStream(file),  MediaType.APPLICATION_OCTET_STREAM_TYPE);
            formParam1.field("file", file.getName(),  MediaType.TEXT_PLAIN_TYPE);

//            MultivaluedMap<String, Object> formData = new MultivaluedMapImpl();
//            formData.add("file", new FileInputStream(file));
//            formData.add("file", file.getName());

//            FormDataMultiPart formParam2 = new FormDataMultiPart();
//            formParam2.field("file", file.getName(),  MediaType.TEXT_PLAIN_TYPE);
//
//            ArrayList<FormDataMultiPart> params = new ArrayList<FormDataMultiPart>();

//            params.add(formParam1);
//            params.add(formParam2);

//            FormDataBodyPart fdp = new FormDataBodyPart("file",
//                    file.getName(),
//                    MediaType.TEXT_PLAIN_TYPE);
//
//            form.bodyPart(fdp);


//            form.field("file", file.getAbsolutePath());

//            FormDataBodyPart fdp = new FormDataBodyPart("file",
//                    new FileInputStream(file),
//                    MediaType.APPLICATION_OCTET_STREAM_TYPE);
//
//            form.bodyPart(fdp);

            WebResource resource = Client.create().resource("http://localhost:9998/helloworld/upload");
//            String response = resource.type(MediaType.MULTIPART_FORM_DATA).post(String.class, formParam1);
            String response = resource.type(MediaType.MULTIPART_FORM_DATA).post(String.class, formParam1);


        }
        catch (Exception exp){
            System.out.println(exp);
        }
*/
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:9998/helloworld").build();
    }
}
