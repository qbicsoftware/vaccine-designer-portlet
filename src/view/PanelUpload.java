package view;

import java.util.ArrayList;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import helper.UploaderInput;

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
  private VerticalLayout panelContent, uploadLayout;
  private TextField immColTf, distanceColTf, uncertaintyColTf, taaColTf,methodColTf;
  private HorizontalLayout inputLayout, dataBaseSelection;
  private NativeSelect comboInput;
  private OptionGroup dataSelection;
  private ArrayList<String> dataBase;

  /**
   * Constructor
   */
  public PanelUpload() {
    
    dataBase = new ArrayList<>();
    dataBase.add("Patient 1");
    dataBase.add("Patient 2");
    
    // Create the upload component and handle all its events
    receiver = new UploaderInput();
    upload = new Upload("Please Upload your Data", receiver);
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
    
    
//    comboInput.addValueChangeListener(new ValueChangeListener() {
//      
//      @Override
//      public void valueChange(ValueChangeEvent event) {
//        if (comboInput.getValue().equals("New Filetype")) {
//          inputLayout.removeComponent(immColTf);
//          inputLayout.removeComponent(distanceColTf);
//          inputLayout.removeComponent(uncertaintyColTf);
//        } else {
//          inputLayout.addComponent(immColTf);
//          inputLayout.addComponent(distanceColTf);
//          inputLayout.addComponent(uncertaintyColTf);
//        }
//        
//      }
//    });
    
    comboInput.addValueChangeListener(new ValueChangeListener() {
      
      @Override
      public void valueChange(ValueChangeEvent event) {
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
        
      }
    });
    
    // method column
    methodColTf = new TextField("Method Column");
    methodColTf.setStyleName(ValoTheme.TEXTFIELD_TINY);
    methodColTf.setImmediate(true);
    methodColTf.setValue("");
    methodColTf.setDescription("Column name of the prediction method");
    
 // taa column
    taaColTf = new TextField("TAA Column");
    taaColTf.setStyleName(ValoTheme.TEXTFIELD_TINY);
    taaColTf.setImmediate(true);
    taaColTf.setValue("");
    taaColTf.setDescription(
        "Column name specifying whether the peptide is a TAA or TSA. (if not specified all peptides are assumed to be TSAs)");

    // immunogenicity column
    immColTf = new TextField("Immunogenicity Column");
    immColTf.setStyleName(ValoTheme.TEXTFIELD_TINY);
    immColTf.setImmediate(true);
    immColTf.setValue("HLA_class1_binding_prediction");
    immColTf.setDescription("Column name of peptide immunogenicity");
    immColTf.addValidator(new StringLengthValidator("Please enter a column name", 1, 100, true));
    immColTf.setRequired(true);

    // distance column
    distanceColTf = new TextField("Distance Column");
    distanceColTf.setStyleName(ValoTheme.TEXTFIELD_TINY);
    distanceColTf.setImmediate(true);
    distanceColTf.setValue("");
    distanceColTf.setDescription("Column name of distance-to-self calculation");

    // uncertainty column
    uncertaintyColTf = new TextField("Uncertainty Column");
    uncertaintyColTf.setStyleName(ValoTheme.TEXTFIELD_TINY);
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
    
    dataSelection.addValueChangeListener(new ValueChangeListener() {
      
      @Override
      public void valueChange(ValueChangeEvent event) {
        if(dataSelection.getValue().equals("Upload")){
          panelContent.removeComponent(dataBaseSelection);
          panelContent.addComponent(uploadLayout);
        } else if (dataSelection.getValue().equals("Database")) {
          panelContent.removeComponent(uploadLayout);
          panelContent.addComponent(createDatabaseSelection());
        }
        
      }
    });
    
    dataSelectionLayout.addComponent(dataSelection);
    
    return dataSelectionLayout;
  }
  
  public HorizontalLayout createDatabaseSelection() {
    dataBaseSelection = new HorizontalLayout();
    dataBaseSelection.setMargin(true);
    dataBaseSelection.setSpacing(true);
    
    ComboBox selection = new ComboBox("Choose from database", dataBase);
    selection.setRequired(true);
    
    Button uploadButton = new Button("Upload");
    uploadButton.addClickListener(new ClickListener() {
      
      @Override
      public void buttonClick(ClickEvent event) {
        // TODO Auto-generated method stub
        
      }
      
    });
    
    dataBaseSelection.addComponents(selection, uploadButton);
    dataBaseSelection.setComponentAlignment(uploadButton, Alignment.BOTTOM_CENTER);
    return dataBaseSelection;
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

  public void setComboInput(NativeSelect comboInput) {
    this.comboInput = comboInput;
  }



}
