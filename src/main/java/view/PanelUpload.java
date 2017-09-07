package view;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import helper.UploaderInput;
import model.DatasetBean;

/**
 * 
 * The class {@link PanelUpload} represents a component which allows the user to define column names
 * and to upload epitope prediction data.
 * 
 * @author spaethju
 * 
 */
@SuppressWarnings("serial")
public class PanelUpload extends CustomComponent {

  private UploaderInput receiver;
  private Upload upload;
  private Panel uploadPanel;
  private VerticalLayout panelContent, uploadLayout, databaseLayout;
  private TextField immColTf, distanceColTf, uncertaintyColTf, taaColTf,methodColTf;
  private HorizontalLayout inputLayout, dataBaseSelection;
  private NativeSelect comboInput;
  private OptionGroup dataSelection;
  private ComboBox projectSelectionCB, fileSelectionCB;
  private Button uploadButton;
  private Grid datasetGrid;
  private BeanItem<DatasetBean> selected;

  /**
   * Constructor
   */
  public PanelUpload() {
    
    projectSelectionCB = new ComboBox("Choose Project");
    projectSelectionCB.setRequired(true);
    fileSelectionCB = new ComboBox("Choose File");
    fileSelectionCB.setRequired(true);
    fileSelectionCB.setEnabled(false);
    uploadButton = new Button("Upload");
    uploadButton.setEnabled(false);
    
    // Create the upload component and handle all its events
    receiver = new UploaderInput();
    upload = new Upload("Please Upload your Data", receiver);
    upload.setSizeFull();
    upload.addProgressListener(receiver);
    upload.addFailedListener(receiver);

    // Create the Upload Layout
    panelContent = new VerticalLayout();
    panelContent.setSpacing(true);
    panelContent.setMargin(true);
    panelContent.addComponent(createInfo());
    
    panelContent.addComponent(createDataSelection());
    panelContent.addComponent(createColumnTextFields());
    uploadLayout = new VerticalLayout();
    uploadLayout.setMargin(true);
    uploadLayout.setSpacing(true);
    uploadLayout.addComponent(upload);
    
    panelContent.addComponent(uploadLayout);
   

    // Create the panel
    uploadPanel = new Panel();
    uploadPanel.setContent(panelContent);
    receiver.getProgress().setVisible(false);
    
    setCompositionRoot(uploadPanel);
  }

  /**
   * Creates the layout including text fields to define the column names of the data
   * @return layout including text fields
   */
  public HorizontalLayout createColumnTextFields() {
    inputLayout = new HorizontalLayout();
    inputLayout.setMargin(true);
    inputLayout.setSpacing(true);
    
    comboInput = new NativeSelect("Select Input Type");
    comboInput.setNullSelectionAllowed(false);
    comboInput.addItems("Standard", "Old Filetype","New Filetype");
    comboInput.setRequired(true);
    comboInput.setValue("Standard");
    
    comboInput.addValueChangeListener((ValueChangeListener) event -> {
      if (comboInput.getValue().equals("New Filetype")) {
        immColTf.setValue("");
        immColTf.setVisible(false);
        distanceColTf.setValue("");
        distanceColTf.setVisible(false);
        uncertaintyColTf.setValue("");
        uncertaintyColTf.setVisible(false);
      } else {
        immColTf.setVisible(true);
        distanceColTf.setVisible(true);
        uncertaintyColTf.setVisible(true);
      }

    });
    
    // method column
    methodColTf = new TextField("Method Column");
    methodColTf.setStyleName(ValoTheme.TEXTFIELD_HUGE);
    methodColTf.setImmediate(true);
    methodColTf.setValue("");
    methodColTf.setDescription("Column name of the prediction method");
    
 // taa column
    taaColTf = new TextField("TAA Column");
    taaColTf.setStyleName(ValoTheme.TEXTFIELD_HUGE);
    taaColTf.setImmediate(true);
    taaColTf.setValue("");
    taaColTf.setDescription(
        "Column name specifying whether the peptide is a TAA or TSA. (if not specified all peptides are assumed to be TSAs)");

    // immunogenicity column
    immColTf = new TextField("Immunogenicity Column");
    immColTf.setStyleName(ValoTheme.TEXTFIELD_HUGE);
    immColTf.setImmediate(true);
    immColTf.setValue("HLA_class1_binding_prediction");
    immColTf.setDescription("Column name of peptide immunogenicity");
    immColTf.addValidator(new StringLengthValidator("Please enter a column name", 1, 100, true));
    immColTf.setRequired(true);

    // distance column
    distanceColTf = new TextField("Distance Column");
    distanceColTf.setStyleName(ValoTheme.TEXTFIELD_HUGE);
    distanceColTf.setImmediate(true);
    distanceColTf.setValue("");
    distanceColTf.setDescription("Column name of distance-to-self calculation");

    // uncertainty column
    uncertaintyColTf = new TextField("Uncertainty Column");
    uncertaintyColTf.setStyleName(ValoTheme.TEXTFIELD_HUGE);
    uncertaintyColTf.setImmediate(true);
    uncertaintyColTf.setValue("");
    uncertaintyColTf.setDescription("Column name of prediction uncertainty");

    inputLayout.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    inputLayout.addComponents(comboInput, methodColTf, taaColTf, distanceColTf, uncertaintyColTf, immColTf);

    return inputLayout;
  }
  
  public VerticalLayout createDataSelection() {
    
    VerticalLayout dataSelectionLayout = new VerticalLayout();
    dataSelectionLayout.setMargin(true);
    dataSelectionLayout.setSpacing(true);
    
    dataSelection = new OptionGroup();
    dataSelection.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
    dataSelection.addItem("Upload");
    dataSelection.addItem("Database");
    dataSelection.setNullSelectionItemId("Upload");
    
    dataSelection.addValueChangeListener((ValueChangeListener) event -> {
      if(dataSelection.getValue().equals("Upload")){
        panelContent.removeComponent(databaseLayout);
        panelContent.addComponent(uploadLayout);
      } else if (dataSelection.getValue().equals("Database")) {
        panelContent.removeComponent(uploadLayout);
        panelContent.addComponent(createDatabaseSelection());
      }

    });
    
    dataSelectionLayout.addComponent(dataSelection);
    
    return dataSelectionLayout;
  }
  
  public VerticalLayout createDatabaseSelection() {
    databaseLayout = new VerticalLayout();
    
    dataBaseSelection = new HorizontalLayout();
    dataBaseSelection.setMargin(true);
    dataBaseSelection.setSpacing(true);
    dataBaseSelection.addComponents(projectSelectionCB, uploadButton);
    dataBaseSelection.setComponentAlignment(uploadButton, Alignment.BOTTOM_CENTER);
    
    datasetGrid = new Grid();
    datasetGrid.setSizeFull();
    datasetGrid.setVisible(false);
    datasetGrid.setImmediate(true);
    datasetGrid.setSelectionMode(SelectionMode.SINGLE);
    datasetGrid.addSelectionListener((SelectionListener) event -> {
           Notification.show("Select row: "+datasetGrid.getSelectedRow());
           Object selected = ((SingleSelectionModel) datasetGrid.getSelectionModel()).getSelectedRow();
           setSelected((BeanItem<DatasetBean>) datasetGrid.getContainerDataSource().getItem(selected));
           uploadButton.setEnabled(true);
           if (selected == null) {
             uploadButton.setEnabled(false);
           }
       });
    
    
    databaseLayout.addComponent(dataBaseSelection);
    databaseLayout.addComponent(datasetGrid);
    
    return databaseLayout;
  }

  public Grid getDatasetGrid() {
    return datasetGrid;
  }

  /**
   * Creates the Layout which displays info text for the user.
   * 
   * @return info layout with information text
   */
  public VerticalLayout createInfo() {
    VerticalLayout infoLayout = new VerticalLayout();
    infoLayout.setMargin(true);
    infoLayout.setSpacing(true);
    infoLayout.addStyleName("padded");
    Label infoLa = new Label(
        "Please upload an epitope prediction file and define its colum names. If the column doesn't exist, leave it empty.");
    infoLa.addStyleName(ValoTheme.LABEL_BOLD);

    infoLayout.addComponents(infoLa);
    return infoLayout;
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

  public UploaderInput getReceiver() {
    return receiver;
  }

  public Upload getUpload() {
    return upload;
  }
  
  public NativeSelect getComboInput() {
    return comboInput;
  }

  public OptionGroup getDataSelection() {
    return dataSelection;
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

}
