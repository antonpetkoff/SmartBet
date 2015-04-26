var host = "http://localhost:3000/";
var selectSides = [
    {
        id:0,
        name: $("#team1-name"),
        nextBtn: $("#t1-next"),
        backBtn: $("#t1-back"),
        image: $("#team1-logo")
    },
    {
        id:1,
        name: $("#team2-name"),
        nextBtn: $("#t2-next"),
        backBtn: $("#t2-back"),
        image: $("#team2-logo")
    }
]
var ChooseTeam = {
    teams : [],
    fireBtn: $(".fire"),
    init : function() {
        ChooseTeam.getTeams();
        ChooseTeam.setup();
    },
    setup: function(){
        selectSides[0].nextBtn.click(function(){ChooseTeam.changeTeam(0,1)});
        selectSides[0].backBtn.click(function(){ChooseTeam.changeTeam(0,-1)});
        selectSides[1].nextBtn.click(function(){ChooseTeam.changeTeam(1,1)});
        selectSides[1].backBtn.click(function(){ChooseTeam.changeTeam(1,-1)});
        ChooseTeam.fireBtn.click(function(){
            Results.init(selectSides[0].id, selectSides[1].id)
        });
    },
    changeTeam : function(selected, step){
        if(selectSides[selected].id + step <= ChooseTeam.teams.length 
            && selectSides[selected].id + step >=0){
            selectSides[selected].id += step;
        }
        ChooseTeam.render(selected);
    },
    render : function(selected){
        selectSides[selected].image.removeClass("bounceIn");
        selectSides[selected].image.attr("src", ChooseTeam.teams[selectSides[selected].id].image);
        setTimeout(function(){
          selectSides[selected].image.addClass("bounceIn");  
        },5);
        selectSides[selected].name.text(ChooseTeam.teams[selectSides[selected].id].name);
    }, 
    getTeams : function() {
        $.ajax({
            url: host+"data/teams",
            dataType: "json",
            cache: false,
            success: function(data) {
                ChooseTeam.teams = data;
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log('error ' + textStatus + " " + errorThrown);
            }
        });
    }
}
var Results = {
    result :[],
    init:function(team1, team2){
        $.ajax({
            url: host+"data/analyze/"+team1+"/"+team2,
            dataType: "json",
            cache: false,
            success: function(data) {
                Results.result = data;
                console.log(data);
                Results.renderData();
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log('error ' + textStatus + " " + errorThrown);
            }
        });
    },
    renderData: function(){
        $(".fire").hide();
        $("#index").fadeOut();
        $("#result").show();
        $("body").css({ 'background-color': '#ffffff' });
        $("body").css('background-image', 'none');
        $("#odd-table tbody").append("<tr><td>"+selectSides[0].name.text()+"</td><td>"+1/Results.result[0]["1"]+"</td></tr>");
        $("#odd-table tbody").append("<tr><td>Draw</td><td>"+1/Results.result[0]["X"]+"</td></tr>");
        $("#odd-table tbody").append("<tr><td>"+selectSides[1].name.text()+"</td><td>"+1/Results.result[0]["2"]+"</td></tr>");
        $("#odd-table tbody").append("<tr><td>"+selectSides[0].name.text()+" or Draw</td><td>"+1/(Results.result[0]["1"]+Results.result[0]["X"])+"</td></tr>");
        $("#odd-table tbody").append("<tr><td>"+selectSides[1].name.text()+" or Draw</td><td>"+1/(Results.result[0]["2"]+Results.result[0]["X"])+"</td></tr>");
        var div1=d3.select(document.getElementById('div1'));
        var div2=d3.select(document.getElementById('div2'));
        var div3=d3.select(document.getElementById('div3'));
        var div4=d3.select(document.getElementById('div4'));

        start();

        function labelFunction(val,min,max) {

        }

        function deselect() {
            div1.attr("class","radial");
            div2.attr("class","radial");
            div3.attr("class","radial");
        }
        function start() {
            var rp1 = radialProgress(document.getElementById('div1'))
                    .label(selectSides[0].name.text())
                    .diameter(160)
                    .value(Results.result[0]["1"]*100)
                    .render();

            var rp2 = radialProgress(document.getElementById('div2'))
                    .label("Draw")
                    .diameter(160)
                    .value(Results.result[0]["X"]*100)
                    .render()

            var rp3 = radialProgress(document.getElementById('div3'))
                    .label(selectSides[1].name.text())
                    .diameter(160)
                    .value(Results.result[0]["2"]*100)
                    .render();
        }
        var brandsData = [];
        for (var key in Results.result[1]) {
            $("#odd-table tbody").append("<tr><td>"+key+"</td><td>"+1/Results.result[1][key]+"</td></tr>");
            brandsData.push({
                name: key,
                y: Results.result[1][key]*100
            });
        }
        
        
        $('#score-chart').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                    text: 'Final Score'
            },
            xAxis: {
                type: 'category'
            },
            yAxis: {
                title: {
                    text: 'Chance'
                }
            },
            legend: {
                enabled: false
            },
            plotOptions: {
                series: {
                    borderWidth: 0,
                    dataLabels: {
                        enabled: true,
                        format: '{point.y:.1f}%'
                    }
                }
            },

            tooltip: {
                headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
                pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> chance<br/>'
            },

            series: [{
                name: 'Score',
                colorByPoint: true,
                data: brandsData
            }]
        });
        $('#goals').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: 0,
                plotShadow: false
            },
            title: {
                text: 'Over/Under 2.5',
                align: 'center',
                verticalAlign: 'middle',
                y: 50
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    dataLabels: {
                        enabled: true,
                        distance: -50,
                        style: {
                            fontWeight: 'bold',
                            color: 'white',
                            textShadow: '0px 1px 2px black'
                        }
                    },
                    startAngle: -90,
                    endAngle: 90,
                    center: ['50%', '75%']
                }
            },
            series: [{
                type: 'pie',
                name: 'Over/Under 2.5',
                innerSize: '50%',
                data: [
                    ['Under 2.5', Results.result[2]["under"]*100],
                    ['Over 2.5', Results.result[2]["over"]*100],
                    {
                        name: 'Others',
                        y: 0.1,
                        dataLabels: {
                            enabled: false
                        }
                    }
                ]
            }]
        });
    }
}
ChooseTeam.init()