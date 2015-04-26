var express = require("express"),
    app = express(),
    fs = require("fs"),
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
console.log(data);
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