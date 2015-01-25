
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.apache.tools.ant.filters.StringInputStream;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.opennaas.extensions.network.model.TopologyValidator;


import java.io.*;
import java.util.List;

/**
 * Created by Mohamed Morsey on 05/10/14.
 */
@PreserveOnRefresh
@Theme("valo")
public class NaaSRequestView extends UI implements Upload.Receiver, Upload.SucceededListener {

    private static Reasoner owlReasoner = ReasonerRegistry.getOWLReasoner();

    private static Reasoner pelletReasoner = PelletReasonerFactory.theInstance().create();

    public static boolean uploadedSucceeded = false;

    final Label lblUploadStatus = new Label();

    VerticalLayout mainLayout;
    Label lblHeader;
    Upload rdfFileUploader;
    Table errorsTable;
    TabSheet RDFDataInputTab;
    TextArea rdfInputText;
    GridLayout layoutUploadRequest;
    GridLayout layoutDirectInputRequest;
    ComboBox cmbRDFFormatsDirectInput;
    ComboBox cmbRDFFormatsUpload;
    Button btnValidateTextRequest;

    @Override
    public void init(VaadinRequest request) {

        Page.getCurrent().setTitle("OpenNaaS Request Form");
        mainLayout = new VerticalLayout();
        mainLayout.setCaption("OpenNaaS Request Form");
//        mainLayout.getParent().setCaption("OpenNaaS Request Form");
        mainLayout.setHeight(100, Unit.PERCENTAGE);
        mainLayout.setWidth(100, Unit.PERCENTAGE);
        setTheme("valo");
        setContent(mainLayout);

        lblHeader = new Label("<b><h1>NaaS Request Validator!</b></h1>");
        HorizontalLayout layoutHeader = new HorizontalLayout();
        layoutHeader.setHeight(50, Unit.PIXELS);

        layoutHeader.addComponent(lblHeader);
//        lblHeader.setHeight(10, Unit.PIXELS);
        lblHeader.setContentMode(ContentMode.HTML);
        mainLayout.addComponent(layoutHeader);
        mainLayout.setExpandRatio(layoutHeader, 0.1f);


//        RDFFileUploader uploaderHandel = new RDFFileUploader();

        //Create the combobox containing the formats, and the button to validate, and add them to a container layout
        cmbRDFFormatsUpload = new ComboBox();
        cmbRDFFormatsUpload.addItem("RDF/XML");
        cmbRDFFormatsUpload.addItem("N-TRIPLE");
        cmbRDFFormatsUpload.addItem("TURTLE");
        cmbRDFFormatsUpload.addItem("N3");
        cmbRDFFormatsUpload.setNullSelectionAllowed(false);
        cmbRDFFormatsUpload.setTextInputAllowed(false);
        cmbRDFFormatsUpload.select("RDF/XML");
        cmbRDFFormatsUpload.setWidth(30, Unit.PERCENTAGE);

        rdfFileUploader  = new Upload("Select request file", this);
        rdfFileUploader.addSucceededListener(this);
        rdfFileUploader.setWidth(40, Unit.PERCENTAGE);
        rdfFileUploader.setHeight(20, Unit.PERCENTAGE);
//        rdfFileUploader.setContentMode

        rdfInputText = new TextArea();
        rdfInputText.setWidth(90, Unit.PERCENTAGE);
        rdfInputText.setHeight(100, Unit.PERCENTAGE);

        RDFDataInputTab = new TabSheet();

        //Create the container layouts that will be added to th tabs
        layoutUploadRequest = new GridLayout(2, 3);
        layoutUploadRequest.addComponent(cmbRDFFormatsUpload,0, 0);
        layoutUploadRequest.addComponent(rdfFileUploader,0, 1);
        layoutUploadRequest.setHeight(100, Unit.PERCENTAGE);
        layoutUploadRequest.setWidth(100, Unit.PERCENTAGE);
        layoutUploadRequest.setRowExpandRatio(0, 0.1f);
        layoutUploadRequest.setRowExpandRatio(1, 0.1f);
        layoutUploadRequest.setRowExpandRatio(2, 0.8f);

//        layoutDirectInputRequest = new HorizontalLayout();
//        layoutDirectInputRequest.addComponent(rdfInputText);
        layoutDirectInputRequest = new GridLayout(3, 3);
        //Set the width of each column
        layoutDirectInputRequest.setColumnExpandRatio(0, 0.05f);
        layoutDirectInputRequest.setColumnExpandRatio(1, 0.7f);
        layoutDirectInputRequest.setColumnExpandRatio(2, 0.25f);
        layoutDirectInputRequest.setRowExpandRatio(0, 0.8f);
        layoutDirectInputRequest.setRowExpandRatio(1, 0.1f);
        layoutDirectInputRequest.setRowExpandRatio(2, 0.1f);

        //Add the textbox
        layoutDirectInputRequest.addComponent(rdfInputText, 1, 0);
        layoutDirectInputRequest.setSizeFull();

        //Create the combobox containing the formats, and the button to validate, and add them to a container layout
        cmbRDFFormatsDirectInput = new ComboBox();
        cmbRDFFormatsDirectInput.addItem("RDF/XML");
        cmbRDFFormatsDirectInput.addItem("N-TRIPLE");
        cmbRDFFormatsDirectInput.addItem("TURTLE");
        cmbRDFFormatsDirectInput.addItem("N3");
        cmbRDFFormatsDirectInput.setNullSelectionAllowed(false);
        cmbRDFFormatsDirectInput.setTextInputAllowed(false);
        cmbRDFFormatsDirectInput.select("RDF/XML");
        cmbRDFFormatsDirectInput.setWidth(100, Unit.PERCENTAGE);

        btnValidateTextRequest = new Button("Validate");
        btnValidateTextRequest.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    InputStream streamDirectInput = new StringInputStream(rdfInputText.getValue());
                    Model inputModel = ModelFactory.createDefaultModel();

//                    inputModel.read(streamDirectInput, null, cmbRDFFormatsDirectInput.getValue().toString());

                    RDFReader jenaReader = inputModel.getReader(cmbRDFFormatsDirectInput.getValue().toString());

                    jenaReader.read(inputModel, streamDirectInput, null);

                    List<ValidityReport.Report> errorReports = TopologyValidator.validate(inputModel);
                    renderErrors(errorReports);

//                    streamDirectInput.close();

                }
                catch (Exception exp){
                    errorsTable.removeAllItems();
                    errorsTable.addItem(new Object[]{1 , "Error", "Format", exp.getMessage()}, 0);
                }
            }
        });

        GridLayout layoutFormatValidator = new GridLayout(3, 1);
        layoutFormatValidator.setWidth(400, Unit.PIXELS);

        Label lblTextFormat = new Label("<b>Format</b>");
        lblTextFormat.setContentMode(ContentMode.HTML);
        layoutFormatValidator.addComponent(lblTextFormat, 0, 0);
        layoutFormatValidator.addComponent(cmbRDFFormatsDirectInput, 1, 0);
        layoutFormatValidator.addComponent(btnValidateTextRequest, 2, 0);
        layoutFormatValidator.setComponentAlignment(lblTextFormat, Alignment.MIDDLE_LEFT);
        layoutFormatValidator.setComponentAlignment(cmbRDFFormatsDirectInput, Alignment.MIDDLE_LEFT);
        layoutFormatValidator.setComponentAlignment(btnValidateTextRequest, Alignment.MIDDLE_RIGHT);
        layoutFormatValidator.setColumnExpandRatio(0, 0.2f);
        layoutFormatValidator.setColumnExpandRatio(1, 0.4f);
        layoutFormatValidator.setColumnExpandRatio(2, 0.4f);

        layoutDirectInputRequest.addComponent(layoutFormatValidator, 2, 0);


        layoutDirectInputRequest.setWidth(100, Unit.PERCENTAGE);
        layoutDirectInputRequest.setHeight(100, Unit.PERCENTAGE);

        //Add the tabs
        RDFDataInputTab.addTab(layoutUploadRequest, "Upload Request");
        RDFDataInputTab.addTab(layoutDirectInputRequest, "Direct Input");
        RDFDataInputTab.setWidth(100, Unit.PERCENTAGE);
        RDFDataInputTab.setHeight(100, Unit.PERCENTAGE);
        mainLayout.addComponent(RDFDataInputTab);
        mainLayout.setExpandRatio(RDFDataInputTab, 0.5f);

//        final Upload rdfFileUploader = new Upload("Select file", this);
//        rdfFileUploader.addSucceededListener(this);
//
//
//        mainLayout.addComponent(rdfFileUploader);


//        mainLayout.addComponent(lblUploadStatus);

        errorsTable = new Table();
        errorsTable.setSizeFull();
        errorsTable.setSelectable(true);
        errorsTable.setMultiSelect(true);
        errorsTable.setImmediate(true);

        errorsTable.addContainerProperty("No.", Integer.class, null);
        errorsTable.addContainerProperty( "Error/Warning" ,  String.class, null);
        errorsTable.addContainerProperty( "ErrorType" ,  String.class, null);
        errorsTable.addContainerProperty("ErrorDescription", String.class, null);

        errorsTable.setColumnHeaders(new String[]{"No.", "Error/Warning", "Error Type", "Error Description"});
        errorsTable.setHeight(100, Unit.PERCENTAGE);

        mainLayout.addComponent(errorsTable);
        mainLayout.setExpandRatio(errorsTable, 0.4f);
//        mainLayout.addComponent(btnSubmitFile);

//        ClientConfig config = new DefaultClientConfig();

//        Logger logger;
//
//        Client client = Client.create(config);
//        WebResource service = client.resource(getBaseURI());
////
//////        ClientResponse response = service.path("parameters").path("todos")
//////                .path(todo.getId()).accept(MediaType.APPLICATION_XML)
//////                .put(ClientResponse.class, todo);
////
//        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
//        params.add("parameter1", "JCG");
//        params.add("parameter2", "xyz");
//        ClientResponse response = service.path("parameters").queryParams(params).get(ClientResponse.class);

    }


    File file;         // File to write to.

    @Override
    public OutputStream receiveUpload(String filename,
                                      String MIMEType) {
        FileOutputStream fos = null; // Output stream to write to
        file = new File("/tmp/" + filename);
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


//        Model uploadedFileModel = ModelFactory.createDefaultModel();
//
//        uploadedFileModel.read("file://" + file.getAbsolutePath()) ;
//
//        PelletOptions.USE_TRACING = true;
//
//        InfModel reasoningModel = ModelFactory.createInfModel(owlReasoner, uploadedFileModel);
//
//
////        UploaderView.errorsTable.setContainerDataSource(reasoningModel.validate().getReports());
////        UploaderView.errorsTable.setVisibleColumns(new Object[] {
////                "No.", "Error" });
//
//        errorsTable.removeAllItems();
//        Iterator<ValidityReport.Report> errors = reasoningModel.validate().getReports();
//
//        int errorCounter = 0;
//        while(errors.hasNext()){
//            ValidityReport.Report error = errors.next();
//            errorsTable.addItem(new Object[]{errorCounter+1 , error.getType(), error.getDescription()}, errorCounter);
//            errorCounter++;
//        }



        try {



        Model uploadedFileModel = ModelFactory.createDefaultModel();
        RDFReader jenaReader = uploadedFileModel.getReader(cmbRDFFormatsUpload.getValue().toString());

        //Load the file to the model, and the format from the combobox
        jenaReader.read(uploadedFileModel,new FileInputStream(file), null) ;



        List<ValidityReport.Report> errorReports = TopologyValidator.validate(uploadedFileModel);

//        int errorCounter = 0;
//        for(ValidityReport.Report error:errorReports){
//            String isErrorOrWarning = error.isError() ? "Error":"Warning";
//            errorsTable.addItem(new Object[]{errorCounter+1 , isErrorOrWarning, error.getType(), error.getDescription()}, errorCounter);
//            errorCounter++;
//        }

        renderErrors(errorReports);

        }
        catch (Exception exp){
            errorsTable.removeAllItems();
            errorsTable.addItem(new Object[]{1 , "Error", "Format", exp.getMessage()}, 0);
        }
    }

    /**
     * Renders the list of errors to the table designated for that
     * @param errorReports  The list of errors
     */
    private void renderErrors(List<ValidityReport.Report> errorReports){

        errorsTable.removeAllItems();

        int errorCounter = 0;
        for(ValidityReport.Report error:errorReports){
            String isErrorOrWarning = error.isError() ? "Error":"Warning";
            errorsTable.addItem(new Object[]{errorCounter+1 , isErrorOrWarning, error.getType(), error.getDescription()}, errorCounter);
            errorCounter++;
        }
    }
}
