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
/**
 *
 */
package org.openo.commontosca.inventory.core.verifier;

import java.util.Arrays;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.Constants.DataKey;
import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.core.verifier.DataTypeVerifier;
import org.openo.commontosca.inventory.core.verifier.StrictPolicy;
import org.openo.commontosca.inventory.core.verifier.ValueError;
import org.openo.commontosca.inventory.core.verifier.ValueType;
import org.openo.commontosca.inventory.core.verifier.VerifyException;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;

public class DataTypeVerifierTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private ValueMap personModel = new ValueMap();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    this.personModel.clear();
    this.addAttribute(this.personModel, "name", ValueType.STRING, true, false);
    this.addAttribute(this.personModel, "age", ValueType.NUMBER, true, false);
    this.addAttribute(this.personModel, "marry", ValueType.BOOLEAN, false, false);
    this.addAttribute(this.personModel, "birthday", ValueType.DATETIME, false, false);
    this.addAttribute(this.personModel, "children", ValueType.REFERENCE, false, true);
    this.addAttribute(this.personModel, "size", ValueType.NUMBER, true, true);
  }

  @Test
  public void testVerifyDefaultId() throws Exception {
    ValueMap person = new ValueMap();
    person.put("name", "John");
    DataTypeVerifier verifier =
        new DataTypeVerifier(person, this.personModel).policy(StrictPolicy.DEFINE_IMPORT_DATA);
    verifier.verify();
    Assert.assertFalse("In the case where the ID is not filled, the output can not have an ID",
        verifier.getStrict().containsKey(DataKey.ID));

    person = new ValueMap();
    person.put("name", "John");
    person.put(CommonKey.ID, null);
    verifier =
        new DataTypeVerifier(person, this.personModel).policy(StrictPolicy.DEFINE_IMPORT_DATA);
    verifier.verify();
    Assert.assertFalse("In the case where the fill ID is null, the output can not have an ID",
        verifier.getStrict().containsKey(DataKey.ID));

    person = new ValueMap();
    person.put("name", "John");
    person.put(CommonKey.ID, "");
    verifier =
        new DataTypeVerifier(person, this.personModel).policy(StrictPolicy.DEFINE_IMPORT_DATA);
    verifier.verify();
    Assert.assertFalse(
        "In the case where the fill ID is an empty string, the output can not have an ID",
        verifier.getStrict().containsKey(DataKey.ID));
  }

  @Test
  public void testVerifyFailedOnRequired() throws Exception {
    ValueMap person = new ValueMap();
    person.put("name", "John");
    DataTypeVerifier verifier = new DataTypeVerifier(person, this.personModel);
    this.thrown.expect(VerifyException.class);
    verifier.verify();
  }

  @Test
  public void testVerifyFailedOnTypeNotMatch() throws Exception {
    ValueMap person = new ValueMap();
    person.put("name", "John");
    person.put("age", "abc");
    DataTypeVerifier verifier = new DataTypeVerifier(person, this.personModel);
    this.thrown.expect(VerifyException.class);
    verifier.verify();
  }

  @Test
  public void testVerifyFailedOnUnkownField() throws Exception {
    ValueMap person = new ValueMap();
    person.put("abc", "John");
    person.put("name", "John");
    DataTypeVerifier verifier = new DataTypeVerifier(person, this.personModel);
    this.thrown.expect(VerifyException.class);
    verifier.verify();
  }

  @Test
  public void testVerifyOnRequired() throws Exception {
    ValueMap person = new ValueMap();
    person.put("name", "John");
    DataTypeVerifier verifier =
        new DataTypeVerifier(person, this.personModel).policy(StrictPolicy.DEFINE_IMPORT_DATA);
    verifier.verify();
    Assert.assertFalse(verifier.getErrors().isEmpty());
    Assert.assertEquals(ValueError.REQUIRED_IS_NULL,
        verifier.getErrors().requireMap(0).requireValue("error", ValueError.class));
    Assert.assertEquals(2, verifier.getErrors().size());
  }

  @Test
  public void testVerifyOnTypeNotMatch() throws Exception {
    ValueMap person = new ValueMap();
    person.put("name", "John");
    person.put("age", "abc");
    DataTypeVerifier verifier =
        new DataTypeVerifier(person, this.personModel).policy(StrictPolicy.DEFINE_IMPORT_DATA);
    verifier.verify();
    Assert.assertFalse(verifier.getErrors().isEmpty());
    Assert.assertEquals(ValueError.TYPE_NOT_MATCHED,
        verifier.getErrors().requireMap(0).requireValue("error", ValueError.class));
    Assert.assertEquals(2, verifier.getErrors().size());
  }

  @Test
  public void testVerifyOnUnkownField() throws Exception {
    ValueMap person = new ValueMap();
    person.put("abc", "John");
    person.put("name", "John");
    DataTypeVerifier verifier =
        new DataTypeVerifier(person, this.personModel).policy(StrictPolicy.DEFINE_IMPORT_DATA);
    verifier.verify();
    Assert.assertFalse(verifier.getErrors().isEmpty());
    Assert.assertEquals(ValueError.UNKNOWN_FIELD, verifier.getErrors()
        .requireMap(verifier.getErrors().size() - 1).requireValue("error", ValueError.class));
    Assert.assertEquals(3, verifier.getErrors().size());
  }

  @Test
  public void testVerifyOnUpdateRequiredField() throws Exception {
    ValueMap person = new ValueMap();
    person.put("name", "John");
    DataTypeVerifier verifier =
        new DataTypeVerifier(person, this.personModel).policy(StrictPolicy.DEFINE_UPDATE_DATA);
    ValueMap strict = verifier.verify();
    Assert.assertTrue(verifier.getErrors().isEmpty());
    Assert.assertNotNull(strict);

    person = new ValueMap();
    person.put("name", "John");
    person.put("age", null);
    verifier =
        new DataTypeVerifier(person, this.personModel).policy(StrictPolicy.DEFINE_UPDATE_DATA);

    this.thrown.expect(VerifyException.class);
    verifier.verify();
  }

  @Test
  public void testVerifyToStrictValue() throws Exception {
    ValueMap person = new ValueMap();
    person.put("name", "John");
    person.put("age", "24");
    person.put("marry", "false");
    person.put("birthday", "2016-03-09");
    person.put("children", Arrays.asList("child-1", "child-2", 3333, 4444));
    person.put("size", Arrays.asList("123", "321", 3333, 4444));
    DataTypeVerifier verifier = new DataTypeVerifier(person, this.personModel);
    verifier.verify();
    ValueMap strict = verifier.getStrict();
    Assert.assertEquals("John", strict.get("name"));
    Assert.assertEquals(24L, strict.get("age"));
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(2016, 2, 9);
    Assert.assertEquals(calendar.getTime(), strict.get("birthday"));
    Assert.assertEquals(Arrays.asList("child-1", "child-2", "3333", "4444"),
        strict.get("children"));
    Assert.assertEquals(Arrays.asList(123L, 321L, 3333, 4444), strict.get("size"));
  }

  private void addAttribute(ValueMap model, String key, ValueType type, boolean required,
      boolean isArray) {
    ValueList list = model.optValue(ModelKey.ATTRIBUTES, null);
    if (list == null) {
      list = new ValueList();
      model.put(ModelKey.ATTRIBUTES, list);
    }
    ValueMap attribute = new ValueMap();
    attribute.put(ModelKey.NAME, key);
    attribute.put(ModelKey.TYPE, type.getTypeString());
    attribute.put(ModelKey.REQUIRED, required);
    attribute.put(ModelKey.IS_ARRAY, isArray);
    list.add(attribute);
  }

}
