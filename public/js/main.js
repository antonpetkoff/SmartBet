var host = "http://localhost:3000/"
var ChooseTeam = {
    teams : [],
    team1 : 0,
    team2 : 1,
    team1Next : $("#t1-next"),
    team1Back : $("#t1-back"),
    team2Next : $("#t2-next"),
    team2Back : $("#t2-back"),
    init : function() {
        ChooseTeam.getTeams();
        ChooseTeam.setup();
    },
    setup: function(){
        ChooseTeam.team1Next.click(function(){ChooseTeam.changeTeam(1,1)})
        ChooseTeam.team2Next.click(function(){ChooseTeam.changeTeam(2,1)})
        ChooseTeam.team1Back.click(function(){ChooseTeam.changeTeam(1,-1)})
        ChooseTeam.team2Back.click(function(){ChooseTeam.changeTeam(2,-1)})
    },
    changeTeam : function(team, step){
        alert(team, step)
    },
    getTeams : function() {
        $.ajax({
            url: host+"data/teams",
            dataType: "json",
            cache: false,
            success: function(data) {
                teams = data;
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log('error ' + textStatus + " " + errorThrown);
            }
        });
    }
}
ChooseTeam.init()