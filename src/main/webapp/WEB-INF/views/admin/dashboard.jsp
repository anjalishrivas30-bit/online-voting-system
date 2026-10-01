<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <h1 class="h3 page-title mb-1">Admin dashboard</h1>
    <p class="text-secondary mb-0">Overview of voters, candidates, elections and requests.</p>
</div>

<c:if test="${pendingRequests > 0}">
    <div class="alert alert-warning d-flex justify-content-between align-items-center" role="alert">
        <span><i class="bi bi-bell"></i> You have <strong><c:out value="${pendingRequests}"/></strong> new pending voter request(s).</span>
        <a class="btn btn-sm btn-warning" href="<c:url value='/admin/requests?status=PENDING'/>">Review now</a>
    </div>
</c:if>

<div class="row g-3 mb-4">
    <div class="col-6 col-lg"><div class="card stat-card h-100"><div class="card-body">
        <div class="stat-icon blue"><i class="bi bi-people"></i></div>
        <div><div class="stat-number"><c:out value="${totalVoters}"/></div><div class="stat-label">Total voters</div></div>
    </div></div></div>
    <div class="col-6 col-lg"><div class="card stat-card h-100"><div class="card-body">
        <div class="stat-icon violet"><i class="bi bi-person-badge"></i></div>
        <div><div class="stat-number"><c:out value="${totalCandidates}"/></div><div class="stat-label">Total candidates</div></div>
    </div></div></div>
    <div class="col-6 col-lg"><div class="card stat-card h-100"><div class="card-body">
        <div class="stat-icon green"><i class="bi bi-calendar-event"></i></div>
        <div><div class="stat-number"><c:out value="${totalElections}"/></div><div class="stat-label">Total elections</div></div>
    </div></div></div>
    <div class="col-6 col-lg"><div class="card stat-card h-100"><div class="card-body">
        <div class="stat-icon amber"><i class="bi bi-broadcast"></i></div>
        <div><div class="stat-number"><c:out value="${activeElections}"/></div><div class="stat-label">Active elections</div></div>
    </div></div></div>
    <div class="col-12 col-lg"><div class="card stat-card h-100"><div class="card-body">
        <div class="stat-icon red"><i class="bi bi-inbox"></i></div>
        <div><div class="stat-number"><c:out value="${pendingRequests}"/></div><div class="stat-label">Pending requests</div></div>
    </div></div></div>
</div>

<div class="row g-3">
    <div class="col-xl-6">
        <div class="card h-100"><div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="h5 mb-0">Latest pending requests</h2>
                <a class="btn btn-outline-primary btn-sm" href="<c:url value='/admin/requests'/>">All requests</a>
            </div>
            <c:choose>
                <c:when test="${empty pendingRequestList}"><p class="text-secondary mb-0">No pending requests.</p></c:when>
                <c:otherwise>
                    <div class="table-responsive">
                    <table class="table align-middle mb-0">
                        <thead><tr><th>Voter</th><th>Type</th><th>Date</th><th></th></tr></thead>
                        <tbody>
                        <c:forEach var="r" items="${pendingRequestList}">
                            <tr>
                                <td><c:out value="${r.voter.fullName}"/></td>
                                <td><c:out value="${r.type.label}"/></td>
                                <td class="text-nowrap"><c:out value="${r.createdAtDisplay}"/></td>
                                <td class="text-end"><a class="btn btn-primary btn-sm" href="<c:url value='/admin/requests/${r.id}'/>">Review</a></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div></div>
    </div>

    <div class="col-xl-6">
        <div class="card h-100"><div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="h5 mb-0">Voter registrations waiting for approval</h2>
                <a class="btn btn-outline-primary btn-sm" href="<c:url value='/admin/voters'/>">All voters</a>
            </div>
            <c:choose>
                <c:when test="${empty pendingVoters}"><p class="text-secondary mb-0">No pending registrations.</p></c:when>
                <c:otherwise>
                    <div class="table-responsive">
                    <table class="table align-middle mb-0">
                        <thead><tr><th>Name</th><th>Voter ID</th><th class="text-end">Decision</th></tr></thead>
                        <tbody>
                        <c:forEach var="v" items="${pendingVoters}">
                            <tr>
                                <td><c:out value="${v.fullName}"/><div class="small text-secondary"><c:out value="${v.email}"/></div></td>
                                <td><c:out value="${v.voterId}"/></td>
                                <td class="text-end text-nowrap">
                                    <form class="d-inline" method="post" action="<c:url value='/admin/voters/${v.id}/approve'/>">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <input type="hidden" name="from" value="dashboard"/>
                                        <button class="btn btn-success btn-sm" type="submit">Approve</button>
                                    </form>
                                    <form class="d-inline" method="post" action="<c:url value='/admin/voters/${v.id}/reject'/>">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <input type="hidden" name="from" value="dashboard"/>
                                        <button class="btn btn-outline-danger btn-sm" type="submit">Reject</button>
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
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
