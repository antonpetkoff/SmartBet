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
ChooseTeam.init()