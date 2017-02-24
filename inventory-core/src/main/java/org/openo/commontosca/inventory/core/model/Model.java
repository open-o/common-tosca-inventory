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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Model {
  public static final String MODEL_DEFAULT_COLLECTION_NAME = "inventory_model";
  private static final Logger LOGGER = LoggerFactory.getLogger(Model.class);
  private String _id;
  private String name;
  private String label;
  private Date createTime;
  private Date lastModifyTime;
  private boolean enable = true;
  private String displayAttribute = "";
  private String description;
  private List<Attribute> attributes = new LinkedList<Attribute>();

  public Model() {}

  public static Model fromValueMap(Map<String, Object> valueMap) {
    Model model = new Model();
    model.fromMap(valueMap);
    return model;
  }

  public void fromMap(Map<String, Object> valueMap) {
    ValueMap map = ValueMap.wrap(valueMap);
    this._id = map.optValue(ModelKey.ID);
    this.name = map.requireValue(ModelKey.NAME);
    this.enable = map.optValue(ModelKey.ENABLE, true);
    this.label = map.optValue(ModelKey.LABEL, "");
    this.description = map.optValue(ModelKey.DESCRITION, "");
    this.createTime = map.optValue(ModelKey.CREATE_TIME, new Date());
    this.lastModifyTime = map.optValue(ModelKey.LAST_MODIFIED, new Date());
    this.displayAttribute = map.optValue(ModelKey.DISPLAY_ATTRUBITE, "");
    this.attributesFromMap(valueMap);
  }

  public String getId() {
    return this._id;
  }

  public Attribute getAttributeByName(String attributeName) {
    for (Attribute attr : this.attributes) {
      if (attr.getName().equals(attributeName)) {
        return attr;
      }
    }
    return null;
  }

  public String getDisplayAttribute() {
    return displayAttribute;
  }

  public void setDisplayAttribute(String displayAttribute) {
    this.displayAttribute = displayAttribute;
  }

  public List<Attribute> getAttributes() {
    return this.attributes;
  }

  public Date getCreateTime() {
    return this.createTime;
  }

  public String getDescription() {
    return this.description;
  }

  public String getLabel() {
    return this.label;
  }

  public Date getLastModifyTime() {
    return this.lastModifyTime;
  }

  public String getName() {
    return this.name;
  }

  public boolean isEnable() {
    return this.enable;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void attributesFromMap(Map<String, Object> valueMap) {
    List<Map> value = (List<Map>) valueMap.get(ModelConst.ATTRIBUTE_ATTRIBUTES);
    List<Attribute> attrList = new ArrayList<Attribute>();
    for (Map attrValueMap : value) {
      Attribute attribute = new Attribute();
      attribute.fromMap(attrValueMap);
      attrList.add(attribute);
    }
    this.attributes = attrList;
  }

  public void setId(String id) {
    this._id = id;
  }

  public void setAttributes(List<Attribute> attributes) {
    this.attributes = attributes;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setLabel(String displayName) {
    this.label = displayName;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public void setLastModifyTime(Date lastModifyTime) {
    this.lastModifyTime = lastModifyTime;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<String, Object>();
    ValueMap valueMap = ValueMap.wrap(map);
    if (this.getId() != null) {
      valueMap.put(ModelKey.ID, this.getId());
    }
    valueMap.put(ModelKey.NAME, this.name);
    valueMap.put(ModelKey.ENABLE, this.enable);
    valueMap.put(ModelKey.LABEL, this.label);
    if (this.description != null) {
      valueMap.put(ModelKey.DESCRITION, this.description);
    }
    valueMap.put(ModelKey.DISPLAY_ATTRUBITE, this.displayAttribute);
    List<Map<String, Object>> listAttribute = new ArrayList<Map<String, Object>>();
    for (Attribute attr : this.attributes) {
      listAttribute.add(attr.toMap());
    }
    valueMap.put(ModelKey.ATTRIBUTES, ValueList.wrap(listAttribute));
    return map;
  }

  /**
   *
   * 
   * @param model
   * @return
   */
  public static String toXml(ValueMap model) {
    Model m = new Model();
    m.fromMap(model);
    return m.toXml();
  }

  /**
   * 
   * 
   * @return
   */
  public String toXml() {
    XMLStreamWriter writer = null;
    ByteArrayOutputStream byteOutput = null;
    String s = "";
    try {
      byteOutput = new ByteArrayOutputStream();
      writer = XMLOutputFactory.newInstance().createXMLStreamWriter(byteOutput, "UTF-8");
      writeHeader(writer);
      writeBody(writer);
      writer.flush();
      s = byteOutput.toString("UTF-8");
    } catch (XMLStreamException | UnsupportedEncodingException e) {
      LOGGER.error("", e);
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (XMLStreamException e) {
          LOGGER.error("", e);
        }
      }
      if (byteOutput != null) {
        try {
          byteOutput.close();
        } catch (IOException e) {
          LOGGER.error("", e);
        }
      }
    }
    return s;
  }

  private void writeBody(XMLStreamWriter writer) throws XMLStreamException {
    writer.writeStartElement(ModelConst.Inventory);
    writer.writeCharacters(ModelConst.NEWLINE + generateTab(1));
    writer.writeStartElement(ModelConst.TAG_MODELS);
    writer.writeCharacters(ModelConst.NEWLINE + generateTab(2));
    writer.writeStartElement(ModelConst.TAG_MODEL);
    writer.writeAttribute(ModelConst.TAG_NAME, this.name);
    if (this.label != null) {
      writer.writeAttribute(ModelConst.TAG_LABEL, this.label);
    }
    if (this.description != null) {
      writer.writeAttribute(ModelConst.TAG_DESCRITION, this.description);
    }
    writer.writeAttribute(ModelConst.TAG_ENABLE, String.valueOf(this.enable));
    if (this.displayAttribute != null) {
      writer.writeAttribute(ModelConst.DISPLAY_ATTRIBUTE, this.displayAttribute);
    }
    writer.writeCharacters(ModelConst.NEWLINE);
    writeAttributes(writer, this.attributes);
    writer.writeCharacters(generateTab(2));
    writer.writeEndElement();
    writer.writeCharacters(ModelConst.NEWLINE);
    writer.writeCharacters(generateTab(1));
    writer.writeEndElement();
    writer.writeCharacters(ModelConst.NEWLINE);
    writer.writeEndElement();
  }

  private String generateTab(int times) {
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i <= times; i++) {
      sb.append(ModelConst.TAB);
    }
    return sb.toString();
  }

  private void writeHeader(XMLStreamWriter writer) throws XMLStreamException {
    writer.writeStartDocument("UTF-8", "1.0");
    writer.writeEndDocument();
    writer.writeCharacters(ModelConst.NEWLINE);
  }

  private void writeAttributes(XMLStreamWriter writer, List<Attribute> attributes)
      throws XMLStreamException {
    for (Attribute attr : attributes) {
      writer.writeCharacters(generateTab(3));
      writer.writeStartElement(ModelConst.TAG_ATTRIBUTE);
      if (attr.getName().equals(CommonKey.ID.getKeyName())) {
        writer.writeAttribute(ModelConst.TAG_NAME, attr.getName());
        if (attr.getLabel() != null) {
          writer.writeAttribute(ModelConst.TAG_LABEL, attr.getLabel());
        }
      } else {
        writer.writeAttribute(ModelConst.TAG_NAME, attr.getName());
        if (attr.getLabel() != null) {
          writer.writeAttribute(ModelConst.TAG_LABEL, attr.getLabel());
        }
        writer.writeAttribute(ModelConst.TAG_TYPE, attr.getType());
        if (attr.getType().equals(Attribute.TYPE_REFERENCE)) {
          writer.writeAttribute(ModelConst.TAG_REF, attr.getRef() == null ? "" : attr.getRef());
        }
        writer.writeAttribute(ModelConst.TAG_ISARRAY, String.valueOf(attr.isArray()));
        writer.writeAttribute(ModelConst.TAG_REQUIRED, String.valueOf(attr.isRequire()));
        writer.writeAttribute(ModelConst.TAG_UNIQUE, String.valueOf(attr.isUnique()));
        writer.writeAttribute(ModelConst.TAG_EDITABLE, String.valueOf(attr.isEditable()));
        writer.writeAttribute(ModelConst.TAG_VISIBLE, String.valueOf(attr.isVisible()));
      }
      writeProperties(writer, attr);
      writer.writeEndElement();
      writer.writeCharacters(ModelConst.NEWLINE);
    }
  }

  private void writeProperties(XMLStreamWriter writer, Attribute attr) throws XMLStreamException {
    if (!attr.getProperties().isEmpty()) {
      writer.writeCharacters(ModelConst.NEWLINE);
      writer.writeCharacters(generateTab(4));
      writer.writeStartElement(ModelConst.TAG_PROPERTIES);
      writer.writeCharacters(ModelConst.NEWLINE);
      for (Map.Entry<String, String> property : attr.getProperties().entrySet()) {
        writer.writeCharacters(generateTab(5));
        writer.writeStartElement(property.getKey());
        writer.writeCharacters(property.getValue());
        writer.writeEndElement();
        writer.writeCharacters(ModelConst.NEWLINE);
      }
      writer.writeCharacters(generateTab(4));
      writer.writeEndElement();
      writer.writeCharacters(ModelConst.NEWLINE);
      writer.writeCharacters(generateTab(3));
    }
  }

  /**
   * 
   * 
   * @return
   */
  public String getDisplayAttributeName() {
    if (!this.displayAttribute.isEmpty()) {
      return this.displayAttribute;
    } else {
      return "Name";
    }
  }

  /**
   * 
   * 
   * @return
   */
  public List<Attribute> getReferenceAttributes() {
    return attributes.stream()
        .filter(attribute -> attribute.getType().equals(Attribute.TYPE_REFERENCE))
        .collect(Collectors.toList());
  }

  /**
   * 
   * 
   * @return
   */
  public boolean isSelfReferenceModel() {
    List<Attribute> refAttrubites = this.getReferenceAttributes();
    return refAttrubites.stream().filter(attribute -> attribute.getRef().equals(this.getName()))
        .count() != 0;
  }

  /**
   * 
   * 
   * @return
   */
  public List<Attribute> getArrayAttributes() {
    return attributes.stream().filter(attribute -> attribute.isArray())
        .collect(Collectors.toList());
  }
}
