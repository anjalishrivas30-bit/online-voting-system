<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 page-header">
    <div>
        <h1 class="h3 page-title mb-1">My requests</h1>
        <p class="text-secondary mb-0">Track the status of your requests.</p>
    </div>
    <a class="btn btn-primary" href="<c:url value='/voter/requests/new'/>"><i class="bi bi-plus-lg"></i> Submit request</a>
</div>

<div class="card"><div class="card-body">
<c:choose>
    <c:when test="${empty requests}"><p class="text-secondary mb-0">You have not submitted any request yet.</p></c:when>
    <c:otherwise>
    <div class="table-responsive">
    <table class="table align-middle">
        <thead><tr><th>ID</th><th>Type</th><th>Description</th><th>Date</th><th>Status</th><th>Admin remark</th></tr></thead>
        <tbody>
        <c:forEach var="r" items="${requests}">
            <tr>
                <td>#<c:out value="${r.id}"/></td>
                <td><c:out value="${r.type.label}"/></td>
                <td style="max-width: 280px; white-space: pre-wrap;"><c:out value="${r.description}"/></td>
                <td class="text-nowrap"><c:out value="${r.createdAtDisplay}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${r.status == 'APPROVED'}"><span class="badge text-bg-success">Approved</span></c:when>
                        <c:when test="${r.status == 'REJECTED'}"><span class="badge text-bg-danger">Rejected</span></c:when>
                        <c:otherwise><span class="badge text-bg-warning">Pending</span></c:otherwise>
                    </c:choose>
                </td>
                <td><c:out value="${empty r.adminRemark ? '-' : r.adminRemark}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>
    </c:otherwise>
</c:choose>
</div></div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
