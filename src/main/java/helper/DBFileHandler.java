package helper;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.util.BeanItemContainer;

import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import ch.systemsx.cisd.openbis.plugin.query.shared.api.v1.dto.QueryTableModel;
import life.qbic.openbis.openbisclient.OpenBisClient;
import model.DatasetBean;

public class DBFileHandler {

  private OpenBisClient openbis;
  
  public DBFileHandler(OpenBisClient openbis){
    this.openbis = openbis;
  }
  
  /**
   * Returns a Container with the informations of de.uni_tuebingen.qbic.beans.DatasetBean.
   */
  public BeanItemContainer<DatasetBean> fillTable(
      List<DataSet> datasets) {
    HashMap<String, DataSet> dataMap = new HashMap<>();
    BeanItemContainer<DatasetBean> container;
    container = new BeanItemContainer<>(DatasetBean.class);

    for (DataSet ds : datasets) {
      dataMap.put(ds.getCode(), ds);
    }

    List<model.DatasetBean> datasetBeans = queryDatasetsForFiles(datasets);

    List<String> fileNames = new ArrayList<>();

    for (model.DatasetBean bean : datasetBeans) {
      fileNames.add(bean.getFileName());

      if (dataMap.get(bean.getCode()).getProperties() != null) {
        bean.setProperties(dataMap.get(bean.getCode()).getProperties());
      }

      container.addBean(bean);
    }

    return container;
  }
  
  /**
   *
   * @return A list of DatasetBeans denoting the roots of the folder structure of each dataset.
   *         Subfolders and files can be reached by calling the getChildren() function on each Bean.
   */
  private List<DatasetBean> queryDatasetsForFolderStructure(
          List<DataSet> datasets) {
    Map<String, List<String>> params = new HashMap<>();
    List<String> dsCodes = new ArrayList<>();
    Map<String, String> types = new HashMap<>();

    Map<String, Map<String, String>> props = new LinkedHashMap<>();

    for (DataSet ds : datasets) {
      dsCodes.add(ds.getCode());
      types.put(ds.getCode(), ds.getDataSetTypeCode());
      props.put(ds.getCode(), ds.getProperties());
    }

    params.put("codes", dsCodes);
    QueryTableModel res = openbis.queryFileInformation(params);

    // TODO this should work, but here starts the new code in case it doesn't 07.08.15 - Andreas
    Map<String, List<DatasetBean>> folderStructure = new HashMap<>();
    Map<String, DatasetBean> fileNames = new HashMap<>();

    for (Serializable[] ss : res.getRows()) {

      DatasetBean b = new DatasetBean();
      String code = (String) ss[0];
      String fileName = (String) ss[2];
      b.setCode(code);
      b.setType(types.get(code));
      b.setFileName(fileName);
      b.setDssPath((String) ss[1]);
      long size = (Long) ss[3];
      b.setFileSize(size);
      b.setRegistrationDate(parseDate((String) ss[5]));
      b.setProperties(props.get(code));

      // both code and filename are needed for the keys to be unique
      fileNames.put(code + fileName, b);

      // store file beans under their respective code+folder, except those with "original"
      String folderKey = (String) ss[4];
      if (!folderKey.equals("original"))
        folderKey = code + folderKey;
      if (folderStructure.containsKey(folderKey)) {
        folderStructure.get(folderKey).add(b);
      } else {
        List<DatasetBean> inFolder = new ArrayList<>();
        inFolder.add(b);
        folderStructure.put(folderKey, inFolder);
      }
    }
    // find children samples for our folders
    for (String fileNameKey : fileNames.keySet()) {
      // if the fileNameKey is in our folder map we have found a folder (other than "original")
      if (folderStructure.containsKey(fileNameKey))
        // and we add the files to this folder bean
        fileNames.get(fileNameKey).setChildren(folderStructure.get(fileNameKey));
    }
    // Now the structure should be set up. Root structures have "original" as parent folder
    List<DatasetBean> roots = folderStructure.get("original");

    // Remove empty folders
    List<DatasetBean> level = roots;
    while (!level.isEmpty()) {
      List<DatasetBean> collect = new ArrayList<>();
      List<DatasetBean> toRemove = new ArrayList<>();
      for (DatasetBean b : level) {
        if (b.hasChildren()) {
          collect.addAll(b.getChildren());
        } else {
          if (b.getFileSize() == 0) {
            toRemove.add(b);
          }
        }
      }
      level.removeAll(toRemove);
      level = collect;
    }

    // TODO remove following lines if it works, this is for debug
    level = roots;
    while (!level.isEmpty()) {
      List<DatasetBean> collect = new ArrayList<>();
      for (DatasetBean b : level) {
        if (b.hasChildren())
          collect.addAll(b.getChildren());
      }
      level = collect;
    }

    return roots;
  }

  // Recursively get all samples which are above the corresponding sample in the tree
  public List<DatasetBean> getAllFiles(List<DatasetBean> found, DatasetBean root) {
    List<DatasetBean> current = root.getChildren();

    if (current == null) {
      found.add(root);
    } else if (current.size() == 0) {
      found.add(root);

    } else {
      for (DatasetBean aCurrent : current) {
        getAllFiles(found, aCurrent);
      }
    }
    return found;
  }


  public List<DatasetBean> queryDatasetsForFiles(
      List<DataSet> datasets) {
    List<DatasetBean> results = new ArrayList<>();

    if (datasets.size() > 0) {
      List<DatasetBean> roots = queryDatasetsForFolderStructure(datasets);

      for (DatasetBean ds : roots) {
        List<DatasetBean> startList = new ArrayList<>();
        results.addAll(getAllFiles(startList, ds));
      }
    }

    return results;
  }
  
  private Date parseDate(String dateString) {
    Date date = null;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      date = formatter.parse(dateString.split("\\+")[0]);

    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

}
