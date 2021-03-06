/*
 * Copyright © 2017, Salesforce.com, Inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.apt.graph.model.storage.apt;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import com.salesforce.apt.graph.model.storage.Resource;
import com.salesforce.apt.graph.model.storage.ResourceLoader;
import com.salesforce.apt.graph.model.storage.classpath.FileStore;

public class AptResourceLoader implements ResourceLoader {

  protected static final String FIELD_NAME = "__DATA_FIELD";
  
  private final Elements elements;
  
  public AptResourceLoader(Elements elements) {
    this.elements = elements;
  }
  
  @Override
  public List<Resource> getEntries(String name) {
    List<Resource> resources = new ArrayList<>();
    String location = name.replace('$', '.') + "_" + FileStore.STANDARD.getPath();
    TypeElement element = elements.getTypeElement(location);
    if (element != null) {
      VariableElement dataField = (VariableElement) element.getEnclosedElements().stream()
          .filter(e -> FIELD_NAME.equals(e.getSimpleName().toString())
              && e.getKind() == ElementKind.FIELD).findFirst().orElse(null);
      String data = (String) dataField.getConstantValue();
      resources.add(new StringResource(location, data));
    }
    return resources;
  }

}
