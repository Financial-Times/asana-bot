<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
    <body>
        <h4>Asana Reporting Tool home page</h4>
        <div>
        Your are logged in as <sec:authentication property="userAuthentication.details"/>
        </div>
    </body>
</html>