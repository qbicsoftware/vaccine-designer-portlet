package view;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;
import helper.*;
import life.qbic.MyPortletUI;
import life.qbic.openbis.openbisclient.OpenBisClient;
import life.qbic.portal.liferayandvaadinhelpers.main.LiferayAndVaadinUtils;
import model.DatasetBean;

/**
 *
 * The class {@link LayoutMain} represents the main layout which is always shown. It handles all the
 * computation and view changes. All important paths are set here.
 *
 * @author spaethju
 *
 */
@SuppressWarnings("serial")
public class LayoutMain extends VerticalLayout implements SucceededListener {

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
  private Boolean gridAcivated;
  private Filterable filterable;
  private static Boolean hasType;
  private static Boolean hasUnc;
  private static Boolean hasDist;
  private OpenBisClient openbis;
  private List<Project> projects;
  private SCPFile scpFile;
  private RandomCharGenerator generator;
  private String code;

  private String tmpPath = "/Users/spaethju/Desktop/";
  //private String tmpPath = "/tmp/";
  private String homePath = "/Users/spaethju/";
  //private String homePath = "/home/luser/";
  private String tmpPathRemote = "/home/jspaeth/";
  private String outputPath = "";
  private String inputPath = "";
  private String allelePath = "";
  private String includePath = "";
  private String excludePath = "";
  private String tmpResultPath = "";
  private String tmpDownloadPath = "";
  private String remoteOutputPath = "";
  private String random = "";
  private String epitopeSelectorVM = "jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de:";
  private String dropbox = "qeana08@data.qbic.uni-tuebingen.de";
  private String registerPath = "qeana08@data.qbic.uni-tuebingen.de:/mnt/nfs/qbic/dropboxes/qeana08_qbic/incoming";



  /**
   * Constructor creating the simple standard layout
   */
  public LayoutMain(List<Project> projects, OpenBisClient openbis) {
    this.openbis = openbis;
    this.projects = projects;
    scpFile = new SCPFile();
    init();
    initDatabase();
  }

  public LayoutMain() {
    init();
  }

  private void init() {
    this.addComponents(createContentAccordion(), createButtonsLayout());
    uploadPanel.getDataSelection().setEnabled(false);

    this.setMargin(true);
    this.setSpacing(true);

    this.setIcon(FontAwesome.CUBES);
    contentAccordion.setSelectedTab(uploadPanel);
    downloadFiles = new ArrayList<>();

    generator = new RandomCharGenerator();
    random = generator.generateRandomChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 10);
    File f = new File(tmpPath+LiferayAndVaadinUtils.getUser().getScreenName());
    if(!(f.exists() && f.isDirectory())) {
      try {
        Runtime.getRuntime().exec("mkdir " + tmpPath + LiferayAndVaadinUtils.getUser().getScreenName());
      } catch (IOException e) {
        MyPortletUI.logger.error("Could not write the folder on the file system");
        e.printStackTrace();
      }
    }
    outputPath = tmpPath +LiferayAndVaadinUtils.getUser().getScreenName() +"/output.txt";
    inputPath = tmpPath+LiferayAndVaadinUtils.getUser().getScreenName()+"/input.txt";
    allelePath = tmpPath+LiferayAndVaadinUtils.getUser().getScreenName()+"/alleles.txt";
    includePath = tmpPath+LiferayAndVaadinUtils.getUser().getScreenName()+"/include.txt";
    excludePath = tmpPath+LiferayAndVaadinUtils.getUser().getScreenName()+"/exclude.txt";
    tmpResultPath = tmpPath+ LiferayAndVaadinUtils.getUser().getScreenName()+"/tmp_result.txt";
    tmpDownloadPath = tmpPath+LiferayAndVaadinUtils.getUser().getScreenName()+"/tmp_download.txt";
    try {
      Files.deleteIfExists(Paths.get(allelePath));
      Files.deleteIfExists(Paths.get(excludePath));
      Files.deleteIfExists(Paths.get(includePath));
      Files.deleteIfExists(Paths.get(outputPath));
      Files.deleteIfExists(Paths.get(tmpDownloadPath));
      Files.deleteIfExists(Paths.get(inputPath));
      Files.deleteIfExists(Paths.get(tmpResultPath));
    } catch (IOException e) {
      MyPortletUI.logger.error("File System error: Old files could not be deleted");
      e.printStackTrace();
    }
  }

  private void initDatabase() {
    uploadPanel.getDataSelection().setEnabled(true);
    gridAcivated = false;
    fileHandler = new DBFileHandler(openbis);

    for (Project project : projects) {
      uploadPanel.getProjectSelectionCB().addItem(project.getIdentifier());
    }

    uploadPanel.getProjectSelectionCB().addValueChangeListener((ValueChangeListener) event -> {
      List<DataSet> dataSets = openbis.getDataSetsOfProjectByIdentifier(uploadPanel.getProjectSelectionCB().getValue().toString());
      BeanItemContainer<DatasetBean> container = fileHandler.fillTable(dataSets);
      if (container.size() > 0) {
        uploadPanel.getDatasetGrid().setEnabled(true);
        uploadPanel.getDatasetGrid().setContainerDataSource(container);
        filterable = (Filterable) uploadPanel.getDatasetGrid().getContainerDataSource();
        filterable.removeAllContainerFilters();
        filter("type", "Q_WF_NGS_EPITOPE_PREDICTION_RESULTS");
        filter("fileName", ".tsv");
        if (!gridAcivated) {
          uploadPanel.getDatasetGrid().removeColumn("children");
          uploadPanel.getDatasetGrid().removeColumn("properties");
          uploadPanel.getDatasetGrid().removeColumn("id");
          uploadPanel.getDatasetGrid().removeColumn("projectBean");
          uploadPanel.getDatasetGrid().removeColumn("dataSetTypeCode");
          uploadPanel.getDatasetGrid().removeColumn("code");
          uploadPanel.getDatasetGrid().removeColumn("dssPath");
          uploadPanel.getDatasetGrid().setHeightMode(HeightMode.ROW);
          uploadPanel.getDatasetGrid().setHeightByRows(5);
          uploadPanel.getDatasetGrid().setVisible(true);
          gridAcivated = true;
        }
      } else {
        uploadPanel.getDatasetGrid().setEnabled(false);
        uploadPanel.getDatasetGrid().setContainerDataSource(container);
      }
    });

      uploadPanel.getUploadButton().addClickListener((ClickListener) event -> {
        String filename = uploadPanel.getSelected().getBean().getFileName();
        code = uploadPanel.getSelected().getBean().getCode();
        Path destination = Paths.get(tmpDownloadPath);
        try {
          InputStream in = openbis.getDatasetStream(code, "result/"+filename);
          Files.copy(in, destination);
          File file = new File(tmpDownloadPath);
          processingData(file);
          Files.delete(destination);
        } catch (IOException e) {
          e.printStackTrace();
        } catch (Exception e) {
          MyPortletUI.logger.error("Something went wrong while uploading/Parsing the file");
          Utils.notification("Upload failed", "Something went wrong while uploading/parsing the file", "error");
          e.printStackTrace();
        }
      });
  }


  /**
   * Creates the content accordion. Every information is shown inside the tabs of the accordion
   * except of the buttons.
   *
   * @return accordion with the four tabs "Upload Data", "Epitope Selection", "Parameter Settings"
   *         and "Results"
   */
  private Accordion createContentAccordion() {
    uploadPanel = new PanelUpload();
    uploadPanel.setImmediate(true);
    uploadPanel.getUpload().addSucceededListener(this);

    epitopeSelectionPanel = new PanelEpitopeSelection();
    epitopeSelectionPanel.setImmediate(true);
    parameterPanel = new PanelParameters();
    parameterPanel.setImmediate(true);
    resultsPanel = new PanelResults();
    resultsPanel.setImmediate(true);
    contentAccordion = new Accordion();
    contentAccordion.setImmediate(true);
    contentAccordion.setSizeFull();
    contentAccordion.addTab(uploadPanel, "Upload Data");
    contentAccordion.getTab(uploadPanel).setIcon(FontAwesome.UPLOAD);
    contentAccordion.getTab(uploadPanel).setEnabled(true);
    contentAccordion.addTab(epitopeSelectionPanel, "Epitope Selection");
    contentAccordion.getTab(epitopeSelectionPanel).setIcon(FontAwesome.MOUSE_POINTER);
    contentAccordion.getTab(epitopeSelectionPanel).setEnabled(false);
    contentAccordion.addTab(parameterPanel, "Parameter Settings");
    contentAccordion.getTab(parameterPanel).setIcon(FontAwesome.SLIDERS);
    contentAccordion.getTab(parameterPanel).setEnabled(false);
    contentAccordion.addTab(resultsPanel, "Results");
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
    nextButton.setDescription("Go on with the next step.");
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
    registerButton.setDescription("Register result in database.");
    registerButton.setIcon(FontAwesome.UPLOAD);
    registerButton.setStyleName(ValoTheme.BUTTON_SMALL);
    registerButton.addClickListener((ClickListener) event -> {
        try {
          String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date());
          String resultName = code + "_" + timeStamp + "_epitopeselection_result" + ".txt";
          Process copy_result = Runtime.getRuntime().exec("cp " + tmpResultPath + " " +  tmpPath + LiferayAndVaadinUtils.getUser().getScreenName() + "/" + resultName);
          copy_result.waitFor();
          MyPortletUI.logger.info("cp " + tmpResultPath + " " +  tmpPath + LiferayAndVaadinUtils.getUser().getScreenName() + "/" + resultName);
          scpFile.scpToRemote(homePath, tmpPath + LiferayAndVaadinUtils.getUser().getScreenName() + "/" + resultName, registerPath);
          Process markAsFinished = Runtime.getRuntime().exec("ssh -i "+ homePath +".ssh/key_rsa " + dropbox + " touch /mnt/nfs/qbic/dropboxes/qeana08_qbic/incoming/.MARKER_is_finished_" + resultName);
          Process remove_result = Runtime.getRuntime().exec("rm -f " + tmpPath + LiferayAndVaadinUtils.getUser().getScreenName() + "/" + resultName);
          remove_result.waitFor();
          MyPortletUI.logger.info("rm -f " + tmpPath + LiferayAndVaadinUtils.getUser().getScreenName() + "/" + resultName);
        } catch (IOException | InterruptedException e) {
          MyPortletUI.logger.error("Could not write the folder on the file system");
          Utils.notification("Error", "Please try again.", "error");
          e.printStackTrace();
        }
      Utils.notification("Results registered", "SUCCESS", "success");
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
    resetButton.setDescription("Reset all. Upload a new File.");
    resetButton.setIcon(FontAwesome.TIMES_CIRCLE_O);
    resetButton.setStyleName(ValoTheme.BUTTON_DANGER);
    resetButton.addStyleName(ValoTheme.BUTTON_SMALL);
    resetButton.addClickListener((ClickListener) event -> {
      downloadFiles.clear();
      resultsPanel.reset();
      epitopeSelectionPanel.reset();
      parameterPanel.reset();
      reset();
      Utils.notification("Reset", "You can now upload new data.", "success");
    });
    resetButton.setEnabled(false);
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
    runButton.setDescription("Computes the set of epitopes.");
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
      }

      if (valuesCorrect) {
        runButton.setCaption("Re-Run");
        try {
          Process mkdir = Runtime.getRuntime().exec("ssh -i "+ homePath +".ssh/key_rsa jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de mkdir "  + tmpPathRemote+random);
          mkdir.waitFor();
        } catch (IOException | InterruptedException e) {
          MyPortletUI.logger.error("Couldn't create folder on virtual machine.");
          e.printStackTrace();
        }
        // set up script input
        ArrayList<String> p = new ArrayList<>();
        p.add("ssh");
        p.add("-i");
        p.add(homePath+".ssh/key_rsa");
        p.add("jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de");

        p.add("singularity");
        p.add("run");
        p.add("--bind");
        p.add("/root/COIN/bin/:/usr/local/bin/");
        p.add("epitopeselector.img");

        p.add("-i");
        p.add(tmpPathRemote+random+"/input.txt");

        p.add("-imm");
        String immCol = uploadPanel.getImmColTf().getValue();
        p.add(immCol);

        String distCol = uploadPanel.getDistanceColTf().getValue();
        if (!distCol.equals("")) {
          p.add("-d");
          p.add(uploadPanel.getDistanceColTf().getValue());
        }

        String uncCol = uploadPanel.getUncertaintyColTf().getValue();
        if (!uncCol.equals("")) {
          p.add("-u");
          p.add(uploadPanel.getUncertaintyColTf().getValue());
        }

        String taaCol = uploadPanel.getTaaColTf().getValue();
        if (!taaCol.equals("")) {
          p.add("-taa");
          p.add(uploadPanel.getTaaColTf().getValue());
        }

        p.add("-a");
        p.add(tmpPathRemote+random+"/alleles.txt");

        p.add("-excl");
        p.add(tmpPathRemote+random+"/exclude.txt");

        p.add("-incl");
        p.add(tmpPathRemote+random+"/include.txt");

        p.add("-k");
        p.add(Integer.toString(parameterPanel.getKSlider().getValue().intValue()));

        p.add("-ktaa");
        p.add(Integer.toString(parameterPanel.getKtaaSlider().getValue().intValue()));

        p.add("-te");
        p.add(parameterPanel.getThreshEpitopeTF().getValue().replaceAll(",", "."));

        p.add("-td");
        p.add(parameterPanel.getThreshDistanceTF().getValue().replaceAll(",", "."));

        p.add("-o");
        remoteOutputPath = tmpPathRemote+random+"/output.txt";
        p.add(remoteOutputPath);


        p.add("-c_al");
        p.add(Double.toString(parameterPanel.getConsAlleleSlider().getValue()));

        p.add("-c_a");
        p.add(Double.toString(parameterPanel.getConsAntigenSlider().getValue()));

        p.add("-c_o");
        p.add(Integer.toString(parameterPanel.getConsOverlapSlider().getValue().intValue()));

        if(parameterPanel.getRankCB().getValue()){
          p.add("-r");
        }


        // writes alleles.txt, include.txt and exclude.txt
        try {
          inputWriter.writeInputData(epitopeSelectionPanel.getContainer(), uploadPanel.getImmColTf().getValue(), uploadPanel.getTaaColTf().getValue(), uploadPanel.getUncertaintyColTf().getValue(), uploadPanel.getDistanceColTf().getValue());
        } catch (IOException e) {
          Utils.notification("Problem!",
                  "There was a problem writing the input files. Please try again", "error");
          MyPortletUI.logger.error("Error while writing the input data");
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
    downloadButton.setDescription("Save your results");
    downloadButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
    downloadButton.addStyleName(ValoTheme.BUTTON_SMALL);
    downloadButton.setVisible(false);
    Resource res = new FileResource(new File(tmpResultPath));
    FileDownloader downloader = new FileDownloader(res);
    downloader.extend(downloadButton);
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
    UploaderInput uploader = uploadPanel.getReceiver();
    uploader.getProgress().setVisible(false);
    try {
      processingData(uploader.getTempFile());
    } catch (Exception e){
      Utils.notification("Upload failed", "Something went wrong. Make sure you have selected an appropriate file and described its parameters correctly ", "error");
    }

  }

  private void processingData(File file) throws Exception {
    Boolean hasMethod;
    if (uploadPanel.getComboInput().getValue().equals("Standard")) {
      ParserInputStandard parser = new ParserInputStandard();
      parser.parse(file, uploadPanel.getMethodColTf().getValue(),
              uploadPanel.getImmColTf().getValue(),
              uploadPanel.getUncertaintyColTf().getValue(),
              uploadPanel.getDistanceColTf().getValue(),
              uploadPanel.getTaaColTf().getValue());
      hasMethod = parser.getHasMethod();
      hasType = parser.getHasType();
      hasDist = parser.getHasDist();
      hasUnc = parser.getHasUnc();
      epitopeSelectionPanel.setDataGrid(parser.getEpitopes(),
              uploadPanel.getMethodColTf().getValue());
      maxLength = parser.getMaxLength();
      if (uploadPanel.getTaaColTf().getValue().equals("")) {
        epitopeSelectionPanel.getDataGrid().removeColumn("type");
      } else {
        epitopeSelectionPanel.addTypeFilter();
      }
    } else if (uploadPanel.getComboInput().getValue().equals("Old Filetype")) {

      ParserInputOldFiletype parser = new ParserInputOldFiletype();

      parser.parse(file, uploadPanel.getMethodColTf().getValue(),
              uploadPanel.getImmColTf().getValue(),
              uploadPanel.getUncertaintyColTf().getValue(),
              uploadPanel.getDistanceColTf().getValue(),
              uploadPanel.getTaaColTf().getValue());

      hasMethod = parser.getHasMethod();
      hasType = parser.getHasType();
      hasDist = parser.getHasDist();
      hasUnc = parser.getHasUnc();
      epitopeSelectionPanel.setDataGrid(parser.getEpitopes(),
              uploadPanel.getMethodColTf().getValue());
      maxLength = parser.getMaxLength();
      if (uploadPanel.getTaaColTf().getValue().equals("")) {
        epitopeSelectionPanel.getDataGrid().removeColumn("type");
      } else {
        epitopeSelectionPanel.addTypeFilter();
      }
    } else if (uploadPanel.getComboInput().getValue().equals("New Filetype")) {
      ParserInputNewFiletype parser = new ParserInputNewFiletype();
      parser.parse(file, uploadPanel.getMethodColTf().getValue(),
              uploadPanel.getTaaColTf().getValue());
      hasMethod = parser.getHasMethod();
      hasType = parser.getHasType();
      epitopeSelectionPanel.setDataGrid(parser.getEpitopes(),
              uploadPanel.getMethodColTf().getValue().trim());
      maxLength = parser.getMaxLength();
      if (uploadPanel.getTaaColTf().getValue().equals("")) {
        epitopeSelectionPanel.getDataGrid().removeColumn("type");
      } else {
        epitopeSelectionPanel.addTypeFilter();
      }
    }

    String distCol = uploadPanel.getDistanceColTf().getValue();
    String uncCol = uploadPanel.getUncertaintyColTf().getValue();
    if (!uploadPanel.getComboInput().getValue().equals("Standard")) {
      epitopeSelectionPanel.getDataGrid().removeColumn("transcriptExpression");
    }
    if ((!uncCol.equals("")) || (!distCol.equals(""))) {
      epitopeSelectionPanel.joinHeader();
    }

    if (distCol.equals("")) {
      epitopeSelectionPanel.getDataGrid().removeColumn("distanceA1");
      epitopeSelectionPanel.getDataGrid().removeColumn("distanceA2");
      epitopeSelectionPanel.getDataGrid().removeColumn("distanceB1");
      epitopeSelectionPanel.getDataGrid().removeColumn("distanceB2");
      epitopeSelectionPanel.getDataGrid().removeColumn("distanceC1");
      epitopeSelectionPanel.getDataGrid().removeColumn("distanceC2");
    }
    if (uncCol.equals("")) {
      epitopeSelectionPanel.getDataGrid().removeColumn("uncertaintyA1");
      epitopeSelectionPanel.getDataGrid().removeColumn("uncertaintyA2");
      epitopeSelectionPanel.getDataGrid().removeColumn("uncertaintyB1");
      epitopeSelectionPanel.getDataGrid().removeColumn("uncertaintyB2");
      epitopeSelectionPanel.getDataGrid().removeColumn("uncertaintyC1");
      epitopeSelectionPanel.getDataGrid().removeColumn("uncertaintyC2");
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
    Utils.notification("Upload completed!", "Your upload completed successfully.", "success");
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
          Process mkdir_random = Runtime.getRuntime().exec("ssh -i "+homePath+".ssh/key_rsa jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de mkdir "  + tmpPathRemote+random);
          mkdir_random.waitFor();
          scpFile.scpToRemote(homePath, inputPath, epitopeSelectorVM+random);
          scpFile.scpToRemote(homePath, allelePath, epitopeSelectorVM+random);
          scpFile.scpToRemote(homePath, includePath, epitopeSelectorVM+random);
          scpFile.scpToRemote(homePath, excludePath, epitopeSelectorVM+random);
        } catch (IOException | InterruptedException e) {
          MyPortletUI.logger.error("Could not copy the files to the VM");
          e.printStackTrace();
        }

        StringBuilder c = new StringBuilder("Execute command on virtual machine: '");
        for (String word : command){
          c.append(" ").append(word);
        }
        MyPortletUI.logger.info(command+"'");
        proc = pb.start();
        BufferedReader stdError =
                new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        pb.redirectErrorStream();
        InputStream inputStream = proc.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while((line = reader.readLine())!= null) {
          System.out.println(line);
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
        loadingWindow.failure();
      } catch (InterruptedException e) {
        Utils.notification("Computation interrupted!", "", "error");
        MyPortletUI.logger.error("Computation interrupted");
      }
    });

    loadingWindow.getCancelBu().addClickListener((ClickListener) event -> {
      t.interrupt();
      proc.destroyForcibly();
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
    if (uploadPanel.getDataSelection().getValue().equals("Database")) {
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
        Process remove_randomRemote = Runtime.getRuntime().exec("ssh -i "+homePath+".ssh/key_rsa jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de rm -rf "  + tmpPathRemote+random);
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
    rp.parse(resultsFile);
    resultsPanel.addResultTab(rp.getResultBeans(), rp.getAlleles());
    WriterResults ow = new WriterResults();
    ow.writeOutputData(downloadFiles, tmpResultPath);
  }

  /**
   * Resets the main layout to the settings from the beginning
   */
  private void reset() {
    this.removeAllComponents();
    init();
    initDatabase();
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

  public static Boolean getHasType() {
    return hasType;
  }

  public static Boolean getHasUnc() {
    return hasUnc;
  }

  public static Boolean getHasDist() {
    return hasDist;
  }
}
