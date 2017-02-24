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

public class VerifyException extends Exception {

  private static final long serialVersionUID = 1L;

  public VerifyException() {
    super();
  }

  public VerifyException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public VerifyException(String message, Throwable cause) {
    super(message, cause);
  }

  public VerifyException(String message) {
    super(message);
  }

  public VerifyException(Throwable cause) {
    super(cause);
  }

}
