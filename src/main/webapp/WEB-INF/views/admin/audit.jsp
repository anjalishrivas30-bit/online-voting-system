<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 class="h3 page-title mb-1">Audit log</h1>
<p class="text-secondary">Latest 100 events. Votes are logged as "vote cast" only; the chosen candidate is never recorded here.</p>

<div class="card"><div class="card-body">
<c:choose>
    <c:when test="${empty logs}"><p class="text-secondary mb-0">Nothing logged yet.</p></c:when>
    <c:otherwise>
    <div class="table-responsive">
    <table class="table table-sm align-middle">
        <thead><tr><th>Time</th><th>User</th><th>Action</th><th>Details</th></tr></thead>
        <tbody>
        <c:forEach var="l" items="${logs}">
            <tr>
                <td class="text-nowrap"><c:out value="${l.createdAtDisplay}"/></td>
                <td><c:out value="${l.actor}"/></td>
                <td><span class="badge text-bg-light border"><c:out value="${l.action}"/></span></td>
                <td><c:out value="${l.details}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>
    </c:otherwise>
</c:choose>
</div></div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
