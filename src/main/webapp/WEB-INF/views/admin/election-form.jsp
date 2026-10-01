<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="card"><div class="card-body p-4">
            <h1 class="h4 page-title mb-3"><c:out value="${heading}"/></h1>
            <form method="post" action="<c:url value='${formAction}'/>" novalidate>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="mb-3">
                    <label for="title" class="form-label">Title</label>
                    <input type="text" id="title" name="title" maxlength="200"
                           class="form-control ${not empty errors['title'] ? 'is-invalid' : ''}"
                           value="<c:out value='${form.title}'/>" required>
                    <div class="invalid-feedback"><c:out value="${errors['title']}"/></div>
                </div>

                <div class="mb-3">
                    <label for="description" class="form-label">Description</label>
                    <textarea id="description" name="description" rows="3" maxlength="1000"
                              class="form-control ${not empty errors['description'] ? 'is-invalid' : ''}"><c:out value="${form.description}"/></textarea>
                    <div class="invalid-feedback"><c:out value="${errors['description']}"/></div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="startTime" class="form-label">Voting opens</label>
                        <input type="datetime-local" id="startTime" name="startTime"
                               class="form-control ${not empty errors['startTime'] ? 'is-invalid' : ''}"
                               value="<c:out value='${form.startTime}'/>" required>
                        <div class="invalid-feedback"><c:out value="${errors['startTime']}"/></div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="endTime" class="form-label">Voting closes</label>
                        <input type="datetime-local" id="endTime" name="endTime"
                               class="form-control ${not empty errors['endTime'] ? 'is-invalid' : ''}"
                               value="<c:out value='${form.endTime}'/>" required>
                        <div class="invalid-feedback"><c:out value="${errors['endTime']}"/></div>
                    </div>
                </div>
                <div class="form-text mb-3">Status changes automatically: upcoming, then active, then closed.</div>

                <button type="submit" class="btn btn-primary">Save election</button>
                <a class="btn btn-link" href="<c:url value='/admin/elections'/>">Cancel</a>
            </form>
        </div></div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
