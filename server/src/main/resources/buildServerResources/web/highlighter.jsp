<%@ include file="/include.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags"%>

<jsp:useBean id="pluginId" type="java.lang.String" scope="request"/>
<jsp:useBean id="model" type="com.jpfeffer.teamcity.highlighter.model.HighlightDataModel" scope="request"/>

<head>
	<style type="text/css">
		p.hlght_title {
			margin-bottom: 5px;
		}
		div.hlght_text {
			padding-left: 10px;
			margin: 0;
			white-space: pre-line;
		}
		img.hlght_level {
			width: 12px;
			height: 12px;
			padding: 2px;
		}
	</style>
</head>

<c:forEach var="highlightData" items="${model.highlightData}" varStatus="group">
	<c:set var="highlightTitle">
		<c:if test="${highlightData.level != 'info'}">
			<bs:icon icon="../../plugins/${pluginId}/img/${highlightData.level}.png" addClass="hlght_level"/>
		</c:if>
		<span>${highlightData.key}</span>
	</c:set>

	<bs:_collapsibleBlock title="${highlightTitle}" id="idhiglightT${group.index + 1}" headerClass="hlght_title" collapsedByDefault="${highlightData.collapsedByDefault}">
		<c:forEach var="value" items="${highlightData.values}" varStatus="stat">
			<div class="hlght_text">${value}</div>
		</c:forEach>
	</bs:_collapsibleBlock>
</c:forEach>
