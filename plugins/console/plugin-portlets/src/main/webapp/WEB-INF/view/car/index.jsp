<%--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
--%>
<%@ page import="org.apache.geronimo.console.util.PortletManager"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="pluginportlets"/>
<portlet:defineObjects/>
<fmt:message key="car.index.summary" />


<form name="<portlet:namespace/>PluginForm" action="<portlet:actionURL/>">
    <input type="hidden" name="mode" value="index-after" />
    <b><fmt:message key="car.common.repository" />:</b> <%-- todo: entry field for user-specified list --%>
    <select name="repository">
        <c:forEach var="repo" items="${repositories}">
            <option<c:if test="${repo eq repository}"> selected</c:if>>${repo}</option>
        </c:forEach>
    </select>
    <c:if test="${!empty repositories}"><br /></c:if>
    <i>(<a href="<portlet:actionURL portletMode="view"><portlet:param name="mode" value="updateList-before" /><portlet:param name="repository" value="${repository}" /></portlet:actionURL>"><fmt:message key="car.index.updateRepositoryList" /></a>
     or <a href="<portlet:actionURL portletMode="view"><portlet:param name="mode" value="addRepository-before" /><portlet:param name="repository" value="${repository}" /></portlet:actionURL>"><fmt:message key="car.common.addRepository" /></a>)</i>
    <%--<input type="text" name="repository" value="${repository}" size="30" maxlength="200" />--%>
    <c:if test="${!empty repositories}">
      <input type="submit" value='<fmt:message key="car.common.searchForPlugins" />' />
      <br /><b><fmt:message key="car.index.optionalAuthentication" />:</b>
         <fmt:message key="consolebase.common.user"/>: <input type="text" name="username" value="${repouser}" size="12" maxlength="200" />
         <fmt:message key="consolebase.common.password"/>: <input type="password" name="password" value="${repopass}" size="12" maxlength="200" />
    </c:if>
</form>

<h2><fmt:message key="car.common.createGeronimoPlugin" /></h2>
<p><fmt:message key="car.index.createGeronimoPluginExp" /></p>

<form name="<portlet:namespace/>ExportForm" action="<portlet:actionURL/>" method="POST">
    <input type="hidden" name="mode" value="configure-before" />
    <select name="configId">
        <option />
      <c:forEach var="config" items="${configurations}">
        <option>${config.configID}</option>
      </c:forEach>
    </select>
    <input type="submit" value='<fmt:message key="car.common.exportPlugin" />' />
</form>

<h2>Assemble a server from plugins in this one</h2>

<form name="<portlet:namespace/>AssemblyForm" action="<portlet:actionURL/>" method="POST">
    <input type="hidden" name="mode" value="listServer-before" />
    <input type="submit" value='Assemble a server' />
</form>

