<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 page-header">
    <div>
        <h1 class="h3 page-title mb-1">Manage elections</h1>
        <p class="text-secondary mb-0">Status changes automatically with the start and end time.</p>
    </div>
    <a class="btn btn-primary" href="<c:url value='/admin/elections/new'/>"><i class="bi bi-plus-lg"></i> Create election</a>
</div>

<div class="filter-pills mb-3 d-flex flex-wrap gap-2">
    <a class="btn btn-sm ${empty selectedStatus ? 'btn-primary' : 'btn-outline-primary'}" href="<c:url value='/admin/elections'/>">All</a>
    <a class="btn btn-sm ${selectedStatus == 'UPCOMING' ? 'btn-primary' : 'btn-outline-primary'}" href="<c:url value='/admin/elections?status=UPCOMING'/>">Upcoming</a>
    <a class="btn btn-sm ${selectedStatus == 'ACTIVE' ? 'btn-primary' : 'btn-outline-primary'}" href="<c:url value='/admin/elections?status=ACTIVE'/>">Active</a>
    <a class="btn btn-sm ${selectedStatus == 'CLOSED' ? 'btn-primary' : 'btn-outline-primary'}" href="<c:url value='/admin/elections?status=COMPLETED'/>">Completed</a>
</div>

<div class="card"><div class="card-body">
<c:choose>
    <c:when test="${empty elections}"><p class="text-secondary mb-0">No elections found.</p></c:when>
    <c:otherwise>
    <div class="table-responsive">
    <table class="table align-middle">
        <thead><tr><th>Title</th><th>Starts</th><th>Ends</th><th>Status</th><th class="text-end">Actions</th></tr></thead>
        <tbody>
        <c:forEach var="e" items="${elections}">
            <tr>
                <td><c:out value="${e.title}"/></td>
                <td class="text-nowrap"><c:out value="${e.startTimeDisplay}"/></td>
                <td class="text-nowrap"><c:out value="${e.endTimeDisplay}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${e.effectiveStatus == 'ACTIVE'}"><span class="badge text-bg-success">Active</span></c:when>
                        <c:when test="${e.effectiveStatus == 'CLOSED'}"><span class="badge text-bg-secondary">Completed</span></c:when>
                        <c:otherwise><span class="badge text-bg-info">Upcoming</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="text-end text-nowrap">
                    <a class="btn btn-outline-primary btn-sm" href="<c:url value='/admin/candidates?electionId=${e.id}'/>">Candidates</a>
                    <a class="btn btn-outline-primary btn-sm" href="<c:url value='/admin/elections/${e.id}/results'/>">Results</a>
                    <c:if test="${e.effectiveStatus != 'CLOSED'}">
                        <a class="btn btn-outline-secondary btn-sm" href="<c:url value='/admin/elections/${e.id}/edit'/>">Edit</a>
                    </c:if>
                    <c:if test="${e.effectiveStatus == 'ACTIVE'}">
                        <form class="d-inline" method="post" action="<c:url value='/admin/elections/${e.id}/close'/>"
                              onsubmit="return confirm('End this election now? Voting will stop immediately.');">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button class="btn btn-warning btn-sm" type="submit">End now</button>
                        </form>
                    </c:if>
                    <form class="d-inline" method="post" action="<c:url value='/admin/elections/${e.id}/delete'/>"
                          onsubmit="return confirm('Delete this election and its candidates? This is only possible when no votes exist.');">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button class="btn btn-outline-danger btn-sm" type="submit">Delete</button>
                    </form>
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
