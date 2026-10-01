<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <h1 class="h3 page-title mb-1">Elections</h1>
    <p class="text-secondary mb-0">The system checks your eligibility for every election.</p>
</div>

<div class="card"><div class="card-body">
<c:choose>
    <c:when test="${empty elections}"><p class="text-secondary mb-0">There are no elections yet.</p></c:when>
    <c:otherwise>
    <div class="table-responsive">
    <table class="table align-middle">
        <thead><tr><th>Election</th><th>Voting period</th><th>Status</th><th>Your eligibility</th><th class="text-end">Action</th></tr></thead>
        <tbody>
        <c:forEach var="item" items="${elections}">
            <tr>
                <td class="fw-semibold"><c:out value="${item.election.title}"/></td>
                <td class="small text-nowrap"><c:out value="${item.election.startTimeDisplay}"/><br>to <c:out value="${item.election.endTimeDisplay}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${item.election.effectiveStatus == 'ACTIVE'}"><span class="badge text-bg-success">Active</span></c:when>
                        <c:when test="${item.election.effectiveStatus == 'CLOSED'}"><span class="badge text-bg-secondary">Completed</span></c:when>
                        <c:otherwise><span class="badge text-bg-info">Upcoming</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:out value="${item.message}"/>
                    <c:if test="${item.voted and item.eligibility == 'ENDED'}"><div class="small text-success"><i class="bi bi-check2"></i> You voted in this election</div></c:if>
                </td>
                <td class="text-end text-nowrap">
                    <c:choose>
                        <c:when test="${item.canVote}">
                            <a class="btn btn-primary btn-sm" href="<c:url value='/voter/elections/${item.election.id}/vote'/>">Vote Now</a>
                        </c:when>
                        <c:when test="${item.election.effectiveStatus == 'CLOSED'}">
                            <a class="btn btn-outline-primary btn-sm" href="<c:url value='/voter/elections/${item.election.id}/results'/>">View results</a>
                        </c:when>
                        <c:otherwise><span class="text-secondary small">-</span></c:otherwise>
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
