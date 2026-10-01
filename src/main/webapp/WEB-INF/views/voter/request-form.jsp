<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row">
    <div class="col-lg-7">
        <div class="card"><div class="card-body p-4">
            <h1 class="h4 page-title mb-1">Submit a request</h1>
            <p class="text-secondary">An admin will review your request and add a remark.</p>

            <form method="post" action="<c:url value='/voter/requests'/>" novalidate>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="mb-3">
                    <label for="type" class="form-label">Request type</label>
                    <select id="type" name="type" class="form-select ${not empty errors['type'] ? 'is-invalid' : ''}" required>
                        <option value="">Select a type</option>
                        <c:forEach var="t" items="${requestTypes}">
                            <option value="${t}" ${form.type == t ? 'selected' : ''}><c:out value="${t.label}"/></option>
                        </c:forEach>
                    </select>
                    <div class="invalid-feedback"><c:out value="${errors['type']}"/></div>
                </div>

                <div class="mb-3">
                    <label for="description" class="form-label">Description</label>
                    <textarea id="description" name="description" rows="5" maxlength="1000"
                              class="form-control ${not empty errors['description'] ? 'is-invalid' : ''}"
                              placeholder="Explain what you need, for example which detail is wrong and what it should be."><c:out value="${form.description}"/></textarea>
                    <div class="invalid-feedback"><c:out value="${errors['description']}"/></div>
                </div>

                <button type="submit" class="btn btn-primary">Submit request</button>
                <a class="btn btn-link" href="<c:url value='/voter/requests'/>">Cancel</a>
            </form>
        </div></div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
