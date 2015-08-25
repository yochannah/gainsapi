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

<div id="filters">
	<div class="filter">
		<div>Filter by Status </div>
		<ul id="status">
			<li>New</li>
			<li>In Progress</li>
			<li>Resolved</li>
		</ul>
	</div>
	<div class="filter">
		<div>Filter by reporter</div>
		<ul id="reporter">
			<li>Alice</li>
			<li>Bob</li>
			<li>Eve</li>
		</ul>
	</div>
	<div class="filter">
	<div>Filter by last&nbsp;updater</div>
		<ul id="lastUpdatedBy">
			<li>Alice</li>
			<li>Bob</li>
			<li>Eve</li>
		</ul>
	</div>
</div>

<div id="map"></div>

<table>
    <thead>
        <th>Status</th>
        <th>Date Reported</th>
        <th>Details</th>
        <th>Last Update</th>
        <th></th>        
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
<td class="date">{{dateCreated}} by {{reporter}}</td>
<td>{{content}}</td>
<td>{{lastUpdated}} by {{lastUpdatedBy}}</td>
<td>{{image}}</td>
</tr>
</script>

</html>
