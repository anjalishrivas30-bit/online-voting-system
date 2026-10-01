<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- Shared layout. Every page includes this file first and footer.jsp last (static include), so the taglib
     declarations above apply to every page.
     Logged-in users get the sidebar layout (admin menu or voter menu); visitors get a simple top bar.
     navUser / navAdmin / navVoter / navPath / nav*Requests are added to every page by GlobalModelAttributes. --%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty pageTitle ? 'Online Voting System' : pageTitle}"/> | Online Voting</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet">
</head>
<body>
<c:choose>
<c:when test="${not empty navUser}">

<div class="app-shell">
    <aside class="sidebar" id="sidebar">
        <a class="sidebar-brand" href="<c:url value='/'/>">
            <i class="bi bi-check2-square"></i> Online Voting
            <span class="sidebar-role"><c:choose><c:when test="${navAdmin}">Admin panel</c:when><c:otherwise>Voter portal</c:otherwise></c:choose></span>
        </a>
        <nav class="sidebar-nav">
            <c:choose>
                <c:when test="${navAdmin}">
                    <a class="${navPath == '/admin/dashboard' ? 'active' : ''}" href="<c:url value='/admin/dashboard'/>"><i class="bi bi-speedometer2"></i> Dashboard</a>
                    <a class="${fn:startsWith(navPath, '/admin/voters') ? 'active' : ''}" href="<c:url value='/admin/voters'/>"><i class="bi bi-people"></i> Manage Voters</a>
                    <a class="${fn:startsWith(navPath, '/admin/candidates') ? 'active' : ''}" href="<c:url value='/admin/candidates'/>"><i class="bi bi-person-badge"></i> Manage Candidates</a>
                    <a class="${fn:startsWith(navPath, '/admin/elections') ? 'active' : ''}" href="<c:url value='/admin/elections'/>"><i class="bi bi-calendar-event"></i> Manage Elections</a>
                    <a class="${fn:startsWith(navPath, '/admin/requests') ? 'active' : ''}" href="<c:url value='/admin/requests'/>"><i class="bi bi-inbox"></i> Voter Requests
                        <c:if test="${navPendingRequests > 0}"><span class="badge rounded-pill text-bg-danger nav-badge"><c:out value="${navPendingRequests}"/></span></c:if></a>
                    <a class="${fn:startsWith(navPath, '/admin/results') ? 'active' : ''}" href="<c:url value='/admin/results'/>"><i class="bi bi-bar-chart"></i> Results</a>
                    <a class="${fn:startsWith(navPath, '/admin/audit') ? 'active' : ''}" href="<c:url value='/admin/audit'/>"><i class="bi bi-journal-text"></i> Audit Log</a>
                </c:when>
                <c:when test="${navVoter}">
                    <a class="${navPath == '/voter/home' or navPath == '/voter/dashboard' ? 'active' : ''}" href="<c:url value='/voter/home'/>"><i class="bi bi-house"></i> Dashboard</a>
                    <a class="${fn:startsWith(navPath, '/voter/elections') ? 'active' : ''}" href="<c:url value='/voter/elections'/>"><i class="bi bi-calendar-event"></i> Elections</a>
                    <a class="${navPath == '/voter/requests' ? 'active' : ''}" href="<c:url value='/voter/requests'/>"><i class="bi bi-inbox"></i> My Requests
                        <c:if test="${navUnseenRequests > 0}"><span class="badge rounded-pill text-bg-danger nav-badge"><c:out value="${navUnseenRequests}"/></span></c:if></a>
                    <a class="${navPath == '/voter/requests/new' ? 'active' : ''}" href="<c:url value='/voter/requests/new'/>"><i class="bi bi-plus-circle"></i> Submit Request</a>
                    <a class="${navPath == '/voter/profile' ? 'active' : ''}" href="<c:url value='/voter/profile'/>"><i class="bi bi-person-circle"></i> My Profile</a>
                </c:when>
            </c:choose>
            <div class="sidebar-logout">
                <%-- Logout is a POST and carries the CSRF token --%>
                <form action="<c:url value='/logout'/>" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="sidebar-btn"><i class="bi bi-box-arrow-right"></i> Logout</button>
                </form>
            </div>
        </nav>
    </aside>

    <div class="content-area">
        <header class="topbar">
            <button class="btn btn-outline-secondary btn-sm d-lg-none" type="button" aria-label="Open menu"
                    onclick="document.getElementById('sidebar').classList.toggle('open')"><i class="bi bi-list"></i></button>
            <div class="ms-auto d-flex align-items-center gap-2">
                <span class="topbar-user"><i class="bi bi-person-circle"></i> <c:out value="${navUser}"/></span>
                <span class="badge text-bg-primary"><c:choose><c:when test="${navAdmin}">Admin</c:when><c:otherwise>Voter</c:otherwise></c:choose></span>
            </div>
        </header>
        <main class="page-body">

</c:when>
<c:otherwise>

<nav class="navbar navbar-expand navbar-dark navbar-ballot">
    <div class="container">
        <a class="navbar-brand" href="<c:url value='/'/>"><i class="bi bi-check2-square"></i> Online Voting</a>
        <ul class="navbar-nav ms-auto">
            <li class="nav-item"><a class="nav-link" href="<c:url value='/login'/>">Sign in</a></li>
            <li class="nav-item"><a class="nav-link" href="<c:url value='/register'/>">Register</a></li>
        </ul>
    </div>
</nav>
<main class="container my-5">

</c:otherwise>
</c:choose>

    <%-- Flash messages set by controllers (RedirectAttributes) and inline errors --%>
    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle"></i> <c:out value="${success}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle"></i> <c:out value="${error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
