var gainsl = gainsl || {};

gainsl.reports = {
    init : function () {
        $.ajax({
            url:"http://192.168.1.86:8888/reportList?orgName=Android"
        }).done(function (data) {
            gainsl.reports.data = data;
        });
    }
};