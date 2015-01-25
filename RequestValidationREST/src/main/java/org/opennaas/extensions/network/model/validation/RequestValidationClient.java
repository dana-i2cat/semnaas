package org.opennaas.extensions.network.model.validation;

import com.hp.hpl.jena.reasoner.ValidityReport;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

/**
 * Created by mohamed on 20/10/14.
 */
public class RequestValidationClient {
    public static void main(String[] args)
    {
//        Client client = Client.create();
//
//        WebResource webResource = client
//                .resource("http://localhost:9998/validate/upload");
//
////        ClientResponse response = webResource.accept("application/xml")
////                .post(ClientResponse.class, st);
//
//        ClientResponse response = webResource.queryParam("file", "/Users/mohamed/Downloads/testing/TestTopology.nt").queryParam("format","N-TRIPLE").accept(MediaType.TEXT_XML)
//                .post(ClientResponse.class);
//
//        if (response.getStatus() != 200) {
//            throw new RuntimeException("Failed : HTTP error code : "
//                    + response.getStatus());
//        }
//
//        String output = response.getEntity(String.class);
//
//        System.out.println("Server response : \n");
//        System.out.println(output);


        /*
        Client client = ClientBuilder.newBuilder()
                .register(ValidityReport.Report.class).build();
        WebTarget webTarget = client.target("http://localhost:9998/validate/upload");
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file",
                new File("/Users/mohamed/Downloads/testing/TestTopology.nt"),
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        multiPart.bodyPart(fileDataBodyPart);

        Response response = webTarget.request(MediaType.APPLICATION_XML)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        System.out.println(response.getStatus() + " "
                + response.getStatusInfo() + " " + response);
                */

        /*
        InputStream stream = RequestValidationClient.class.getClassLoader().getResourceAsStream("/Users/mohamed/Downloads/testing/TestTopology.nt");
        FormDataMultiPart part = new FormDataMultiPart().field("file", stream, MediaType.TEXT_PLAIN_TYPE);

        WebResource resource = Client.create().resource("http://localhost:9998/validate/upload");
        String response = resource.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(String.class, part);
*/

        /*
        HttpClient client = new DefaultHttpClient() ;
        HttpPost postRequest = new HttpPost ("http://localhost:9998/validate/upload") ;
        try
        {
            File file = new File("/Users/mohamed/Downloads/testing/TestTopology.nt") ;
            String fileDescription = "";
            //Set various attributes
            MultipartEntity multiPartEntity = new MultipartEntity () ;
            multiPartEntity.addPart("fileDescription", new StringBody(fileDescription != null ? fileDescription : "")) ;
            multiPartEntity.addPart("fileName", new StringBody("/Users/mohamed/Downloads/testing/TestTopology.nt" != null ? "/Users/mohamed/Downloads/testing/TestTopology.nt" : file.getName())) ;

            FileBody fileBody = new FileBody(file, "application/octect-stream") ;
            //Prepare payload
            multiPartEntity.addPart("attachment", fileBody) ;

            //Set to request body
            postRequest.setEntity(multiPartEntity) ;

            //Send request
            HttpResponse response = client.execute(postRequest) ;

            //Verify response if any
            if (response != null)
            {
                System.out.println(response.getStatusLine().getStatusCode());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace() ;
        }
        */

        /**Correct
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File("/Users/mohamed/Downloads/testing/TestTopology.nt"));
            byte[] data;
            try {
                data = IOUtils.toByteArray(inputStream);

                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost httpPost = new HttpPost("http://localhost:9998/validate/upload");

                InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(data), "TestTopology.nt");
                MultipartEntity multipartEntity = new MultipartEntity();
                multipartEntity.addPart("file", inputStreamBody);
                multipartEntity.addPart("format", new StringBody("N-TRIPLE"));
                httpPost.setEntity(multipartEntity);

                HttpResponse httpResponse = httpClient.execute(httpPost);

                // Handle response back from script.
                if(httpResponse != null) {

                } else { // Error, no response.

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
         */

        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File("/Users/mohamed/Downloads/testing/TestTopology.nt"));
            byte[] data;
            try {
                data = IOUtils.toByteArray(inputStream);

                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost httpPost = new HttpPost("http://localhost:9998/validate/upload");

                InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(data), "TestTopology.nt");
//                MultipartEntityBuilder.create().build();
//                MultipartEntity multipartEntity = new MultipartEntity();
//                multipartEntity.addPart("file", inputStreamBody);
//                multipartEntity.addPart("format", new StringBody("N-TRIPLE"));
//                httpPost.setEntity(multipartEntity);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                builder.addPart("file", inputStreamBody);
                builder.addPart("format", new StringBody("N-TRIPLE", ContentType.TEXT_PLAIN));
                httpPost.setEntity(builder.build());

                HttpResponse httpResponse = httpClient.execute(httpPost);

                String responseAsString = EntityUtils.toString(httpResponse.getEntity());

                // Handle response back from script.
                if(httpResponse != null) {

                } else { // Error, no response.

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

    }
}
