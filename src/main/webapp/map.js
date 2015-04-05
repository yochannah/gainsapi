var gainsl = gainsl || {};

gainsl.map = {
    markers : [],
    init : function (aMap) {
        var self = this;
        this.theMap = L.map(aMap).setView([53.390829, -1.841100], 12);
        L.tileLayer('http://{s}.tiles.mapbox.com/v4/{mapId}/{z}/{x}/{y}.png?access_token={token}', 
        {
            attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
            maxZoom: 18,
        subdomains: ['a','b','c','d'],        
            mapId: 'yochannah.ll9ibof6',
            token: 'pk.eyJ1IjoieW9jaGFubmFoIiwiYSI6Iko5TU1xcW8ifQ.AlR1faR7rfR1CoJRyIPEAg'
        }).addTo(self.theMap);
    },
    addReports : function(reportsArray) {
        var self = this, marker, report;
        for (var i=0; i < reportsArray.length; i++) {
            report = reportsArray[i].propertyMap;
                //ensure only valid reports are shown. 
                if(report.hasOwnProperty('latitude')) {
                    marker = L.marker([report.longitude, report.latitude]).addTo(self.theMap);
                    marker.bindPopup(report.content);
                    self.markers.push(marker);
                }
        }
    }
};