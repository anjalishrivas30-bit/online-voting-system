<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 class="h3 page-title mb-1"><c:out value="${election.title}"/></h1>
<p class="text-secondary">Choose one candidate. You will confirm before your vote is recorded, and a vote cannot be changed afterwards.</p>

<c:choose>
    <c:when test="${empty candidates}">
        <div class="alert alert-info">No candidates have been added to this election yet.</div>
        <a class="btn btn-link px-0" href="<c:url value='/voter/elections'/>">Back to elections</a>
    </c:when>
    <c:otherwise>
        <form method="post" action="<c:url value='/voter/elections/${election.id}/confirm'/>">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="row g-3 mb-4">
                <c:forEach var="c" items="${candidates}">
                    <div class="col-md-6">
                        <input type="radio" class="btn-check" name="candidateId" id="cand${c.id}" value="${c.id}" required>
                        <label class="ballot-option w-100" for="cand${c.id}">
                            <span class="d-block fw-semibold"><c:out value="${c.name}"/></span>
                            <span class="d-block text-secondary small"><c:out value="${c.party}"/></span>
                            <span class="d-block small mt-2"><c:out value="${c.description}"/></span>
                        </label>
                    </div>
                </c:forEach>
            </div>
            <button type="submit" class="btn btn-primary">Review my choice</button>
            <a class="btn btn-link" href="<c:url value='/voter/elections'/>">Cancel</a>
        </form>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
