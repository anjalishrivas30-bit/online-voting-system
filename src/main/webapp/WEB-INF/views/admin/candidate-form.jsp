<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="card"><div class="card-body p-4">
            <h1 class="h4 page-title mb-3"><c:out value="${heading}"/></h1>
            <form method="post" action="<c:url value='${formAction}'/>" novalidate>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="mb-3">
                    <label for="electionId" class="form-label">Election</label>
                    <c:choose>
                        <c:when test="${editing}">
                            <%-- The election of an existing candidate is fixed --%>
                            <input type="hidden" name="electionId" value="${form.electionId}"/>
                            <select id="electionId" class="form-select" disabled>
                                <c:forEach var="e" items="${elections}">
                                    <option ${e.id == form.electionId ? 'selected' : ''}><c:out value="${e.title}"/></option>
                                </c:forEach>
                            </select>
                        </c:when>
                        <c:otherwise>
                            <select id="electionId" name="electionId"
                                    class="form-select ${not empty errors['electionId'] ? 'is-invalid' : ''}" required>
                                <option value="">Select an election</option>
                                <c:forEach var="e" items="${elections}">
                                    <option value="${e.id}" ${e.id == form.electionId ? 'selected' : ''}><c:out value="${e.title}"/></option>
                                </c:forEach>
                            </select>
                            <div class="invalid-feedback"><c:out value="${errors['electionId']}"/></div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="name" class="form-label">Candidate name</label>
                        <input type="text" id="name" name="name" maxlength="100"
                               class="form-control ${not empty errors['name'] ? 'is-invalid' : ''}"
                               value="<c:out value='${form.name}'/>" required>
                        <div class="invalid-feedback"><c:out value="${errors['name']}"/></div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="party" class="form-label">Party / Organization</label>
                        <input type="text" id="party" name="party" maxlength="100"
                               class="form-control ${not empty errors['party'] ? 'is-invalid' : ''}"
                               value="<c:out value='${form.party}'/>" required>
                        <div class="invalid-feedback"><c:out value="${errors['party']}"/></div>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="position" class="form-label">Position contested (optional)</label>
                    <input type="text" id="position" name="position" maxlength="100"
                           class="form-control ${not empty errors['position'] ? 'is-invalid' : ''}"
                           value="<c:out value='${form.position}'/>">
                    <div class="invalid-feedback"><c:out value="${errors['position']}"/></div>
                </div>

                <div class="mb-3">
                    <label for="description" class="form-label">Description</label>
                    <textarea id="description" name="description" rows="3" maxlength="1000"
                              class="form-control ${not empty errors['description'] ? 'is-invalid' : ''}"><c:out value="${form.description}"/></textarea>
                    <div class="invalid-feedback"><c:out value="${errors['description']}"/></div>
                </div>

                <button type="submit" class="btn btn-primary">Save candidate</button>
                <a class="btn btn-link" href="<c:url value='/admin/candidates'/>">Cancel</a>
            </form>
        </div></div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
