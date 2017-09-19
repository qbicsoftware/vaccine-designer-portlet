package helper;

public class DescriptionHandler {

    // PanelUpload
    private String uploadData_selectUpload = "Welcome to the <b>Interactive Vaccine Designer</b>. The functionality is based on  <a href=\"https://github.com/APERIM-EU/WP3-EpitopeSelector\"target=\"_blank\">EpitopeSelector</a>, a software package for RNA-Seq based neo-epitope selection. \n" +
            "\n" +
            "You can either <b>upload</b> your epitope prediction results from your computer or select one from our <b>database</b>.";
    private String uploadData_selectAlleleUpload = "You can now decide wether you want to <b>upload</b> an allele file or choose one from our <b>database</b>. The allele file has to contain the alleles for which the epitope prediction has been performed.";
    private String uploadData_selectStructure = "Your epitope prediction file has to be in one of the <b>following structures</b>. Choose the fitting structure.";
    private String uploadData_specifyColumns = "Specify the <b>column names</b> of your epitope prediction file. If the columns do not exist leave them empty.";
    private String uploadData_specifyAlleleExpression = "Specify the <b>allele expressions</b> as FPKM values.";
    private String uploadData_specifyAlleles = "Specify the corresponding <b>HLA alleles</b> and the <b>allele expressions</b> as FPKM values.";
    private String uploadData_databaseUploadAndAllele = "Choose the project containing your epitope prediction file and select the file. Finally choose the corresponding allele file and press <b>Upload</b>.";
    private String uploadData_databaseUpload = "Choose the project containing your epitope prediction file, select the file and finally press <b>Upload</b>.";
    private String uploadData_upload = "Choose your epitope prediction file from your computer's directory and press <b>Upload</b>.";
    private String uploadData_immValidatorDescription = "Please enter the column name of the immunogenicity to continue.";
    private String uploadData_hlaValidatorDescription = "Please enter valid HLA alleles.";
    private String uploadData_hlaExprValidatorDescription = "Please enter valid HLA expression values.";
    private String uploadData_columnMethod = "Column name of the prediction method.";
    private String uploadData_columnImm = "Column name of peptide immunogenicity.";
    private String uploadData_columnUncertainty = "Column name of prediction uncertainty.";
    private String uploadData_columnDistance = "Column name of distance-to-self calculation.";
    private String uploadData_columnTAA = "Column name specifying whether the peptide is a TAA or TSA (if not specified all peptides are assumed to be TSAs).";

    // PanelEpitopeSelection
    private String epitopeSelection = "Select the <b>peptide sequences</b> you would like to <b>exclude</b> or <b>include</b> in your final solution. Double click on the corresponding peptide and check one of the boxes. Click next to continue with the parameter settings.";

    // PanelParameters
    private String parameterSettings = "Set the parameter values to your wishes if you want change the default values.";
    private String parameter_numberOfEpitopes = "Specifies the number of epitopes to select.";
    private String parameter_numberOfTAA = "Specifies the maximum number of TAAs to select.";
    private String parameter_alleleConstraint = "Activates an allele coverage constraint with specified threshold.";
    private String parameter_antigenConstraint = "Activates an antigen coverage constraint with specified threshold.";
    private String parameter_overlapConstraint = "Activates an epitope overlapping constraint with specified threshold.";
    private String parameter_epitopeTreshold = "Specifies the binding/immunogenicity threshold for all alleles.";
    private String parameter_distanceThreshold = "Specifies the distance-to-self threshold for all alleles.";
    private String parameter_rank = "Activates rank based estimation of immunogenicity .";

    // PanelResults
    private String results = "Here are the <b>results</b> of your current session. You have now the following options: \n" +
            "<ul><li><b>Reset</b> all settings and upload new files</li> <li><b>Save</b> the current results locally on your computer</li> <li><b>Register</b> the current results in our database</li> <li><b>Change parameters</b> and <b>re-run</b> the epitope selection (adds another tab to your results) </li></ul>";

    // ParserScriptResults
    private String parseScriptResultError = "It was not possible to parse the result file. Please try again.";

    // UploaderInput
    private String uploadInputIOError = "It was not possible to upload your data. Please try again.";
    private String uploadInputFailedError = "The upload of your epitope prediction data failed. Please try again.";

    // WriterResults
    private  String writeResultOutputError = "Writing or Saving your results failed. Please try again.";

    // MyPortletUI
    private String databaseConnectionError = "The connection to the database could not have been established. Please log in or make sure you are allowed to use this portlet.";

    // WindowLoading
    private String window_loading = "Selecting the optimal set of epitopes...";
    private String window_success = "The epitope selection finished successfully.";
    private String window_fail = "The epitope selection failed. Please try again using different parameters.";

    // LayoutMain
    private String noAlleleFileSelected = "Choose an allele file from the database to continue.";
    private String fileProcessError = "Your file could not be processed by the vaccine designer. Please try again and make sure all your columns and your allele file are specified correctly.";
    private String nextButtonDescription = "Continue with the next step.";
    private String registerButtonDescription = "Register all results of the current session in the database.";
    private String registerError = "It was not possible to register your epitope selection result file in the database. Please try again later.";
    private String registerSuccess = "Your file was registered successfully in the database.";
    private String runButtonDescription = "Run epitope selection.";
    private String rerunButtonDescription = "Re-run epitope selection.";
    private String resetButtonDescription = "Reset all settings and upload new files.";
    private String resetButtonSuccess = "Reset successful. You can start from the beginning now.";
    private String writeInputError = "It was not possible to prepare the obligatory input files. Please try again.";
    private String processingDataError = "It was not possible to process your input or allele file. Please try again and make sure all your columns and your allele file are specified correctly.";
    private String uploadSuccess = "Your epitope prediction file was uploaded successfully.";
    private String computationError = "The epitope selection on our server was interrupted. Please try again.";
    private String saveButtonDescription = "Save result file on your computer";

    public DescriptionHandler() {

    }

    public String getUploadData_selectUpload() {
        return uploadData_selectUpload;
    }

    public String getUploadData_selectAlleleUpload() {
        return uploadData_selectAlleleUpload;
    }

    public String getUploadData_selectStructure() {
        return uploadData_selectStructure;
    }

    public String getUploadData_specifyColumns() {
        return uploadData_specifyColumns;
    }

    public String getUploadData_specifyAlleleExpression() {
        return uploadData_specifyAlleleExpression;
    }

    public String getUploadData_specifyAlleles() {
        return uploadData_specifyAlleles;
    }

    public String getUploadData_databaseUploadAndAllele() {
        return uploadData_databaseUploadAndAllele;
    }

    public String getUploadData_databaseUpload() {
        return uploadData_databaseUpload;
    }

    public String getUploadData_upload() {
        return uploadData_upload;
    }

    public String getEpitopeSelection() {
        return epitopeSelection;
    }

    public String getParameterSettings() {
        return parameterSettings;
    }

    public String getParameter_numberOfEpitopes() {
        return parameter_numberOfEpitopes;
    }

    public String getParameter_numberOfTAA() {
        return parameter_numberOfTAA;
    }

    public String getParameter_alleleConstraint() {
        return parameter_alleleConstraint;
    }

    public String getParameter_antigenConstraint() {
        return parameter_antigenConstraint;
    }

    public String getParameter_overlapConstraint() {
        return parameter_overlapConstraint;
    }

    public String getParameter_epitopeTreshold() {
        return parameter_epitopeTreshold;
    }

    public String getParameter_distanceThreshold() {
        return parameter_distanceThreshold;
    }

    public String getParameter_rank() {
        return parameter_rank;
    }

    public String getResults() {
        return results;
    }

    public String getParseScriptResultError() {
        return parseScriptResultError;
    }

    public String getUploadInputIOError() {
        return uploadInputIOError;
    }

    public String getUploadInputFailedError() {
        return uploadInputFailedError;
    }

    public String getWriteResultOutputError() {
        return writeResultOutputError;
    }

    public String getDatabaseConnectionError() {
        return databaseConnectionError;
    }

    public String getWindow_loading() {
        return window_loading;
    }

    public String getWindow_success() {
        return window_success;
    }

    public String getWindow_fail() {
        return window_fail;
    }

    public String getNoAlleleFileSelected() {
        return noAlleleFileSelected;
    }

    public String getFileProcessError() {
        return fileProcessError;
    }

    public String getNextButtonDescription() {
        return nextButtonDescription;
    }

    public String getRegisterButtonDescription() {
        return registerButtonDescription;
    }

    public String getRegisterError() {
        return registerError;
    }

    public String getRegisterSuccess() {
        return registerSuccess;
    }

    public String getResetButtonDescription() {
        return resetButtonDescription;
    }

    public String getResetButtonSuccess() {
        return resetButtonSuccess;
    }

    public String getWriteInputError() {
        return writeInputError;
    }

    public String getProcessingDataError() {
        return processingDataError;
    }

    public String getUploadSuccess() {
        return uploadSuccess;
    }

    public String getComputationError() {
        return computationError;
    }

    public String getUploadData_hlaValidatorDescription() {
        return uploadData_hlaValidatorDescription;
    }

    public String getUploadData_hlaExprValidatorDescription() {
        return uploadData_hlaExprValidatorDescription;
    }

    public String getUploadData_immValidatorDescription() {
        return uploadData_immValidatorDescription;
    }

    public String getUploadData_columnMethod() {
        return uploadData_columnMethod;
    }

    public String getUploadData_columnImm() {
        return uploadData_columnImm;
    }

    public String getUploadData_columnUncertainty() {
        return uploadData_columnUncertainty;
    }

    public String getUploadData_columnDistance() {
        return uploadData_columnDistance;
    }

    public String getUploadData_columnTAA() {
        return uploadData_columnTAA;
    }

    public String getRunButtonDescription() {
        return runButtonDescription;
    }

    public String getRerunButtonDescription() {
        return rerunButtonDescription;
    }

    public String getSaveButtonDescription() {
        return saveButtonDescription;
    }
}
