<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <title>GAINSL: Reports</title>
    <link href='http://fonts.googleapis.com/css?family=Raleway' rel='stylesheet' type='text/css'>
    <link type="text/css" rel="stylesheet" href="/css/style.css"/>
    <link rel="shortcut icon" href="http://faviconist.com/icons/2e4ff8c8a427d0559f763ceb86572c0c/favicon.ico" />
</head>

<body>
<header>
    <div id="greeting"> 
    <%
        String orgName = request.getParameter("orgName");
        if (orgName == null) {
            orgName = "default";
        }
        pageContext.setAttribute("orgName", orgName);
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user != null) {
            pageContext.setAttribute("user", user);
    %>
        <p>Hello, ${fn:escapeXml(user.nickname)}! (You can
            <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
        <%
        } else {
        %>
        <p>Hello!
            <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
            to include your name with reports you post.</p>
        <%
            }
        %>
        </p>
    </div>
</header>
<main>
<%
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key reportStoreKey = KeyFactory.createKey("gainsl", orgName);
    // Run an ancestor query to ensure we see the most up-to-date
    // view of the reports belonging to the selected Guestbook.
    Query query = new Query("Report", reportStoreKey).addSort("date", Query.SortDirection.DESCENDING);
    List<Entity> reports = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
    if (reports.isEmpty()) {
%>
<p>'${fn:escapeXml(orgName)}' has no reports.</p>
<%
    } else {
%>
<p>'${fn:escapeXml(orgName)}' reports:</p>
<%
        for (Entity report : reports) {
            pageContext.setAttribute("report_content",
                    report.getProperty("content"));
            String author;
            if (report.getProperty("author_email") == null) {
                author = "An anonymous person";
            } else {
                author = (String)report.getProperty("author_email");
                String author_id = (String)report.getProperty("author_id");
                if (user != null && user.getUserId().equals(author_id)) {
                    author += " (You)";
                }
            }
            pageContext.setAttribute("report_user", author);
%>
<p><b>${fn:escapeXml(report_user)}</b> wrote:</p>
<blockquote>${fn:escapeXml(report_content)}</blockquote>
<%
        }
    }
%>
<div id="postNewReport">
    <form action="/report" method="post">
        <div><textarea name="content" rows="3" cols="60"></textarea></div>
        <div><input type="submit" value="Post report"/></div>
        <input type="hidden" name="orgName" value="${fn:escapeXml(orgName)}"/>
    </form>
</div>

<div id="switchOrg">
    <form action="/gainslapi.jsp" method="get">
        <div><input type="text" name="orgName" value="${fn:escapeXml(orgName)}"/></div>
        <div><input type="submit" value="Switch Org"/></div>
    </form>
</div>
</main>
</body>
</html>