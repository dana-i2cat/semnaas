package org.opennaas.extensions.network.model.validation;

import com.hp.hpl.jena.reasoner.ValidityReport;

import javax.annotation.Resource;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed Morsey on 21/10/14.
 */
@XmlRootElement(name = "ValidityReports")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(ResourceElement.class)
public class ValidationReport {
    @XmlElement(name = "ValidityReport")
    List<ValidityReport.Report> errorList;

    public List<ValidityReport.Report> getErrorList() {
        return errorList;
    }

    public ValidationReport(List<ValidityReport.Report> errorList) {
        this.errorList = errorList;
    }

    public ValidationReport() {
        errorList = new ArrayList<>();
    }
}
