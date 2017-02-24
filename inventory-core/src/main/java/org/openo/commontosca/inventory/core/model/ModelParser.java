/**
 * Copyright  2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openo.commontosca.inventory.core.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.core.utils.I18n;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelParser {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModelParser.class);
  private File modelXmlFiles = null;
  private InputStream fileInput = null;
  private List<Model> resultModelList = new LinkedList<Model>();
  private List<String> listAttributeName = new LinkedList<String>();
  private List<String> listAttributeLabel = new LinkedList<String>();

  public ModelParser(File modelXmlFiles) {
    this.modelXmlFiles = modelXmlFiles;
  }

  public ModelParser(InputStream fileInput) {
    super();
    this.fileInput = fileInput;
  }

  private void loadModel(XMLStreamReader reader) throws XMLStreamException {
    while (reader.hasNext()) {
      int point = reader.next();
      switch (point) {
        case XMLStreamConstants.START_ELEMENT: {
          String elementName = reader.getName().toString();
          if (ModelConst.TAG_MODEL.equals(elementName)) {
            this.processModel(reader);
          }
          break;
        }
        case XMLStreamConstants.END_ELEMENT: {
          break;
        }
        default:
          break;
      }
    }
  }

  public List<Model> parse() throws FileNotFoundException, XMLStreamException {
    XMLStreamReader reader = null;
    try {
      if (this.fileInput == null) {
        fileInput = new FileInputStream(this.modelXmlFiles);
      }
      XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
      reader = xmlFactory.createXMLStreamReader(fileInput, "utf-8");
      this.loadModel(reader);
      return this.resultModelList;
    } catch (FileNotFoundException | XMLStreamException ex) {
      ModelParser.LOGGER.error("parse {} error", this.modelXmlFiles, ex);
      throw ex;
    } finally {
      if (fileInput != null) {
        try {
          fileInput.close();
        } catch (IOException e) {
          ModelParser.LOGGER.error("parse {} error", this.modelXmlFiles, e);
        }
      }
      if (reader != null) {
        reader.close();
      }
    }

  }


  private void parseAttribute(XMLStreamReader reader, Attribute attribute, Model model)
      throws XMLStreamException {
    attribute.setEnable(true);
    int attributeCount = reader.getAttributeCount();
    for (int i = 0; i < attributeCount; i++) {
      String attributeName = reader.getAttributeName(i).toString();
      String attributeValue = reader.getAttributeValue(i).toString();
      switch (attributeName) {
        case ModelConst.TAG_NAME: {
          attribute.setName(attributeValue);
          this.validateAttributeName(reader, model, attribute);
          this.listAttributeName.add(attributeValue);
          break;
        }
        case ModelConst.TAG_LABEL: {
          attribute.setLabel(attributeValue);
          this.validateAttributeLabel(reader, model, attribute);
          this.listAttributeLabel.add(attributeValue);
          break;
        }
        case ModelConst.TAG_TYPE: {
          attribute.setType(attributeValue);
          this.validateType(reader, attribute);
          break;
        }
        case ModelConst.TAG_VISIBLE: {
          boolean visible = this.parseBoolean(attributeValue, true);
          attribute.setVisible(visible);
          break;
        }
        case ModelConst.TAG_REQUIRED: {
          boolean required = this.parseBoolean(attributeValue, false);
          attribute.setRequire(required);
          break;
        }
        case ModelConst.TAG_ISARRAY: {
          boolean isArray = this.parseBoolean(attributeValue, false);
          attribute.setArray(isArray);
          break;
        }
        case ModelConst.TAG_UNIQUE: {
          boolean unique = this.parseBoolean(attributeValue, false);
          attribute.setUnique(unique);
          break;
        }
        case ModelConst.TAG_REF: {
          attribute.setRef(attributeValue);
          break;
        }
        case ModelConst.TAG_EDITABLE: {
          boolean editable = this.parseBoolean(attributeValue, true);
          attribute.setEditable(editable);
          break;
        }
        default:
          break;
      }
    }
  }

  private boolean parseBoolean(String strBoolean, boolean defaultValue) {
    boolean b = defaultValue;
    if (strBoolean != null) {
      b = Boolean.valueOf(strBoolean);
    }
    return b;
  }

  private void parseModel(XMLStreamReader reader, Model model) throws XMLStreamException {
    int attributeCount = reader.getAttributeCount();
    for (int i = 0; i < attributeCount; i++) {
      String attributeName = reader.getAttributeName(i).toString();
      String attributeValue = reader.getAttributeValue(i).toString();
      switch (attributeName) {
        case ModelConst.TAG_NAME: {
          model.setName(attributeValue);
          this.validateModelName(reader, model);
          break;
        }
        case ModelConst.TAG_LABEL: {
          model.setLabel(attributeValue);
          this.validateModelLabel(reader, model);
          break;
        }
        case ModelConst.TAG_DESCRITION: {
          model.setDescription(attributeValue);
          break;
        }
        case ModelConst.TAG_ENABLE: {
          boolean enable = this.parseBoolean(attributeValue, true);
          model.setEnable(enable);
          break;
        }
        case ModelConst.DISPLAY_ATTRIBUTE: {
          model.setDisplayAttribute(attributeValue);
          break;
        }
        default:
          break;
      }
    }
  }

  private void processAttribute(XMLStreamReader reader, Model model) throws XMLStreamException {
    Attribute attribute = new Attribute();
    this.parseAttribute(reader, attribute, model);
    if (ModelKey.ID.getKeyName().equals(attribute.getName())) {
      Attribute idAttribute = new Attribute();
      idAttribute.setName(ModelKey.ID.getKeyName());
      idAttribute.setLabel(attribute.getLabel());
      attribute = idAttribute;
    } else {
      this.validateType(reader, attribute);
      this.validateRef(reader, attribute);
    }
    while (reader.hasNext()) {
      int point = reader.next();
      switch (point) {
        case XMLStreamConstants.START_ELEMENT: {
          String elementName = reader.getName().toString();
          if (ModelConst.TAG_PROPERTIES.equals(elementName)) {
            this.processProperties(reader, attribute);
          }
          break;
        }
        case XMLStreamConstants.END_ELEMENT: {
          String elementName = reader.getName().toString();
          if (ModelConst.TAG_ATTRIBUTE.equals(elementName)) {
            model.getAttributes().add(attribute);
            return;
          }
          break;
        }
        default:
          break;
      }
    }
  }

  private void processModel(XMLStreamReader reader) throws XMLStreamException {
    Model model = new Model();
    this.parseModel(reader, model);
    this.validateModelNameAndLabel(reader, model);
    while (reader.hasNext()) {
      int point = reader.next();
      switch (point) {
        case XMLStreamConstants.START_ELEMENT: {
          String elementName = reader.getName().toString();
          if (ModelConst.TAG_ATTRIBUTE.equals(elementName)) {
            this.processAttribute(reader, model);
          }
          break;
        }
        case XMLStreamConstants.END_ELEMENT: {
          String elementName = reader.getName().toString();
          if (ModelConst.TAG_MODEL.equals(elementName)) {
            this.validateDefaultId(reader, model);
            this.validateDisplayAttribute(reader, model);
            this.resultModelList.add(model);
            this.listAttributeName.clear();
            return;
          }
        }
        default:
          break;
      }
    }
  }

  /**
   * @param reader
   * @param model
   */
  private void validateDefaultId(XMLStreamReader reader, Model model) {
    Attribute defaultIdAttribute = model.getAttributeByName(CommonKey.ID.getKeyName());
    if (defaultIdAttribute == null) {
      String message = I18n.getLabel("model.primaykey.not.found", getXmlPath(this.modelXmlFiles),
          model.getName(), CommonKey.ID.getKeyName());
      throw new InventoryException(message);
    }
  }

  private void processProperties(XMLStreamReader reader, Attribute attribute)
      throws XMLStreamException {
    while (reader.hasNext()) {
      int point = reader.next();
      switch (point) {
        case XMLStreamConstants.START_ELEMENT: {
          String elementName = reader.getName().toString();
          String value = reader.getElementText();
          attribute.getProperties().put(elementName, value);
          break;
        }
        case XMLStreamConstants.END_ELEMENT: {
          String elementName = reader.getName().toString();
          if (ModelConst.TAG_PROPERTIES.equals(elementName)) {
            return;
          }
        }
        default:
          break;
      }

    }
  }

  private void validateDisplayAttribute(XMLStreamReader reader, Model model) {
    if (model.getDisplayAttribute() == null || model.getDisplayAttribute().isEmpty()) {
      String message = I18n.getLabel("displayattribute.not.empty", getXmlPath(this.modelXmlFiles),
          model.getName());
      throw new InventoryException(message);
    }
    if (model.getAttributes().stream()
        .filter(attribute -> attribute.getName().equals(model.getDisplayAttribute()))
        .count() == 0) {
      String message = I18n.getLabel("displayattribute.not.found", getXmlPath(this.modelXmlFiles),
          model.getName());
      throw new InventoryException(message);
    }
  }

  private String getXmlPath(File modelXmlFiles) {
    return modelXmlFiles == null ? "" : modelXmlFiles.getAbsolutePath();
  }

  private void validateAttributeName(XMLStreamReader reader, Model model, Attribute attribute) {
    String location = I18n.getLabel("parse.error.location", reader.getLocation().getLineNumber(),
        reader.getLocation().getColumnNumber());
    if (attribute.getName() == null || attribute.getName().isEmpty()) {
      String message = I18n.getLabel("attribute.name.is.empty", getXmlPath(this.modelXmlFiles),
          attribute.getName());
      throw new InventoryException(location + message);
    }
    if (this.listAttributeName.contains(attribute.getName())) {
      String message = I18n.getLabel("attribute.name.is.duplicate", getXmlPath(this.modelXmlFiles),
          attribute.getName());
      throw new InventoryException(location + message);
    }
  }

  private void validateAttributeLabel(XMLStreamReader reader, Model model, Attribute attribute) {
    String location = I18n.getLabel("parse.error.location", reader.getLocation().getLineNumber(),
        reader.getLocation().getColumnNumber());
    if (attribute.getLabel() == null || attribute.getLabel().isEmpty()) {
      String message = I18n.getLabel("attribute.label.is.empty", getXmlPath(this.modelXmlFiles),
          attribute.getName());
      throw new InventoryException(location + message);
    }
    if (this.listAttributeLabel.contains(attribute.getLabel())) {
      String message = I18n.getLabel("attribute.label.is.duplicate", getXmlPath(this.modelXmlFiles),
          attribute.getName());
      throw new InventoryException(location + message);
    }
  }

  private void validateModelNameAndLabel(XMLStreamReader reader, Model model) {
    validateModelName(reader, model);
    validateModelLabel(reader, model);
  }

  private void validateModelName(XMLStreamReader reader, Model model) {
    String location = I18n.getLabel("parse.error.location", reader.getLocation().getLineNumber(),
        reader.getLocation().getColumnNumber());
    if (model.getName() == null || model.getName().isEmpty()) {
      String message =
          I18n.getLabel("model.name.is.empty", getXmlPath(this.modelXmlFiles), model.getName());
      throw new InventoryException(location + message);
    }
    if ("".equals(model.getLabel())) {
      model.setLabel(model.getName());
    }
  }

  private void validateModelLabel(XMLStreamReader reader, Model model) {
    String location = I18n.getLabel("parse.error.location", reader.getLocation().getLineNumber(),
        reader.getLocation().getColumnNumber());
    if (model.getLabel() == null || model.getLabel().isEmpty()) {
      String message =
          I18n.getLabel("model.label.is.empty", getXmlPath(this.modelXmlFiles), model.getName());
      throw new InventoryException(location + message);
    }

  }

  private void validateRef(XMLStreamReader reader, Attribute attribute) {
    if (!(attribute.getType().equals(Attribute.TYPE_REFERENCE)) && (attribute.getRef() != null)) {
      String location = I18n.getLabel("parse.error.location", reader.getLocation().getLineNumber(),
          reader.getLocation().getColumnNumber());
      String message = I18n.getLabel("ref.must.usewith.reference", getXmlPath(this.modelXmlFiles),
          attribute.getName());
      throw new InventoryException(location + message);
    }
  }

  private void validateType(XMLStreamReader reader, Attribute attribute) {
    if (attribute.getType() == null) {
      String location = I18n.getLabel("parse.error.location", reader.getLocation().getLineNumber(),
          reader.getLocation().getColumnNumber());
      String message = I18n.getLabel("attribute.must.set.datatype", getXmlPath(this.modelXmlFiles),
          attribute.getName());
      throw new InventoryException(location + message);
    }
    if (!Attribute.isAvaliableDataType(attribute.getType())) {
      String location = I18n.getLabel("parse.error.location", reader.getLocation().getLineNumber(),
          reader.getLocation().getColumnNumber());
      String message =
          I18n.getLabel("datatype.is.illegal", getXmlPath(this.modelXmlFiles), attribute.getType());
      throw new InventoryException(location + message);
    }
  }

}
