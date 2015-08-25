var gainsl = gainsl || {};

gainsl.reports = {
    init : function (reportLocation, url) {
        var self = this;
        self.reportLocation = reportLocation;
        $.ajax({
            url: url || gainsl.reports.buildUrl()
        }).done(function (data) {
            self.data = data;
            gainsl.map.clear();
            gainsl.map.addReports(gainsl.reports.data); 
            self.generateTable(reportLocation);           
        });
        self.listenForFilters();
    },
    generateTable : function (tableLocation) {
        var self = this, data = self.data, report, template, html = "";
        template = document.getElementById('reportTemplate').innerHTML;
        for(var i = 0; i < data.length; i++) {
            report = data[i].propertyMap;
            html += Mustache.render(template, report);
        }
        document.getElementById(tableLocation).innerHTML = html;
    },
    listenForFilters : function() {
    	self = this;
    	$('#filters li').click(function(e){
    		$target = $(e.target);
    		
    		//get filter vals
    		var filterVal = $target.text();
    		var filterType = $target.parent().attr('id');
    		
    		//remove old selected
    		$("#filters li.selected").each(function (k,v) {
    			$(v).removeClass('selected');
    		});
    		
    		//show which one is active:
    		$target.addClass("selected");
    		
    		//re-init the data with the filter active:
    		console.log("Filtering by", filterType, filterVal);
    		self.init("reports", self.buildUrl(self.buildFilter(filterType,filterVal)));
    	});
    },
    buildFilter : function (type, val) {
    	return "?" + type + "=" + val;
    },
    buildUrl : function (params) {
    	var ret = "http://gainsl-offline.appspot.com/reportList";
    	ret = params ? ret + params : ret;
    	return ret;
    }
    
    
};