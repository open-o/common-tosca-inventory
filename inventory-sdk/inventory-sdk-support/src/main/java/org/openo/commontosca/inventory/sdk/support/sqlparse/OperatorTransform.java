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
package org.openo.commontosca.inventory.sdk.support.sqlparse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.Criteria.Group;
import org.openo.commontosca.inventory.sdk.support.DefaultCriteria;

public class OperatorTransform {

  @SuppressWarnings("unchecked")
  private Criteria createCriteriaByExpress(List<?> expressList, Group op) {
    Criteria criteria = new DefaultCriteria();
    criteria.setGroup(op);
    for (int i = 0; i < expressList.size(); i++) {
      Object express = expressList.get(i);
      if (express instanceof ObjectExpression) {
        ObjectExpression objectExpress = (ObjectExpression) express;
        criteria.addCriterion(objectExpress.getColumnname(), objectExpress.getOP(),
            objectExpress.getValue());
      } else if (express instanceof LinkedList) {

        boolean not = false;
        if (((LinkedList<?>) express).get(0).equals(ObjectExpression.NOT)) {
          not = true;
        }

        Criteria subCriteria = this.transform((LinkedList<Object>) express);
        if (not && !subCriteria.isNot()) {
          subCriteria.not();
        }
        criteria.add(subCriteria);
      }
    }
    return criteria;
  }

  private Criteria mergeAndOperator(List<Object> expressList, List<OperatorPosition> andList) {
    List<List<Object>> andSubList = new ArrayList<List<Object>>();
    Criteria topCriteria = null;

    for (OperatorPosition position : andList) {
      if (position.getOperator().equals(ObjectExpression.AND)) {
        List<Object> tmp = expressList.subList(position.getStartIndex(), position.getEndIndex());
        List<Object> subList = new LinkedList<Object>(tmp);
        andSubList.add(subList);

        for (int i = position.getStartIndex(); i < position.getEndIndex(); i++) {
          expressList.set(i, null);
        }
      }
    }

    Iterator<Object> it = expressList.iterator();
    while (it.hasNext()) {
      if (it.next() == null) {
        it.remove();
      }
    }

    topCriteria = this.createCriteriaByExpress(expressList, Group.OR);

    if (andSubList.size() > 0 && topCriteria.isEmpty()) {
      topCriteria = this.createCriteriaByExpress(andSubList.get(0), Group.AND);
    } else {
      for (List<?> tmpList : andSubList) {
        Criteria criteria = this.createCriteriaByExpress(tmpList, Group.AND);
        topCriteria.add(criteria);
      }
    }
    return topCriteria;
  }

  private List<OperatorPosition> searchAndOperator(List<?> expressList) {
    List<OperatorPosition> andList = new ArrayList<OperatorPosition>();
    int beginIdx = -1;
    int endIdx = -1;
    for (int i = 0; i < expressList.size(); i++) {
      Object express = expressList.get(i);
      if (express.equals(ObjectExpression.AND) && beginIdx == -1) {
        beginIdx = i - 1;
      }
      if (express.equals(ObjectExpression.OR)) {
        endIdx = i;
      }
      if (beginIdx != -1 && (i == expressList.size() - 1)) {
        endIdx = expressList.size();
      }
      if (beginIdx != -1 && endIdx != -1 && beginIdx <= endIdx) {
        OperatorPosition andPosition = new OperatorPosition(ObjectExpression.AND, beginIdx, endIdx);
        andList.add(andPosition);

        beginIdx = -1;
        endIdx = -1;
      }
    }
    return andList;
  }

  public Criteria transform(List<Object> expressList) {
    if (expressList == null) {
      return new DefaultCriteria();
    }
    List<OperatorPosition> andList = this.searchAndOperator(expressList);
    return this.mergeAndOperator(expressList, andList);
  }
}
