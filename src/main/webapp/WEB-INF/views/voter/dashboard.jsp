<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <h1 class="h3 page-title mb-1">Welcome, <c:out value="${voter.fullName}"/></h1>
    <p class="text-secondary mb-0">Your voter dashboard.</p>
</div>

<%-- In-app notifications: request decisions the voter has not seen yet --%>
<c:forEach var="n" items="${notifications}">
    <div class="alert ${n.status == 'APPROVED' ? 'alert-success' : 'alert-danger'} d-flex justify-content-between align-items-start" role="alert">
        <span>
            <i class="bi bi-bell"></i>
            Your request #<c:out value="${n.id}"/> (<c:out value="${n.type.label}"/>) was
            <strong><c:out value="${n.status.label}"/></strong>.
            <c:if test="${not empty n.adminRemark}">Admin remark: <em><c:out value="${n.adminRemark}"/></em></c:if>
        </span>
        <a class="btn btn-sm btn-outline-secondary ms-3 text-nowrap" href="<c:url value='/voter/requests'/>">My requests</a>
    </div>
</c:forEach>

<c:choose>
    <c:when test="${voter.status == 'PENDING'}">
        <div class="alert alert-warning">Your registration is waiting for admin approval. You can vote once it is approved.</div>
    </c:when>
    <c:when test="${voter.status == 'REJECTED'}">
        <div class="alert alert-danger">Your registration was rejected, so you cannot vote. You can submit a request to the administrator.</div>
    </c:when>
</c:choose>

<%-- Quick actions --%>
<div class="card mb-4"><div class="card-body d-flex flex-wrap gap-2 align-items-center">
    <span class="fw-semibold me-2">Quick actions</span>
    <a class="btn btn-outline-primary btn-sm" href="<c:url value='/voter/elections'/>"><i class="bi bi-calendar-event"></i> View Elections</a>
    <c:choose>
        <c:when test="${not empty quickVoteElectionId}">
            <a class="btn btn-primary btn-sm" href="<c:url value='/voter/elections/${quickVoteElectionId}/vote'/>"><i class="bi bi-check2-square"></i> Vote Now</a>
        </c:when>
        <c:otherwise>
            <a class="btn btn-primary btn-sm disabled" aria-disabled="true"><i class="bi bi-check2-square"></i> Vote Now</a>
        </c:otherwise>
    </c:choose>
    <a class="btn btn-outline-primary btn-sm" href="<c:url value='/voter/requests/new'/>"><i class="bi bi-plus-circle"></i> Submit Request</a>
    <a class="btn btn-outline-primary btn-sm" href="<c:url value='/voter/requests'/>"><i class="bi bi-inbox"></i> My Requests</a>
    <a class="btn btn-outline-primary btn-sm" href="<c:url value='/voter/profile'/>"><i class="bi bi-person-circle"></i> My Profile</a>
</div></div>

<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="card h-100"><div class="card-body">
            <h2 class="h6 text-secondary text-uppercase mb-3">Voter profile</h2>
            <div class="fw-semibold"><c:out value="${voter.fullName}"/></div>
            <div class="small text-secondary mb-2"><c:out value="${voter.email}"/></div>
            <div class="small">Voter ID: <strong><c:out value="${voter.voterId}"/></strong></div>
            <div class="mt-2">
                <c:choose>
                    <c:when test="${voter.status == 'APPROVED'}"><span class="badge text-bg-success">Approved voter</span></c:when>
                    <c:when test="${voter.status == 'REJECTED'}"><span class="badge text-bg-danger">Rejected</span></c:when>
                    <c:otherwise><span class="badge text-bg-warning">Approval pending</span></c:otherwise>
                </c:choose>
            </div>
        </div></div>
    </div>
    <div class="col-md-4">
        <div class="card stat-card h-100"><div class="card-body">
            <div class="stat-icon green"><i class="bi bi-check2-circle"></i></div>
            <div><div class="stat-number"><c:out value="${electionsVoted}"/></div><div class="stat-label">Elections you have voted in</div></div>
        </div></div>
    </div>
    <div class="col-md-4">
        <div class="card stat-card h-100"><div class="card-body">
            <div class="stat-icon amber"><i class="bi bi-hourglass-split"></i></div>
            <div><div class="stat-number"><c:out value="${pendingRequestCount}"/></div><div class="stat-label">Requests waiting for admin</div></div>
        </div></div>
    </div>
</div>

<h2 class="h5 mb-3">Active elections</h2>
<c:choose>
    <c:when test="${empty activeElections}"><p class="text-secondary">No election is open right now.</p></c:when>
    <c:otherwise>
        <div class="row g-3 mb-4">
        <c:forEach var="item" items="${activeElections}">
            <div class="col-md-6">
                <div class="card h-100"><div class="card-body d-flex flex-column">
                    <h3 class="h6"><c:out value="${item.election.title}"/></h3>
                    <p class="text-secondary small mb-2"><c:out value="${item.election.description}"/></p>
                    <p class="small mb-3">Ends <c:out value="${item.election.endTimeDisplay}"/></p>
                    <div class="mt-auto">
                        <c:choose>
                            <c:when test="${item.canVote}">
                                <a class="btn btn-primary btn-sm" href="<c:url value='/voter/elections/${item.election.id}/vote'/>">Vote Now</a>
                            </c:when>
                            <c:when test="${item.eligibility == 'ALREADY_VOTED'}"><span class="badge text-bg-success"><i class="bi bi-check2"></i> <c:out value="${item.message}"/></span></c:when>
                            <c:otherwise><span class="badge text-bg-secondary"><c:out value="${item.message}"/></span></c:otherwise>
                        </c:choose>
                    </div>
                </div></div>
            </div>
        </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<c:if test="${not empty upcomingElections}">
    <h2 class="h5 mb-3">Upcoming elections</h2>
    <ul class="list-group mb-4">
        <c:forEach var="item" items="${upcomingElections}">
            <li class="list-group-item d-flex justify-content-between flex-wrap">
                <strong><c:out value="${item.election.title}"/></strong>
                <span class="text-secondary">Opens <c:out value="${item.election.startTimeDisplay}"/></span>
            </li>
        </c:forEach>
    </ul>
</c:if>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="h5 mb-0">My recent requests</h2>
    <a class="btn btn-outline-primary btn-sm" href="<c:url value='/voter/requests'/>">All requests</a>
</div>
<div class="card"><div class="card-body">
<c:choose>
    <c:when test="${empty recentRequests}"><p class="text-secondary mb-0">You have not submitted any request yet.</p></c:when>
    <c:otherwise>
        <div class="table-responsive">
        <table class="table align-middle mb-0">
            <thead><tr><th>ID</th><th>Type</th><th>Date</th><th>Status</th></tr></thead>
            <tbody>
            <c:forEach var="r" items="${recentRequests}">
                <tr>
                    <td>#<c:out value="${r.id}"/></td>
                    <td><c:out value="${r.type.label}"/></td>
                    <td class="text-nowrap"><c:out value="${r.createdAtDisplay}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${r.status == 'APPROVED'}"><span class="badge text-bg-success">Approved</span></c:when>
                            <c:when test="${r.status == 'REJECTED'}"><span class="badge text-bg-danger">Rejected</span></c:when>
                            <c:otherwise><span class="badge text-bg-warning">Pending</span></c:otherwise>
                        </c:choose>
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
