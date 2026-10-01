<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <a class="small" href="<c:url value='/admin/requests'/>"><i class="bi bi-arrow-left"></i> Back to requests</a>
    <h1 class="h3 page-title mt-1 mb-0">Request #<c:out value="${req.id}"/></h1>
</div>

<div class="row g-3">
    <div class="col-lg-7">
        <div class="card"><div class="card-body p-4">
            <div class="row">
                <div class="col-sm-6">
                    <div class="detail-label">Voter</div>
                    <div class="detail-value"><c:out value="${req.voter.fullName}"/></div>
                </div>
                <div class="col-sm-6">
                    <div class="detail-label">Voter ID / Email</div>
                    <div class="detail-value"><c:out value="${req.voter.voterId}"/> &middot; <c:out value="${req.voter.email}"/></div>
                </div>
                <div class="col-sm-6">
                    <div class="detail-label">Request type</div>
                    <div class="detail-value"><c:out value="${req.type.label}"/></div>
                </div>
                <div class="col-sm-6">
                    <div class="detail-label">Submitted</div>
                    <div class="detail-value"><c:out value="${req.createdAtDisplay}"/></div>
                </div>
            </div>
            <div class="detail-label">Description</div>
            <div class="detail-value" style="white-space: pre-wrap;"><c:out value="${req.description}"/></div>

            <div class="detail-label">Status</div>
            <div class="detail-value">
                <c:choose>
                    <c:when test="${req.status == 'APPROVED'}"><span class="badge text-bg-success">Approved</span></c:when>
                    <c:when test="${req.status == 'REJECTED'}"><span class="badge text-bg-danger">Rejected</span></c:when>
                    <c:otherwise><span class="badge text-bg-warning">Pending</span></c:otherwise>
                </c:choose>
            </div>

            <c:if test="${req.status != 'PENDING'}">
                <div class="detail-label">Admin remark</div>
                <div class="detail-value"><c:out value="${empty req.adminRemark ? '-' : req.adminRemark}"/></div>
                <div class="detail-label">Decided on</div>
                <div class="detail-value mb-0"><c:out value="${req.decidedAtDisplay}"/></div>
            </c:if>
        </div></div>
    </div>

    <c:if test="${req.status == 'PENDING'}">
    <div class="col-lg-5">
        <div class="card"><div class="card-body p-4">
            <h2 class="h5 mb-3">Your decision</h2>
            <form method="post" id="decisionForm">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="mb-3">
                    <label for="remark" class="form-label">Remark (required when rejecting)</label>
                    <textarea id="remark" name="remark" rows="3" maxlength="500" class="form-control"
                              placeholder="Short note for the voter"></textarea>
                </div>
                <button type="submit" class="btn btn-success"
                        formaction="<c:url value='/admin/requests/${req.id}/approve'/>"
                        onclick="return confirm('Approve this request?');">Approve</button>
                <button type="submit" class="btn btn-outline-danger"
                        formaction="<c:url value='/admin/requests/${req.id}/reject'/>"
                        onclick="return confirm('Reject this request?');">Reject</button>
            </form>
        </div></div>
    </div>
    </c:if>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
