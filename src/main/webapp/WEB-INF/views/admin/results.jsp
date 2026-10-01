<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <h1 class="h3 page-title mb-1">Results</h1>
    <p class="text-secondary mb-0">Admins can open live results of a running election. Voters see results only after the election is completed.</p>
</div>

<div class="card"><div class="card-body">
<c:choose>
    <c:when test="${empty elections}"><p class="text-secondary mb-0">No elections yet.</p></c:when>
    <c:otherwise>
    <div class="table-responsive">
    <table class="table align-middle">
        <thead><tr><th>Election</th><th>Ends</th><th>Status</th><th class="text-end">Result</th></tr></thead>
        <tbody>
        <c:forEach var="e" items="${elections}">
            <tr>
                <td><c:out value="${e.title}"/></td>
                <td class="text-nowrap"><c:out value="${e.endTimeDisplay}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${e.effectiveStatus == 'ACTIVE'}"><span class="badge text-bg-success">Active</span></c:when>
                        <c:when test="${e.effectiveStatus == 'CLOSED'}"><span class="badge text-bg-secondary">Completed</span></c:when>
                        <c:otherwise><span class="badge text-bg-info">Upcoming</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="text-end">
                    <a class="btn btn-primary btn-sm" href="<c:url value='/admin/elections/${e.id}/results'/>">
                        <c:choose><c:when test="${e.effectiveStatus == 'CLOSED'}">View results</c:when><c:otherwise>View live count</c:otherwise></c:choose>
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>
    </c:otherwise>
</c:choose>
</div></div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
