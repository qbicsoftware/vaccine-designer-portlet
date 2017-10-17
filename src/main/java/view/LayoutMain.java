package view;

import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Sample;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.themes.ValoTheme;
import helper.*;
import life.qbic.MyPortletUI;
import life.qbic.openbis.openbisclient.OpenBisClient;
import life.qbic.portal.liferayandvaadinhelpers.main.LiferayAndVaadinUtils;
import model.DatasetBean;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The class {@link LayoutMain} represents the main layout which is always shown. It handles all the
 * computation and view changes. All important paths are set here.
 *
 * @author spaethju
 */
@SuppressWarnings("serial")
public class LayoutMain extends VerticalLayout implements SucceededListener {

    private static Boolean hasType;
    private static Boolean hasUnc;
    private static Boolean hasDist;
    private static Boolean hasTranscriptExpression;
    private Boolean success;
    private Button nextButton, runButton, downloadButton, resetButton, registerButton;
    private HorizontalLayout buttonsLayout;
    private Accordion contentAccordion;
    private PanelUpload uploadPanel;
    private PanelEpitopeSelection epitopeSelectionPanel;
    private PanelParameters parameterPanel;
    private PanelResults resultsPanel;
    private WriterScriptInput inputWriter;
    private ArrayList<File> downloadFiles;
    private Process proc;
    private int maxLength;
    private DBFileHandler fileHandler;
    private Boolean gridActivated;
    private Filterable filterable;
    private OpenBisClient openbis;
    private List<Project> projects;
    private SCPFile scpFile;
    private RandomCharGenerator generator;
    private String code, path, folder;
    private String sampleBarcode;
    private String sampleCode;
    private String alleleFileCode, alleleFileName, alleleDssPath, alleleFileFolder;
    private BeanItemContainer<DatasetBean> container;
    private BeanItemContainer<DatasetBean> alleleFileContainer = new BeanItemContainer<DatasetBean>(DatasetBean.class);
    private DescriptionHandler dh = new DescriptionHandler();

    //private String tmpPath = "/Users/spaethju/Desktop/";
    private String tmpPath = "/tmp/";
    //private String homePath = "/Users/spaethju/";
    private String homePath = "/home/luser/";
    private String tmpPathRemote = "/home/jspaeth/";
    private String outputPath = "";
    private String inputPath = "";
    private String allelePath = "";
    private String includePath = "";
    private String excludePath = "";
    private String tmpResultPath = "";
    private String tmpDownloadPath = "";
    private String remoteOutputPath = "";
    private String tmpAllelesPath = "";
    private String random = "";
    private String epitopeSelectorVM = "jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de:";
    private String dropbox = "qeana08@data.qbic.uni-tuebingen.de";
    private String registerPath = "qeana08@data.qbic.uni-tuebingen.de:/mnt/nfs/qbic/dropboxes/qeana08_qbic/incoming";
    private String filename = "";


    /**
     * Constructor creating the simple standard layout
     */
    public LayoutMain(List<Project> projects, OpenBisClient openbis, Boolean success) {
        this.success = success;
        this.openbis = openbis;
        this.projects = projects;
        scpFile = new SCPFile();
        init();
        initDatabase();
    }

    public LayoutMain(Boolean success) {
        this.success = success;
        init();
    }

    private void init() {
        this.addComponents(createContentAccordion(), createButtonsLayout());
        uploadPanel.getDataSelectionDatabaseButton().setEnabled(false);

        this.setMargin(true);
        this.setSpacing(true);

        this.setIcon(FontAwesome.CUBES);
        contentAccordion.setSelectedTab(uploadPanel);
        downloadFiles = new ArrayList<>();

        generator = new RandomCharGenerator();
        random = generator.generateRandomChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 10);

        String username = "";
        if (LiferayAndVaadinUtils.isLiferayPortlet()) {
            username = LiferayAndVaadinUtils.getUser().getScreenName();
        }

        File f = new File(tmpPath + username);
        try {
            if (f.exists() && f.isDirectory()) {
                FileUtils.cleanDirectory(f);
                FileUtils.forceDelete(f);
                Runtime.getRuntime().exec("mkdir " + tmpPath + username);
            } else {
                Runtime.getRuntime().exec("mkdir " + tmpPath + username);
            }
        } catch (IOException e) {
            MyPortletUI.logger.error("Could not write the folder on the file system");
            e.printStackTrace();
        }

        outputPath = tmpPath + username + "/output.txt";
        inputPath = tmpPath + username + "/input.txt";
        allelePath = tmpPath + username + "/alleles.txt";
        includePath = tmpPath + username + "/include.txt";
        excludePath = tmpPath + username + "/exclude.txt";
        tmpResultPath = tmpPath + username + "/tmp_result.txt";
        tmpDownloadPath = tmpPath + username + "/tmp_download.txt";
        tmpAllelesPath = tmpPath + username + "/tmp_alleles.txt";
    }

    private void initDatabase() {
        uploadPanel.getDataSelectionDatabaseButton().setEnabled(true);
        gridActivated = false;
        fileHandler = new DBFileHandler(openbis);


        for (Project project : projects) {
            uploadPanel.getProjectSelectionCB().addItem(project.getIdentifier());
        }

        uploadPanel.getProjectSelectionCB().addValueChangeListener((ValueChangeListener) event -> {
            List<DataSet> dataSets = new ArrayList<>();
            try {
                dataSets = openbis.getDataSetsOfProjectByIdentifier(uploadPanel.getProjectSelectionCB().getValue().toString());
            } catch (NullPointerException e) {
                Utils.notification("Error", "Dataset could not be found in openbis", "error");
            }
            container = fileHandler.fillTable(dataSets);
            alleleFileContainer.removeAllItems();
            alleleFileContainer = fileHandler.fillTable(dataSets);
            uploadPanel.getAlleleFileSelectionCB().removeAllItems();
            uploadPanel.getDatasetGrid().setContainerDataSource(container);
            if (uploadPanel.getDatasetGrid().getContainerDataSource().size() > 0) {
                uploadPanel.getDatasetGrid().setEnabled(true);
                if (uploadPanel.getAlleleFileUpload()) {
                    uploadPanel.getAlleleFileSelectionCB().setVisible(true);
                    uploadPanel.getAlleleFileSelectionCB().setValue("");
                    uploadPanel.getAlleleFileSelectionCB().setInputPrompt("");
                    for (Object itemId : alleleFileContainer.getItemIds()) {
                        Item item = alleleFileContainer.getItem(itemId);
                        String type = item.getItemProperty("type").toString();
                        filename = item.getItemProperty("name").toString();
                        if (type.equalsIgnoreCase("Q_WF_NGS_HLATYPING_RESULTS") && (filename.contains(".txt") || filename.contains(".tsv") || filename.contains(".alleles"))) {
                            uploadPanel.getAlleleFileSelectionCB().addItem(item.getItemProperty("name"));
                        }
                    }
                }
                filterable = (Filterable) uploadPanel.getDatasetGrid().getContainerDataSource();
                filterable.removeAllContainerFilters();
                filter("type", "Q_WF_NGS_EPITOPE_PREDICTION_RESULTS");
                filter("name", ".tsv");
                if (!gridActivated) {
                    uploadPanel.getDatasetGrid().getColumn("size").setHeaderCaption("Size (KB)");
                    uploadPanel.getDatasetGrid().removeColumn("children");
                    uploadPanel.getDatasetGrid().removeColumn("sampleIdentifier");
                    uploadPanel.getDatasetGrid().removeColumn("type");
                    uploadPanel.getDatasetGrid().removeColumn("properties");
                    uploadPanel.getDatasetGrid().removeColumn("id");
                    uploadPanel.getDatasetGrid().removeColumn("projectBean");
                    uploadPanel.getDatasetGrid().removeColumn("dataSetTypeCode");
                    uploadPanel.getDatasetGrid().removeColumn("code");
                    uploadPanel.getDatasetGrid().removeColumn("dssPath");
                    uploadPanel.getDatasetGrid().setHeightMode(HeightMode.ROW);
                    uploadPanel.getDatasetGrid().setHeightByRows(5);
                    uploadPanel.getDatasetGrid().setVisible(true);
                    gridActivated = true;
                }
            } else {
                uploadPanel.getDatasetGrid().setEnabled(false);
                uploadPanel.getDatasetGrid().setContainerDataSource(container);
            }
        });

        uploadPanel.getAlleleFileSelectionCB().addValueChangeListener((ValueChangeListener) event -> {
            for (Object itemId : alleleFileContainer.getItemIds()) {
                Item item = alleleFileContainer.getItem(itemId);
                String filename = item.getItemProperty("name").toString();
                if (filename.equalsIgnoreCase(uploadPanel.getAlleleFileSelectionCB().getValue().toString())) {
                    alleleFileName = item.getItemProperty("name").toString();
                    alleleFileCode = item.getItemProperty("code").toString();
                    alleleDssPath = item.getItemProperty("dssPath").toString();
                }
            }
        });

        uploadPanel.getUploadButton().addClickListener((ClickListener) event -> {
            if (uploadPanel.getAlleleFileSelectionCB().isValid() || !uploadPanel.getAlleleFileUpload()) {
                Project project = openbis.getProjectByIdentifier(uploadPanel.getProjectSelectionCB().getValue().toString());
                List<Sample> allSamples =
                        openbis.getSamplesWithParentsAndChildrenOfProjectBySearchService(project.getIdentifier());
                for (Sample sample : allSamples) {
                    if (sample.getSampleTypeCode().equals("Q_NGS_SINGLE_SAMPLE_RUN")) {
                        sampleBarcode = sample.getCode();
                    }
                }
                String filename = uploadPanel.getSelected().getBean().getName();
                code = uploadPanel.getSelected().getBean().getCode();
                path = uploadPanel.getSelected().getBean().getDssPath();
                folder = path.replace("original/", "").replace(filename, "");
                sampleCode = openbis.getSampleByIdentifier(uploadPanel.getSelected().getBean().getSampleIdentifier()).getCode();
                Path destination = Paths.get(tmpDownloadPath);
                try {
                    InputStream in = openbis.getDatasetStream(code, folder + filename);
                    Files.copy(in, destination);
                    File file = new File(tmpDownloadPath);
                    if (uploadPanel.getAlleleFileUpload()) {
                        alleleFileFolder = alleleDssPath.replace("original/", "").replace(alleleFileName, "");
                        InputStream inAllele = openbis.getDatasetStream(alleleFileCode, alleleFileFolder + alleleFileName);
                        Files.copy(inAllele, Paths.get(tmpAllelesPath));
                        File alleleFile = new File(tmpAllelesPath);
                        ParserAlleleFile alleleParser = new ParserAlleleFile();
                        uploadPanel.setAlleles(alleleParser.parse(alleleFile));
                        Files.deleteIfExists(Paths.get(tmpAllelesPath));
                    }
                    processingData(file);
                    Files.delete(destination);
                } catch (AllelesException e) {
                    Utils.notification("Error", "Your alleles did not fit to your epitope prediction file. Please try again.", "error");
                    MyPortletUI.logger.error("Alleles do not fit to the epitope prediction file");
                    reset();
                } catch (Exception e) {
                    MyPortletUI.logger.error("Something went wrong while uploading/Parsing the file");
                    Utils.notification("Upload failed", dh.getUploadInputFailedError(), "error");
                    e.printStackTrace();
                    reset();
                }
            } else {
                Utils.notification("Error", dh.getNoAlleleFileSelected(), "error");
            }
        });
    }

    /**
     * Creates the content accordion. Every information is shown inside the tabs of the accordion
     * except of the buttons.
     *
     * @return accordion with the four tabs "Upload Data", "Epitope Selection", "Parameter Settings"
     * and "Results"
     */
    private Accordion createContentAccordion() {
        uploadPanel = new PanelUpload();
        uploadPanel.setImmediate(true);
        uploadPanel.getInputUpload().addSucceededListener(this);

        epitopeSelectionPanel = new PanelEpitopeSelection();
        epitopeSelectionPanel.setImmediate(true);
        parameterPanel = new PanelParameters();
        parameterPanel.setImmediate(true);
        resultsPanel = new PanelResults();
        resultsPanel.setImmediate(true);
        contentAccordion = new Accordion();
        contentAccordion.setImmediate(true);
        contentAccordion.setSizeFull();
        contentAccordion.addTab(uploadPanel, "Data Preperation");
        contentAccordion.getTab(uploadPanel).setIcon(FontAwesome.UPLOAD);
        contentAccordion.getTab(uploadPanel).setEnabled(true);
        contentAccordion.addTab(epitopeSelectionPanel, "Epitope Pre-Selection");
        contentAccordion.getTab(epitopeSelectionPanel).setIcon(FontAwesome.MOUSE_POINTER);
        contentAccordion.getTab(epitopeSelectionPanel).setEnabled(false);
        contentAccordion.addTab(parameterPanel, "Parameter Adjustment");
        contentAccordion.getTab(parameterPanel).setIcon(FontAwesome.SLIDERS);
        contentAccordion.getTab(parameterPanel).setEnabled(false);
        contentAccordion.addTab(resultsPanel, "Results Overview");
        contentAccordion.getTab(resultsPanel).setIcon(FontAwesome.PIE_CHART);
        contentAccordion.getTab(resultsPanel).setEnabled(false);
        contentAccordion.setStyleName("accordion-color");


        return contentAccordion;
    }

    /**
     * Creates a button bringing the user from the "Epitope Selection" tab to the "Parameter Settings"
     * tab
     *
     * @return next button
     */
    private Button createNextButton() {
        nextButton = new Button("Next");
        nextButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        nextButton.addStyleName(ValoTheme.BUTTON_SMALL);
        nextButton.setIcon(FontAwesome.ARROW_CIRCLE_O_RIGHT);
        nextButton.setDescription(dh.getNextButtonDescription());
        nextButton.addClickListener((ClickListener) event -> {
            epitopeSelectionPanel.getContainer().removeAllContainerFilters();
            if (!uploadPanel.getMethodColTf().getValue().equals("")) {
                epitopeSelectionPanel.applyMethodFilter();
                setParameterRange();
                parameterPanel.update();
            }
            epitopeSelectionPanel.getGeneTf().setValue("");
            epitopeSelectionPanel.getMutationTf().setValue("");
            epitopeSelectionPanel.getLengthTf().setValue("");
            epitopeSelectionPanel.getNeopeptideTf().setValue("");
            contentAccordion.getTab(parameterPanel).setEnabled(true);
            contentAccordion.getTab(epitopeSelectionPanel).setEnabled(true);
            contentAccordion.setSelectedTab(parameterPanel);

            buttonsLayout.removeComponent(nextButton);
            runButton = createRunButton();
            buttonsLayout.addComponent(runButton);
            setParameterRange();
        });
        nextButton.setEnabled(false);
        return nextButton;
    }

    private Button createRegisterButton() {
        registerButton = new Button("Register");
        registerButton.setDescription(dh.getRegisterButtonDescription());
        registerButton.setIcon(FontAwesome.UPLOAD);
        registerButton.setStyleName(ValoTheme.BUTTON_SMALL);
        registerButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        registerButton.addClickListener((ClickListener) event -> {
            try {
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
                String resultName = sampleBarcode + "_" + sampleCode + "_" + timeStamp + "_epitopeselection_result" + ".txt";
                Process copy_result = Runtime.getRuntime().exec("cp " + tmpResultPath + " " + tmpPath + LiferayAndVaadinUtils.getUser().getScreenName() + "/" + resultName);
                copy_result.waitFor();
                MyPortletUI.logger.info("cp " + tmpResultPath + " " + tmpPath + LiferayAndVaadinUtils.getUser().getScreenName() + "/" + resultName);
                scpFile.scpToRemote(homePath, tmpPath + LiferayAndVaadinUtils.getUser().getScreenName() + "/" + resultName, registerPath);
                Process markAsFinished = Runtime.getRuntime().exec("ssh -i " + homePath + ".ssh/key_rsa " + dropbox + " touch /mnt/nfs/qbic/dropboxes/qeana08_qbic/incoming/.MARKER_is_finished_" + resultName);
                Process remove_result = Runtime.getRuntime().exec("rm -f " + tmpPath + LiferayAndVaadinUtils.getUser().getScreenName() + "/" + resultName);
                remove_result.waitFor();
                MyPortletUI.logger.info("rm -f " + tmpPath + LiferayAndVaadinUtils.getUser().getScreenName() + "/" + resultName);
            } catch (IOException | InterruptedException e) {
                MyPortletUI.logger.error("Could not write the folder on the file system");
                Utils.notification("Error", dh.getRegisterError(), "error");
                e.printStackTrace();
            }
            Utils.notification("Success", dh.getRegisterSuccess(), "success");
            MyPortletUI.logger.info("Result registered");
        });
        registerButton.setVisible(false);
        return registerButton;
    }

    /**
     * Creates a button which resets the whole progress and allows a new upload of the data. The user
     * starts from the beginning again.
     *
     * @return reset button
     */
    private Button createResetButton() {
        resetButton = new Button("Reset");
        resetButton.setDescription(dh.getResetButtonDescription());
        resetButton.setIcon(FontAwesome.TIMES_CIRCLE_O);
        resetButton.setStyleName(ValoTheme.BUTTON_DANGER);
        resetButton.addStyleName(ValoTheme.BUTTON_SMALL);
        resetButton.addClickListener((ClickListener) event -> {
            reset();
            Utils.notification("Reset", dh.getResetButtonSuccess(), "success");
        });
        return resetButton;
    }

    /**
     * Creates the run button, running the computation of the optimal set of epitopes. Therefore it
     * runs a thread with the process NeoOptiTope.py and opens it with the previous adjustet
     * parameters and aruguments.
     *
     * @return run button
     */
    private Button createRunButton() {

        runButton = new Button("Run");
        runButton.setIcon(FontAwesome.PLAY_CIRCLE_O);
        runButton.setDescription(dh.getRunButtonDescription());
        runButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        runButton.addStyleName(ValoTheme.BUTTON_SMALL);
        runButton.addClickListener((ClickListener) (Button.ClickEvent event) -> {

            // remove the filters and set back the filters text fields
            epitopeSelectionPanel.getContainer().removeAllContainerFilters();
            if (!uploadPanel.getMethodColTf().getValue().equals("")) {
                epitopeSelectionPanel.applyMethodFilter();
                parameterPanel.update();
                setParameterRange();
            }
            epitopeSelectionPanel.getGeneTf().setValue("");
            epitopeSelectionPanel.getMutationTf().setValue("");
            epitopeSelectionPanel.getLengthTf().setValue("");
            epitopeSelectionPanel.getNeopeptideTf().setValue("");

            Boolean valuesCorrect = Boolean.FALSE;
            try {
                inputWriter = new WriterScriptInput(inputPath, allelePath, includePath, excludePath);
                setParameterRange();
                parameterPanel.getKSlider().validate();
                parameterPanel.getConsAlleleSlider().validate();
                parameterPanel.getConsAntigenSlider().validate();
                parameterPanel.getConsOverlapSlider().validate();
                valuesCorrect = true;

            } catch (InvalidValueException e) {
                Notification.show(e.getMessage());
                e.printStackTrace();
            }

            if (valuesCorrect) {
                runButton.setCaption("Re-Run");
                runButton.setDescription(dh.getRerunButtonDescription());
                try {
                    Process mkdir = Runtime.getRuntime().exec("ssh -i " + homePath + ".ssh/key_rsa jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de mkdir " + tmpPathRemote + random);
                    mkdir.waitFor();
                } catch (IOException | InterruptedException e) {
                    MyPortletUI.logger.error("Couldn't create folder on virtual machine.");
                    e.printStackTrace();
                }
                // set up script input
                ArrayList<String> p = new ArrayList<>();
                p.add("ssh");
                p.add("-i");
                p.add(homePath + ".ssh/key_rsa");
                p.add("jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de");

                p.add("singularity");
                p.add("run");
                p.add("--bind");
                p.add("/root/COIN/bin/:/usr/local/bin/");
                p.add("epitopeselector.img");

                p.add("-i");
                p.add(tmpPathRemote + random + "/input.txt");

                p.add("-imm");
                p.add("score");

                String taaCol = uploadPanel.getTaaColTf().getValue();
                if (!taaCol.equals("") && hasType) {
                    p.add("-taa");
                    p.add(uploadPanel.getTaaColTf().getValue());
                }
                String uncCol = uploadPanel.getUncertaintyColTf().getValue();
                if (!uncCol.equals("") && hasUnc) {
                    p.add("-u");
                    p.add(uploadPanel.getUncertaintyColTf().getValue());
                }
                String distCol = uploadPanel.getDistanceColTf().getValue();
                if (!distCol.equals("") && hasDist) {
                    p.add("-d");
                    p.add(uploadPanel.getDistanceColTf().getValue());
                }

                p.add("-a");
                p.add(tmpPathRemote + random + "/alleles.txt");

                p.add("-excl");
                p.add(tmpPathRemote + random + "/exclude.txt");

                p.add("-incl");
                p.add(tmpPathRemote + random + "/include.txt");

                p.add("-k");
                p.add(Integer.toString(parameterPanel.getKSlider().getValue().intValue()));

                p.add("-ktaa");
                p.add(Integer.toString(parameterPanel.getKtaaSlider().getValue().intValue()));

                p.add("-te");
                p.add(parameterPanel.getThreshEpitopeTF().getValue().replaceAll(",", "."));

                p.add("-td");
                p.add(parameterPanel.getThreshDistanceTF().getValue().replaceAll(",", "."));

                p.add("-o");
                remoteOutputPath = tmpPathRemote + random + "/output.txt";
                p.add(remoteOutputPath);


                p.add("-c_al");
                p.add(Double.toString(parameterPanel.getConsAlleleSlider().getValue()));

                p.add("-c_a");
                p.add(Double.toString(parameterPanel.getConsAntigenSlider().getValue()));

                p.add("-c_o");
                p.add(Integer.toString(parameterPanel.getConsOverlapSlider().getValue().intValue()));

                if (parameterPanel.getRankCB().getValue()) {
                    p.add("-r");
                }


                // writes alleles.txt, include.txt and exclude.txt
                try {
                    inputWriter.writeInputData(epitopeSelectionPanel.getContainer(), uploadPanel.getAlleles(), uploadPanel.getAllele_expressions(), uploadPanel.getTaaColTf().getValue(), uploadPanel.getUncertaintyColTf().getValue(), uploadPanel.getDistanceColTf().getValue(), hasTranscriptExpression, hasType, hasUnc, hasDist);
                } catch (IOException e) {
                    Utils.notification("Error!",
                            dh.getWriteInputError(), "error");
                    MyPortletUI.logger.error("Error while writing the input data");
                    e.printStackTrace();
                }

                runScript(p);
                runButton.setStyleName(null);
                runButton.addStyleName(ValoTheme.BUTTON_SMALL);
            }
        });

        return runButton;
    }

    /**
     * Creates a button which gives the user the opportunity to download all already computed results.
     *
     * @return download button
     */
    private Button createDownloadButton() {
        downloadButton = new Button("Save");
        downloadButton.setIcon(FontAwesome.DOWNLOAD);
        downloadButton.setDescription(dh.getSaveButtonDescription());
        downloadButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        downloadButton.addStyleName(ValoTheme.BUTTON_SMALL);
        downloadButton.setVisible(false);
        return downloadButton;
    }

    /**
     * Creates a layout presenting all buttons in a horizontal order.
     *
     * @return layout with buttons
     */
    private HorizontalLayout createButtonsLayout() {
        buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        resetButton = createResetButton();
        nextButton = createNextButton();
        downloadButton = createDownloadButton();
        registerButton = createRegisterButton();
        buttonsLayout.addComponents(resetButton, nextButton, downloadButton, registerButton);
        return buttonsLayout;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vaadin.ui.Upload.SucceededListener#uploadSucceeded(com.vaadin.ui.Upload.SucceededEvent)
     */
    @Override
    public void uploadSucceeded(SucceededEvent event) {
        UploaderInput uploader = uploadPanel.getInputReceiver();
        uploader.getProgress().setVisible(false);
        filename = event.getFilename();
        try {
            processingData(uploader.getTempFile());
        } catch (Exception e) {
            Utils.notification("Upload failed", dh.getProcessingDataError(), "error");
            e.printStackTrace();
            reset();
        }

    }

    private void processingData(File file) throws Exception {
        Boolean hasMethod;
        if (!uploadPanel.getHlaAsColumns()) {
            ParserInputAllelesAsRows parser = new ParserInputAllelesAsRows();
            parser.parse(file, uploadPanel.getMethodColTf().getValue(),
                    uploadPanel.getImmColTf().getValue(), uploadPanel.getTaaColTf().getValue(), uploadPanel.getUncertaintyColTf().getValue(), uploadPanel.getDistanceColTf().getValue(), uploadPanel.getTranscriptExpressionColTf().getValue(), uploadPanel.getAlleles());
//            if (!parser.checkAlleles(uploadPanel.getAlleles())){
//                throw new AllelesException("Allele files do not fit to the uploaded epitope prediction file.");
//            }
            hasType = parser.getHasType();
            hasDist = parser.getHasDist();
            hasUnc = parser.getHasUnc();
            hasTranscriptExpression = parser.getHasTranscriptExpression();
            epitopeSelectionPanel.setDataGrid(parser.getEpitopes(), uploadPanel.getMethodColTf().getValue().trim(), parser.getAlleles(), hasType, hasTranscriptExpression, hasUnc, hasDist);
            maxLength = parser.getMaxLength();
            if (uploadPanel.getTaaColTf().getValue().equals("")) {
                epitopeSelectionPanel.getDataGrid().removeColumn("type");
            } else {
                epitopeSelectionPanel.addTypeFilter();
            }
        } else if (uploadPanel.getHlaAsColumns()) {
            ParserInputAllelesAsColumns parser = new ParserInputAllelesAsColumns();
            parser.parse(file, uploadPanel.getMethodColTf().getValue(),
                    uploadPanel.getTaaColTf().getValue(), uploadPanel.getTranscriptExpressionColTf().getValue());
            hasType = parser.getHasType();
            hasDist = false;
            hasUnc = false;
            hasTranscriptExpression = parser.getHasTranscriptExpression();
            MyPortletUI.logger.info(hasType);
            MyPortletUI.logger.info(hasTranscriptExpression);
            epitopeSelectionPanel.setDataGrid(parser.getEpitopes(),
                    uploadPanel.getMethodColTf().getValue().trim(), parser.getAlleles(), hasType, hasTranscriptExpression, hasUnc, hasDist);
            maxLength = parser.getMaxLength();
            if (uploadPanel.getTaaColTf().getValue().equals("")) {
                epitopeSelectionPanel.getDataGrid().removeColumn("type");
            } else {
                epitopeSelectionPanel.addTypeFilter();
            }
        }

        contentAccordion.getTab(epitopeSelectionPanel).setEnabled(true);
        contentAccordion.getTab(uploadPanel).setStyleName("upload-succeeded");
        contentAccordion.getTab(uploadPanel).setEnabled(false);
        contentAccordion.getTab(uploadPanel).setEnabled(false);
        contentAccordion.setSelectedTab(epitopeSelectionPanel);
        contentAccordion.addSelectedTabChangeListener((SelectedTabChangeListener) event -> {
            epitopeSelectionPanel.getContainer().removeAllContainerFilters();
            if (!uploadPanel.getMethodColTf().getValue().equals("")) {
                epitopeSelectionPanel.applyMethodFilter();
                parameterPanel.update();
                setParameterRange();
            }
            epitopeSelectionPanel.getGeneTf().setValue("");
            epitopeSelectionPanel.getMutationTf().setValue("");
            epitopeSelectionPanel.getLengthTf().setValue("");
            epitopeSelectionPanel.getNeopeptideTf().setValue("");
        });
        contentAccordion.getTab(uploadPanel).setIcon(FontAwesome.CHECK_CIRCLE);
        buttonsLayout.addComponent(nextButton);
        if (uploadPanel.getMethodColTf().getValue().equals("")) {
            nextButton.setEnabled(true);
        } else {
            epitopeSelectionPanel.getMethodSelect().addValueChangeListener((ValueChangeListener) event -> {
                epitopeSelectionPanel.applyMethodFilter();
                parameterPanel.update();
                setParameterRange();
                epitopeSelectionPanel.getDataGrid().setEnabled(true);
                nextButton.setEnabled(true);
            });
        }
        Utils.notification("Upload completed!", dh.getUploadSuccess(), "success");
        life.qbic.MyPortletUI.logger.info("Upload successful");
        resetButton.setEnabled(true);
    }

    /**
     * Runs the epitope selection script
     *
     * @param command list of all arguments to start the script
     */
    public void runScript(ArrayList<String> command) {
        WindowLoading loadingWindow = new WindowLoading();

        Thread t = new Thread(() -> {

            ProcessBuilder pb = new ProcessBuilder(command);

            try {
                try {
                    Process mkdir_random = Runtime.getRuntime().exec("ssh -i " + homePath + ".ssh/key_rsa jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de mkdir " + tmpPathRemote + random);
                    System.out.println("ssh -i " + homePath + ".ssh/key_rsa jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de mkdir " + tmpPathRemote + random);
                    mkdir_random.waitFor();
                    scpFile.scpToRemote(homePath, inputPath, epitopeSelectorVM + random);
                    scpFile.scpToRemote(homePath, allelePath, epitopeSelectorVM + random);
                    scpFile.scpToRemote(homePath, includePath, epitopeSelectorVM + random);
                    scpFile.scpToRemote(homePath, excludePath, epitopeSelectorVM + random);
                } catch (IOException | InterruptedException e) {
                    MyPortletUI.logger.error("Could not copy the files to the VM");
                    e.printStackTrace();
                }

                StringBuilder c = new StringBuilder("Execute command on virtual machine: '");
                for (String word : command) {
                    c.append(" ").append(word);
                }
                MyPortletUI.logger.info(command);
                proc = pb.start();
                BufferedReader stdError =
                        new BufferedReader(new InputStreamReader(proc.getErrorStream()));

                pb.redirectErrorStream();
                InputStream inputStream = proc.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    MyPortletUI.logger.info(line);
                }
                while ((line = stdError.readLine()) != null) {
                    MyPortletUI.logger.error(line);
                }
                if (0 == proc.waitFor()) {
                    proc.destroyForcibly();
                    prepareResults();
                    cleanFiles();
                    loadingWindow.success();
                } else {
                    proc.destroyForcibly();
                    loadingWindow.failure();
                    cleanFiles();
                }
            } catch (IOException e) {
                MyPortletUI.logger.error("NeoOptiTope could not be found");
                Utils.notification("Error", dh.getComputationError(), "error");
                loadingWindow.failure();
                e.printStackTrace();
            } catch (InterruptedException e) {
                Utils.notification("Error", dh.getComputationError(), "error");
                MyPortletUI.logger.error("Computation interrupted");
                e.printStackTrace();

            }
        });

        loadingWindow.getCancelBu().addClickListener((ClickListener) event -> {
            t.interrupt();
            proc.destroyForcibly();
            cleanFiles();
            loadingWindow.close();
        });

        t.start();

        UI.getCurrent().setPollInterval(200);
    }

    /**
     * Prepares the result to show them in the "Results" tab of the content accordion.
     */
    private void prepareResults() {
        scpFile.scpFromRemote(homePath, epitopeSelectorVM, remoteOutputPath, outputPath);
        getResults();
        downloadButton.setVisible(true);
        String downloadFilePath = tmpResultPath.replace("tmp_result.txt", filename + "_epitopeSelection_results.tsv");
        try {
            Process copy_download = Runtime.getRuntime().exec("cp " + tmpResultPath + " " + downloadFilePath);
            copy_download.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Resource res = new FileResource(new File(downloadFilePath));
        FileDownloader downloader = new FileDownloader(res);
        downloader.extend(downloadButton);
        if (uploadPanel.getUseDatabase()) {
            registerButton.setVisible(true);
        }

        contentAccordion.getTab(resultsPanel).setEnabled(true);
        contentAccordion.getTab(parameterPanel).setEnabled(true);
        contentAccordion.getTab(epitopeSelectionPanel).setEnabled(true);
        contentAccordion.setSelectedTab(resultsPanel);
        contentAccordion.addSelectedTabChangeListener((SelectedTabChangeListener) event -> {
            epitopeSelectionPanel.getContainer().removeAllContainerFilters();
            if (!uploadPanel.getMethodColTf().getValue().equals("")) {
                epitopeSelectionPanel.applyMethodFilter();
                setParameterRange();
                parameterPanel.update();
            }
            epitopeSelectionPanel.getGeneTf().setValue("");
            epitopeSelectionPanel.getMutationTf().setValue("");
            epitopeSelectionPanel.getLengthTf().setValue("");
            epitopeSelectionPanel.getNeopeptideTf().setValue("");
            for (TabResult tr : resultsPanel.getTabs()) {
                tr.getFilterable().removeAllContainerFilters();
            }
            setParameterRange();
        });

    }

    /**
     * Deletes all files needed by the epitope selection script: allele file, exclude file, include
     * file, and the ouput file.
     */
    private void cleanFiles() {
        try {
            try {
                Process remove_randomRemote = Runtime.getRuntime().exec("ssh -i " + homePath + ".ssh/key_rsa jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de rm -rf " + tmpPathRemote + random);
                remove_randomRemote.waitFor();
                random = generator.generateRandomChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 10);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            Files.deleteIfExists(Paths.get(allelePath));
            Files.deleteIfExists(Paths.get(excludePath));
            Files.deleteIfExists(Paths.get(includePath));
            Files.deleteIfExists(Paths.get(outputPath));
            Files.deleteIfExists(Paths.get(tmpDownloadPath));
            Files.deleteIfExists(Paths.get(tmpAllelesPath));
        } catch (IOException e) {
            life.qbic.MyPortletUI.logger.error("At least one file could not have been deleted");
        }
    }

    /**
     * Get all the results and add a new result to the downloadable file. Reads the output file and
     * shows it in a tab. Prepares the downloadable result file.
     */
    private void getResults() {
        File resultsFile = new File(outputPath);
        downloadFiles.add(resultsFile);
        ParserScriptResult rp = new ParserScriptResult();
        rp.parse(resultsFile, hasDist);
        resultsPanel.addResultTab(rp.getResultBeans(), rp.getAlleles(), hasDist);
        WriterResults ow = new WriterResults();
        ow.writeOutputData(downloadFiles, tmpResultPath);
    }

    /**
     * Resets the main layout to the settings from the beginning
     */
    private void reset() {
        downloadFiles.clear();
        resultsPanel.reset();
        epitopeSelectionPanel.reset();
        parameterPanel.reset();
        filename = "";
        this.removeAllComponents();
        init();
        if (success) {
            initDatabase();
        }
        contentAccordion.setSelectedTab(uploadPanel);
        downloadFiles = new ArrayList<>();
    }

    /**
     * Sets the range of the parameters which can be adjusted by the user in the "Parameter Settings"
     * tab of the accordion
     */
    private void setParameterRange() {
        double numberOfIncluded = epitopeSelectionPanel.getIncludedBeans().size();
        double numberOfExcluded = epitopeSelectionPanel.getExcludedBeans().size();
        double numberOfPeptides = epitopeSelectionPanel.getDataGrid().getContainerDataSource().size();

        parameterPanel.getKSlider().setMin(numberOfIncluded);

        if ((numberOfPeptides - numberOfExcluded) < 100) {
            parameterPanel.getKSlider().setMax(numberOfPeptides - numberOfExcluded);
        } else {
            parameterPanel.getKSlider().setMax(100);
        }
        if (uploadPanel.getTaaColTf().getValue().equals("")) {
            parameterPanel.getKtaaSlider().setMax(0);
        } else {
            parameterPanel.getKtaaSlider().setMax(parameterPanel.getKSlider().getValue());
        }
        parameterPanel.getConsOverlapSlider().setMax(maxLength);

    }

    /**
     * Sets up the filter to a certain column filtering by a certain string
     *
     * @param column to filter
     * @param filter string to filter the column
     */
    private void filter(String column, String filter) {
        Filter tmpFilter = new SimpleStringFilter(column, filter, false, false);
        if (!filterable.getContainerFilters().contains(tmpFilter)) {
            filterable.addContainerFilter(tmpFilter);
        } else {
            filterable.removeContainerFilter(tmpFilter);
        }
    }

    public Boolean getHasType() {
        return hasType;
    }

    public Boolean getHasUnc() {
        return hasUnc;
    }

    public Boolean getHasDist() {
        return hasDist;
    }

    public Boolean getHasTranscriptExpression() {
        return hasTranscriptExpression;
    }

    public class AllelesException extends Exception {

        public AllelesException(String message) {
            super(message);
        }
    }
}