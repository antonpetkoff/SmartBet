var express = require("express"),
    app = express(),
    fs = require("fs"),
    algorithm = require("./proxy.js");
    data = require("./res/epl_teamNames.json");
app.set("views", "./views");
app.set('view engine', 'jade');

app.use(express.static("public"));

app.get("/", function(req, res, next){
	if (fs.existsSync(__dirname+"/views/index.jade")) {
		res.render("index", {teams:data});
	}else {
    next();
  };
});
algorithm.runAlgorithm("1", "2", function(error, data){
  console.log(error);
  console.log(typeof data);
});
/*app.get("/product/:id", function(req, res, next){
  if (fs.existsSync(__dirname+"/views/index.jade")) {
    res.render("product", {shop:data.products[req.params.id]});
  }else {
    next();
  };
});*/

app.get("/data/teams", function(req, res, next){
  res.json(data)
});
app.get("/data/analyze/:team1/:team2", function(req, res, next){
  if(req.params){
    algorithm.runAlgorithm(req.params.team1, req.params.team2, function(error, data){
      data = data.replace(/[^a-z0-9{}[].:,\s]/gi, '');
      data = data.replace(/\r\n/g, '');
      console.log(data);
      data = JSON.parse(data);
      res.json(data)
    });
  }
  
});
app.get("/:fileName", function(req, res, next){
	if(req.params && req.params.fileName && fs.existsSync(__dirname+"/views/"+req.params.fileName+".jade")){
		res.render(req.params.fileName, {teams:data});
	} else {
		next();
	}
})

var server = app.listen(3000, function () {

  var host = server.address().address
  var port = server.address().port

  console.log('Example app listening at http://%s:%s', host, port)

})