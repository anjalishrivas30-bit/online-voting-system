```jsp
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row justify-content-center">

    <div class="col-md-6 col-lg-5">

        <div class="card shadow-sm">

            <div class="card-body p-4">

                <h1 class="h4 mb-3">Login</h1>

                <!-- Invalid email/password -->
                <c:if test="${param.error != null}">
                    <div class="alert alert-danger" role="alert">
                        Invalid email or password.
                    </div>
                </c:if>

                <!-- Role mismatch -->
                <c:if test="${param.roleError != null}">
                    <div class="alert alert-danger" role="alert">
                        Selected login role does not match your account.
                        Please select the correct role.
                    </div>
                </c:if>

                <!-- Logout message -->
                <c:if test="${param.logout != null}">
                    <div class="alert alert-success" role="alert">
                        You have been logged out successfully.
                    </div>
                </c:if>

                <!-- Registration success message -->
                <c:if test="${success != null}">
                    <div class="alert alert-success" role="alert">
                        <c:out value="${success}"/>
                    </div>
                </c:if>


                <form method="post"
                      action="${pageContext.request.contextPath}/login">

                    <!-- CSRF Token -->
                    <input type="hidden"
                           name="${_csrf.parameterName}"
                           value="${_csrf.token}"/>


                    <!-- Login Role -->
                    <div class="mb-3">

                        <label class="form-label">
                            Login As
                        </label>

                        <div>

                            <!-- Voter -->
                            <div class="form-check form-check-inline">

                                <input class="form-check-input"
                                       type="radio"
                                       name="loginRole"
                                       id="voterRole"
                                       value="VOTER"
                                       checked>

                                <label class="form-check-label"
                                       for="voterRole">
                                    Voter
                                </label>

                            </div>


                            <!-- Admin -->
                            <div class="form-check form-check-inline">

                                <input class="form-check-input"
                                       type="radio"
                                       name="loginRole"
                                       id="adminRole"
                                       value="ADMIN">

                                <label class="form-check-label"
                                       for="adminRole">
                                    Admin
                                </label>

                            </div>

                        </div>

                    </div>


                    <!-- Email -->
                    <div class="mb-3">

                        <label for="email"
                               class="form-label">
                            Email
                        </label>

                        <input type="email"
                               class="form-control"
                               id="email"
                               name="email"
                               required
                               autofocus
                               autocomplete="username">

                    </div>


                    <!-- Password -->
                    <div class="mb-3">

                        <label for="password"
                               class="form-label">
                            Password
                        </label>

                        <input type="password"
                               class="form-control"
                               id="password"
                               name="password"
                               required
                               autocomplete="current-password">

                    </div>


                    <!-- Login Button -->
                    <button type="submit"
                            class="btn btn-primary w-100">
                        Login
                    </button>

                </form>


                <!-- Registration -->
                <p class="mt-3 mb-0 text-center">

                    New voter?

                    <a href="<c:url value='/register'/>">
                        Register here
                    </a>

                </p>

            </div>

        </div>

    </div>

</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
```
