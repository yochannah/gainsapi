var gainsl = gainsl || {};

gainsl.reports = {
    init : function (reportLocation) {
        var self = this;
        self.reportLocation = reportLocation;
        $.ajax({
            url:"http://192.168.1.86:8888/reportList?orgName=Android"
        }).done(function (data) {
            self.data = data;
            gainsl.map.addReports(gainsl.reports.data); 
            self.generateTable(reportLocation);           
        });
    },
    generateTable : function (tableLocation) {
        var self = this, data = self.data, report, template, html = "";
        template = document.getElementById('reportTemplate').innerHTML;
        for(var i = 0; i < data.length; i++) {
            report = data[i].propertyMap;
            html += Mustache.render(template, report);
            console.log(report);
        }
        document.getElementById(tableLocation).innerHTML = html;
    }
};