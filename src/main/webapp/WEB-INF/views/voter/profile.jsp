<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <h1 class="h3 page-title mb-1">My profile</h1>
    <p class="text-secondary mb-0">Your account information.</p>
</div>

<div class="row">
    <div class="col-lg-7">
        <div class="card"><div class="card-body p-4">
            <div class="row">
                <div class="col-sm-6"><div class="detail-label">Name</div><div class="detail-value"><c:out value="${voter.fullName}"/></div></div>
                <div class="col-sm-6"><div class="detail-label">Email</div><div class="detail-value"><c:out value="${voter.email}"/></div></div>
                <div class="col-sm-6"><div class="detail-label">Voter ID</div><div class="detail-value"><c:out value="${voter.voterId}"/></div></div>
                <div class="col-sm-6"><div class="detail-label">Date of birth</div><div class="detail-value"><c:out value="${voter.dateOfBirth}"/></div></div>
                <div class="col-sm-6"><div class="detail-label">Account type</div><div class="detail-value">Voter</div></div>
                <div class="col-sm-6"><div class="detail-label">Registered on</div><div class="detail-value"><c:out value="${voter.createdAtDisplay}"/></div></div>
                <div class="col-sm-6">
                    <div class="detail-label">Registration status</div>
                    <div class="detail-value">
                        <c:choose>
                            <c:when test="${voter.status == 'APPROVED'}"><span class="badge text-bg-success">Approved</span></c:when>
                            <c:when test="${voter.status == 'REJECTED'}"><span class="badge text-bg-danger">Rejected</span></c:when>
                            <c:otherwise><span class="badge text-bg-warning">Pending</span></c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            <hr>
            <p class="mb-2 text-secondary">Something wrong in your profile? You cannot edit it directly. Send a correction request to the admin.</p>
            <a class="btn btn-primary btn-sm" href="<c:url value='/voter/requests/new'/>"><i class="bi bi-plus-circle"></i> Submit a request</a>
        </div></div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
