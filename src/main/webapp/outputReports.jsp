<html>
<head>
    <title>GAINSL: Reports</title>
    <link href='http://fonts.googleapis.com/css?family=Raleway' rel='stylesheet' type='text/css'>
    <link type="text/css" rel="stylesheet" href="/css/style.css"/>
    <link rel="shortcut icon" href="http://faviconist.com/icons/2e4ff8c8a427d0559f763ceb86572c0c/favicon.ico" />
    <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.css" />
    <script src="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js"></script>
    <script src="mustache.min.js"></script>
    <script src="reports.js"></script>
    <script src="map.js"></script>

</head>

<body>
<html>

<body>

<div id="map"></div>

<table>
    <thead>
        <th>Status</th>
        <th>Image</th>        
        <th>Details</th>
        <th>Author</th>
        <th>Date</th>
    </thead>
    <tbody id="reports">
        <tr><td colspan="4">
            <div class="spinner">
              <div class="dot1"></div>
              <div class="dot2"></div>
            </div>
        </td></tr>
    </tbody>
</table>


<script src="http://code.jquery.com/jquery-1.11.2.min.js"></script>
<script>

$('document').ready(function () { 
    gainsl.map.init('map');
    gainsl.reports.init('reports');
});    
</script>
</body>

<script type="text/template" id="reportTemplate">
<tr>
<td class="status status-{{status}}">{{status}}</td>
<td>{{image}}</td>
<td>{{content}}</td>
<td>{{author}}</td>
<td class="date">{{date}}</td>
</tr>
</script>

</html>
