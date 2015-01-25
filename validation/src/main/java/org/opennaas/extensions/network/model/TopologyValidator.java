package org.opennaas.extensions.network.model;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;
import org.apache.log4j.Logger;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mohamed Morsey on 10/10/14.
 * Validator for RDF based on the schema
 */
public class TopologyValidator {

    private static Reasoner owlReasoner = ReasonerRegistry.getOWLReasoner();

    private static Reasoner pelletReasoner = PelletReasonerFactory.theInstance().create();
    private static OntModel ontologyModel = ModelFactory.createOntologyModel();
//    private static InfModel reasoningModel;

    private static Logger logger = Logger.getLogger(TopologyValidator.class);

    static {
        try {
            InputStream ontologyFileStream = new FileInputStream("/Users/mohamed/UniversityOfAmsterdam/Ontology/NML/nml-NewVersion_Ver10_No_Instances.owl");

            ontologyModel.read(ontologyFileStream, null);
            owlReasoner = owlReasoner.bindSchema(ontologyModel);
            pelletReasoner = pelletReasoner.bindSchema(ontologyModel);
            PelletOptions.USE_TRACING = true;
            PelletOptions.VALIDATE_ABOX = true;

            ontologyFileStream.close();
        }
        catch (FileNotFoundException exp){
            logger.error("Ontology file not found, validation cannot continue");
        }
        catch (IOException exp){
            logger.error("Ontology file cannot be closed");
        }
    }

    /**
     * Validate the passed model against an ontology
     * @param modelToValidate   The model that should be validated
     * @return  A list containing the errors found in the model, if the model is correct, and empty list is returned
     */
    public static List<ValidityReport.Report> validate(Model modelToValidate){


//        uploadedFileModel.read("file://" + file.getAbsolutePath()) ;







//        UploaderView.errorsTable.setContainerDataSource(reasoningModel.validate().getReports());
//        UploaderView.errorsTable.setVisibleColumns(new Object[] {
//                "No.", "Error" });

//        InfModel inferredModel = ModelFactory.createInfModel(owlReasoner, modelToValidate);
                InfModel inferredModel = ModelFactory.createInfModel(pelletReasoner, modelToValidate);

//        /////////////
//        ExtendedIterator<Statement> stmts =reasoningModel.listStatements(new ResourceImpl("http://www.science.uva.nl/research/sne/nml/2014/08#NW1"), null, "");
//        while(stmts.hasNext()){
//            Statement stmt = stmts.next();
//            logger.info(stmt);
//        }
//
//
//        /////////////


        Iterator<ValidityReport.Report> errorIterator = inferredModel.validate().getReports();
        List<ValidityReport.Report> errorReports = new ArrayList<ValidityReport.Report>();
        int errorCounter = 0;
        while(errorIterator.hasNext()){
            ValidityReport.Report error = errorIterator.next();
            errorReports.add(error);
            errorCounter++;
        }

        errorReports.addAll(validateConnections(inferredModel));

        return errorReports;
    }

    /**
     * Validates that each port is reachable from all other ports
     * @param modelToValidate   The model that should be validated
     * @return  List containing the warnings about unreachability, if no one found an empty list is returned
     */
    private static List<ValidityReport.Report> validateConnections(InfModel modelToValidate){

        List<ValidityReport.Report> listUnreachabilityWarnings = new ArrayList<ValidityReport.Report>();

        String prefixes = "PREFIX sne:<http://ivi.fnwi.uva.nl/sne/indl/nml/2014/10#>\n" +
                "PREFIX port:<http://ivi.fnwi.uva.nl/sne/indl/nml/resource/port/>\n" ;

        //Query to list all ports in the request, so we can later make sure that there is a path from each port to the other
        String queryStringListAllPorts = prefixes +
                "SELECT DISTINCT ?port WHERE {?port a sne:Port }";


        Query queryListAllPorts = QueryFactory.create(queryStringListAllPorts) ;
        QueryExecution qexec = QueryExecutionFactory.create(queryListAllPorts, modelToValidate);
        ResultSet resultingPorts = qexec.execSelect() ;

        ArrayList<String> listPorts = new ArrayList<String>();

        while (resultingPorts.hasNext()){
            QuerySolution sln= resultingPorts.next();
//            logger.info(sln.get("port"));
            listPorts.add(sln.get("port").asResource().getURI());
        }

        logger.info(listPorts.size() + " ports found in the passed request");

        String queryCheckPath = prefixes +
                "ASK WHERE {<%s> (sne:isSource/sne:hasSink)+ <%s>  }";

//                String queryCheckPath = prefixes +
//                "ASK WHERE {<%s> (sne:isSink/sne:hasSource)+ <%s>  }";

        //Loop over all ports to make sure that there is a path from each one to all other ports
        for(String startPort:listPorts){

            for(String endPort:listPorts) {

                //if the start and end ports are the same, then simple skip it
                if(startPort.compareTo(endPort) ==0 )
                    continue;

                //prepare the required query, after replacing the placeholders with the actual values
                String actualConnectionQuery = String.format(queryCheckPath, startPort, endPort);
//                String actualConnectionQuery = String.format(queryCheckPath, endPort, startPort );

                Query queryListReachablePorts = QueryFactory.create(actualConnectionQuery) ;
                qexec = QueryExecutionFactory.create(queryListReachablePorts, modelToValidate);

                //Execute an ASK query and check the results
                boolean isReachable = qexec.execAsk() ;

                if(isReachable)
                    logger.info("Port " + endPort + " is reachable from port " + startPort);
                else {
                    String unreachabilityWarning = "Port " + endPort + " is NOT reachable from port " + startPort;
                    logger.info(unreachabilityWarning);
                    listUnreachabilityWarnings.add(new ValidityReport.Report(false, "Unreachability", unreachabilityWarning));
                }

            }
        }

        return listUnreachabilityWarnings;
    }
}
