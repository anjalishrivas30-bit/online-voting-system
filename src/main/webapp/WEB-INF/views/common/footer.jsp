        </main>
<c:if test="${not empty navUser}">
    </div><%-- end content-area --%>
</div><%-- end app-shell --%>
</c:if>
<c:if test="${empty navUser}">
<footer class="py-3 border-top bg-white">
    <div class="container footer-note">Online Voting System &middot; internship project &middot; Spring Boot, Spring Security, MySQL, JSP</div>
</footer>
</c:if>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
