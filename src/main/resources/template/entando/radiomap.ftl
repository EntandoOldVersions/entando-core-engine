<#--
/*
 * $Id: radiomap.ftl 720258 2008-11-24 19:05:16Z musachy $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
-->

<#-- 
         removed iteration cycle (no list expected as it used to be in the original Struts2 template)
     added 'checked' 
-->

<input type="radio" <#rt/>
<#if parameters.name??>
 name="${parameters.name?html}"<#rt/>
</#if>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.nameValue?exists>
 value="${parameters.nameValue}"<#rt/>
</#if>
<#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.tabindex??>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.cssClass??>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle??>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#if parameters.title??>
 title="${parameters.title?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
<#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
<#if parameters.checked?? >
 checked="checked"<#rt/>
</#if>
/><#rt/>
<#if parameters.label?exists>
        <label for="${parameters.id?html}"><#rt/>
            ${parameters.label}<#t/>
        </label>
</#if>