<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-8 col-lg-6">
        <div class="card">
            <div class="card-body p-4">
                <h1 class="h4 page-title mb-1">Register to vote</h1>
                <p class="text-secondary">An admin reviews every registration before you can vote.</p>

                <form method="post" action="<c:url value='/register'/>" novalidate>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                    <div class="mb-3">
                        <label for="fullName" class="form-label">Full name</label>
                        <input type="text" id="fullName" name="fullName"
                               class="form-control ${not empty errors['fullName'] ? 'is-invalid' : ''}"
                               value="<c:out value='${form.fullName}'/>" required>
                        <div class="invalid-feedback"><c:out value="${errors['fullName']}"/></div>
                    </div>

                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" id="email" name="email"
                               class="form-control ${not empty errors['email'] ? 'is-invalid' : ''}"
                               value="<c:out value='${form.email}'/>" required>
                        <div class="invalid-feedback"><c:out value="${errors['email']}"/></div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="voterId" class="form-label">Voter ID</label>
                            <input type="text" id="voterId" name="voterId" maxlength="20"
                                   class="form-control ${not empty errors['voterId'] ? 'is-invalid' : ''}"
                                   value="<c:out value='${form.voterId}'/>" required>
                            <div class="invalid-feedback"><c:out value="${errors['voterId']}"/></div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="dateOfBirth" class="form-label">Date of birth (18+)</label>
                            <input type="date" id="dateOfBirth" name="dateOfBirth" max="${maxDob}"
                                   class="form-control ${not empty errors['dateOfBirth'] ? 'is-invalid' : ''}"
                                   value="<c:out value='${form.dateOfBirth}'/>" required>
                            <div class="invalid-feedback"><c:out value="${errors['dateOfBirth']}"/></div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" id="password" name="password" autocomplete="new-password"
                               class="form-control ${not empty errors['password'] ? 'is-invalid' : ''}" required>
                        <div class="form-text">At least 8 characters with upper-case, lower-case and a digit.</div>
                        <div class="invalid-feedback"><c:out value="${errors['password']}"/></div>
                    </div>

                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Confirm password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" autocomplete="new-password"
                               class="form-control ${not empty errors['confirmPassword'] ? 'is-invalid' : ''}" required>
                        <div class="invalid-feedback"><c:out value="${errors['confirmPassword']}"/></div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100">Register</button>
                </form>
                <p class="mt-3 mb-0 text-center">Already registered? <a href="<c:url value='/login'/>">Sign in</a></p>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
