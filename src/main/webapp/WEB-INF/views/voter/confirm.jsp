<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row">
    <div class="col-lg-7">
        <div class="card"><div class="card-body p-4">
            <h1 class="h4 page-title mb-3">Confirm your vote</h1>
            <p class="mb-1 text-secondary">Election</p>
            <p class="fw-semibold"><c:out value="${election.title}"/></p>

            <p class="mb-1 text-secondary">Your choice</p>
            <div class="border rounded p-3 mb-4 bg-light">
                <div class="fw-semibold fs-5"><c:out value="${candidate.name}"/></div>
                <div class="text-secondary"><c:out value="${candidate.party}"/></div>
            </div>

            <div class="alert alert-warning">Are you sure you want to vote for this candidate? You cannot vote again in this election.</div>

            <%-- confirm() asks once more; the button is disabled after the first click so a double click cannot send two votes --%>
            <form method="post" action="<c:url value='/voter/elections/${election.id}/cast'/>"
                  onsubmit="if (!confirm('Are you sure you want to vote for this candidate? You cannot vote again in this election.')) { return false; } this.querySelector('button[type=submit]').disabled = true;">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <input type="hidden" name="candidateId" value="${candidate.id}"/>
                <button type="submit" class="btn btn-primary">Submit vote</button>
                <a class="btn btn-link" href="<c:url value='/voter/elections/${election.id}/vote'/>">Change my choice</a>
            </form>
        </div></div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
