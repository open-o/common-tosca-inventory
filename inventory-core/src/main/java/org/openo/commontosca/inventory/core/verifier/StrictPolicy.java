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
package org.openo.commontosca.inventory.core.verifier;

public interface StrictPolicy {

  public static final long DISCARD_UNKNOWN_FIELD = 1;


  public static final long FAIL_ON_VERIFY = 1 << 1;


  public static final long RECORD_ERROR = 1 << 2;


  public static final long SAVE_STRICT_VALUE = 1 << 3;


  public static final long IGNORE_REQUIRED_KEY_WHEN_UPDATE = 1 << 4;


  public static final long DEFINE_INSERT_DATA = StrictPolicy.DISCARD_UNKNOWN_FIELD
      | StrictPolicy.FAIL_ON_VERIFY | StrictPolicy.SAVE_STRICT_VALUE;


  public static final long DEFINE_UPDATE_DATA =
      StrictPolicy.DEFINE_INSERT_DATA | StrictPolicy.IGNORE_REQUIRED_KEY_WHEN_UPDATE;


  public static final long DEFINE_IMPORT_DATA =
      StrictPolicy.RECORD_ERROR | StrictPolicy.SAVE_STRICT_VALUE;

}
