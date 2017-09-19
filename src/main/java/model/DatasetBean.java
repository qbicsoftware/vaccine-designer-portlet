package model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DatasetBean {

  private ProjectBean projectBean;
  private String code, dataSetTypeCode, type, name, dssPath, sampleIdentifier;
  private long id, size;
  private List<DatasetBean> children;
  private Map<String, String> properties;
  private Date registrationDate;
  private List<String> parents;
 
  public DatasetBean() {
 
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<DatasetBean> getChildren() {
    return children;
  }

  public boolean hasChildren() {
    return children != null && children.size() > 0;
  }

  public void setChildren(List<DatasetBean> children) {
    this.children = children;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
  
  public String getDssPath() {
    return dssPath;
  }

  public void setDssPath(String dssPath) {
    this.dssPath = dssPath;
  }

  public void setRegistrationDate(Date registrationDate) {
    this.registrationDate = registrationDate;
  }

  public void setSampleIdentifier(String sampleIdentifier){
    this.sampleIdentifier = sampleIdentifier;
  }

  public String getSampleIdentifier(){
    return sampleIdentifier;
  }

  public ProjectBean getProjectBean() {
    return projectBean;
  }

  public String getDataSetTypeCode() {
    return dataSetTypeCode;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public Date getRegistrationDate() {
    return registrationDate;
  }

  public void setProjectBean(ProjectBean projectBean) {
    this.projectBean = projectBean;
  }

  public void setDataSetTypeCode(String dataSetTypeCode) {
    this.dataSetTypeCode = dataSetTypeCode;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getName() {
    return name;

  }
}
