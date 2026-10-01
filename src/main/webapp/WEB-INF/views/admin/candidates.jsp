<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 page-header">
    <div>
        <h1 class="h3 page-title mb-1">Manage candidates</h1>
        <p class="text-secondary mb-0">Add candidates and assign them to an election.</p>
    </div>
    <a class="btn btn-primary"
       href="<c:url value='/admin/candidates/new'><c:param name='electionId' value='${selectedElectionId}'/></c:url>"><i class="bi bi-plus-lg"></i> Add candidate</a>
</div>

<form method="get" action="<c:url value='/admin/candidates'/>" class="row g-2 mb-3">
    <div class="col-md-4">
        <label for="q" class="visually-hidden">Search</label>
        <input type="text" id="q" name="q" class="form-control" placeholder="Search by name or party"
               value="<c:out value='${q}'/>">
    </div>
    <div class="col-md-4">
        <label for="electionId" class="visually-hidden">Election</label>
        <select id="electionId" name="electionId" class="form-select">
            <option value="">All elections</option>
            <c:forEach var="e" items="${elections}">
                <option value="${e.id}" ${e.id == selectedElectionId ? 'selected' : ''}><c:out value="${e.title}"/></option>
            </c:forEach>
        </select>
    </div>
    <div class="col-auto">
        <button type="submit" class="btn btn-primary"><i class="bi bi-search"></i> Search</button>
        <a class="btn btn-outline-secondary" href="<c:url value='/admin/candidates'/>">Reset</a>
    </div>
</form>

<div class="card"><div class="card-body">
<c:choose>
    <c:when test="${empty candidates}"><p class="text-secondary mb-0">No candidates found.</p></c:when>
    <c:otherwise>
    <div class="table-responsive">
    <table class="table align-middle">
        <thead><tr><th>Name</th><th>Party / Organization</th><th>Election</th><th>Description</th><th class="text-end">Actions</th></tr></thead>
        <tbody>
        <c:forEach var="c" items="${candidates}">
            <tr>
                <td class="fw-semibold"><c:out value="${c.name}"/></td>
                <td><c:out value="${c.party}"/></td>
                <td><c:out value="${c.election.title}"/></td>
                <td class="text-secondary small"><c:out value="${c.description}"/></td>
                <td class="text-end text-nowrap">
                    <a class="btn btn-outline-secondary btn-sm" href="<c:url value='/admin/candidates/${c.id}/edit'/>">Edit</a>
                    <form class="d-inline" method="post" action="<c:url value='/admin/candidates/${c.id}/delete'/>"
                          onsubmit="return confirm('Delete this candidate?');">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button class="btn btn-outline-danger btn-sm" type="submit">Delete</button>
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

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
