<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 class="h3 page-title mb-1"><c:out value="${result.election.title}"/></h1>

<c:choose>
    <c:when test="${result.closed}"><p class="text-secondary">Final results. Voting ended <c:out value="${result.election.endTimeDisplay}"/>.</p></c:when>
    <c:otherwise><div class="alert alert-warning">Live results, visible to admins only. The election is still running and the numbers can change.</div></c:otherwise>
</c:choose>

<c:if test="${result.closed}">
    <c:choose>
        <c:when test="${result.tie}">
            <div class="card winner-banner mb-4"><div class="card-body">
                <div class="fw-semibold">Tie</div>
                <div>Joint highest votes: <c:out value="${result.winnerText}"/></div>
            </div></div>
        </c:when>
        <c:when test="${result.winnerDeclared}">
            <div class="card winner-banner mb-4"><div class="card-body">
                <div class="text-secondary small">Winner</div>
                <div class="fs-4 fw-semibold"><c:out value="${result.winnerText}"/></div>
            </div></div>
        </c:when>
        <c:otherwise><div class="alert alert-info">No votes were cast in this election.</div></c:otherwise>
    </c:choose>
</c:if>

<div class="row g-3 mb-4">
    <div class="col-6 col-md-4"><div class="card h-100"><div class="card-body">
        <div class="stat-number"><c:out value="${result.totalVotes}"/></div><div class="stat-label">Votes cast</div>
    </div></div></div>
    <div class="col-6 col-md-4"><div class="card h-100"><div class="card-body">
        <div class="stat-number"><c:out value="${result.eligibleVoters}"/></div><div class="stat-label">Approved voters</div>
    </div></div></div>
    <div class="col-12 col-md-4"><div class="card h-100"><div class="card-body">
        <div class="stat-number"><fmt:formatNumber value="${result.turnoutPercent}" maxFractionDigits="1"/>%</div><div class="stat-label">Turnout</div>
    </div></div></div>
</div>

<div class="card"><div class="card-body">
<c:choose>
    <c:when test="${empty result.candidateResults}"><p class="text-secondary mb-0">This election has no candidates.</p></c:when>
    <c:otherwise>
    <div class="table-responsive">
    <table class="table align-middle">
        <thead><tr><th>Candidate</th><th>Party</th><th class="text-end">Votes</th><th style="min-width:180px">Share</th></tr></thead>
        <tbody>
        <c:forEach var="r" items="${result.candidateResults}">
            <tr>
                <td>
                    <c:out value="${r.candidate.name}"/>
                    <c:if test="${result.closed and r.winner}"><span class="badge text-bg-success ms-1">Winner</span></c:if>
                </td>
                <td><c:out value="${r.candidate.party}"/></td>
                <td class="text-end"><c:out value="${r.votes}"/></td>
                <td>
                    <div class="progress" role="progressbar" aria-valuenow="${r.percentage}" aria-valuemin="0" aria-valuemax="100">
                        <div class="progress-bar" style="width: ${r.percentage}%"></div>
                    </div>
                    <span class="small"><fmt:formatNumber value="${r.percentage}" maxFractionDigits="1"/>%</span>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>
    </c:otherwise>
</c:choose>
</div></div>

<div class="mt-3">
    <c:choose>
        <c:when test="${navAdmin}">
            <a class="btn btn-link px-0" href="<c:url value='/admin/elections'/>">Back to elections</a>
        </c:when>
        <c:otherwise>
            <a class="btn btn-link px-0" href="<c:url value='/voter/elections'/>">Back to elections</a>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
