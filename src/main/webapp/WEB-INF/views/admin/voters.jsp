<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <h1 class="h3 page-title mb-1">Manage voters</h1>
    <p class="text-secondary mb-0">Search voters and approve or reject their registration.</p>
</div>

<form method="get" action="<c:url value='/admin/voters'/>" class="row g-2 mb-3">
    <div class="col-md-5">
        <label for="q" class="visually-hidden">Search</label>
        <input type="text" id="q" name="q" class="form-control" placeholder="Search by name, email or voter ID"
               value="<c:out value='${q}'/>">
    </div>
    <div class="col-md-3">
        <label for="status" class="visually-hidden">Status</label>
        <select id="status" name="status" class="form-select">
            <option value="">All statuses</option>
            <option value="PENDING" ${selectedStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
            <option value="APPROVED" ${selectedStatus == 'APPROVED' ? 'selected' : ''}>Approved</option>
            <option value="REJECTED" ${selectedStatus == 'REJECTED' ? 'selected' : ''}>Rejected</option>
        </select>
    </div>
    <div class="col-auto">
        <button type="submit" class="btn btn-primary"><i class="bi bi-search"></i> Search</button>
        <a class="btn btn-outline-secondary" href="<c:url value='/admin/voters'/>">Reset</a>
    </div>
</form>

<div class="card"><div class="card-body">
<c:choose>
    <c:when test="${empty voters}"><p class="text-secondary mb-0">No voters found.</p></c:when>
    <c:otherwise>
    <div class="table-responsive">
    <table class="table align-middle">
        <thead><tr><th>Name</th><th>Email</th><th>Voter ID</th><th>Date of birth</th><th>Status</th><th class="text-end">Actions</th></tr></thead>
        <tbody>
        <c:forEach var="v" items="${voters}">
            <tr>
                <td><c:out value="${v.fullName}"/></td>
                <td><c:out value="${v.email}"/></td>
                <td><c:out value="${v.voterId}"/></td>
                <td><c:out value="${v.dateOfBirth}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${v.status == 'APPROVED'}"><span class="badge text-bg-success">Approved</span></c:when>
                        <c:when test="${v.status == 'REJECTED'}"><span class="badge text-bg-danger">Rejected</span></c:when>
                        <c:otherwise><span class="badge text-bg-warning">Pending</span></c:otherwise>
                    </c:choose>
                    <c:if test="${v.accountLocked}"><span class="badge text-bg-secondary">Locked</span></c:if>
                </td>
                <td class="text-end text-nowrap">
                    <c:if test="${v.status != 'APPROVED'}">
                        <form class="d-inline" method="post" action="<c:url value='/admin/voters/${v.id}/approve'/>">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button class="btn btn-success btn-sm" type="submit">Approve</button>
                        </form>
                    </c:if>
                    <c:if test="${v.status != 'REJECTED'}">
                        <form class="d-inline" method="post" action="<c:url value='/admin/voters/${v.id}/reject'/>">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button class="btn btn-outline-danger btn-sm" type="submit">Reject</button>
                        </form>
                    </c:if>
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
