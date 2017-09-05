package view;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
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

  private Button nextButton, runButton, downloadButton, resetButton;
  private HorizontalLayout buttonsLayout;
  private Accordion contentAccordion;
  private PanelUpload uploadPanel;
  private PanelEpitopeSelection epitopeSelectionPanel;
  private PanelParameters parameterPanel;
  private PanelResults resultsPanel;
  private WriterScriptInput inputWriter;
  private ArrayList<File> downloadFiles;
  private FileDownloader downloader;
  private Process proc;
  private int maxLength;
  private DBFileHandler fileHandler;
  private Boolean gridAcivated;
  private Filterable filterable;
  private static Boolean hasType;
  private static Boolean hasMethod;
  private static Boolean hasUnc;
  private static Boolean hasDist;
  private OpenBisClient openbis;
  private List<Project> projects;
  private SCPFile scpFile;
  private RandomCharGenerator generator;



// All Paths are set here
  private String outputPath = new String("/Users/spaethju/Desktop/output.txt");
  private String scriptPath =
      new String("/Users/spaethju/WP3-EpitopeSelector-master/NeoOptiTope.py");
  private String inputPath = new String("/Users/spaethju/Desktop/input.txt");
  private String allelePath = new String("/Users/spaethju/Desktop/alleles.txt");
  private String includePath = new String("/Users/spaethju/Desktop/include.txt");
  private String excludePath = new String("/Users/spaethju/Desktop/exclude.txt");
  private String solverPath = new String("/Users/spaethju/PycharmProjects/epitopeSelectionScript");
  private String tmpResultPath = new String("/Users/spaethju/Desktop/tmp_result.txt");
  private String tmpDownloadPath = new String("/Users/spaethju/Desktop/tmp_download.txt");

  //TODO RANDOM FOLDER!
//  private String outputPath = new String("/tmp/output.txt");
//  private String scriptPath =
//          new String("/usr/share/neooptitope/NeoOptiTope.py");
//  private String inputPath = new String("/tmp/input.txt");
//  private String allelePath = new String("/tmp/alleles.txt");
//  private String includePath = new String("/tmp/include.txt");
//  private String excludePath = new String("/tmp/exclude.txt");
//  private String solverPath = new String("/usr/local/sbin/");
//  private String tmpResultPath = new String("/tmp/tmp_result.txt");
//  private String tmpDownloadPath = new String("/tmp/tmp_download.txt");
  private String epitopeSelectorVM = new String("jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de:");


  /**
   * Constructor creating the simple standard layout
   */
  public LayoutMain(List<Project> projects, OpenBisClient openbis) {
    this.openbis = openbis;
    this.projects = projects;
    generator = new RandomCharGenerator();
    scpFile = new SCPFile();
    init();
    initDatabase();
  }

  public LayoutMain() {
    init();
  }

  public void init() {
    this.addComponents(createContentAccordion(), createButtonsLayout());
    uploadPanel.getDataSelection().setEnabled(false);

    this.setMargin(true);
    this.setSpacing(true);

    this.setIcon(FontAwesome.CUBES);
    contentAccordion.setSelectedTab(uploadPanel);
    downloadFiles = new ArrayList<>();

  }

  public void initDatabase() {
    uploadPanel.getDataSelection().setEnabled(true);
    gridAcivated = false;
    fileHandler = new DBFileHandler(openbis);

    for (Project project : projects) {
      uploadPanel.getProjectSelectionCB().addItem(project.getIdentifier());
    }

    uploadPanel.getProjectSelectionCB().addValueChangeListener(new ValueChangeListener() {

      @Override
      public void valueChange(ValueChangeEvent event) {
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
            //uploadPanel.getDatasetGrid().removeColumn("type");
            uploadPanel.getDatasetGrid().setHeightMode(HeightMode.ROW);
            uploadPanel.getDatasetGrid().setHeightByRows(5);
            uploadPanel.getDatasetGrid().setVisible(true);
            gridAcivated = true;
          }
        } else {
          uploadPanel.getDatasetGrid().setEnabled(false);
          uploadPanel.getDatasetGrid().setContainerDataSource(container);
        }
      }
    });

      uploadPanel.getUploadButton().addClickListener(new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event) {
        String filename = uploadPanel.getSelected().getBean().getFileName();
        String code = uploadPanel.getSelected().getBean().getCode();
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
  public Accordion createContentAccordion() {
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
  public Button createNextButton() {
    nextButton = new Button("Next");
    nextButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
    nextButton.setIcon(FontAwesome.ARROW_CIRCLE_O_RIGHT);
    nextButton.setDescription("Go on with the next step.");
    nextButton.addClickListener(new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
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
      }

    });
    nextButton.setEnabled(false);
    return nextButton;
  }

  /**
   * Creates a button which resets the whole progress and allows a new upload of the data. The user
   * starts from the beginning again.
   *
   * @return reset button
   */
  public Button createResetButton() {
    resetButton = new Button("Reset");
    resetButton.setDescription("Reset all. Upload a new File.");
    resetButton.setIcon(FontAwesome.TIMES_CIRCLE_O);
    resetButton.setStyleName(ValoTheme.BUTTON_DANGER);
    resetButton.addClickListener(new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        downloadFiles.clear();
        resultsPanel.reset();
        epitopeSelectionPanel.reset();
        parameterPanel.reset();
        reset();
        Utils.notification("Reset", "You can now upload new data.", "success");
      }


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
  public Button createRunButton() {

    runButton = new Button("Run");
    runButton.setIcon(FontAwesome.PLAY_CIRCLE_O);
    runButton.setDescription("Computes the set of epitopes.");
    runButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
    runButton.addClickListener(new ClickListener() {


      @Override
      public void buttonClick(ClickEvent event) {

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

        Boolean valuesCorrect = new Boolean(false);
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

//          // set up script input
//          ArrayList<String> p = new ArrayList<>();
//          p.add("python");
//          p.add(scriptPath);
//
//          p.add("-i");
//          p.add(inputPath);
//
//          p.add("-imm");
//          String immCol = uploadPanel.getImmColTf().getValue();
//          p.add(immCol);
//
//          String distCol = uploadPanel.getDistanceColTf().getValue();
//          if (!distCol.equals("")) {
//            p.add("-d");
//            p.add(uploadPanel.getDistanceColTf().getValue());
//          }
//
//          String uncCol = uploadPanel.getUncertaintyColTf().getValue();
//          if (!uncCol.equals("")) {
//            p.add("-u");
//            p.add(uploadPanel.getUncertaintyColTf().getValue());
//          }
//
//          String taaCol = uploadPanel.getTaaColTf().getValue();
//          if (!taaCol.equals("")) {
//            p.add("-taa");
//            p.add(uploadPanel.getTaaColTf().getValue());
//          }
//
//          p.add("-a");
//          p.add(allelePath);
//
//          p.add("-excl");
//          p.add(excludePath);
//
//          p.add("-incl");
//          p.add(includePath);
//
//          p.add("-k");
//          p.add(Integer.toString(parameterPanel.getKSlider().getValue().intValue()));
//
//          p.add("-ktaa");
//          p.add(Integer.toString(parameterPanel.getKtaaSlider().getValue().intValue()));
//
//          p.add("-te");
//          p.add(parameterPanel.getThreshEpitopeTF().getValue().replaceAll(",", "."));
//
//          p.add("-td");
//          p.add(parameterPanel.getThreshDistanceTF().getValue().replaceAll(",", "."));
//
//          p.add("-o");
//          p.add(outputPath);
//
//          p.add("-c_al");
//          p.add(Double.toString(parameterPanel.getConsAlleleSlider().getValue()));
//
//          p.add("-c_a");
//          p.add(Double.toString(parameterPanel.getConsAntigenSlider().getValue()));
//
//          p.add("-c_o");
//          p.add(Integer.toString(parameterPanel.getConsOverlapSlider().getValue().intValue()));
//
//          if(parameterPanel.getRankCB().getValue() == true){
//            p.add("-r");
//          }


          // writes alleles.txt, include.txt and exclude.txt
          try {
            inputWriter.writeInputData(epitopeSelectionPanel.getContainer(), uploadPanel.getImmColTf().getValue(), uploadPanel.getTaaColTf().getValue(), uploadPanel.getUncertaintyColTf().getValue(), uploadPanel.getDistanceColTf().getValue());
          } catch (IOException e) {
            Utils.notification("Problem!",
                    "There was a problem writing the input files. Please try again", "error");
            life.qbic.MyPortletUI.logger.error("Error while writing the input data");
          }

          String random = generator.generateRandomChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 10);
          try {
            Runtime.getRuntime().exec("ssh -i ~/.ssh/key_rsa jspaeth@qbic-epitopeselector.am10.uni-tuebingen.de mkdir "  + "~/"+random);
            scpFile.scp(inputPath, epitopeSelectorVM+random);
            scpFile.scp(allelePath, epitopeSelectorVM+random);
            scpFile.scp(includePath, epitopeSelectorVM+random);
            scpFile.scp(excludePath, epitopeSelectorVM+random);
          } catch (IOException e) {
            e.printStackTrace();
          }

          runButton.setStyleName(null);
        }
      }

    });

    return runButton;
  }

  /**
   * Creates a button which gives the user the opportunity to download all already computed results.
   *
   * @return download button
   */
  public Button createDownloadButton() {
    downloadButton = new Button("Save");
    downloadButton.setIcon(FontAwesome.DOWNLOAD);
    downloadButton.setDescription("Save your results");
    downloadButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
    downloadButton.setVisible(false);
    Resource res = new FileResource(new File(tmpResultPath));
    downloader = new FileDownloader(res);
    downloader.extend(downloadButton);
    return downloadButton;
  }

  /**
   * Creates a layout presenting all buttons in a horizontal order.
   *
   * @return layout with buttons
   */
  public HorizontalLayout createButtonsLayout() {
    buttonsLayout = new HorizontalLayout();
    buttonsLayout.setSpacing(true);
    resetButton = createResetButton();
    nextButton = createNextButton();
    // firstNextButton = createFirstNextButton();
    downloadButton = createDownloadButton();
    buttonsLayout.addComponents(resetButton, createNextButton(), downloadButton);
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

  public void processingData(File file) throws Exception {
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
    contentAccordion.addSelectedTabChangeListener(new SelectedTabChangeListener() {
      @Override
      public void selectedTabChange(SelectedTabChangeEvent event) {
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
      }
    });
    contentAccordion.getTab(uploadPanel).setIcon(FontAwesome.CHECK_CIRCLE);
    buttonsLayout.addComponent(nextButton);
    if (uploadPanel.getMethodColTf().getValue().equals("")) {
      nextButton.setEnabled(true);
    } else {
      epitopeSelectionPanel.getMethodSelect().addValueChangeListener(new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
          epitopeSelectionPanel.applyMethodFilter();
          parameterPanel.update();
          setParameterRange();
          epitopeSelectionPanel.getDataGrid().setEnabled(true);
          nextButton.setEnabled(true);
        }
      });
    }
    Utils.notification("Upload completed!", "Your upload completed successfully.", "success");
    life.qbic.MyPortletUI.logger.info("Upload successful");
    resetButton.setEnabled(true);
  }

  /**
   * Runs the epitope selection script
   *
   * @param program list of all arguments to start the script
   */
  public void runScript(ArrayList<String> program) {
    WindowLoading loadingWindow = new WindowLoading();

    Thread t = new Thread(new Runnable() {

      @Override
      public void run() {

        ProcessBuilder pb = new ProcessBuilder(program);
        pb.environment().put("PATH", solverPath);

        try {
          proc = pb.start();
          BufferedReader stdError =
                  new BufferedReader(new InputStreamReader(proc.getErrorStream()));
          String s;
          System.out.println("Here is the standard error of the command (if any):\n");
          while ((s = stdError.readLine()) != null) {
            life.qbic.MyPortletUI.logger.error(s);
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
          life.qbic.MyPortletUI.logger.error("NeoOptiTope could not be found");
          loadingWindow.failure();
        } catch (InterruptedException e) {
          Utils.notification("Computation interrupted!", "", "error");
          life.qbic.MyPortletUI.logger.error("Computation interrupted");
        }
      }
    });

    loadingWindow.getCancelBu().addClickListener(new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        t.interrupt();
        proc.destroyForcibly();
        loadingWindow.close();
      }
    });

    t.start();

    UI.getCurrent().setPollInterval(200);
  }

  /**
   * Prepares the result to show them in the "Results" tab of the content accordion.
   */
  public void prepareResults() {
    getResults();
    downloadButton.setVisible(true);

    contentAccordion.getTab(resultsPanel).setEnabled(true);
    contentAccordion.getTab(parameterPanel).setEnabled(true);
    contentAccordion.getTab(epitopeSelectionPanel).setEnabled(true);
    contentAccordion.setSelectedTab(resultsPanel);
    contentAccordion.addSelectedTabChangeListener(new SelectedTabChangeListener() {
      @Override
      public void selectedTabChange(SelectedTabChangeEvent event) {
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
      }
    });

  }

  /**
   * Deletes all files needed by the epitope selection script: allele file, exclude file, include
   * file, and the ouput file.
   */
  public void cleanFiles() {
    try {
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
  public void getResults() {
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
  public void reset() {
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
  public void setParameterRange() {
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
  public void filter(String column, String filter) {
    Filter tmpFilter = new SimpleStringFilter(column, filter, false, false);
    if (!filterable.getContainerFilters().contains(tmpFilter)) {
      filterable.addContainerFilter(tmpFilter);
    } else {
      filterable.removeContainerFilter(tmpFilter);
    }

  }

  public static Boolean getHasMethod() {
    return hasMethod;
  }

  public static Boolean getHasType() {
    return hasType;
  }

  public static void setHasType(Boolean hasType) {
    LayoutMain.hasType = hasType;
  }

  public static Boolean getHasUnc() {
    return hasUnc;
  }

  public static void setHasUnc(Boolean hasUnc) {
    LayoutMain.hasUnc = hasUnc;
  }

  public static Boolean getHasDist() {
    return hasDist;
  }

  public static void setHasDist(Boolean hasDist) {
    LayoutMain.hasDist = hasDist;
  }

}
