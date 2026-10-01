<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <h1 class="h3 page-title mb-1">Voter requests</h1>
    <p class="text-secondary mb-0">Review requests from voters and approve or reject them with a short remark.</p>
</div>

<div class="filter-pills mb-3 d-flex flex-wrap gap-2">
    <a class="btn btn-sm ${empty selectedStatus ? 'btn-primary' : 'btn-outline-primary'}" href="<c:url value='/admin/requests'/>">All (<c:out value="${countAll}"/>)</a>
    <a class="btn btn-sm ${selectedStatus == 'PENDING' ? 'btn-primary' : 'btn-outline-primary'}" href="<c:url value='/admin/requests?status=PENDING'/>">Pending (<c:out value="${countPending}"/>)</a>
    <a class="btn btn-sm ${selectedStatus == 'APPROVED' ? 'btn-primary' : 'btn-outline-primary'}" href="<c:url value='/admin/requests?status=APPROVED'/>">Approved (<c:out value="${countApproved}"/>)</a>
    <a class="btn btn-sm ${selectedStatus == 'REJECTED' ? 'btn-primary' : 'btn-outline-primary'}" href="<c:url value='/admin/requests?status=REJECTED'/>">Rejected (<c:out value="${countRejected}"/>)</a>
</div>

<div class="card"><div class="card-body">
<c:choose>
    <c:when test="${empty requests}"><p class="text-secondary mb-0">No requests found.</p></c:when>
    <c:otherwise>
    <div class="table-responsive">
    <table class="table align-middle">
        <thead><tr><th>ID</th><th>Voter</th><th>Request type</th><th>Date</th><th>Status</th><th class="text-end">Action</th></tr></thead>
        <tbody>
        <c:forEach var="r" items="${requests}">
            <tr>
                <td>#<c:out value="${r.id}"/></td>
                <td><c:out value="${r.voter.fullName}"/><div class="small text-secondary"><c:out value="${r.voter.voterId}"/></div></td>
                <td><c:out value="${r.type.label}"/></td>
                <td class="text-nowrap"><c:out value="${r.createdAtDisplay}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${r.status == 'APPROVED'}"><span class="badge text-bg-success">Approved</span></c:when>
                        <c:when test="${r.status == 'REJECTED'}"><span class="badge text-bg-danger">Rejected</span></c:when>
                        <c:otherwise><span class="badge text-bg-warning">Pending</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="text-end">
                    <a class="btn btn-sm ${r.status == 'PENDING' ? 'btn-primary' : 'btn-outline-secondary'}" href="<c:url value='/admin/requests/${r.id}'/>">
                        <c:choose><c:when test="${r.status == 'PENDING'}">Review</c:when><c:otherwise>View</c:otherwise></c:choose>
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
