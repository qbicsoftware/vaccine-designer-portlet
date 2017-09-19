package view;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.StringToFloatConverter;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.FloatRangeValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import helper.DescriptionHandler;

/**
 * The class {@link PanelParameters} represents a component for showing and adjusting the
 * arguments/parameters expected by the epitope selection script
 *
 * @author spaethju
 */
@SuppressWarnings("serial")
public class PanelParameters extends CustomComponent {

    private Panel panel;
    private VerticalLayout panelContent;
    private Slider kSlider, consAlleleSlider, consAntigenSlider, consOverlapSlider, ktaaSlider;
    private TextField threshEpitopeTF, threshDistanceTF;
    private Label kLabel, consAlleleLabel, consAntigenLabel, consOverlapLabel, ktaaLabel;
    private CheckBox rankCB;
    private DescriptionHandler dh = new DescriptionHandler();

    /**
     * Constructor
     */
    public PanelParameters() {
        panel = new Panel();
        setCompositionRoot(createPanel());
    }

    /**
     * Creates the main panel
     *
     * @return panel providing everything for the parameter ajustment
     */
    public Panel createPanel() {

        panelContent = new VerticalLayout();
        HorizontalLayout parameterLayout = new HorizontalLayout();
        parameterLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        panel.setContent(panelContent);

        // The number of epitopes
        VerticalLayout kLayout = new VerticalLayout();
        kLayout.setImmediate(true);
        kSlider = new Slider("Number of Epitopes");
        kSlider.setImmediate(true);
        kSlider.setDescription(dh.getParameter_numberOfEpitopes());
        kSlider.setImmediate(true);
        kSlider.setResolution(0);
        kSlider.setValue(10.0);
        kSlider.setWidth("150px");
        kLabel = new Label("10");
        kLabel.setImmediate(true);
        kLabel.setStyleName("parameter");
        kSlider.addValueChangeListener((Property.ValueChangeListener) event -> {
            int value = kSlider.getValue().intValue();
            // Use the value
            kLabel.setValue(String.valueOf(value));
            //change ktaa
            ktaaSlider.setMax(value);
            if (ktaaSlider.getValue().intValue() > value) {
                ktaaSlider.setValue((double) value);
            }
        });
        kLayout.addComponents(kSlider, kLabel);
        kLayout.setComponentAlignment(kLabel, Alignment.MIDDLE_CENTER);
        kLayout.addStyleName("padded");

        // Percentage of alleles which have to be covered [0,1]
        VerticalLayout consAlleleLayout = new VerticalLayout();
        consAlleleSlider = new Slider("Allele Constraint");
        consAlleleSlider
                .setDescription(dh.getParameter_alleleConstraint());
        consAlleleSlider.setImmediate(true);
        consAlleleSlider.setValue(0.0);
        consAlleleSlider.setMax(1.0);
        consAlleleSlider.addValidator(
                new DoubleRangeValidator("Please select a number between 0 and 1", (double) 0, (double) 1));
        consAlleleSlider.setResolution(2);
        consAlleleSlider.setWidth("150px");
        consAlleleLabel = new Label("deactivated");
        consAlleleLabel.setStyleName("parameter");
        consAlleleSlider.addValueChangeListener((Property.ValueChangeListener) event -> {
            double value = consAlleleSlider.getValue();
            // Use the value
            if (value == 0) {
                consAlleleLabel.setValue("deactivated");
            } else {
                consAlleleLabel.setValue(String.valueOf(value));
            }
        });
        consAlleleLayout.addComponents(consAlleleSlider, consAlleleLabel);
        consAlleleLayout.setComponentAlignment(consAlleleLabel, Alignment.MIDDLE_CENTER);
        consAlleleLayout.addStyleName("padded");

        // The number of epitopes which have to come from each variation ]0,1]
        VerticalLayout consAntigenLayout = new VerticalLayout();
        consAntigenSlider = new Slider("Antigen Constraint");
        consAntigenSlider
                .setDescription(dh.getParameter_antigenConstraint());
        consAntigenSlider.setImmediate(true);
        consAntigenSlider.setValue(0.0);
        consAntigenSlider.setMax(1.0);
        consAntigenSlider.addValidator(
                new DoubleRangeValidator("Please select a number between 0 and 1", (double) 0, (double) 1));
        consAntigenSlider.setResolution(2);
        consAntigenSlider.setWidth("150px");
        consAntigenLabel = new Label("deactivated");
        consAntigenLabel.setStyleName("parameter");
        consAntigenSlider.addValueChangeListener((Property.ValueChangeListener) event -> {
            double value = consAntigenSlider.getValue();
            // Use the value
            if (value == 0) {
                consAntigenLabel.setValue("deactivated");
            } else {
                consAntigenLabel.setValue(String.valueOf(value));
            }
        });
        consAntigenLayout.addComponents(consAntigenSlider, consAntigenLabel);
        consAntigenLayout.setComponentAlignment(consAntigenLabel, Alignment.MIDDLE_CENTER);
        consAntigenLayout.addStyleName("padded");

        // Activates epitope overlapping constraint with specified threshold
        VerticalLayout consOverlapLayout = new VerticalLayout();
        consOverlapSlider = new Slider("Overlap Constraint");
        consOverlapSlider
                .setDescription(dh.getParameter_overlapConstraint());
        consOverlapSlider.setImmediate(true);
        consOverlapSlider.setValue(0.0);
        consOverlapSlider.setWidth("150px");
        consOverlapLabel = new Label("deactivated");
        consOverlapLabel.setStyleName("parameter");
        consOverlapSlider.addValueChangeListener((Property.ValueChangeListener) event -> {
            int value = consOverlapSlider.getValue().intValue();
            if (value == 0) {
                consOverlapLabel.setValue("deactivated");
            } else {
                consOverlapLabel.setValue(String.valueOf(value));
            }

        });
        consOverlapLayout.addComponents(consOverlapSlider, consOverlapLabel);
        consOverlapLayout.setComponentAlignment(consOverlapLabel, Alignment.MIDDLE_CENTER);
        consOverlapLayout.addStyleName("padded");

        // Specifies the number of TAA epitopes that are allowed to select
        VerticalLayout ktaaLayout = new VerticalLayout();
        ktaaSlider = new Slider("Number of TAA");
        ktaaSlider.setDescription(dh.getParameter_numberOfTAA());
        ktaaSlider.setImmediate(true);
        ktaaSlider.setValue(0.0);
        ktaaSlider.setWidth("150px");
        ktaaLabel = new Label("0");
        ktaaLabel.setStyleName("parameter");
        ktaaSlider.addValueChangeListener((Property.ValueChangeListener) event -> {
            int value = ktaaSlider.getValue().intValue();

            // Use the value
            ktaaLabel.setValue(String.valueOf(value));
        });
        ktaaLayout.addComponents(ktaaSlider, ktaaLabel);
        ktaaLayout.setComponentAlignment(ktaaLabel, Alignment.MIDDLE_CENTER);
        ktaaLayout.addStyleName("padded");

        // Specifies the binding/immunogenicity threshold for all alleles
        VerticalLayout threshEpitopeLayout = new VerticalLayout();
        threshEpitopeTF = new TextField("Epitope Threshold");
        threshEpitopeTF
                .setDescription(dh.getParameter_epitopeTreshold());
        threshEpitopeTF.setConverter(new StringToFloatConverter());
        threshEpitopeTF
                .addValidator(new FloatRangeValidator("Please enter a float number", null, null));
        threshEpitopeTF.setNullRepresentation("0.0");
        threshEpitopeTF.setValue("0.0");
        threshEpitopeTF.setWidth("150px");
        threshEpitopeTF.setNullSettingAllowed(true);
        threshEpitopeLayout.addComponent(threshEpitopeTF);
        threshEpitopeLayout.addStyleName("padded");

        // Specifies the distance-to-self threshold for all alleles
        VerticalLayout threshDistanceLayout = new VerticalLayout();
        threshDistanceTF = new TextField("Distance Threshold");
        threshDistanceTF.setDescription(dh.getParameter_distanceThreshold());
        threshDistanceLayout.addComponent(threshDistanceTF);
        threshDistanceTF.setConverter(new StringToFloatConverter());
        threshDistanceTF
                .addValidator(new FloatRangeValidator("Please enter a float number", null, null));
        threshDistanceTF.setNullRepresentation("0.0");
        threshDistanceTF.setValue("0.0");
        threshDistanceTF.setNullSettingAllowed(true);
        threshDistanceTF.setWidth("150px");
        threshDistanceLayout.addStyleName("padded");

        VerticalLayout rankLayout = new VerticalLayout();
        rankCB = new CheckBox("Rank");
        rankCB.setDescription(dh.getParameter_rank());
        rankLayout.addComponent(rankCB);
        rankCB.setValue(false);
        rankLayout.addStyleName("padded");


        parameterLayout.addComponents(kLayout, ktaaLayout, consAlleleLayout, consAntigenLayout,
                consOverlapLayout, threshEpitopeLayout, threshDistanceLayout, rankLayout);
        panelContent.setMargin(true);
        panelContent.setSpacing(true);

        panelContent.addComponents(createInfo(), parameterLayout);


        return panel;
    }

    /**
     * creates the Layout which displays info text for the user.
     *
     * @return info layout with information text
     */
    public VerticalLayout createInfo() {
        VerticalLayout infoLayout = new VerticalLayout();

        Label infoLa = createDescriptionLabel(dh.getParameterSettings());

        infoLayout.addComponents(infoLa);

        return infoLayout;
    }

    public Label createDescriptionLabel(String info) {
        Label descriptionLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml() + "    " + info, ContentMode.HTML);
        descriptionLabel.addStyleName("description");
        return descriptionLabel;
    }

    public void update() {
        setCompositionRoot(createPanel());
    }

    /**
     * resets the whole panel
     */
    public void reset() {
        panelContent.removeAllComponents();
    }

    public Slider getConsAlleleSlider() {
        return consAlleleSlider;
    }

    public Slider getConsAntigenSlider() {
        return consAntigenSlider;
    }

    public Slider getKSlider() {
        return kSlider;
    }

    public Slider getConsOverlapSlider() {
        return consOverlapSlider;
    }

    public Slider getKtaaSlider() {
        return ktaaSlider;
    }

    public TextField getThreshEpitopeTF() {
        return threshEpitopeTF;
    }

    public TextField getThreshDistanceTF() {
        return threshDistanceTF;
    }

    public CheckBox getRankCB() {
        return rankCB;
    }


}
