package model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DatasetBean {

  private ProjectBean projectBean;
  private String code, dataSetTypeCode, type, dssPath, name, sampleIdentifier;
  private long id, fileSize;
  private List<DatasetBean> children;
  private Map<String, String> properties;
  private Date registrationDate;
  private List<String> parents;
 
  public DatasetBean() {
 
  }

  public ProjectBean getProjectBean() {
    return projectBean;
  }
  public void setProjectBean(ProjectBean projectBean) {
    this.projectBean = projectBean;
  }
  public String getCode() {
    return code;
  }
  public void setCode(String code) {
    this.code = code;
  }
  public String getDataSetTypeCode() {
    return dataSetTypeCode;
  }
  public void setDataSetTypeCode(String dataSetTypeCode) {
    this.dataSetTypeCode = dataSetTypeCode;
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

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

  public void setChildren(List<DatasetBean> children) {
    this.children = children;
  }

  public Map<String, String> getProperties() {
    return properties;
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
  
  public String getFileName() {
    return name;
  }

  public void setFileName(String fileName) {
    this.name = fileName;
  }

  public Date getRegistrationDate() {
    return registrationDate;
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

  public void setParents(List parents){
    this.parents = parents;
  }

  public List<String> getParents(){
    return parents;
  }
}
