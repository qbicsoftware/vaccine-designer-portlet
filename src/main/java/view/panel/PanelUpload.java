package view.panel;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import helper.DescriptionHandler;
import helper.Utils;
import helper.upload_input.UploaderInput;
import java.io.File;
import java.util.HashMap;
import model.DatasetBean;

/**
 * The class {@link PanelUpload} represents a component which allows the user to define column names
 * and to upload epitope prediction data.
 *
 * @author spaethju
 */
@SuppressWarnings("serial")
public class PanelUpload extends CustomComponent {

  private UploaderInput inputReceiver;
  private Upload inputUpload;
  private Panel uploadPanel;
  private VerticalLayout panelContent, databaseLayout, hlaExpressionLayout, columnLayout, dataLayout, fileTypeSelectionLayout, uploadLayout, alleleFileSelection;
  private HorizontalLayout buttonLayout, alleleTFLayout;
  private TextField immColTf, distanceColTf, uncertaintyColTf, taaColTf, methodColTf, hlaA1TF, hlaB1TF, hlaC1TF, hlaA2TF, hlaB2TF, hlaC2TF, hlaAEVTF, hlaBEVTF, hlaCEVTF, transcriptExpressionColTf;
  private ComboBox projectSelectionCB;
  private ComboBox alleleFileSelectionCB;
  private Button uploadButton, dataSelectionUploadButton, dataSelectionDatabaseButton, manualButton, databaseButton, nextButton, downloadManualButton;
  private Grid datasetGrid;
  private BeanItem<DatasetBean> selected;
  private Boolean useDatabase;
  private Boolean hlaAsColumns;
  private Boolean alleleFileUpload;
  private HashMap<String, String> alleles, allele_expressions;
  private DescriptionHandler dh = new DescriptionHandler();
  private Label hlaDescription, databaseUploadDescription;
  private NullValidator nv = new NullValidator("Please choose an allele file from the " +
      "database", false);
  private String basepath;

  /**
   * Constructor
   */
  public PanelUpload() {
    basepath = VaadinService.getCurrent()
        .getBaseDirectory().getAbsolutePath();
    init();
  }

  public void init() {
    useDatabase = null;
    hlaAsColumns = null;
    alleles = new HashMap<>();
    allele_expressions = new HashMap<>();
    // Create the upload component and handle all its events
    inputReceiver = new UploaderInput();
    inputReceiver.getProgress().setVisible(true);
    inputUpload = new Upload("Please Upload your Data", inputReceiver);
    inputUpload.setSizeFull();
    inputUpload.addProgressListener(inputReceiver);
    inputUpload.addFailedListener(inputReceiver);

    // Create the Upload Layout
    panelContent = new VerticalLayout();
    panelContent.setSpacing(true);
    panelContent.setMargin(true);

    uploadLayout = createUpload();
    uploadLayout.setVisible(false);
    buttonLayout = createButtons();
    buttonLayout.setVisible(false);
    columnLayout = createColumnTextFields();
    columnLayout.setVisible(false);
    hlaExpressionLayout = createHlaExpressionTextFields();
    hlaExpressionLayout.setVisible(false);
    databaseLayout = createDatabaseSelection();
    databaseLayout.setVisible(false);
    dataLayout = createDataSelection();
    dataLayout.setVisible(true);
    alleleFileSelection = createAlleleFileSelection();
    alleleFileSelection.setVisible(false);
    fileTypeSelectionLayout = createFileTypeSelection();
    fileTypeSelectionLayout.setVisible(false);

    panelContent
        .addComponents(dataLayout, alleleFileSelection, fileTypeSelectionLayout, uploadLayout,
            columnLayout, hlaExpressionLayout, databaseLayout, buttonLayout);
    panelContent.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

    // Create the panel
    uploadPanel = new Panel();
    uploadPanel.setContent(panelContent);
    setCompositionRoot(uploadPanel);
  }

  public VerticalLayout createUpload() {
    uploadButton = new Button("Upload");
    uploadButton.setEnabled(false);

    VerticalLayout allUploadLayout = new VerticalLayout();
    Label description = createDescriptionLabel(dh.getUploadData_upload());

    HorizontalLayout uploadLayout = new HorizontalLayout();
    uploadLayout.setMargin(true);
    uploadLayout.setSpacing(true);
    uploadLayout.addComponents(inputUpload, uploadButton);

    allUploadLayout.addComponents(description, uploadLayout);

    return allUploadLayout;
  }

  public HorizontalLayout createButtons() {
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    buttonsLayout.setSizeFull();
    buttonsLayout.setSpacing(true);
    Button backButton = new Button("Back");
    backButton.setIcon(FontAwesome.ARROW_CIRCLE_O_LEFT);
    backButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
    backButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
    backButton.addStyleName(ValoTheme.BUTTON_HUGE);
    nextButton = new Button("Next");
    nextButton.setIcon(FontAwesome.ARROW_CIRCLE_O_RIGHT);
    nextButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
    nextButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
    nextButton.addStyleName(ValoTheme.BUTTON_HUGE);

    buttonsLayout.addComponents(backButton, nextButton);
    buttonsLayout.setComponentAlignment(backButton, Alignment.MIDDLE_CENTER);
    buttonsLayout.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);

    backButton.addClickListener((Button.ClickListener) event -> {
      if (dataLayout.isVisible()) {
      } else if (alleleFileSelection.isVisible()) {
        alleleFileSelection.setVisible(false);
        dataLayout.setVisible(true);
        buttonLayout.setVisible(false);
      } else if (fileTypeSelectionLayout.isVisible()) {
        fileTypeSelectionLayout.setVisible(false);
        nextButton.setVisible(false);
        if (!useDatabase) {
          dataLayout.setVisible(true);
          buttonLayout.setVisible(false);
        } else {
          alleleFileSelection.setVisible(true);
        }
      } else if (columnLayout.isVisible()) {
        columnLayout.setVisible(false);
        fileTypeSelectionLayout.setVisible(true);
      } else if (hlaExpressionLayout.isVisible()) {
        hlaExpressionLayout.setVisible(false);
        columnLayout.setVisible(true);
      } else if (uploadLayout.isVisible() || databaseLayout.isVisible()) {
        uploadLayout.setVisible(false);
        databaseLayout.setVisible(false);
        hlaExpressionLayout.setVisible(true);
        nextButton.setVisible(true);
      }
    });

    nextButton.addClickListener((Button.ClickListener) event -> {
      if (dataLayout.isVisible()) {
      } else if (alleleFileSelection.isVisible()) {

      } else if (fileTypeSelectionLayout.isVisible()) {
        if (hlaAsColumns != null) {
          fileTypeSelectionLayout.setVisible(false);
          columnLayout.setVisible(true);
          if (hlaAsColumns) {
            uncertaintyColTf.setVisible(false);
            distanceColTf.setVisible(false);
          } else {
            uncertaintyColTf.setVisible(true);
            distanceColTf.setVisible(true);
          }
        } else {
        }
      } else if (columnLayout.isVisible()) {
        if (immColTf.isValid() || hlaAsColumns) {
          columnLayout.setVisible(false);
          hlaExpressionLayout.setVisible(true);
          if (alleleFileUpload) {
            alleleTFLayout.setVisible(false);
            hlaDescription = createDescriptionLabel(dh.getUploadData_specifyAlleleExpression());
          } else {
            alleleTFLayout.setVisible(true);
            hlaDescription = createDescriptionLabel(dh.getUploadData_specifyAlleles());
          }
        } else {
          Utils.notification("Error", dh.getUploadData_immValidatorDescription(), "error");
        }
      } else if (hlaExpressionLayout.isVisible()) {
        if (isHlaExpressionlValid()) {
          if (!alleleFileUpload) {
            if (isHlalValid()) {
              alleles.clear();
              alleles.put("A1", hlaA1TF.getValue().replace("HLA-", ""));
              alleles.put("A2", hlaA2TF.getValue().replace("HLA-", ""));
              alleles.put("B1", hlaB1TF.getValue().replace("HLA-", ""));
              alleles.put("B2", hlaB2TF.getValue().replace("HLA-", ""));
              alleles.put("C1", hlaC1TF.getValue().replace("HLA-", ""));
              alleles.put("C2", hlaC2TF.getValue().replace("HLA-", ""));
            } else {
              Utils.notification("Error", dh.getUploadData_hlaValidatorDescription(), "error");
            }
          }
          allele_expressions.put("A", hlaAEVTF.getValue());
          allele_expressions.put("B", hlaBEVTF.getValue());
          allele_expressions.put("C", hlaCEVTF.getValue());
          hlaExpressionLayout.setVisible(false);
          nextButton.setVisible(false);
          if (useDatabase) {
            databaseLayout.setVisible(true);
            if (alleleFileUpload) {
              databaseUploadDescription = createDescriptionLabel(
                  dh.getUploadData_databaseUploadAndAllele());
              alleleFileSelectionCB.addValidator(nv);
            } else {
              databaseUploadDescription = createDescriptionLabel(dh.getUploadData_databaseUpload());
              alleleFileSelectionCB.setVisible(false);
              alleleFileSelectionCB.removeValidator(nv);
            }
          } else if (!useDatabase) {
            uploadLayout.setVisible(true);
          }
        }
      } else {
        Utils.notification("Error", dh.getUploadData_hlaExprValidatorDescription(), "error");
      }
    });
    return buttonsLayout;
  }

  /**
   * Creates the layout including text fields to define the column names of the data
   *
   * @return layout including text fields
   */
  public VerticalLayout createColumnTextFields() {
    VerticalLayout allColumnLayout = new VerticalLayout();
    Label description = createDescriptionLabel(dh.getUploadData_specifyColumns());

    HorizontalLayout columnTFLayout = new HorizontalLayout();
    columnTFLayout.setSpacing(true);

    // method column
    methodColTf = new TextField("Method Column");
    methodColTf.setStyleName("padded");
    methodColTf.setImmediate(true);
    methodColTf.setValue("");
    methodColTf.setDescription(dh.getUploadData_columnMethod());

    // taa column
    taaColTf = new TextField("TAA Column");
    taaColTf.setStyleName("padded");
    taaColTf.setImmediate(true);
    taaColTf.setValue("");
    taaColTf.setDescription(dh.getUploadData_columnTAA());

    // taa column
    transcriptExpressionColTf = new TextField("Transcript Expression Column");
    transcriptExpressionColTf.setStyleName("padded");
    transcriptExpressionColTf.setImmediate(true);
    transcriptExpressionColTf.setValue("");
    transcriptExpressionColTf.setDescription(dh.getUploadData_columnTranscriptExpression());

    // immunogenicity column
    immColTf = new TextField("Immunogenicity Column");
    immColTf.setStyleName("padded");
    immColTf.setImmediate(true);
    immColTf.setValue("");
    immColTf.setDescription(dh.getUploadData_columnImm());
    immColTf.addValidator(new StringLengthValidator("Please enter a column name", 1, 100, true));
    immColTf.setRequired(true);

    // distance column
    distanceColTf = new TextField("Distance Column");
    distanceColTf.setStyleName("padded");
    distanceColTf.setImmediate(true);
    distanceColTf.setValue("");
    distanceColTf.setDescription(dh.getUploadData_columnDistance());

    // uncertainty column
    uncertaintyColTf = new TextField("Uncertainty Column");
    uncertaintyColTf.setStyleName("padded");
    uncertaintyColTf.setImmediate(true);
    uncertaintyColTf.setValue("");
    uncertaintyColTf.setDescription(dh.getUploadData_columnUncertainty());

    columnTFLayout.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    columnTFLayout
        .addComponents(immColTf, methodColTf, transcriptExpressionColTf, taaColTf, distanceColTf,
            uncertaintyColTf);

    allColumnLayout.addComponents(description, columnTFLayout);
    return allColumnLayout;
  }

  public VerticalLayout createHlaExpressionTextFields() {
    VerticalLayout allAlleleLayout = new VerticalLayout();
    hlaDescription = createDescriptionLabel(dh.getUploadData_specifyAlleles());
    alleleTFLayout = new HorizontalLayout();
    HorizontalLayout alleleEVTFLayout = new HorizontalLayout();
    alleleEVTFLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    alleleTFLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);

    VerticalLayout hlaALayout = new VerticalLayout();
    Label hlaALabel = new Label("HLA-A alleles");
    hlaALayout.setSpacing(true);
    hlaALayout.setMargin(new MarginInfo(false, true, false, true));
    hlaALayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
    hlaALabel.addStyleName(ValoTheme.LABEL_COLORED);
    hlaALabel.addStyleName(ValoTheme.LABEL_BOLD);
    StringLengthValidator hlaValidator = new StringLengthValidator(
        "Please enter a valid HLA-allele");
    hlaValidator.setMinLength(11);
    hlaValidator.setMaxLength(11);
    hlaA1TF = new TextField();
    hlaA1TF.setStyleName("padded");
    hlaA1TF.setImmediate(true);
    hlaA1TF.setValue("HLA-A*");
    hlaA1TF.setRequired(true);
    hlaA1TF.addValidator(hlaValidator);
    hlaA1TF.setDescription("HLA-A Allele");
    hlaA2TF = new TextField();
    hlaA2TF.setStyleName("padded");
    hlaA2TF.setImmediate(true);
    hlaA2TF.setValue("HLA-A*");
    hlaA2TF.setDescription("HLA-A Allele");
    hlaA2TF.setRequired(true);
    hlaA2TF.addValidator(hlaValidator);
    hlaAEVTF = new TextField("HLA-A expression");
    hlaAEVTF.setStyleName("padded");
    hlaAEVTF.setImmediate(true);
    hlaAEVTF.setDescription("HLA-A expression");
    hlaAEVTF.setRequired(true);
    hlaAEVTF.setConverter(new StringToDoubleConverter());
    hlaAEVTF.addValidator(new DoubleRangeValidator("Please enter a float value", 0.0, 1000.0));
    hlaAEVTF.setValue("10,0");
    hlaAEVTF.setNullSettingAllowed(false);
    hlaALayout.addComponents(hlaALabel, hlaA1TF, hlaA2TF);

    VerticalLayout hlaBLayout = new VerticalLayout();
    hlaBLayout.setSpacing(true);
    hlaBLayout.setMargin(new MarginInfo(false, true, false, true));
    Label hlaBLabel = new Label("HLA-B alleles");
    hlaBLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
    hlaBLabel.addStyleName(ValoTheme.LABEL_COLORED);
    hlaBLabel.addStyleName(ValoTheme.LABEL_BOLD);
    hlaB1TF = new TextField();
    hlaB1TF.setStyleName("padded");
    hlaB1TF.setImmediate(true);
    hlaB1TF.setValue("HLA-B*");
    hlaB1TF.setDescription("HLA-B Allele");
    hlaB1TF.setRequired(true);
    hlaB1TF.addValidator(hlaValidator);
    hlaB2TF = new TextField();
    hlaB2TF.setStyleName("padded");
    hlaB2TF.setImmediate(true);
    hlaB2TF.setValue("HLA-B*");
    hlaB2TF.setDescription("HLA-B Allele");
    hlaB2TF.setRequired(true);
    hlaB2TF.addValidator(hlaValidator);
    hlaBEVTF = new TextField("HLA-B expression");
    hlaBEVTF.setStyleName("padded");
    hlaBEVTF.setImmediate(true);
    hlaBEVTF.setDescription("HLA-B expression");
    hlaBEVTF.setRequired(true);
    hlaBEVTF.setConverter(new StringToDoubleConverter());
    hlaBEVTF.addValidator(new DoubleRangeValidator("Please enter a float number", 0.0, 1000.0));
    hlaBEVTF.setValue("10,0");
    hlaBEVTF.setNullSettingAllowed(false);
    hlaBLayout.addComponents(hlaBLabel, hlaB1TF, hlaB2TF);

    VerticalLayout hlaCLayout = new VerticalLayout();
    hlaCLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
    hlaCLayout.setSpacing(true);
    Label hlaCLabel = new Label("HLA-C alleles");
    hlaCLayout.setMargin(new MarginInfo(false, true, false, true));
    hlaCLabel.addStyleName(ValoTheme.LABEL_COLORED);
    hlaCLabel.addStyleName(ValoTheme.LABEL_BOLD);
    hlaC1TF = new TextField();
    hlaC1TF.setStyleName("padded");
    hlaC1TF.setImmediate(true);
    hlaC1TF.setValue("HLA-C*");
    hlaC1TF.setDescription("HLA-C Allele");
    hlaC1TF.setRequired(true);
    hlaC1TF.addValidator(hlaValidator);
    hlaC2TF = new TextField();
    hlaC2TF.setStyleName("padded");
    hlaC2TF.setImmediate(true);
    hlaC2TF.setValue("HLA-C*");
    hlaC2TF.setDescription("HLA-C Allele");
    hlaC2TF.setRequired(true);
    hlaC2TF.addValidator(hlaValidator);
    hlaCEVTF = new TextField("HLA-C expression");
    hlaCEVTF.setStyleName("padded");
    hlaCEVTF.setImmediate(true);
    hlaCEVTF.setDescription("HLA-C expression");
    hlaCEVTF.setRequired(true);
    hlaCEVTF.setConverter(new StringToDoubleConverter());
    hlaCEVTF.addValidator(new DoubleRangeValidator("Please enter a float number", 0.0, 1000.0));
    hlaCEVTF.setValue("10,0");
    hlaCEVTF.setNullSettingAllowed(false);
    hlaCLayout.addComponents(hlaCLabel, hlaC1TF, hlaC2TF);

    alleleTFLayout.addComponents(hlaALayout, hlaBLayout, hlaCLayout);
    alleleEVTFLayout.addComponents(hlaAEVTF, hlaBEVTF, hlaCEVTF);
    alleleEVTFLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    alleleEVTFLayout.setSpacing(true);
    alleleEVTFLayout.setMargin(new MarginInfo(false, true, false, true));

    allAlleleLayout.addComponents(hlaDescription, alleleTFLayout, alleleEVTFLayout);

    return allAlleleLayout;
  }

  public VerticalLayout createDataSelection() {

    VerticalLayout allDataSelectionLayout = new VerticalLayout();
    Label description = createDescriptionLabel(dh.getUploadData_selectUpload());

    HorizontalLayout dataSelectionLayout = new HorizontalLayout();
    dataSelectionLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    dataSelectionLayout.setSizeFull();
    dataSelectionLayout.setSpacing(true);

    dataSelectionUploadButton = new Button("Directory");
    dataSelectionUploadButton.setStyleName(ValoTheme.BUTTON_LARGE);
    dataSelectionUploadButton.setIcon(FontAwesome.DESKTOP);
    dataSelectionUploadButton.addClickListener((Button.ClickListener) event -> {
      useDatabase = false;
      dataLayout.setVisible(false);
      buttonLayout.setVisible(true);
      nextButton.setVisible(true);
      alleleFileUpload = false;
      fileTypeSelectionLayout.setVisible(true);
    });

    dataSelectionDatabaseButton = new Button("Database");
    dataSelectionDatabaseButton.setStyleName(ValoTheme.BUTTON_LARGE);
    dataSelectionDatabaseButton.setIcon(FontAwesome.DATABASE);
    dataSelectionDatabaseButton.addClickListener((Button.ClickListener) event -> {
      useDatabase = true;
      dataLayout.setVisible(false);
      buttonLayout.setVisible(true);
      nextButton.setVisible(false);
      alleleFileSelection.setVisible(true);
    });

    downloadManualButton = new Button("How-to");
    downloadManualButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
    downloadManualButton.setIcon(FontAwesome.BOOK);
    Resource res = new FileResource(
        new File(basepath + "/WEB-INF/files/vaccineDesigner_howto.pdf"));
    FileDownloader downloader = new FileDownloader(res);
    downloader.extend(downloadManualButton);

    dataSelectionLayout.addComponents(dataSelectionUploadButton, dataSelectionDatabaseButton,
        downloadManualButton);
    dataSelectionLayout.setComponentAlignment(dataSelectionUploadButton, Alignment.MIDDLE_CENTER);
    dataSelectionLayout.setComponentAlignment(dataSelectionDatabaseButton, Alignment.MIDDLE_CENTER);

    allDataSelectionLayout.addComponents(description, dataSelectionLayout);

    return allDataSelectionLayout;
  }

  public Panel getUploadPanel() {
    return uploadPanel;
  }

  public VerticalLayout createDatabaseSelection() {
    databaseUploadDescription = createDescriptionLabel(dh.getUploadData_databaseUpload());

    projectSelectionCB = new ComboBox("Choose Project");
    projectSelectionCB.setRequired(true);
    projectSelectionCB.setFilteringMode(FilteringMode.CONTAINS);
    projectSelectionCB.setValidationVisible(true);

    alleleFileSelectionCB = new ComboBox("Choose Allele-file");
    alleleFileSelectionCB.setVisible(false);
    alleleFileSelectionCB.setRequired(true);
    alleleFileSelectionCB.setFilteringMode(FilteringMode.CONTAINS);

    VerticalLayout databaseLayout = new VerticalLayout();

    HorizontalLayout dataBaseSelection = new HorizontalLayout();
    dataBaseSelection.setMargin(true);
    dataBaseSelection.setSpacing(true);
    dataBaseSelection.addComponents(projectSelectionCB, alleleFileSelectionCB, uploadButton);
    dataBaseSelection.setComponentAlignment(uploadButton, Alignment.BOTTOM_CENTER);

    datasetGrid = new Grid();
    datasetGrid.setSizeFull();
    datasetGrid.setVisible(false);
    datasetGrid.setImmediate(true);
    datasetGrid.setSelectionMode(SelectionMode.SINGLE);
    datasetGrid.addSelectionListener((SelectionListener) event -> {
      Object selected = ((SingleSelectionModel) datasetGrid.getSelectionModel()).getSelectedRow();
      setSelected((BeanItem<DatasetBean>) datasetGrid.getContainerDataSource().getItem(selected));
      uploadButton.setEnabled(true);
      if (alleleFileUpload) {
        alleleFileSelectionCB.setVisible(true);
      }
      if (selected == null) {
        uploadButton.setEnabled(false);
        alleleFileSelectionCB.setVisible(false);
      }
    });

    databaseLayout.addComponents(databaseUploadDescription, dataBaseSelection, datasetGrid);

    return databaseLayout;
  }

  public VerticalLayout createAlleleFileSelection() {

    VerticalLayout allAlleleFileSelectionLayout = new VerticalLayout();
    Label description = createDescriptionLabel(dh.getUploadData_selectAlleleUpload());

    HorizontalLayout alleleFileSelectionLayout = new HorizontalLayout();
    alleleFileSelectionLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    alleleFileSelectionLayout.setSizeFull();
    alleleFileSelectionLayout.setSpacing(true);

    manualButton = new Button("Manual");
    manualButton.setStyleName(ValoTheme.BUTTON_LARGE);
    manualButton.setIcon(FontAwesome.KEYBOARD_O);
    manualButton.addClickListener((Button.ClickListener) event -> {
      this.alleleFileSelection.setVisible(false);
      this.fileTypeSelectionLayout.setVisible(true);
      this.nextButton.setVisible(true);
      alleleFileUpload = false;
    });

    databaseButton = new Button("Database");
    databaseButton.setStyleName(ValoTheme.BUTTON_LARGE);
    databaseButton.setIcon(FontAwesome.DATABASE);
    databaseButton.addClickListener((Button.ClickListener) event -> {
      this.alleleFileSelection.setVisible(false);
      alleleFileUpload = true;
      alleles = new HashMap<>();
      this.fileTypeSelectionLayout.setVisible(true);
      this.nextButton.setVisible(true);
    });

    alleleFileSelectionLayout.addComponents(manualButton, databaseButton);
    alleleFileSelectionLayout.setComponentAlignment(manualButton, Alignment.MIDDLE_CENTER);
    alleleFileSelectionLayout.setComponentAlignment(databaseButton, Alignment.MIDDLE_CENTER);

    allAlleleFileSelectionLayout.addComponents(description, alleleFileSelectionLayout);

    return allAlleleFileSelectionLayout;
  }

  public VerticalLayout createFileTypeSelection() {
    VerticalLayout fileLayout = new VerticalLayout();
    Label description = createDescriptionLabel(dh.getUploadData_selectStructure());

    HorizontalLayout imagesLayout = new HorizontalLayout();
    imagesLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    HorizontalLayout colLayout = new HorizontalLayout();
    colLayout.addStyleName("clickable");
    colLayout.addStyleName("notselected");
    HorizontalLayout rowLayout = new HorizontalLayout();
    rowLayout.addStyleName("clickable");
    rowLayout.addStyleName("notselected");
    FileResource rowRe = new FileResource(new File(basepath + "/WEB-INF/images/row.png"));
    Image rowImage = new Image(null, rowRe);
    rowImage.addClickListener((MouseEvents.ClickListener) event -> {
      hlaAsColumns = false;
      immColTf.setVisible(true);
      colLayout.addStyleName("notselected");
      rowLayout.removeStyleName("notselected");
    });
    FileResource colRe = new FileResource(new File(basepath + "/WEB-INF/images/column.png"));
    Image colImage = new Image(null, colRe);
    colImage.addClickListener((MouseEvents.ClickListener) event -> {
      hlaAsColumns = true;
      immColTf.setVisible(false);
      colLayout.removeStyleName("notselected");
      rowLayout.addStyleName("notselected");
    });
    colImage.setWidth("40%");
    colImage.setWidth("40%");
    rowImage.setWidth("40%");
    colLayout.addComponent(colImage);
    rowLayout.addComponent(rowImage);
    imagesLayout.addComponents(rowLayout, colLayout);
    imagesLayout.setSizeFull();

    fileLayout.addComponents(description, imagesLayout);

    return fileLayout;
  }

  public Label createDescriptionLabel(String info) {
    Label descriptionLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml() + "    " + info,
        ContentMode.HTML);
    descriptionLabel.addStyleName("description");
    return descriptionLabel;
  }

  public Grid getDatasetGrid() {
    return datasetGrid;
  }

  public TextField getImmColTf() {
    return immColTf;
  }

  public TextField getDistanceColTf() {
    return distanceColTf;
  }

  public TextField getUncertaintyColTf() {
    return uncertaintyColTf;
  }

  public TextField getTaaColTf() {
    return taaColTf;
  }

  public TextField getMethodColTf() {
    return methodColTf;
  }

  public UploaderInput getInputReceiver() {
    return inputReceiver;
  }

  public Upload getInputUpload() {
    return inputUpload;
  }

  public ComboBox getProjectSelectionCB() {
    return projectSelectionCB;
  }

  public Button getUploadButton() {
    return uploadButton;
  }

  public BeanItem<DatasetBean> getSelected() {
    return selected;
  }

  public void setSelected(BeanItem<DatasetBean> selected) {
    this.selected = selected;
  }

  public Button getDataSelectionDatabaseButton() {
    return dataSelectionDatabaseButton;
  }

  public Boolean getUseDatabase() {
    return useDatabase;
  }

  public Boolean getHlaAsColumns() {
    return hlaAsColumns;
  }

  public HashMap<String, String> getAlleles() {
    return alleles;
  }

  public void setAlleles(HashMap<String, String> alleles) {
    this.alleles = alleles;
  }

  public HashMap<String, String> getAllele_expressions() {
    return allele_expressions;
  }

  public Boolean getAlleleFileUpload() {
    return alleleFileUpload;
  }

  public ComboBox getAlleleFileSelectionCB() {
    return alleleFileSelectionCB;
  }

  public TextField getTranscriptExpressionColTf() {
    return transcriptExpressionColTf;
  }

  public Boolean isHlalValid() {
    Boolean isAllValid;
    if (hlaA1TF.isValid() && hlaA2TF.isValid() && hlaB1TF.isValid() && hlaB2TF.isValid() && hlaC1TF
        .isValid() && hlaC2TF.isValid()) {
      isAllValid = true;
    } else {
      isAllValid = false;
    }
    return isAllValid;
  }

  public Boolean isHlaExpressionlValid() {
    Boolean isAllValid;
    if (hlaAEVTF.isValid() && hlaBEVTF.isValid() && hlaCEVTF.isValid()) {
      isAllValid = true;
    } else {
      isAllValid = false;
    }
    return isAllValid;
  }
}
